package cc.ibooker.zkeepalivelib.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cc.ibooker.zkeepalivelib.ZKeepAlive
import cc.ibooker.zkeepalivelib.keep.OnePixelActivity

/**
 * 开屏和闭屏监听
 *
 * @author 邹峰立
 */
class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ZKeepAlive.isKeepalive) {
            intent?.apply {
                when (this.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        // 屏幕关闭 - 启动OnePixelActivity
                        context?.let {
                            val intentPixel = Intent(it, OnePixelActivity::class.java)
                            intentPixel.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            it.startActivity(intentPixel)
                        }
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        // 屏幕开启 - 关闭OnePixelActivity
                        context?.run {
                            // 发送关闭OnePixelActivity广播
                            context.sendBroadcast(Intent(OnePixelActivity.ACTION))
                            // 启动Main Activity
                            val intentMain = Intent(Intent.ACTION_MAIN)
                            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intentMain.addCategory(Intent.CATEGORY_HOME)
                            this.startActivity(intentMain)
                        }
                    }
                }
            }
        }
    }
}