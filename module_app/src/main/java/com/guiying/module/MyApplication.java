package com.guiying.module;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.guiying.module.common.base.BaseApplication;
import com.guiying.module.common.utils.Utils;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.EmailIntentSender;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

/**
 * <p>这里仅需做一些初始化的工作</p>
 *
 * @author 张华洋 2017/2/15 20:14
 * @version V1.2.0
 * @name MyApplication
 */
@ReportsCrashes(
        mailTo = "guiying705@Gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = {
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.BRAND,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT,
                ReportField.USER_COMMENT},
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogTitle = R.string.crash_dialog_title)
public class MyApplication extends BaseApplication {

    //------------------------------------------------------------------------------


    //组件本质上还是studio项目中的一个module。集成模式下是com.android.library。单独运行模式是com.android.application。
    //组件化只是让组件之间不能相互引用实现了解耦。
    //1.组件之间通信？
    //2.组件之间页面跳转？


    //------------------------------------------------------------------------------

    //优化1：全局配置组件是否启用集成或组件模式  不用每个组件都写
    //优化2：组件中自定义application并在菜单文件中配置好 单个组件运行可以出做一些初始化

    //------------------------------------------------------------------------------

    //问题1：集成模式下 各个组件不能使用自己的application 什么情况下使用？集成模式下组件代码中
    // getApplicationContext获取的不应该是统一的壳工程的application呢

    //如何使用全局的application 还是靠共同依赖作为中间桥梁。如果不用共同依赖的全局application，单独运行模式下，
    //获取的是自己的application。集成模式下即使不用共同依赖中的全局application，getapplication获取到的也是壳工程的application==共同依赖的application。


    //------------------------------------------------------------------------------


    //问题2：组件化工程模型的构成？
    //在组件化工程模型中主要有：app壳工程、业务组件和功能组件3种类型，
    // 而业务组件中的Main组件和功能组件中的Common组件比较特殊


    //------------------------------------------------------------------------------


    //疑问：功能组件 例如做一个sdk 需要不需要单独搞个功能组件  还是直接在common组件中依赖或写代码？
    //个人理解：功能一般是没有界面的那种做成第三库比较好。没必要搞个组件项目。

    //------------------------------------------------------------------------------


    //问题3：Fragment或View如何支持组件化  就是如何拿到组件中的view或Fragment

    // 跟上一个demo中的一样 都是通过底层依赖实现
    //不同：DexFile查找包下面的所有类   isAssignableFrom找出所有实现类  然后反射注册


    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------


    @Override
    public void onCreate() {
        super.onCreate();
        if (Utils.isAppDebug()) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
        //崩溃日志记录初始化
        ACRA.init(this);
        ACRA.getErrorReporter().removeAllReportSenders();
        ACRA.getErrorReporter().setReportSender(new CrashReportSender());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // dex突破65535的限制
        MultiDex.install(this);
    }


    /**
     * 发送崩溃日志
     */
    private class CrashReportSender implements ReportSender {
        CrashReportSender() {
            ACRA.getErrorReporter().putCustomData("PLATFORM", "ANDROID");
            ACRA.getErrorReporter().putCustomData("BUILD_ID", android.os.Build.ID);
            ACRA.getErrorReporter().putCustomData("DEVICE_NAME", android.os.Build.PRODUCT);
        }

        @Override
        public void send(Context context, CrashReportData crashReportData) throws ReportSenderException {
            EmailIntentSender emailSender = new EmailIntentSender(getApplicationContext());
            emailSender.send(context, crashReportData);
        }
    }
}
