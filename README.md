# Builder

1、文件说明

[builder-lib] 公共代码

[builder-cli] 编译java、res代码工程，执行脚本如下：
my_clear.bat
my_delete.bat
my_gradle_hot.bat
my_gradle_hot_res.bat
my_install_run.bat
my_reset.bat

[builder-runtime] 增量加载lib工程

[buildsrc] gradle编译插件工程

[builder-sample-android] demo工程

[build_tool] 工具包

[builder-studio-plugin] ide插件，执行脚本工程

2、gradle断点调试方法

https://www.jianshu.com/p/b2000f80b818

1、gradlew.bat assembleDebug -Dorg.gradle.daemon=false -Dorg.gradle.debug=true

2、新建一个remode

3、点击debug按钮