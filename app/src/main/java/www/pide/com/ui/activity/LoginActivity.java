package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import www.pide.com.R;
import www.pide.com.base.BaseActivity;
import www.pide.com.base.MyApplication;
import www.pide.com.ui.fragment.LoginFragment;
import www.pide.com.ui.fragment.RegisterFragment;
import www.pide.com.utils.DoubleClickExitHelper;
import www.pide.com.utils.URLs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/3/13.
 * 登录注册页
 */
public class LoginActivity extends BaseActivity {
    private Context context;
    private RelativeLayout login_r;
    private RelativeLayout rigister_r;
    private ImageView look;
    private DoubleClickExitHelper mDoubleClickExit;
    private RelativeLayout login_l;
    private RelativeLayout register_l;
    private ViewPager wCordFrameLayout;
    private List<Fragment> fragmentList;
    private ImageView wx_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        initView();
        initOnclick();
    }

    private void initView() {
        login_r = (RelativeLayout) findViewById(R.id.login_r);
        rigister_r = (RelativeLayout) findViewById(R.id.rigister_r);
        look = (ImageView) findViewById(R.id.look);
        mDoubleClickExit = new DoubleClickExitHelper(this);
        login_l = (RelativeLayout) findViewById(R.id.login_l);
        register_l = (RelativeLayout) findViewById(R.id.register_l);
        wx_login= (ImageView) findViewById(R.id.wx_login);
        wCordFrameLayout = (ViewPager) findViewById(R.id.wCordFrameLayout);
        LoginFragment f1 = new LoginFragment();
        RegisterFragment f2 = new RegisterFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(f1);
        fragmentList.add(f2);
        wCordFrameLayout.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        wCordFrameLayout.setCurrentItem(0);
        wCordFrameLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        login_l.setVisibility(View.VISIBLE);
                        register_l.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        login_l.setVisibility(View.INVISIBLE);
                        register_l.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.look:
                        Intent intent=new Intent(LoginActivity.this,WCardActivity.class);
                        intent.putExtra("name","look");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.login_r:
                        login_l.setVisibility(View.VISIBLE);
                        register_l.setVisibility(View.INVISIBLE);
                        wCordFrameLayout.setCurrentItem(0);
                        break;
                    case R.id.rigister_r:
                        login_l.setVisibility(View.INVISIBLE);
                        register_l.setVisibility(View.VISIBLE);
                        wCordFrameLayout.setCurrentItem(1);
                        break;
                    case R.id.wx_login:
                        //登录微信
                        //先判断是否安装微信APP,按照微信的说法，目前移动应用上微信登录只提供原生的登录方式，需要用户安装微信客户端才能配合使用。
//                        if (!isWxAppInstalledAndSupported(context)) {
//                            UIHelper.ToastMessageCenter(context,"您还未安装微信客户端",200);
//                            return;
//                        }
//                        wxLogin();
                        break;
                }
            }
        };
        look.setOnClickListener(listener);
        login_r.setOnClickListener(listener);
        rigister_r.setOnClickListener(listener);
        wx_login.setOnClickListener(listener);
    }
    //微信登录
    public void wxLogin() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        //像微信发送请求
        MyApplication.mWxApi.sendReq(req);
    }
    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }
    /**
     * 判断手机是否安装微信
     *
     * @param context 上下文
     * @return
     */
    public static boolean isWxAppInstalledAndSupported(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, URLs.WEIXIN_APP_ID);

        boolean bIsWXAppInstalledAndSupported = wxApi .isWXAppInstalled();
        if (!bIsWXAppInstalledAndSupported) {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equals("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }

        return true;
    }
}