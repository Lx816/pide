package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import www.pide.com.R;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.CodeBean;
import www.pide.com.bean.Sticky;
import www.pide.com.ui.view.CenterWindow;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import www.pide.com.utils.Util;

public class BindPhoneActivity extends BaseActivity {
    private RelativeLayout left_back;
    private TextView title_w;
    private Context context;
    private TextView send_code;
    private EditText phone_l,hidden_t,code_l;
    private String s_phone_l,s_code_l;
    private TimeCount time;
    private Button save;
    private Intent intent;
    private String phone_s;
    private CenterWindow bottomWindow;
    private String sign="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context=this;
        setTranslucentStatus_b(context,R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_phone);
        intent=getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        left_back = (RelativeLayout) findViewById(R.id.left_back);
        title_w = (TextView) findViewById(R.id.title_w);
        title_w.setText("修改绑定手机");
        left_back.setVisibility(View.VISIBLE);
        send_code= (TextView) findViewById(R.id.send_code);
        phone_l= (EditText) findViewById(R.id.phone_l);
        hidden_t= (EditText) findViewById(R.id.hidden_t);
        code_l= (EditText) findViewById(R.id.code_l);
        save= (Button) findViewById(R.id.save);
        time = new TimeCount(60000,1000);
        phone_s=intent.getStringExtra("phone");
    }

    private void initOnclick() {
        View.OnClickListener listener;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.left_back:
                        finish();
                        break;
                    case R.id.send_code:
                        s_phone_l = phone_l.getText().toString().trim();
                        if (isMobileNO(s_phone_l) != false && s_phone_l.length() == 11) {
                            if(phone_s!=s_phone_l){
                                send_code_l(s_phone_l);
                            }else {
                                UIHelper.ToastMessageCenter(context, "号码未变化", 200);
                            }
                        } else {
                            if (s_phone_l.length() == 0) {
                                UIHelper.ToastMessageCenter(context, "请输入你的手机号码", 200);
                            } else {
                                UIHelper.ToastMessageCenter(context, "你输入的是一个无效的手机号码", 200);
                            }
                        }
                        break;
                    case R.id.save:
                        save();
                        break;
                }
            }
        };
        left_back.setOnClickListener(listener);
        send_code.setOnClickListener(listener);
        save.setOnClickListener(listener);
    }

    private void send_code_l(final String s_phone) {
        time.start();
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.SENDUPDATECODE)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .addParams("currentTime", mcurrentTime)
                .addParams("phone", s_phone)
                .addParams("sign", sign)
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                time.onFinish();
                time.cancel();
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UIHelper.ToastMessageCenter(context, "验证码已发送，请注意查收", 200);
                CodeBean coseBean = JsonUtil.fromJson(response, CodeBean.class);
                if (!coseBean.getSuccess()) {
                    time.onFinish();
                    time.cancel();
                    UIHelper.ToastMessageCenter(context, coseBean.getMsg(), 200);
                    if(coseBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(BindPhoneActivity.this,LoginActivity.class);
                    }
                }
            }
        });
    }
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            send_code.setClickable(false);
            send_code.setText("验证码："+millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            send_code.setText("获取验证码");
            send_code.setClickable(true);

        }
    }

    private void save() {
        Util.showLoadingDialog(this,"加载中");
        s_phone_l = phone_l.getText().toString().trim();
        s_code_l = code_l.getText().toString().trim();
        if (s_phone_l.length() > 0 && s_code_l.length() > 0 && isMobileNO(s_phone_l) != false && s_phone_l.length() == 11) {
            OkHttpUtils.post().url(URLs.IMGEURL + URLs.UPDATEPHONE)
                    .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                    .addParams("phone", s_phone_l)
                    .addParams("smsCode", s_code_l)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    Util.closeLoadingDialog(context);
                    CodeBean coseBean = JsonUtil.fromJson(response, CodeBean.class);
                    if (coseBean.getSuccess()) {
                        EventBus.getDefault().post(new Sticky("2"));
                        finish();
                    }else {
                        UIHelper.ToastMessageCenter(context, coseBean.getMsg(), 200);
                        if(coseBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(BindPhoneActivity.this,LoginActivity.class);
                        }
                    }
                }
            });
        } else if (s_phone_l.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请输入你的手机号码", 200);
        } else if (s_code_l.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "验证码不能为空", 200);
        } else {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "你输入的是一个无效的手机号码", 200);
        }
    }
}
