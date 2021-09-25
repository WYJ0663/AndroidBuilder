lib/hot.jar  执行增量编译的包，代码BuilderJava

lib/apktool-cli-all.jar 反编译resources.arsc等资源文件

dex/patch_dex.jar   重置增量包,空包，废弃

jars/hot_hack.jar android导入包，废弃

increase_class_list.txt.txt  需要加入编译的java类，解决继承类修改问题，放于build\my下

expel_class_list.txt    不需要加入编译的java类，解决编译问题，放于build\my下

debug.keystore 无签名时，自动使用改签名 -storepass android -keypass android androiddebugkey

values 添加编译的资源id，放于build\my下