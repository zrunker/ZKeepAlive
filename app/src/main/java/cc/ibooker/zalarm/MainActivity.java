package cc.ibooker.zalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 闹钟实现-AlarmManager
 */
public class MainActivity extends AppCompatActivity {
    private EditText hourEd, minutesEd, secondsEd;
    private TextView alarmTv;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAlarm();
    }

    // 初始化控件
    private void initView() {
        hourEd = (EditText) findViewById(R.id.ed_hour);
        minutesEd = (EditText) findViewById(R.id.ed_min);
        secondsEd = (EditText) findViewById(R.id.ed_seconds);
        alarmTv = (TextView) findViewById(R.id.tv_alarm);
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
    }

    // 初始化闹钟
    private void initAlarm() {
        // 实例化AlarmManager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 设置闹钟触发动作
        Intent intent = new Intent(this, AlarmBroadcast.class);
        intent.setAction("startAlarm");
        pendingIntent = PendingIntent.getBroadcast(this, 110, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // 设置闹钟
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
//        alarmManager.set(@AlarmType int type, long triggerAtMillis, PendingIntent operation);

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
//        alarmManager.setRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);

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
//        alarmManager.setInexactRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);


        // 设置时区（东八区）-需要加权限SET_TIME_ZONE
        alarmManager.setTimeZone("GMT+08:00");

        // 取消闹钟
//        alarmManager.cancel(pendingIntent);
    }

    // 设置闹钟
    private void setAlarm(Calendar calendar) {
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 5), pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        alarmTv.setText(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(calendar.getTime()));
        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        hourEd.setText("");
        minutesEd.setText("");
        secondsEd.setText("");
    }
}
