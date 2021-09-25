package com.qiqi.util

import com.android.build.gradle.AppExtension
import com.qiqi.utils.*
import org.gradle.api.Project
import java.io.File

object ProjectUtil {

    fun cancelTask(project: Project, taskName: String) {
        val task = project.getTasks().findByName(taskName)
        if (task != null) {
            Log.i("增量跳过task：" + taskName)
            task.enabled = false
        }
    }

    fun cancelIncrementalTask(project: Project) {

        cancelTask(project, "javaPreCompileDebug")
        cancelTask(project, "compileDebugJavaWithJavac")
        cancelTask(project, "transformClassesWithJarMergingForDebug")
        cancelTask(project, "transformClassesWithMultidexlistForDebug")
        cancelTask(project, "transformClassesWithDexForDebug")
        cancelTask(project, "validateSigningDebug")
        cancelTask(project, "packageDebug")
        cancelTask(project, "assembleDebug")
    }

    val cancleTasks = arrayOf(
            "javaPreCompileDebu", "compileDebugJavaWithJavac", "transformClassesWithJarMergingForDebug", "transformClassesWithMultidexlistForDebug", "transformClassesWithDexForDebug", "validateSigningDebug", "packageDebug", "assembleDebug"
    )

    fun resOpen(project: Project) {
        val cmd = ArrayList<String>()
        cmd.add(BuilderInitializer.getJavaHome() + "\\bin\\java.exe")
        cmd.add("-jar")
        cmd.add("-Dfile.encoding=UTF-8")
        cmd.add(BuildUtils.getBuildToolPath(project) + "\\lib\\hot.jar ")
        cmd.add(BuildUtils.getRootProjectPath(project) + "\\build")
        cmd.add("res")
        CmdUtil.cmd(cmd)
    }

    fun installApp(project: Project, path: String) {
        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("install")
        cmd.add("-t")
        cmd.add("-r")
        cmd.add(path)
        CmdUtil.cmd(cmd)
    }

    fun restartApp(project: Project) {
        project.extensions.findByType(AppExtension::class.java)
        val packageName = BuildUtils.getPackageName(project)
        val bootActivity = BuildUtils.getBootActivity(project)
        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("shell")
        cmd.add("am")
        cmd.add("force-stop")
        cmd.add(packageName)
        CmdUtil.cmd(cmd)
        cmd.clear()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("shell")
        cmd.add("am")
        cmd.add("start")
        cmd.add("-n")
        cmd.add(packageName + "/" + bootActivity)
        CmdUtil.cmd(cmd)
    }

    fun signerApk(project: Project, newApk: String, oldApk: String) {
        val cmd = ArrayList<String>()
        cmd.add("jarsigner")
        cmd.add("-verbose")
        cmd.add("-keystore")
        cmd.add(BuildUtils.getBuildToolPath(project) + "\\debug.keystore")
        cmd.add("-storepass")
        cmd.add("android")
        cmd.add("-keypass")
        cmd.add("android")
        cmd.add("-signedjar")
        cmd.add(newApk)
        cmd.add(oldApk)
        cmd.add("androiddebugkey")
        CmdUtil.cmd(cmd)
    }

    //d8
    fun jar2Dex(project: Project,  jarPathTemp: String){
        var jarPath = jarPathTemp
        Log.i("jar2Dex jarPath：" + jarPath)
        val file = File(jarPath)
        if (file.isDirectory) {
            val time = System.currentTimeMillis()
            val jarOut = getJarPath(project, jarPath)
            val zipPath = jarOut + File.separator + "class.jar"
            ZipUtil.zip(zipPath, jarPath)
            jarPath = zipPath
            Log.i("class zip time：" + (System.currentTimeMillis() - time))
        }

        val dexOut = getDexPath(project, jarPath)
        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getD8CmdPath(project))
        cmd.add("--debug")
        cmd.add("--min-api")
        cmd.add("26")//26解决java8特性问题
        cmd.add("--lib")
        cmd.add(BuildUtils.getAndroidJarPath(project))
        cmd.add("--output")
        cmd.add(dexOut)
        cmd.add(jarPath)
//        cmd.add("-JXms1024M")
//        cmd.add("-JXmx2048M")
        CmdUtil.cmd(cmd)
    }

    //dx
    fun jar2DexDx(project: Project, jarPath: String) {
        val dexOut = getDexPath(project, jarPath)
        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getDxCmdPath(project))
        cmd.add("--multi-dex")
        cmd.add("--dex")
        cmd.add("--min-sdk-version=26")//26解决java8特性问题
        cmd.add("--core-library")//解决定义[java.* or javax.*]包名报错
        cmd.add("--num-threads=6")
        cmd.add("--output=" + dexOut)
        cmd.add(jarPath)
        cmd.add("-JXms1024M")
        cmd.add("-JXmx2048M")
        CmdUtil.cmd(cmd)
    }

    fun getDexPath(project: Project, jarPath: String): String {
        val md5Name = MD5Util.getMd5(jarPath)
        val dexOutPath = BuildUtils.getBuildMyPath(project) + "\\dex\\package\\" + md5Name
        FileUtil.deleteDir(File(dexOutPath))
        FileUtil.ensumeDir(File(dexOutPath))
        return dexOutPath
    }

    fun getJarPath(project: Project, jarPath: String):
            String {
        val md5Name = MD5Util.getMd5(jarPath)
        val dexOutPath = BuildUtils.getBuildMyPath(project) + "\\jar\\" + md5Name
        FileUtil.deleteDir(File(dexOutPath))
        FileUtil.ensumeDir(File(dexOutPath))
        return dexOutPath
    }

    fun resetApp(project: Project) {
//        adb shell  rm -r /sdcard/patch_dex.jar
//        adb shell  rm -r /sdcard/patch_resources.apk
        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("shell")
        cmd.add("rm")
        cmd.add("-r")
        cmd.add(BuildUtils.getExternalCacheDir(project) + "/patch_dex.jar")
        CmdUtil.cmd(cmd)

        cmd.clear()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("shell")
        cmd.add("rm")
        cmd.add("-r")
        cmd.add(BuildUtils.getExternalCacheDir(project) + "/patch_resources.apk")
        CmdUtil.cmd(cmd)
    }

    fun pushFile2SD(project: Project, path: String) {
        if (!FileUtil.fileExists(path)) {
            return
        }

        val cmd = ArrayList<String>()
        cmd.add(BuildUtils.getAdbCmdPath(project))
        cmd.add("push")
        cmd.add(path)
        cmd.add(BuildUtils.getExternalCacheDir(project))
        CmdUtil.cmd(cmd)
    }
}
