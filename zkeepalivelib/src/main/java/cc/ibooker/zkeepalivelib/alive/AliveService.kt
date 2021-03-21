package cc.ibooker.zkeepalivelib.alive

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import cc.ibooker.zkeepalivelib.IAliveServiceInterface
import cc.ibooker.zkeepalivelib.R
import cc.ibooker.zkeepalivelib.ZKeepAlive
import cc.ibooker.zkeepalivelib.manager.AliveManager
import cc.ibooker.zkeepalivelib.manager.ForeManager
import cc.ibooker.zkeepalivelib.manager.ZNotificationManager

/**
 * 保活服务 - 在进程存在的情况下，降低系统回收几率 - 双进程保活 - 适用于Android 5.0以下
 *
 * @author 邹峰立
 */
class AliveService : Service() {
    private val TAG = "AliveService"
    private var startArgFlags = 0
    private val ID = 211223344
    private val CHANNEL_ID = "2-1122334455"
    private val CHANNEL_NAME = "保活服务"
    private var conn: AliveServiceConnection? = null
    private var isExcute: Boolean = false

    override fun onBind(intent: Intent?): IBinder {
        return AliveBind()
    }

    internal inner class AliveBind : IAliveServiceInterface.Stub() {
        override fun getServiceName(): String {
            return AliveService::class.java.simpleName
        }
    }

    override fun onCreate() {
        super.onCreate()
        init()
        isExcute = false
    }

    private fun init() {
        // 保证内存不足，杀死会重新创建
        startArgFlags =
            if (applicationInfo.targetSdkVersion < Build.VERSION_CODES.ECLAIR) START_STICKY_COMPATIBILITY else START_STICKY
        // 开启前置服务
        startForeground(
            ID, ZNotificationManager.instance.getNotification(
                applicationContext, CHANNEL_ID, CHANNEL_NAME,
                getString(R.string.alive_service), getString(R.string.alive_service_text)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ZKeepAlive.isKeepalive) {
            // 启动自身
            AliveManager.instance.startAliveService(applicationContext)
        } else {
            unBindForeService()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isExcute) {
            // 绑定服务
            bindForeService()
            isExcute = true
        }
        return startArgFlags
    }

    internal inner class AliveServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "bindForeService - Success")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bindForeService()
        }
    }

    private fun bindForeService() {
        if (ZKeepAlive.isKeepalive) {
            // 启动服务
            ForeManager.instance.startForeService(applicationContext)
            // 绑定服务
            if (conn == null)
                conn = AliveServiceConnection()
            ForeManager.instance.bindForeService(applicationContext, conn!!)
        }
    }

    private fun unBindForeService() {
        conn?.run {
            ForeManager.instance.unBindForeService(applicationContext, conn!!)
            conn = null
        }
    }
}