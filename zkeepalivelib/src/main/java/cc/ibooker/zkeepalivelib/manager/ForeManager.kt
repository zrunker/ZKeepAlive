package cc.ibooker.zkeepalivelib.manager

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import cc.ibooker.zkeepalivelib.keep.ForeService

/**
 * 前置服务管理类
 *
 * @author 邹峰立
 */
class ForeManager private constructor() {
    companion object {
        val instance: ForeManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ForeManager() }
    }

    /**
     * 启动ForeService
     */
    fun startForeService(context: Context) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, ForeService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_fore_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            applicationContext.startForegroundService(intent)
        else
            applicationContext.startService(intent)
    }

    /**
     * 关闭ForeService
     */
    fun stopForeService(context: Context) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, ForeService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_fore_service"
        applicationContext.stopService(intent)
    }

    /**
     * 绑定ForeService
     */
    fun bindForeService(context: Context, conn: ServiceConnection) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, ForeService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_fore_service"
        applicationContext.bindService(intent, conn, Context.BIND_IMPORTANT)
    }

    /**
     * 解绑ForeService
     */
    fun unBindForeService(context: Context, conn: ServiceConnection) {
        val applicationContext = context.applicationContext
        applicationContext.unbindService(conn)
    }
}