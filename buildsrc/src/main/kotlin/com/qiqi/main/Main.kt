package com.qiqi.main

import com.qiqi.extension.BuilderExtension
import com.qiqi.transform.InjectTransform
import com.qiqi.util.BuildUtils
import com.qiqi.util.BuilderInitializer
import com.qiqi.utils.FileUtil
import com.qiqi.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class Main : Plugin<Project> {

        private val isDebug = false //true 全量编译
//    private val isDebug = true //true 全量编译

    companion object {
        var builderExtension: BuilderExtension? = null
    }

    override fun apply(project: Project) {
        Log.i("========================")
        Log.i("kt 这是自定义编译插件! " + project.name)
        Log.i("========================")

        builderExtension = project.extensions.create("builder", BuilderExtension::class.java)

        start(project)
    }

    private fun registerTransform(project: Project) {
        val appExtension = BuildUtils.getAndroid(project)
        if (appExtension != null) {
            appExtension.registerTransform(InjectTransform(project))//注入代码

            if (builderExtension != null && builderExtension!!.versionCode > 0) {
                appExtension.defaultConfig.setVersionCode(builderExtension!!.versionCode) //？没找到也可以编译？
            }
        }
    }

    private fun start(project: Project) {
        project.afterEvaluate {
            Log.i("工程：" + project.name)
            BuilderInitializer.getSourcePath(project)
            if (!project.plugins.hasPlugin("com.android.application")) {
                Log.i("不是application工程，结束")
                return@afterEvaluate
            }

            Log.i(builderExtension.toString())

            autoDependency(project)

            registerTransform(project)

            BuildUtils.getAndroid(project)?.applicationVariants?.all { variant ->
                var variantName = variant.name.capitalize()
                Log.i("环境 $variantName " + project.name)
                if (variantName.equals("debug", ignoreCase = true)) {
                    doTask(project)
                }
            }
        }
    }

    fun hasCompile(project: Project): Boolean {
        val infoTxt = BuildUtils.getBuildMyPath(project) + "\\java_info.txt"
        return File(infoTxt).exists()
    }

    private fun doTask(project: Project) {
        val compile: BaseCompile
        if (!isDebug && hasCompile(project)) {
            Log.i("增量编译")
            compile = IncrementalCompile()

        } else {
            Log.i("全量编译")

            FileUtil.deleteDir(File(BuildUtils.getBuildMyPath(project)))
            FileUtil.ensumeDir(File(BuildUtils.getBuildMyPath(project)))

            compile = AllCompile()
        }

        compile.project = project
        compile.apply()
    }

    private fun autoDependency(project: Project) {
        if (builderExtension != null) {
            project.afterEvaluate {
                project.dependencies.add("compile", "com.qiqi.hack:aar:2.0.1")
            }
        }
    }
}