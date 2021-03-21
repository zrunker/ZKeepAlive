package cc.ibooker.zkeepalivelib.manager

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import cc.ibooker.zkeepalivelib.ZKeepAlive
import cc.ibooker.zkeepalivelib.alive.JobSchedulerService

/**
 * JobService管理类
 *
 * @author 邹峰立
 */
class JobServiceManager private constructor() {
    private val JOB_ID = 122

    companion object {
        val instance: JobServiceManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JobServiceManager() }
    }

    /**
     * 开启JobSchedulerService，1分钟执行一次
     */
    fun startJobSchedulerService(context: Context) {
        if (ZKeepAlive.isKeepalive) {
            val applicationContext = context.applicationContext
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val jobScheduler =
                    applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
                val builder = JobInfo.Builder(
                    JOB_ID,
                    ComponentName(applicationContext.packageName, JobSchedulerService::class.java.name)
                )

                // Android 7.0+ 要求时间间隔大于15分钟
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    builder.setPeriodic(900001)
                else
                    builder.setPeriodic(60001)
                builder.setPersisted(false)// 设备重启以后是否重新执行任务
                builder.setRequiresCharging(false)// 设置是否在只有插入充电器的时候执行
                builder.setRequiresDeviceIdle(false)// 设置手机系统处于空闲状态下执行
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

                if (jobScheduler != null) {
                    val result = jobScheduler.schedule(builder.build())
                    if (result <= 0) {
                        // If something goes wrong
                        jobScheduler.cancel(JOB_ID)
                    }
                }
            }
        }
    }

    /**
     * 取消JobScheduler
     */
    fun cancelJobScheduler(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val applicationContext = context.applicationContext
            val jobScheduler =
                applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.cancel(JOB_ID)
        }
    }
}