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

##### ~~方案一：双进程保活jni。~~

##### 方案一：双进程保活Aidl。

Android 5.0以上版本，采用双进程保活基本上没用了。



##### 方案二：Android 5.0 JobScheduler。

JobScheduler是Android 5.0提出的定时事件方案，本例中是在循环定时任务中启动保活服务。但JobScheduler在Android 8.0+中体现不是很好，甚至定时任务会失效。



##### 方案三：1像素Activity。

即监听亮屏和暗屏广播，实现Activity开启和关闭。



##### 方案四：Android 6.0 Doze模式-后台运行白名单。

即运行应用在低电量情况下进行后台运行。

```
/**
 * 针对N以上的Doze模式
 */
@SuppressLint("BatteryLife")
fun isIgnoreBatteryOption(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ZKeepAlive.isKeepalive) {
        try {
            val intent = Intent()
            val packageName = context.packageName
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                 intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                 intent.data = Uri.parse("package:$packageName")
                 context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```



##### 方案五：应用自启动白名单。

```
// 根据不同厂商进入自启动设置
private fun getSettingIntent(): Intent {
    var componentName: ComponentName? = null
    val brand = Build.BRAND
    when (brand.toLowerCase(Locale.ROOT)) {
        "samsung" -> componentName = ComponentName(
                "com.samsung.android.sm",
                "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity"
            )
        "huawei" -> componentName = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
            )
        "xiaomi" -> componentName = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        "vivo" -> componentName = ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
            )
        "oppo" -> componentName = ComponentName(
                "com.coloros.oppoguardelf",
                "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
            )
        "360" -> componentName = ComponentName(
                "com.yulong.android.coolsafe",
                "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity"
            )
         "meizu" -> componentName = ComponentName(
                "com.meizu.safe",
                "com.meizu.safe.permission.SmartBGActivity"
            )
        "oneplus" -> componentName = ComponentName(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )
    }
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (componentName != null) {
        intent.component = componentName
    } else {
        intent.action = Settings.ACTION_SETTINGS
    }
    return intent
}
```



##### 方案六：Workmanager循环任务。

Workmanager为Android 8.0+提供了完美的定时任务方案，利用Workmanager实现一个循环任务，并在任务中启动保活服务。



##### 方案七：AppWidget小组件。

如果使用应用者是合作方，不妨试试这种方案，基本上可以达到100%保活，前提条件是用户要将小组件移动桌面。

本例中是自定义倒计时小组件，并在小组件onUpdate中启动保活服务。



##### ~~方案八：账号同步拉活。~~

##### ~~方案九：低内存白名单。~~



##### 加强方案一：高优先级Service。

提高Service的优先级，在进程存在的情况下，降低系统回收几率。

- A：android:priority="1000"最高权限。

- B：onStartCommand返回值设置：

  getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY

- C：前置服务，startForeground(int id, Notification notification)。

- D：onDestroy()方法中重启服务。

##### 加强方案二：监听系统广播。

```
<!--开机广播-->
<action android:name="android.intent.action.BOOT_COMPLETED" />
<!--网络状态更新-->
<action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
<action android:name="android.net.wifi.STATE_CHANGE" />
<action
  android:name="android.net.conn.CONNECTIVITY_CHANGE"
  tools:ignore="BatteryLife" />
<!--电池电量变化-->
<action android:name="android.intent.action.BATTERY_CHANGED" />
<!--应用安装状态变化-->
<action android:name="android.intent.action.PACKAGE_ADDED" />
<action android:name="android.intent.action.PACKAGE_REPLACED" />
<action android:name="android.intent.action.PACKAGE_REMOVED" />
<!--屏幕亮度变化-->
<action android:name="android.intent.action.SCREEN_OFF" />
<action android:name="android.intent.action.SCREEN_ON" />
<!--锁屏-->
<action android:name="android.intent.action.USER_PRESENT" />
```

监听系统广播，在广播中启动相关进程和保活服务。



----------

![微信公众号：书客创作](http://upload-images.jianshu.io/upload_images/3480018-9c2adddde310e4ad..jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)