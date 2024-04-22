JDK：1.8
Mysql版本：8.0.27
客户端(FMBankClient)和服务端(FeiMaBank)的程序入口都在Main.java中。
FMBANK.sql需首先导入mysql数据库中，不然数据库将连接失败。请将mysql部署于Linux环境下。
请建立一个名为"FMBANK"的数据库，然后将FMBANK.sql使用source命令导入。
服务端需部署在linux环境中,与mysql部署于同一实例下，客户端需部署在Windows环境中。
启动顺序应为：启动mysql->启动服务端->启动客户端。
由于都带有jar包，使用javac无参数编译会失败。
在linux服务端可以直接运行在/src目录下的start.sh进行编译、运行、进入程序。
在客户端可以运行编译过的\FMBankClient\out\production\FMBankClient 中的Main.class 进入程序，也可使用idea打开项目以编译运行。
请注意：
进入客户端界面后，请首先点击"Chang IP"按钮将IP地址改为服务端的地址。
虽然服务端支持多线程，但若两个客户端同时使用同一功能，第二个客户端将出现socket端口被占用的exception。
若客户端程序在某功能使用过程中异常退出，服务端可能会出现该socket未关闭的问题，导致下一个客户端也无法使用该功能(该程序使用固定端口号通讯)。此时需要通过start.sh 重启服务端。