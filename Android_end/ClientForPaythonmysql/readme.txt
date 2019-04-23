1.运行app 在首页输入ip port和url （url 保存是使用Android的SharedPreference）
the storage of url is in "SharedPreference"
2.连接服务的操作在（app\src\main\java\com\cart\manager）包下，所有的操作服务操作在这里
base socket connection,the android-end is the client-end of this socket connection, detailed operation in app\src\main\java\com\cart\manager
3.连接服务器和断开连接由类app\src\main\java\com\cart\service\ClientService.java实现
4.接收服务器的数据接口（app\src\main\java\com\cart\listener）
5.接收数据通知到界面采用观察者模式，主要实现在（app\src\main\java\com\cart\observer）