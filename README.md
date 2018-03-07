# Zalarm
Android利用AlarmManager设置闹钟小实例。
>作者：邹峰立，微博：zrunker，邮箱：zrunker@yahoo.com，微信公众号：书客创作，个人平台：[www.ibooker.cc](http://www.ibooker.cc)。

>本文选自[书客创作](http://www.ibooker.cc)平台第52篇文章。[阅读原文](http://www.ibooker.cc/article/52/detail) 。

![书客创作](http://upload-images.jianshu.io/upload_images/3480018-55590f6eebd7c261..jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### AlarmManager简介
在Android中可以通过AlarmManager实现调用系统闹钟服务，从而实现闹钟功能。

AlarmManager提供了对系统闹钟服务访问的接口。可以通过它来设定在未来某个时间唤醒的功能。闹钟响起，实际上是系统发出了为这个闹钟注册的广播，会自动开启目标应用。注册的闹钟在设备睡眠的时候仍然会保留，可以选择性地设置是否唤醒设备，但是当设备关机和重启后，闹钟将会被清除。

在闹钟广播接收器（简称alarmreceiver）的onReceive()方法被执行的时候，AlarmManager持有一个CPU唤醒锁，这样就保证了设备在处理完广播之前不会sleep。一旦onReceive()方法返回，AlarmManager就会释放这个锁，表明一些情况下可能onReceive()方法一执行完设备就会sleep。如果alarmreceiver中调用了Context.startService()，那么很可能service还没起来设备就sleep了。为了阻止这种情况，BroadcastReceiver和Service需要实现不同的唤醒锁机制，来确保设备持续运行到service可用为止。

注意：AlarmManager主要是用来在特定时刻运行的代码，即便是应用在那个特定时刻没有运行。对于常规的计时操作(ticks, timeouts, etc)，使用Handler处理更加方便和有效率。另外，从API 19开始，闹钟的机制都是非准确传递，操作系统将会转换闹钟，来最小化唤醒和电池使用。有一些新的API会支持严格准确的传递，如setWindow(int, long, long, PendingIntent)和setExact(int, long, PendingIntent)。targetSdkVersion在API19之前应用仍将继续使用以前的行为，所有的闹钟在要求准确传递的情况下都会准确传递。

### AlarmManager常用方法

1、初始化AlarmManager
```
AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
```
2、设置一次闹钟
```
/**
 * AndroidL开始，设置的alarm的触发时间必须大于当前时间 5秒
 *
 * 设置一次闹钟-(5s后启动闹钟)
 *
 * @AlarmType int type：闹钟类型
 * ELAPSED_REALTIME：在指定的延时过后，发送广播，但不唤醒设备（闹钟在睡眠状态下不可用）。如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒。
 * ELAPSED_REALTIME_WAKEUP：在指定的延时过后，发送广播，并唤醒设备（即使关机也会执行operation所对应的组件）。延时是要把系统启动的时间SystemClock.elapsedRealtime()算进去的。
 * RTC：指定当系统调用System.currentTimeMillis()方法返回的值与triggerAtTime相等时启动operation所对应的设备（在指定的时刻，发送广播，但不唤醒设备）。如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒（闹钟在睡眠状态下不可用）。
 * RTC_WAKEUP：指定当系统调用System.currentTimeMillis()方法返回的值与triggerAtTime相等时启动operation所对应的设备（在指定的时刻，发送广播，并唤醒设备）。即使系统关机也会执行 operation所对应的组件。
 *
 * long triggerAtMillis：触发闹钟的时间。
 * PendingIntent operation：闹钟响应动作（发广播，启动服务等）
 */
alarmManager.set(@AlarmType int type, long triggerAtMillis, PendingIntent operation);
```
3、设置精准周期闹钟
```
/**
 * AndroidL开始repeat的周期必须大于60秒
 *
 * 设置精准周期闹钟-该方法提供了设置周期闹钟的入口，闹钟执行时间严格按照startTime来处理，使用该方法需要的资源更多，不建议使用。
 *
 * @AlarmType int type：闹钟类型
 * long triggerAtMillis：触发闹钟的时间。
 * long intervalMillis：闹钟两次执行的间隔时间
 * PendingIntent operation：闹钟响应动作（发广播，启动服务等）
 */
alarmManager.setRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);
```
4、设置不精准周期闹钟
```
/**
 * AndroidL开始repeat的周期必须大于60秒
 *
 * 设置不精准周期闹钟- 该方法也用于设置重复闹钟，与第二个方法相似，不过其两个闹钟执行的间隔时间不是固定的而已。它相对而言更省电（power-efficient）一些，因为系统可能会将几个差不多的闹钟合并为一个来执行，减少设备的唤醒次数。
 *
 * @AlarmType int type：闹钟类型
 * long triggerAtMillis：触发闹钟的时间。
 * long intervalMillis：闹钟两次执行的间隔时间
 * 内置变量
 * INTERVAL_DAY： 设置闹钟，间隔一天
 * INTERVAL_HALF_DAY： 设置闹钟，间隔半天
 * INTERVAL_FIFTEEN_MINUTES：设置闹钟，间隔15分钟
 * INTERVAL_HALF_HOUR： 设置闹钟，间隔半个小时
 * INTERVAL_HOUR： 设置闹钟，间隔一个小时
 *
 * PendingIntent operation：闹钟响应动作（发广播，启动服务等）
 */
alarmManager.setInexactRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);
```
5、设置时区
```
alarmManager.setTimeZone(String timeZone);
如：东八区
alarmManager.setTimeZone("GMT+08:00");
```
时区设置需要SET_TIME _ZONE，所以要在清单文件中添加权限：
```
<!-- 允许设置时区-->
<uses-permission android:name="android.permission.SET_TIME_ZONE" />
```
6、取消闹钟
```
alarmManager.cancel(PendingIntent operation)
```
### 实例
首先看一下最终效果图：

![效果图](http://upload-images.jianshu.io/upload_images/3480018-1eefce763d4e079f..gif?imageMogr2/auto-orient/strip)

该实例是通过输入框输入想要定时的时间，然后设置闹钟，等待闹钟响应。对于这样的一个实例要怎么去实现呢？
1、布局：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">

<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="10dp">

<EditText
            android:id="@+id/ed_hour"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="时"
            android:inputType="number"
            android:padding="10dp" />

<EditText
            android:id="@+id/ed_min"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="分"
            android:inputType="number"
            android:padding="10dp" />

<EditText
            android:id="@+id/ed_seconds"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="秒"
            android:inputType="number"
            android:padding="10dp" />
    </LinearLayout>

<TextView
        android:id="@+id/tv_alarm"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

<Button
        android:id="@+id/btn_alarm"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        android:padding="10dp"
        android:text="设置闹钟" />

</LinearLayout>
```
2、业务逻辑实现
首先定义两个全局变量：这两个变量伴随着整个逻辑操作。
```
private AlarmManager alarmManager;
private PendingIntent pendingIntent;
```
这里是通过广播来接收并处理闹钟响应事件。
```
/**
 * 定义闹钟广播
 * Created by 邹峰立 on 2017/10/19.
 */
public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("startAlarm".equals(intent.getAction())) {
            Toast.makeText(context, "闹钟提醒", Toast.LENGTH_LONG).show();
            // 处理闹钟事件
            // 振动、响铃、或者跳转页面等
        }
    }
}
```
注意：要将该广播接收器添加到清单文件当中。
```
<receiver android:name=".AlarmBroadcast" android:process=":remote" />
```
最后通过延时Intent来添加闹钟响应广播。
```
// 设置闹钟触发动作
Intent intent = new Intent(this, AlarmBroadcast.class);
intent.setAction("startAlarm");
pendingIntent = PendingIntent.getBroadcast(this, 110, intent, PendingIntent.FLAG_CANCEL_CURRENT);
```
之后进行AlarmManager的初始化，并在界面onCreate方法中进行调用。
```
// 初始化闹钟
private void initAlarm() {
    // 实例化AlarmManager
    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    // 设置闹钟触发动作
    Intent intent = new Intent(this, AlarmBroadcast.class);
    intent.setAction("startAlarm");
    pendingIntent = PendingIntent.getBroadcast(this, 110, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    // 设置时区（东八区）-需要加权限SET_TIME_ZONE
    alarmManager.setTimeZone("GMT+08:00");
}
```
准备工作完成之后，最后便是实现闹钟功能了，当点击设置闹钟按钮的时候进行闹钟设置，所以这里是把设置闹钟处理工作写成一个方法。
```
// 设置闹钟
private void setAlarm(Calendar calendar) {
    //    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 5), pendingIntent);
    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

alarmTv.setText(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(calendar.getTime()));
    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
    hourEd.setText("");
    minutesEd.setText("");
    secondsEd.setText("");
}
```
只要在点击设置闹钟按钮的时候，实现该方法即可。这里借助于Calendar进行时间的转换。
```
Button alarmBtn = (Button) findViewById(R.id.btn_alarm);
alarmBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int mHour = Integer.parseInt(hourEd.getText().toString().trim());
        int mMinute = Integer.parseInt(minutesEd.getText().toString().trim());
        int mSeconds = Integer.parseInt(secondsEd.getText().toString().trim());
        // 设置闹钟时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, mSeconds);

      setAlarm(calendar);
    }
});
```
[Github地址](https://github.com/zrunker/Zalarm)
[阅读原文](http://www.ibooker.cc/article/52/detail)

----------
![微信公众号：书客创作](http://upload-images.jianshu.io/upload_images/3480018-9c2adddde310e4ad..jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
