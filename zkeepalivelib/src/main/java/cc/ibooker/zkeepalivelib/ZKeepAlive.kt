package cc.ibooker.zkeepalivelib

import android.content.Context
import cc.ibooker.zkeepalivelib.manager.*

/**
 * 进程保护管理类
 *
 * @author 邹峰立
 */
class ZKeepAlive private constructor() {

    companion object {
        var isKeepalive: Boolean = true
        val instance: ZKeepAlive by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ZKeepAlive()
        }
    }

    fun register(context: Context) {
        isKeepalive = true
        ForeManager.instance.startForeService(context)
        AliveManager.instance.startAliveService(context)
        JobServiceManager.instance.startJobSchedulerService(context)
        DozeManager.instance.isIgnoreBatteryOption(context)
        SelfStartWhiteListManager.instance.enterWhiteListSetting(context)
        WorkerManager.instance.startAliveWorker()
    }

    fun unRegister(context: Context) {
        isKeepalive = false
        ForeManager.instance.stopForeService(context)
        AliveManager.instance.stopAliveService(context)
        JobServiceManager.instance.cancelJobScheduler(context)
        WorkerManager.instance.stopAliveWorker()
    }

}