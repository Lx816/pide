package www.pide.com.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.hyphenate.util.EMLog;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import www.pide.com.hxsdk.ConferenceInfo;
import www.pide.com.hxsdk.DemoHelper;
import www.pide.com.utils.URLs;
import android.os.Process;

/**
 * Created by Administrator on 2020/4/2.
 */
public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{
    public static IWXAPI mWxApi;
    public static Context applicationContext;
    private static MyApplication instance;
    static public ConferenceInfo conferenceInstance;
    static public String baseurl = "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/RtcDemo/headImage/";

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;

        //init demo helper
        DemoHelper.getInstance().init(applicationContext);

        conferenceInstance = ConferenceInfo.getInstance();
        addErrorListener();
        registerToWX();
    }

    private void registerToWX() {
        //第二个参数是指你应用在微信开放平台上的AppID
        mWxApi = WXAPIFactory.createWXAPI(this, URLs.WEIXIN_APP_ID, false);
        // 将该app注册到微信
        mWxApi.registerApp(URLs.WEIXIN_APP_ID);
    }
    private void addErrorListener() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        EMLog.e("uncaughtException : ", e.getMessage());
        System.exit(1);
        Process.killProcess(Process.myPid());
    }
}
