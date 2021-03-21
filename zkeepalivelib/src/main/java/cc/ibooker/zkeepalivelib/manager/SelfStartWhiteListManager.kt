package cc.ibooker.zkeepalivelib.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.util.*


/**
 * 自启动白名单管理类
 *
 * @author 邹峰立
 */
class SelfStartWhiteListManager private constructor() {
    companion object {
        val instance: SelfStartWhiteListManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { SelfStartWhiteListManager() }
    }

    // 进入自启动白名单设置
    fun enterWhiteListSetting(context: Context) {
        try {
            context.startActivity(getSettingIntent())
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    // 根据不同厂商进入自启动设置
    private fun getSettingIntent(): Intent {
        var componentName: ComponentName? = null
        val brand = Build.BRAND
        when (brand.toLowerCase(Locale.ROOT)) {
            "samsung" -> componentName = ComponentName(
                "com.samsung.android.sm",
                "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity"
            )
            "huawei" -> componentName = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
            )
            "xiaomi" -> componentName = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
            "vivo" -> componentName = ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
            )
            "oppo" -> componentName = ComponentName(
                "com.coloros.oppoguardelf",
                "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
            )
            "360" -> componentName = ComponentName(
                "com.yulong.android.coolsafe",
                "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity"
            )
            "meizu" -> componentName = ComponentName(
                "com.meizu.safe",
                "com.meizu.safe.permission.SmartBGActivity"
            )
            "oneplus" -> componentName = ComponentName(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )
        }
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (componentName != null) {
            intent.component = componentName
        } else {
            intent.action = Settings.ACTION_SETTINGS
        }
        return intent
    }
}