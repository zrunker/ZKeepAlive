package cc.ibooker.zkeepalivelib.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import cc.ibooker.zkeepalivelib.R
import cc.ibooker.zkeepalivelib.manager.AliveManager
import cc.ibooker.zkeepalivelib.manager.CountDownManager
import cc.ibooker.zkeepalivelib.manager.ForeManager


/**
 * 添加执行过程：onEnabled -> onReceive -> onUpdate -> onReceive -> onAppWidgetOptionsChanged -> onReceive
 * Implementation of App Widget functionality.
 *
 * @author 邹峰立
 */
class CountDownWidget : AppWidgetProvider() {
    private val TAG = "CountDownWidget"

    // 更新操作
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "onUpdate")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        // 启动服务
        ForeManager.instance.startForeService(context)
        AliveManager.instance.startAliveService(context)
    }

    // Widget第一个被添加到桌面执行方法
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled")
        // 启动倒计时小组件
        CountDownManager.instance.startTimer(context)
    }

    // Widget最后一个被移除执行方法
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled")
    }

    // 从屏幕移除
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(TAG, "onDeleted")
    }

    // 接收广播
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive")
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val widgetText: CharSequence = context.getString(R.string.appwidget_text)
        // Construct the RemoteViews object
        val views = RemoteViews(
            context.applicationContext.packageName,
            R.layout.zka_countdown_widget
        )
        views.setTextViewText(R.id.appwidget_text, widgetText)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}