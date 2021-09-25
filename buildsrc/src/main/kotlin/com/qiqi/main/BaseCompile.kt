package com.qiqi.main

import com.android.build.gradle.AppExtension
import com.qiqi.util.BuildUtils
import com.qiqi.utils.FileUtil
import com.qiqi.utils.Log
import org.gradle.api.Project
import java.io.File

/**
 * 增量打包
 */
open class BaseCompile {
    lateinit var project: Project

    open fun apply() {

    }

    protected fun copyRes() {
        val android = project.extensions.findByType(AppExtension::class.java)
        //缓存id aapt2
        FileUtil.ensumeDir(File(BuildUtils.getBuildMyResourcesPath(project)))
        val publicTxtPath = BuildUtils.getBuildMyResourcesPath(project) + "\\public.txt"
        android?.aaptOptions?.additionalParameters("--emit-ids", publicTxtPath)
//        String test = new File(BuildUtils.getMyBuildPath(project) + "\\public.txt")
        if (File(publicTxtPath).exists()) {
            Log.i("--stable-ids $publicTxtPath")
            android?.aaptOptions?.additionalParameters("-v", "--stable-ids", publicTxtPath)
        }

        val generateSources = project.tasks.findByName("processDebugResources")
        generateSources?.doLast {
            Log.i("复制资源，task " + generateSources.name)
            for (f in generateSources.outputs.files.files) {
                Log.i("文件:" + f.path)
                val path = BuildUtils.getBuildMyResourcesPath(project)//缓存
                if (f.isFile()) {
                    FileUtil.fileCopy(f.path, path + "\\" + f.name)
                    if (f.name.contains("resources-debug.ap_")) {
                        FileUtil.fileCopy(f.path, path + "\\" + (f.name.replace("resources-debug.ap_", "patch_resources.apk")))
                    }
                } else {
                    if (f.path.contains("\\r\\debug")) {
                        FileUtil.copy(f.path, path + "\\r")
                    } else {
                        FileUtil.copy(f.path, path)
                    }
                }
            }
            val resPath = BuildUtils.getBuildMyResourcesPath(project) + "\\resources-debug.ap_"
            if (FileUtil.fileExists(resPath)) {
                val dexPath = BuildUtils.getBuildMyResourcesPath(project) + "\\patch_resources.apk"
                FileUtil.fileCopy(resPath, dexPath)
            }
            onUpdateResources()
        }
    }

    private fun onUpdateResources() {

    }

    protected fun copyRes2() {
        val generateSources = project.tasks.findByName("processDebugResources")
        generateSources?.doLast {
            Log.i("复制资源，task " + generateSources.name)
            for (f in generateSources.outputs.files.files) {
                Log.i("文件:" + f.path)
                val path = BuildUtils.getBuildMyResourcesPath(project)//缓存
                if (f.isFile()) {
                    FileUtil.fileCopy(f.path, path + "\\" + f.name)
                    if (f.name.contains("resources-debug.ap_")) {
                        FileUtil.fileCopy(f.path, path + "\\" + (f.name.replace("resources-debug.ap_", "patch_resources.apk")))
                    }
                } else {
                    if (f.path.contains("\\r\\debug")) {
                        FileUtil.copy(f.path, "$path\\r")
                    } else {
                        FileUtil.copy(f.path, path)
                    }
                }
            }
        }
    }

}
