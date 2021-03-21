package cc.ibooker.zkeepalivelib.alive

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import cc.ibooker.zkeepalivelib.ZKeepAlive
import cc.ibooker.zkeepalivelib.manager.AliveManager
import cc.ibooker.zkeepalivelib.manager.ForeManager
import java.lang.ref.WeakReference


/**
 * JobSchedulerService定时任务，启动双进程服务，适用于Android 8.0以下
 *
 * @author 邹峰立
 */
class JobSchedulerService : JobService() {
    private val TAG = "JobSchedulerService"
    private val JS_WHAT = 100
    private val zHandler = ZHandler(this)

    internal class ZHandler(jobSchedulerService: JobSchedulerService) :
        Handler(Looper.getMainLooper()!!) {
        private var mWeakRef: WeakReference<JobSchedulerService> =
            WeakReference(jobSchedulerService)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mWeakRef.get()?.run {
                if (msg.what == this.JS_WHAT) {
                    if (ZKeepAlive.isKeepalive) {
                        ForeManager.instance.startForeService(this)
                        AliveManager.instance.startAliveService(this)
                        Log.d(TAG, "handleMessage")
                    }
                    // 通知系统当前任务已完成
                    msg.obj?.run {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            jobFinished(this as JobParameters, false)
                        } else {
                            onStartJob(this as JobParameters)
                        }
                    }
                }
            }
        }
    }

    // 返回true，表示该工作耗时，同时工作处理完成后需要调用onStopJob销毁（jobFinished）
    // 返回false，任务运行不需要很长时间，到return时已完成任务处理
    override fun onStartJob(params: JobParameters?): Boolean {
        val message = Message.obtain()
        message.obj = params
        message.what = JS_WHAT
        zHandler.sendMessage(message)
        return true
    }

    // 有且仅有onStartJob返回值为true时，才会调用onStopJob来销毁job
    // 返回false来销毁这个工作
    override fun onStopJob(params: JobParameters?): Boolean {
        zHandler.removeMessages(JS_WHAT)
        return false
    }

}