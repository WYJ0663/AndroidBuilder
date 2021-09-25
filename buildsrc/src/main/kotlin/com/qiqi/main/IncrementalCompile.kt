package com.qiqi.main


/**
 * 增量打包
 */
class IncrementalCompile : BaseCompile() {

    override fun apply() {
        project.tasks.findByName("preBuild")?.doFirst {
            throw  Error("不可编译，请删除build/my再编译")
        }
    }

}
