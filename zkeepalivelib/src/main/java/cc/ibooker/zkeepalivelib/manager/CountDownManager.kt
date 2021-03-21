package cc.ibooker.zkeepalivelib.manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import cc.ibooker.zkeepalivelib.R
import cc.ibooker.zkeepalivelib.ZKeepAlive
import cc.ibooker.zkeepalivelib.appwidget.CountDownWidget
import java.util.*

/**
 * 倒计时AppWidget管理类
 *
 * @author 邹峰立
 */
class CountDownManager private constructor() {
    private val TAG = "CountDownManager"
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var calendar: Calendar? = null
    private var remoteViews: RemoteViews? = null
    private var appWidgetManager: AppWidgetManager? = null
    private var provider: ComponentName? = null

    companion object {
        val instance: CountDownManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { CountDownManager() }
    }

    init {
        calendar = Calendar.getInstance()
        calendar!!.timeZone = TimeZone.getTimeZone("GMT+8")
    }

    // 开启定时器 - 一分钟更新一次Widget
    fun startTimer(context: Context) {
        Log.d(TAG, "startTimer")
        if (ZKeepAlive.isKeepalive) {
            if (timerTask == null) {
                val applicationContext = context.applicationContext
                timerTask = object : TimerTask() {
                    override fun run() {
                        val hour = calendar!!.get(Calendar.HOUR)
                        val minute = calendar!!.get(Calendar.MINUTE)
                        var formatHour: String = "" + hour
                        var formatMinute: String = "" + minute
                        if (hour < 10)
                            formatHour = "0$hour"
                        if (minute < 10)
                            formatMinute = "0$minute"
                        val widgetText = """
                        ${formatHour}时
                        ${formatMinute}分
                        """.trimIndent()
                        updateWidget(applicationContext, widgetText)
                    }
                }
            }
            if (timer == null) {
                timer = Timer()
                timer!!.schedule(timerTask, 0, 60000)
            }
        } else {
            Log.d(TAG, "startTimer = false")
            timerTask?.let {
                it.cancel()
                timerTask = null
            }
            timer?.run {
                this.purge()
                this.cancel()
            }
        }
    }

    /**
     * 更新Widget界面
     *
     * @param widgetText Widget显示内容
     */
    private fun updateWidget(context: Context, widgetText: String) {
        Log.d(TAG, "updateWidget = $widgetText")
        if (appWidgetManager == null)
            appWidgetManager = AppWidgetManager.getInstance(context)
        if (provider == null)
            provider = ComponentName(context, CountDownWidget::class.java)
        if (remoteViews == null)
            remoteViews = RemoteViews(context.packageName, R.layout.zka_countdown_widget)
        remoteViews!!.setTextViewText(R.id.appwidget_text, widgetText)
        appWidgetManager!!.updateAppWidget(provider, remoteViews)
    }
}