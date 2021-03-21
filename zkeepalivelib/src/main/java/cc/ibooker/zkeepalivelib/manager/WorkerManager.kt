package cc.ibooker.zkeepalivelib.manager

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import cc.ibooker.zkeepalivelib.alive.AliveWorker
import java.util.concurrent.TimeUnit


/**
 * WorkerManager管理类
 *
 * @author 邹峰立
 */
class WorkerManager private constructor() {
    private val TAG_ALIVE_WORK = "cc.ibooker.zkeepalivelib.aliveworker"

    companion object {
        val instance: WorkerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { WorkerManager() }
    }

    // 启动AliveWorker
    fun startAliveWorker() {
//        WorkManager.getInstance().cancelAllWorkByTag(TAG_KEEP_WORK)
//        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(AliveWorker::class.java)
//            .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS)
//            .addTag(TAG_KEEP_WORK)
//            .build()
//        WorkManager.getInstance().enqueue(oneTimeWorkRequest)

        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
        // 周期任务不能小于15分钟
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(AliveWorker::class.java, 15, TimeUnit.MINUTES)
                .addTag(TAG_ALIVE_WORK)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance().enqueue(periodicWorkRequest)
    }

    // 停止AliveWorker
    fun stopAliveWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(TAG_ALIVE_WORK)
    }
}