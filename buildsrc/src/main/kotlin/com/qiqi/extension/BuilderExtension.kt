package com.qiqi.extension

open class BuilderExtension {

    var versionCode: Int = 0

    var applicationName: String = ""

    var monitorResources: Array<String> = emptyArray();

    var isCustomDex = false

    fun getNameSet(): HashSet<String> {
        val nameSet: HashSet<String> = HashSet()

        nameSet.addAll(monitorResources)
        nameSet.add(applicationName)
        return nameSet
    }

    override fun toString(): String {
        return "BuilderExtension(versionCode=$versionCode, applicationName='$applicationName', monitorResources=${monitorResources.contentToString()}, isCustomDex=$isCustomDex)"
    }

}
