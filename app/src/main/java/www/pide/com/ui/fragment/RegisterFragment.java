package www.pide.com.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.MediaType;
import www.pide.com.bean.CodeBean;
import www.pide.com.bean.RegisterBean;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.utils.DesEcbUtil;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.activity.CompleteInformationActivity;
import www.pide.com.ui.activity.ServiceAgreementActivity;
import www.pide.com.ui.view.CustomProgressDialog;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/3/27.
 */
public class RegisterFragment extends BasePadFragment {
    View mRootView;
    private EditText phone_r;
    private EditText code_r;
    private EditText t_code_r;
    private Button r_b;
    private EditText hidden_t;
    private String s_phone_r, s_code_r, s_t_code_r;
    private Button send_code_r;
    private Context context;
    private TimeCount time;
    private String sign = "";
    private RegisterBean registerBean=new RegisterBean();
    private String username="",password="";
    private CustomProgressDialog dialog;
    private TextView fwxy;
    private CheckBox wx_login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.register_fragment, container, false);
        context = getActivity();
        initView();
        setDrawableLeft();
        initOnclick();
        return mRootView;
    }

    private void initView() {
        r_b = (Button) mRootView.findViewById(R.id.r_b);
        phone_r = (EditText) mRootView.findViewById(R.id.phone_r);
        code_r = (EditText) mRootView.findViewById(R.id.code_r);
        t_code_r = (EditText) mRootView.findViewById(R.id.t_code_r);
        hidden_t = (EditText) mRootView.findViewById(R.id.hidden_t);
        send_code_r = (Button) mRootView.findViewById(R.id.send_code_r);
        fwxy=mRootView.findViewById(R.id.fwxy);
        wx_login=mRootView.findViewById(R.id.wx_login);
        wx_login.setBackground(getResources().getDrawable(R.mipmap.img_selected));
        time = new TimeCount(60000, 1000);
    }

    private void setDrawableLeft() {
        Drawable phone_r_drawable = context.getResources().getDrawable(R.mipmap.user);
        phone_r_drawable.setBounds(0, 0, 40, 40);
        phone_r.setCompoundDrawables(phone_r_drawable, null, null, null);

        Drawable code_r_drawable = context.getResources().getDrawable(R.mipmap.code);
        code_r_drawable.setBounds(0, 0, 40, 40);
        code_r.setCompoundDrawables(code_r_drawable, null, null, null);

        Drawable t_code_r_drawable = context.getResources().getDrawable(R.mipmap.t_code);
        t_code_r_drawable.setBounds(0, 0, 40, 40);
        t_code_r.setCompoundDrawables(t_code_r_drawable, null, null, null);
    }

    private void initOnclick() {
        View.OnClickListener listener;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.r_b:
                        if(wx_login.isChecked()){
                            r_b();
                        }else {
                            UIHelper.ToastMessageCenter(context, "请勾选底部匹得服务协议", 200);
                        }
                        break;
                    case R.id.send_code_r:
                        s_phone_r = phone_r.getText().toString().trim();
                        if (isMobileNO(s_phone_r) != false && s_phone_r.length() == 11) {
                            send_code_l(s_phone_r);
                        } else {
                            if (s_phone_r.length() == 0) {
                                UIHelper.ToastMessageCenter(context, "请输入你的手机号码", 200);
                            } else {
                                UIHelper.ToastMessageCenter(context, "你输入的是一个无效的手机号码", 200);
                            }

                        }
                        break;
                    case R.id.fwxy:
                        UIHelper.OpenActivity(context, ServiceAgreementActivity.class);
                        break;
                    case R.id.wx_login:
                        if(wx_login.isChecked()){
                            wx_login.setBackground(getResources().getDrawable(R.mipmap.img_selected));
                        }else {
                            wx_login.setBackground(getResources().getDrawable(R.mipmap.img_selected_no));
                        }
                        break;
                }
            }
        };
        r_b.setOnClickListener(listener);
        send_code_r.setOnClickListener(listener);
        fwxy.setOnClickListener(listener);
        wx_login.setOnClickListener(listener);
    }

    private void send_code_l(final String s_phone) {
        time.start();
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.SENDCODE)
                .addParams("currentTime", mcurrentTime)
                .addParams("phone", s_phone)
                .addParams("sign", sign)
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                time.onFinish();
                time.cancel();
                UIHelper.ToastMessageCenter(context, "验证码发送失败，请重新获取验证码", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UIHelper.ToastMessageCenter(context, "验证码已发送，请注意查收", 200);
                CodeBean coseBean = JsonUtil.fromJson(response, CodeBean.class);
                if (!coseBean.getSuccess()) {
                    time.onFinish();
                    time.cancel();
                    UIHelper.ToastMessageCenter(context, coseBean.getMsg(), 200);
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

    private void r_b() {
        try {
            Util.showLoadingDialog(context,"加载中");
        }catch (Exception e){}
        String mcurrentTime=QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        s_phone_r = phone_r.getText().toString().trim();
        s_code_r = code_r.getText().toString().trim();
        s_t_code_r = t_code_r.getText().toString().trim();
        String mdata= null;
        try {
            mdata = DesEcbUtil.encode(URLs.KEY,s_phone_r+"|"+s_code_r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (s_phone_r.length() > 0 && s_code_r.length() > 0 && isMobileNO(s_phone_r) != false && s_phone_r.length() == 11) {
            Map<String,String> map=new HashMap<>();
            map.put("currentTime",mcurrentTime);
            map.put("sign", sign);
            map.put("data", mdata);
            map.put("lType", "0");
            map.put("recommendCode", s_t_code_r);
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.REGISTER)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(map))
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    registerBean = JsonUtil.fromJson(response, RegisterBean.class);
                    if(registerBean.getSuccess()){
                        PreferenceManager.getInstance().setCurrentUserName(registerBean.getData().getHxUserName());
                        PreferenceManager.getInstance().setCurrentuserPassword(registerBean.getData().getHxPassword());
                        loginhx();
                    }else {
                        Util.closeLoadingDialog(context);
                        UIHelper.ToastMessageCenter(context, registerBean.getMsg(), 200);
                    }
                }
            });
        } else if (s_phone_r.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请输入你的手机号码", 200);
        } else if (s_code_r.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "验证码不能为空", 200);
        } else {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "你输入的是一个无效的手机号码", 200);
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            send_code_r.setClickable(false);
            send_code_r.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            send_code_r.setText("发送验证码");
            send_code_r.setClickable(true);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
    /**
     登录IM账号
     */
    private void loginhx() {
        username=PreferenceManager.getInstance().getCurrentUsername();
        password=PreferenceManager.getInstance().getCurrentUserPassWord();
        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Util.closeLoadingDialog(context);
                EMClient.getInstance().chatManager().loadAllConversations();
                SharedPreferencesUtil.write(getActivity(), "LtAreaPeople", URLs.TOKEN, registerBean.getData().getJwtToken());
                UIHelper.OpenActivity(context, CompleteInformationActivity.class);
                getActivity().finish();
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(final int code, final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.closeLoadingDialog(context);
                    }
                });

            }
        });
    }
}
