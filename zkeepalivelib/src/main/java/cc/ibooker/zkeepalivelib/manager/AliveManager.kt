package cc.ibooker.zkeepalivelib.manager

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import cc.ibooker.zkeepalivelib.alive.AliveService

/**
 * 保活服务管理类
 *
 * @author 邹峰立
 */
class AliveManager private constructor() {
    companion object {
        val instance: AliveManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AliveManager() }
    }

    /**
     * 启动AliveService
     */
    fun startAliveService(context: Context) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, AliveService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_alive_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            applicationContext.startForegroundService(intent)
        else
            applicationContext.startService(intent)
    }

    /**
     * 停止AliveService
     */
    fun stopAliveService(context: Context) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, AliveService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_alive_service"
        applicationContext.stopService(intent)
    }

    /**
     * 绑定AliveService
     */
    fun bindAliveService(context: Context, conn: ServiceConnection) {
        val applicationContext = context.applicationContext
        val intent = Intent(applicationContext, AliveService::class.java)
        intent.action = "cc.ibooker.zkeepalivelib.keep_alive_service"
        applicationContext.bindService(intent, conn, Context.BIND_IMPORTANT)
    }

    /**
     * 解绑AliveService
     */
    fun unBindAliveService(context: Context, conn: ServiceConnection) {
        val applicationContext = context.applicationContext
        applicationContext.unbindService(conn)
    }
}