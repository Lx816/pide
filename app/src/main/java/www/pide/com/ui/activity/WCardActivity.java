package www.pide.com.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTabHost;

import www.pide.com.R;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.BottomEnum;
import www.pide.com.bean.UpdateAppBean;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean1;
import www.pide.com.ui.view.DownloadService;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class WCardActivity extends BaseActivity {
    private ImageView share;
    private FragmentTabHost tabhost;
    private TextView title_w;
    BottomEnum[] tabhosts;
    TextView indicator;
    View view;
    private Context context;
    private String access_token = "";
    private RelativeLayout w_title;
    private RelativeLayout r_l;
    private MyHandler wcartHandler=new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;
        public MyHandler(Activity activity) {
            mWeakReference=new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        access_token = SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN);
        if (access_token.equals("")) {
            if (getIntent().getExtras() == null) {
                UIHelper.OpenActivity(context, LoginActivity.class);
                finish();
            } else {
                setContentView(R.layout.center_item);
            }
        } else {
            setContentView(R.layout.w_card_activity);
            initView();
        }
    }

    private void initView() {
        share = (ImageView) findViewById(R.id.share);
        title_w = (TextView) findViewById(R.id.title_w);
        tabhost = (FragmentTabHost) findViewById(R.id.tabhost);
        w_title = (RelativeLayout) findViewById(R.id.w_title);
        r_l = (RelativeLayout) findViewById(R.id.r_l);
        r_l.getBackground().setAlpha(20);
        share.setVisibility(View.VISIBLE);
        title_w.setText("人脉广场");
        tabhosts = BottomEnum.values();
        tabhost.setup(this, getSupportFragmentManager(), R.id.wCordFrameLayout);
        tabhost.getTabWidget().setDividerDrawable(null);
        tabhost.getTabWidget().setBackgroundResource(R.color.white);
        tabhost.getTabWidget().setPadding(0, 0, 0, 8);
        //初始化TAb
        for (int i = 0; i < tabhosts.length; i++) {
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(getString(tabhosts[i].getResName()));
            tabSpec.setIndicator(getIndicatorView(tabhosts[i]));
            tabhost.addTab(tabSpec, tabhosts[i].getClz(), null);
        }
        //去除底部按钮之间的分割线
        if (Build.VERSION.SDK_INT > 10) {
            tabhost.getTabWidget().setShowDividers(0);
        }
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                title_w.setText(tabId);
                switch (tabId) {
                    case "人脉广场":
                        setTranslucentStatus_b(context, R.color.white);
                        w_title.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);
                        break;
                    case "趣味会议":
                        setTranslucentStatus_b(context, R.color.white);
                        share.setVisibility(View.GONE);
                        w_title.setVisibility(View.VISIBLE);
                        break;
                    case "个人中心":
                        setTranslucentStatus_a(context, R.color.themeColor);
                        share.setVisibility(View.GONE);
                        w_title.setVisibility(View.GONE);
                        break;
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.OpenActivity(context, SearchConnectionActivity.class);
            }
        });
        wcartHandler=new MyHandler(this){
            @Override
            public void handleMessage(Message msg) {
                final Activity activity=mWeakReference.get();
                if(activity!=null) {
                    switch (msg.what) {
                        case 0:
                            isUpdateApp();
                            initData();
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                wcartHandler.sendEmptyMessage(0);
            }
        }).start();
    }
    /**
     * 返回设置好的底部按钮
     *
     * @param indicator1
     * @return
     */
    private View getIndicatorView(BottomEnum indicator1) {
        view = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
        indicator = (TextView) view.findViewById(R.id.tab_title);
        indicator.setText(getString(indicator1.getResName()));
        indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        Drawable icon = ContextCompat.getDrawable(this, indicator1.getResIcon());
        indicator.setCompoundDrawablePadding(3);
        indicator.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        indicator.setPadding(0, 8, 0, 5);
        return view;
    }

    @Override
    protected void onDestroy() {
        wcartHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
    private long clickTime=0;
    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
        exit(); 
        return true; 
      } 
      return super.onKeyDown(keyCode, event);
    }
    private void exit() { 
      if ((System.currentTimeMillis() - clickTime) > 2000) { 
        Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show(); 
        clickTime = System.currentTimeMillis(); 
      } else {
        this.finish(); 
        System.exit(0); 
      } 
    }

    private void initData() {
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYDETAIL)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean1 user1 = JsonUtil.fromJson(response, UserBean1.class);
                if (user1.isSuccess()) {
                    User user = user1.getData();
                    SharedPreferencesUtil.write(context, "LtAreaPeople", "USERID", user.getUserId() + "");
                    SharedPreferencesUtil.write(context, "LtAreaPeople", "NAME", user.getName() + "");
                    if (user.getAvatar() != null) {
                        SharedPreferencesUtil.write(context, "LtAreaPeople", "AVATAR", user.getAvatar());
                        SharedPreferencesUtil.write(context, "LtAreaPeople", "COMPANY", user.getCompany() + "");
                        SharedPreferencesUtil.write(context, "LtAreaPeople", "POSITION", user.getPosition() + "");
                    }
                } else {
                    UIHelper.ToastMessageCenter(context, user1.getMsg(), 200);
                }
            }
        });
    }
    private void isUpdateApp() {
        OkHttpUtils.get().url(URLs.IMGEURL +"file/isUpdateApp")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .addParams("phoneType","0")
                .addParams("vesion","1.0")
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UpdateAppBean updateAppBean = JsonUtil.fromJson(response, UpdateAppBean.class);
                if (updateAppBean.getSuccess()) {
                    UpdateAppBean.UpdateApp updateApp = updateAppBean.getData();
                    if(updateApp.getType()==0){//非强制更新
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("url", updateApp.getDownUrl());
                        startService(intent);
                    }else {//强制更新
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("url", updateApp.getDownUrl());
                        startService(intent);
                    }
                }
            }
        });
    }
}
