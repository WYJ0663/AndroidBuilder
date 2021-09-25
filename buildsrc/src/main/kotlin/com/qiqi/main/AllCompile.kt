package com.qiqi.main


import com.qiqi.util.BuildUtils
import com.qiqi.util.BuilderInitializer
import com.qiqi.util.ProjectUtil
import com.qiqi.utils.FileScanHelper
import com.qiqi.utils.FileUtil
import com.qiqi.utils.Log
import com.qiqi.utils.ZipUtil
import java.io.File

/**
 * 全量编译时，备份数据
 */
class AllCompile : BaseCompile() {

    override fun apply() {
        dexTask()
    }

    private fun dexTask() {

        project.tasks.findByName("preBuild")?.doLast {
            BuilderInitializer.init(project)

            copyRes2()

            scanJavaAndKotlin(BuilderInitializer.javaSet)

            scanResources(BuilderInitializer.resSet)
        }

        project.getTasks().findByName("assembleDebug")?.doLast {
            val startTime = System.currentTimeMillis()
            //            adb push ./buildsrc/dex/patch_dex.jar sdcard
            Log.i("打包结束：" + (System.currentTimeMillis() - startTime) / 1000)

        }

        val packageTask = project.getTasks().findByName("packageDebug")
        packageTask?.doLast {
            ProjectUtil.resetApp(project)

            val startTime = System.currentTimeMillis()
            var apk = ""
            for (f in packageTask.outputs.files.files) {
                if (f != null && f.isDirectory && f.listFiles() != null) {
                    for (cf in f.listFiles()) {
                        Log.i("cf.absolutePath:" + cf.absolutePath)
                        if (cf.absolutePath.endsWith(".apk")) {
                            apk = cf.absolutePath
                            Log.i("apk:$apk")
                        }
                    }
                }
            }
            JarClassPathManager.writeFile(project)

//            jarsigner -verbose -keystore debug.keystore -storepass android -keypass android -signedjar %APK_NAME%_signed.apk %APK_NAME%.apk androiddebugkey
            if (apk != "") {
                val oldApk = BuildUtils.getBuildMyPath(project) + "\\apk\\" + File(apk).name
                val newApk = BuildUtils.getBuildMyPath(project) + "\\apk\\signed.apk"
                FileUtil.ensumeDir(BuildUtils.getBuildMyPath(project) + "\\apk")
                FileUtil.fileCopy(apk, oldApk)

                try {
                    val isSigned = ZipUtil.checkZipFile(oldApk, "META-INF/.*.RSA")
                    if (FileUtil.fileExists(BuildUtils.getBuildToolPath(project) + "\\debug.keystore")
                            && !isSigned) {
                        ProjectUtil.signerApk(project, newApk, oldApk)
                        FileUtil.fileCopy(newApk, apk)
//                        installApp(newApk)
//                        restartApp()
                    } else {
                        FileUtil.fileCopy(oldApk, newApk)
                    }
                } catch (e: Exception) {
                    FileUtil.fileCopy(oldApk, newApk)
                }
                Log.i("apk签名时间：" + (System.currentTimeMillis() - startTime) / 1000)
                ProjectUtil.installApp(project, newApk)
                ProjectUtil.restartApp(project)
                Log.i("打包结束：" + (System.currentTimeMillis() - startTime) / 1000)
                ProjectUtil.resOpen(project)
                throw Error("=======编译完成，强制暂停=======")
            }
        }

//        taskTest()
    }

    private fun scanJavaAndKotlin(srcPath: Set<String>) {
        val startTime = System.currentTimeMillis()
        Log.i("备份java信息:")
        val helper = FileScanHelper()
        for (path in srcPath) {
            if (FileUtil.dirExists(path)) {
                Log.i("加入扫描 $path")
                helper.scanJavaAndKotlin(File(path))
            }
        }
        FileScanHelper.writeFile(helper.pathList, BuildUtils.getBuildMyPath(project) + "\\java_info.txt")

        Log.i("扫描java时间：" + (System.currentTimeMillis() - startTime) / 1000)
    }


    private fun scanResources(resPath: Set<String>) {
        val startTime = System.currentTimeMillis()
        Log.i("备份resources信息:")
        val helper = FileScanHelper()
        for (path in resPath) {
            if (FileUtil.dirExists(path)) {
                Log.i("加入扫描 $path")
                helper.scan(File(path))
            }
        }
        FileScanHelper.writeFile(helper.pathList, BuildUtils.getBuildMyPath(project) + "\\resources_info.txt")

        Log.i("扫描resources时间：" + (System.currentTimeMillis() - startTime) / 1000)
    }

}
