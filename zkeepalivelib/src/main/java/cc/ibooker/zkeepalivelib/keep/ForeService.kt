package cc.ibooker.zkeepalivelib.keep

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
 * 前置服务 - 在进程存在的情况下，降低系统回收几率 - 双进程保活
 *
 * A：android:priority="1000"最高权限；
 * B：onStartCommand返回值设置，getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY;
 * C：前置服务，startForeground(int id, Notification notification);
 * D：onDestroy()方法中重启服务。
 *
 * @author 邹峰立
 */
class ForeService : Service() {
    private val TAG = "ForeService"
    private var startArgFlags = 0
    private val ID = 111223344
    private val CHANNEL_ID = "1-1122334455"
    private val CHANNEL_NAME = "前置服务"
    private var conn: ForeServiceConnection? = null
    private var isExcute: Boolean = false

    override fun onBind(intent: Intent?): IBinder {
        return ForeBinder()
    }

    inner class ForeBinder : IAliveServiceInterface.Stub() {
        override fun getServiceName(): String {
            return ForeService::class.java.simpleName
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
                getString(R.string.fore_service), getString(R.string.fore_service_text)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ZKeepAlive.isKeepalive) {
            // 启动自身
            ForeManager.instance.startForeService(applicationContext)
        } else {
            unBindAliveService()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isExcute) {
            // 绑定服务
            bindAliveService()
            isExcute = true
        }
        return startArgFlags
    }

    inner class ForeServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "bindAliveService - Success")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bindAliveService()
        }
    }

    private fun bindAliveService() {
        if (ZKeepAlive.isKeepalive) {
            // 启动服务
            AliveManager.instance.startAliveService(applicationContext)
            // 绑定服务
            if (conn == null)
                conn = ForeServiceConnection()
            AliveManager.instance.bindAliveService(applicationContext, conn!!)
        }
    }

    private fun unBindAliveService() {
        // 绑定服务
        conn?.run {
            AliveManager.instance.unBindAliveService(applicationContext, this)
            conn = null
        }
    }

}