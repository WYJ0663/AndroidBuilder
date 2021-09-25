package com.qiqi.util

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.android.builder.model.Version
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by tong on 17/3/14.
 */
object BuildUtils {

    fun getExternalCacheDir(project: Project): String {
        return "sdcard/Android/data/" + getPackageName(project) + "/cache"
    }

    fun getBuildPath(project: Project): String {
        return project.buildDir.absolutePath
    }

    fun getProjectPath(project: Project): String {
        return project.projectDir.absolutePath
    }

    fun getRootProjectPath(project: Project): String {
        return project.rootDir.absolutePath
    }

    fun getBuildResourcesPath(project: Project): String {
        return getBuildPath(project) + "\\resources"
    }

    fun getBuildSrcPath(project: Project): String {
        return getRootProjectPath(project) + "\\buildsrc"
    }

    fun getBuildToolPath(project: Project): String {
        return getRootProjectPath(project) + "\\build_tool"
//        return "C:\\qiqi\\build_tool"
    }

    fun getBuildMyPath(project: Project): String {
        return getRootProjectPath(project) + "\\build\\my"
    }

    fun getBuildMyResourcesPath(project: Project): String {
        return getBuildMyPath(project) + "\\resources"
    }

    fun getBuildMyJarPath(project: Project): String {
        return getBuildMyPath(project) + "\\jar"
    }

    fun getDebugPath(project: Project): String {
        return getBuildPath(project) + "\\" + "intermediates\\classes\\debug"
    }

    fun getReleasePath(project: Project): String {
        return getBuildPath(project) + "\\" + "intermediates\\classes\\release"
    }

    fun getClassPath(project: Project): String {
        return getBuildPath(project) + "\\" + "intermediates\\classes"
    }

    fun getBuildMyClassPath(project: Project): String {
        return getBuildPath(project) + "\\my\\classes"
    }

    fun getAndroidJarPath(project: Project): String {
        return "${getSdkDirectory(project)}${File.separator}platforms${File.separator}${getAndroid(project)?.compileSdkVersion}${File.separator}android.jar"
    }

    /**
     * 获取sdk路径
     * @param project
     * @return
     */
    fun getSdkDirectory(project: Project): String {
        var sdkDirectory = getAndroid(project)?.sdkDirectory?.absolutePath
        if (sdkDirectory?.contains("\\") == true) {
            sdkDirectory = sdkDirectory.replace("\\", "/")
        }
        return sdkDirectory ?: ""
    }


//    fun getDex2jarCmdPath(project: Project) {
//        return getBuildSrcPath(project) + "\\dex2jar-2.0\\d2j-dex2jar.bat"
//    }

    /**
     * 获取dx命令路径
     * @param project
     * @return
     */
    fun getDxCmdPath(project: Project): String {
        val dx = File(getSdkDirectory(project), "build-tools${File.separator}${getAndroid(project)?.buildToolsVersion}${File.separator}dx")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${dx.absolutePath}.bat"
        }
        return dx.absolutePath
    }

    /**
     * 获取d8命令路径
     * @param project
     * @return
     */
    fun getD8CmdPath2(project: Project): String {
        val dx = File(getSdkDirectory(project), "build-tools${File.separator}${getAndroid(project)?.buildToolsVersion}${File.separator}d8")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${dx.absolutePath}.bat"
        }
        return dx.absolutePath
    }

    fun getD8CmdPath(project: Project): String {
        val dx = File(getSdkDirectory(project), "build-tools${File.separator}28.0.3${File.separator}d8")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${dx.absolutePath}.bat"
        }
        return dx.absolutePath
    }

    /**
     * 获取aapt命令路径
     * @param project
     * @return
     */
    fun getAaptCmdPath(project: Project): String {
        val aapt = File(getSdkDirectory(project), "build-tools${File.separator}${getAndroid(project)?.buildToolsVersion}${File.separator}aapt")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${aapt.absolutePath}.exe"
        }
        return aapt.absolutePath
    }

    /**
     * 获取aapt2命令路径
     * @param project
     * @return
     */
    fun getAapt2CmdPath(project: Project): String {
        val aapt = File(getSdkDirectory(project), "build-tools${File.separator}${getAndroid(project)?.buildToolsVersion}${File.separator}aapt2")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${aapt.absolutePath}.exe"
        }
        return aapt.absolutePath
    }

    /**
     * 获取adb命令路径
     * @param project
     * @return
     */
    fun getAdbCmdPath(project: Project): String {
        val adb = File(getSdkDirectory(project), "platform-tools${File.separator}adb")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${adb.absolutePath}.exe"
        }
        return adb.absolutePath
    }


    fun getAndroidManifestPath(project: Project): String {
        val android = project.extensions.findByType(AppExtension::class.java)
        val main: AndroidSourceSet? = android?.sourceSets?.getByName("main")
        return main?.manifest?.srcFile?.path.toString()
    }

    fun getAndroid(project: Project): AppExtension? {
        return project.extensions.findByType(AppExtension::class.java)
    }

    /**
     * 获取AndroidManifest.xml文件package属性值
     */
    fun getPackageName(project: Project): String {
        return getPackageName(getAndroidManifestPath(project)).toString()
    }

    /**
     * 获取AndroidManifest.xml文件package属性值
     */
    private fun getPackageName(path: String): String? {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document: Document = builder.parse(File(path))
        val root = document.documentElement
        return root.attributes?.getNamedItem("package")?.nodeValue
    }

    /**
     * 获取启动的activity
     */
    fun getBootActivity(project: Project): String {
        return getBootActivity(getAndroidManifestPath(project)).toString()
    }

    /**
     * 获取启动的activity
     */
    private fun getBootActivity(path: String): String? {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document: Document = builder.parse(File(path))
        val root = document.documentElement
        val nodeList = root.childNodes
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node.nodeType == Node.ELEMENT_NODE && node.nodeName == "application") {
                for (i in 0 until node.childNodes.length) {
                    val activity = node?.childNodes?.item(i)
                    if (activity != null && activity.nodeType == Node.ELEMENT_NODE && activity.nodeName == "activity") {
                        for (i in 0 until activity.childNodes.length) {
                            val filter = activity.childNodes?.item(i)
                            if (filter != null && filter.nodeType == Node.ELEMENT_NODE && filter.nodeName == "intent-filter") {
                                var hasMainAttr = false
                                var hasLauncherAttr = false
                                for (i in 0 until filter.childNodes.length) {
                                    val action = filter.childNodes?.item(i)
                                    if (action != null && action.nodeType == Node.ELEMENT_NODE && action.nodeName == "action"
                                            && action.attributes.getNamedItem("android:name").nodeValue == "android.intent.action.MAIN"
                                    ) {
                                        hasMainAttr = true
                                    }
                                    if (action != null && action.nodeType == Node.ELEMENT_NODE && action.nodeName == "category"
                                            && action.attributes.getNamedItem("android:name").nodeValue == "android.intent.category.LAUNCHER"
                                    ) {
                                        hasLauncherAttr = true
                                    }
                                }
                                if (hasMainAttr && hasLauncherAttr) {
                                    return activity.attributes?.getNamedItem("android:name")?.nodeValue
                                }
                            }
                        }
                    }
                }
            }
        }
        return ""
    }

    /**
     * 获取android gradle插件版本
     * @return
     */
    fun getAndroidGradlePluginVersion(): String {
        return Version.ANDROID_GRADLE_PLUGIN_VERSION
    }


}