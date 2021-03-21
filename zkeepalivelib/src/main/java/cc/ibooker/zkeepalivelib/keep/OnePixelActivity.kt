package cc.ibooker.zkeepalivelib.keep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.Gravity
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

/**
 * 一像素Activity，监听开屏锁屏广播
 * 1. 在手机锁屏状态下启动该Activity。
 * 2. 在手机开屏状态下关闭该Activity。
 *
 * @author 邹峰立
 */
class OnePixelActivity : AppCompatActivity() {
    private var finishOnePixelReceiver: BroadcastReceiver? = null

    companion object {
        const val ACTION = "cc.ibooker.zkeepalivelib.finishOnePixelActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        // 放在左上角
        window.setGravity(Gravity.START or Gravity.TOP)
        val attributes = window.attributes
        // 宽高设计为1个像素
        attributes.width = 1
        attributes.height = 1
        // 起始坐标
        attributes.x = 0
        attributes.y = 0
        window.attributes = attributes

        // 注册广播
        if (finishOnePixelReceiver == null)
            finishOnePixelReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    finish()
                }
            }
        registerReceiver(finishOnePixelReceiver, IntentFilter(ACTION))
    }

    override fun onResume() {
        super.onResume()
        checkScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishOnePixelReceiver)
    }

    /**
     * 检查屏幕状态  isScreenOn为true  屏幕“亮”结束该Activity
     */
    private fun checkScreen() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                pm.isInteractive
            } else {
                pm.isScreenOn
            }
        if (isScreenOn) {
            finish()
        }
    }
}