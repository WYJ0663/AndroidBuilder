package com.qiqi.main

import com.qiqi.util.BuildUtils
import com.qiqi.utils.FileUtil
import org.gradle.api.Project;


object JarClassPathManager {

    private val mClassPath = HashSet<String>()

    fun addClassPath(path: String) {
        mClassPath.add(path)
    }

    fun writeFile(project: Project) {
        FileUtil.writeFile(mClassPath, BuildUtils.getBuildMyPath(project) + "\\jar_list.txt")
    }

}