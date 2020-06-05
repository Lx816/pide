package www.pide.com.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.Sticky;
import www.pide.com.ui.activity.LoginActivity;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import www.pide.com.R;

public class MeetingZDialog extends Dialog {
    private String mname, mtitle, mcolor, mavatar, mConfrId, mTalkers, mId,admin,t5;
    private Activity context;
    private String sign="";
    private EMConferenceManager.EMConferenceRole  conferenceRole;
    public MeetingZDialog(Activity mcontext, String t1, String t2, String t3, String t4, String t5, int theme, String mavatar, String mConfrId, String mTalkers, String mId, String admin) {
        super(mcontext, theme);
        mname = t1;
        mtitle = t2;
        mcolor = t3;
        context = mcontext;
        this.mavatar = mavatar;
        this.mConfrId = mConfrId;
        this.mTalkers = mTalkers;
        this.mId = mId;
        this.admin = admin;
        this.t5 = t5;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.y_center_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView name = findViewById(R.id.name);
        name.setText(mname);
        TextView title = findViewById(R.id.title);
        title.setText(mtitle);
        title.setTextColor(Color.parseColor(mcolor));
        Button qx = findViewById(R.id.qx);
        Button qd = findViewById(R.id.qd);
        qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                switch (mtitle) {
                    case "将其移出会议?":
                        kickMember();
                        break;
                    case "转让房主给TA":
                        grantRole();
                        break;
                }
            }
        });
        XCRoundImageView avatar = findViewById(R.id.avatar);
        Glide.with(context)
                .load(mavatar)
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(avatar);
    }

    private void grantRole() {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId);
        map.put("ownerUserId", mTalkers.replace(URLs.APPKEY+"_", ""));
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/updateMeetOwner")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingBean meetingBean = JsonUtil.fromJson(response, MeetingBean.class);
                if (meetingBean.getSuccess()) {
                    if(t5.length()>0){
                        EventBus.getDefault().post(new Sticky("是否退出会议"));
                    }else {
                        EventBus.getDefault().post(new Sticky("zrfz"));
                    }
                } else {
                    UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                    if(meetingBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }

    private void kickMember() {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId);
        map.put("userId", mTalkers.replace(URLs.APPKEY+"_", ""));
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/removeMeetUser")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingBean meetingBean = JsonUtil.fromJson(response, MeetingBean.class);
                if (meetingBean.getSuccess()) {
                    EventBus.getDefault().post(new Sticky("zrfz"));
                } else {
                    UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                    if(meetingBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context,LoginActivity.class);
                    }
                }
            }
        });
    }
}
