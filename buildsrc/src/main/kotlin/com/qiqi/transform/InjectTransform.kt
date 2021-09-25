package com.qiqi.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.qiqi.inject.MatrixInjector
import com.qiqi.main.JarClassPathManager
import com.qiqi.utils.Log
import com.qiqi.utils.MultiThreadManager
import org.gradle.api.Project


/**
 * 注入代码，针对具体项目修改
 */
class InjectTransform(var project: Project) : Transform() {

    override fun getName(): String {
        return "BuilderInjectTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(context: Context?, inputs: MutableCollection<TransformInput>?, referencedInputs: MutableCollection<TransformInput>?, outputProvider: TransformOutputProvider?, isIncremental: Boolean) {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        Log.i("injectApplication Transform " + Thread.currentThread().getName())

        val startTime = System.currentTimeMillis();
        // inputs有两种类型，一种是目录，一种是jar，需要分别遍历。
        val manager = MultiThreadManager<String>()
        if (inputs != null) {
            for (input in inputs) {
                for (directoryInput in input.directoryInputs) {
                    val dest = outputProvider?.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                    JarClassPathManager.addClassPath(directoryInput.file.absolutePath)
                    manager.addTask {
                        dest?.absolutePath?.let { MatrixInjector.inject(directoryInput.file, it) }.toString()
                    }
                }
                for (jarInput in input.jarInputs) {
                    Log.i("Transform jar path:" + jarInput.file.absolutePath)
                    // 重命名输出文件（同目录copyFile会冲突）
                    var jarName = jarInput.name
                    val md5Name = org.apache.commons.codec.digest.DigestUtils.md5Hex(jarInput.file.absolutePath)
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length - 4)
                    }
                    val dest = outputProvider?.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                    JarClassPathManager.addClassPath(jarInput.file.absolutePath)
                    manager.addTask {
                        dest?.absolutePath?.let { MatrixInjector.inject(jarInput.file, it) }.toString()
                    }
                }
            }
        }
        manager.start()
        JarClassPathManager.writeFile(project)
        Log.i("transform 时间：" + (System.currentTimeMillis() - startTime) / 1000)
    }

}