package cc.ibooker.zkeepalivelib.alive

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import cc.ibooker.zkeepalivelib.manager.AliveManager
import cc.ibooker.zkeepalivelib.manager.ForeManager


/**
 * 应用饿保活 - WorkerManager - 应用没启动也能保证任务能被执行
 *
 * @author 邹峰立
 */
class AliveWorker(private var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("AliveWorker=", "doWork")
        ForeManager.instance.startForeService(context)
        AliveManager.instance.startAliveService(context)
        return Result.success()
    }
}