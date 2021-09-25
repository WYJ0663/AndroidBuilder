package com.qiqi.util

import com.android.build.gradle.AndroidConfig
import com.qiqi.utils.FileUtil
import com.qiqi.utils.Log
import org.gradle.api.Project
import org.json.simple.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * Created by huangyong on 16/7/19.
 */
object BuilderInitializer {

    private val projectDescription = LinkedHashMap<String, Any>()

    val javaSet = HashSet<String>()
    val resSet = HashSet<String>()

    fun init(project: Project) {
        val android = BuildUtils.getAndroid(project) ?: return

        projectDescription["project_type"] = "gradle"
        projectDescription["out_dir"] = BuildUtils.getBuildMyPath(project)
        projectDescription["build_tool_dir"] = BuildUtils.getBuildToolPath(project)
        projectDescription["java_home"] = getJavaHome()
        projectDescription["root_dir"] = project.rootDir.path
        projectDescription["main_project_name"] = project.name
        projectDescription["build_directory"] = project.buildDir.path
        projectDescription["build_tools_version"] = android.buildToolsVersion
        projectDescription["sdk_directory"] = android.sdkDirectory.path
        projectDescription["build_tools_directory"] = joinPath(arrayOf(android.sdkDirectory.path, "build-tools", android.buildToolsVersion))
        projectDescription["compile_sdk_version"] = android.compileSdkVersion
        projectDescription["compile_sdk_directory"] = joinPath(arrayOf(android.sdkDirectory.path, "platforms", android.compileSdkVersion))
//        projectDescription["package_name"] = android.defaultConfig.applicationId
        projectDescription["package_name_manifest"] = BuildUtils.getPackageName(project)
        projectDescription["main_manifest_path"] = BuildUtils.getAndroidManifestPath(project)
        projectDescription["boot_activity"] = BuildUtils.getBootActivity(project)

        saveJson(project)
    }

    private fun saveJson(project: Project) {
        projectDescription.put("scan_src", change(javaSet))
        projectDescription.put("scan_res", change(resSet))
        val json = JSONObject.toJSONString(projectDescription)
        //        println json
        val savaPath = BuildUtils.getBuildMyPath(project) + "\\build_info.json"
        FileUtil.ensumeDir(File(BuildUtils.getBuildMyPath(project)))
        FileUtil.writeFile(savaPath, json)
    }

    // from retrolambda
    fun getJavaHome(): String {
        val javaHomeProp = System.getProperties().getProperty("java.home")
        if (javaHomeProp != null) {
            val jreIndex = javaHomeProp.lastIndexOf("${File.separator}jre")
            if (jreIndex != -1) {
                return javaHomeProp.substring(0, jreIndex)
            } else {
                return javaHomeProp
            }
        } else {
            return System.getenv("JAVA_HOME")
        }
    }

    private fun appendDirs(targetCollections: HashSet<String>?, collections: MutableSet<File>?) {
        if (collections != null) {
            for (dir in collections) {
                targetCollections?.add(dir.absolutePath)
            }
        }
    }

    private fun change(collections: MutableSet<String>): List<String> {
        return ArrayList<String>(collections)
    }

    private fun joinPath(sep: Array<String>): String {
        if (sep.isEmpty()) {
            return ""
        }
        if (sep.size == 1) {
            return sep[0]
        }

        return File(sep[0], joinPath(Arrays.copyOfRange(sep, 1, sep.size))).getPath()
    }

    fun getSourcePath(project: Project) {
        try {
            if (project.hasProperty("android")) {
                val android = project.extensions.getByName("android") as AndroidConfig
                val sourceSetsValue = android.sourceSets?.findByName("main")
                Log.i("java path " + sourceSetsValue?.java?.srcDirs)
                appendDirs(javaSet, sourceSetsValue?.java?.srcDirs)
                appendDirs(resSet, sourceSetsValue?.res?.srcDirs)
            }
        } catch (e: Exception) {

        }
    }
}
