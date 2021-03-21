# ZKeepAlive - 进程保活

### 一、前言

当系统资源不足时，系统将会启动回收机制。根据进程重要性和优先级依次进行回收，直至新建进程或运行的更重要进程有足够的资源。



### 二、进程优先级

根据进程重要性可分为五类（解释来源于网络）：

#### 1、前台进程 ★★★★★

前台进程可简单理解为用户当前操作所必需的进程。

举个例子：如果当进程包含如下任一条件，即可视为前台进程：

- 用户正在交互的Activity，即Activity已调用onResume() 方法。
- 已绑定到用户正在交互的Activity的Service。
- 正在“前台”运行的 Service，即服务已调用startForeground()。
- 正在执行一个生命周期回调的 Service，如onCreate()、onStart() 或 onDestroy()。
- 正在执行其 onReceive() 方法的 BroadcastReceiver。

通常情况下，前台进程数量不是很多，系统只有在内存不足以支撑其运行的情况下才会终止它们。

#### 2、可见进程 ★★★★

没有任何前台组件、但仍会影响用户在屏幕上所见内容的进程。

举个例子：如果当进程包含如下任一条件，即可视为可见进程：

- 已绑定到可见Activity的Service。

- 用户可见的Activity，即Activity已调用其 onPause() 方法。如：

  1. 前台Activity启动一个透明背景的Activity。

  2. 前台 Activity 启动了一个对话框，允许在其后显示上一 Activity，则有可能会发生这种情况。

可见进程重要性弱次于前台进程，所以除非为了维持所有前台进程同时运行而必须终止，否则系统不会终止这些进程。

#### 3、服务进程 ★★★

- 正在运行已使用 startService() 方法启动的服务且不属于上述两个更高类别进程的进程。

尽管服务进程与用户所见内容没有直接关联，但是它们通常在执行一些用户关心的操作。例如，在后台播放音乐或从网络下载数据。

#### 4、后台进程 ★★

- 包含目前对用户不可见的Activity的进程，即Activity已调用onStop() 方法。

这些进程对用户体验没有直接影响，系统可能随时终止它们，以回收内存供前台进程、可见进程或服务进程使用。 通常会有很多后台进程在运行，因此它们会保存在 LRU （最近最少使用）列表中，以确保包含用户最近查看的 Activity 的进程最后一个被终止。如果某个 Activity 正确实现了生命周期方法，并保存了其当前状态，则终止其进程不会对用户体验产生明显影响，因为当用户导航回该 Activity 时，Activity 会恢复其所有可见状态。

#### 5、空进程 ★

- 不含任何活动应用组件的进程。

保留这种进程的的唯一目的是用作缓存，以缩短下次在其中运行组件所需的启动时间。 为使总体系统资源在进程缓存和底层内核缓存之间保持平衡，系统往往会终止这些进程。



### ~~三、Android进程回收策略（LowMemoryKiller）~~



### 四、如何进行进程保活？

#### 1、原则

一、降低进程被杀死概率。

二、提升杀死后进程拉活机制。



#### 2、实现

##### 2.1、降低进程被杀死概率

方案一：1像素Activity，



##### 2.2、提升杀死后进程拉活机制



~~1、高优先级Service。~~
~~2、双进程保护Aidl。~~
~~3、Android 5.0 JobScheduler。~~
~~4、AppWidget小组件。~~
~~5、对一些系统广播监听（开机、锁屏、变暗、APP安装更新...）。~~
~~6、自启动白名单。~~
~~7、Android 6.0 Doze模式-后台运行白名单。~~
~~8、1像素Activity。~~
~~9、Workmanager。~~

10、 低内存白名单。

11、双进程保活jni。

12、账号同步拉活。

/**

 * Android 闹钟实例：

 * <p>

 * 1、杀不死的服务Service，只能在进程存在的情况下，降低系统回收几率。

 * - A：android:priority="1000"最高权限；

 * - B:onStartCommand返回值设置，getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY;

 * - C:前置服务，startForeground(int id, Notification notification);

 * - D:onDestroy()方法中重启服务。

 * <p>

 * 2、双进程保护：AIDL，开启两个Service(A和B)，运行在两个不同的进程中android:process=":remote_service"，实现A和B相互守护。

 * <p>

 * 3、Android 5.0 JobScheduler，Android 6.0 Doze模式。

 * <p>

 * 4、AppWidget小组件开发，定义倒计时小组件，在小组件中启动闹钟服务。

 * <p>

 * 5、对一些系统广播监听（开机、锁屏、安装更新APP...）
   *

 * @author 邹峰立
   */



----------

![微信公众号：书客创作](http://upload-images.jianshu.io/upload_images/3480018-9c2adddde310e4ad..jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
