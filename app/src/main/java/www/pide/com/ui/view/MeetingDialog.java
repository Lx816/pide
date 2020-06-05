package www.pide.com.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.bean.UserBean;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.ui.activity.CompleteInformation;
import www.pide.com.ui.activity.CompleteInformationFK;
import www.pide.com.ui.activity.LoginActivity;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import www.pide.com.R;

public class MeetingDialog extends Dialog {
    private String mname,mcompany,mposition,userId,mavatar,mConfrId,mTalkers,mId,admin,cId;
    private Activity context;
    private Boolean isAdmin;
    private RelativeLayout add_gz_r;
    private ImageView add_gz;
    private TextView add_gz_t;
    public MeetingDialog(Activity mcontext, String t1, String t2, String t3, Boolean isAdmin, int theme, String userId, String avatar, String mConfrId, String mTalkers, String mId, String admin,String cId) {
        super(mcontext,theme);
        mname=t1;
        mcompany=t2;
        mposition=t3;
        context=mcontext;
        this.isAdmin=isAdmin;
        this.userId=userId;
        mavatar=avatar;
        this.mConfrId=mConfrId;
        this.mTalkers=mTalkers;
        this.mId=mId;
        this.admin=admin;
        this.cId=cId;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        ImageView avatar=findViewById(R.id.avatar);
        TextView name=findViewById(R.id.name);
        TextView company=findViewById(R.id.company);
        TextView position=findViewById(R.id.position);
        name.setText(mname);
        company.setText(mcompany);
        position.setText(mposition);
        Button look_x=findViewById(R.id.look_x);
        look_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(PreferenceManager.getInstance().getCurrentUsername().equals(userId)){
                    Intent intent=new Intent(context, CompleteInformation.class);
                    intent.putExtra("id",userId);
                    context.startActivity(intent);
                }else {
                    Intent intent=new Intent(context, CompleteInformationFK.class);
                    intent.putExtra("id",userId);
                    context.startActivity(intent);
                }
            }
        });
        RelativeLayout yc_meeting=findViewById(R.id.yc_meeting);
        RelativeLayout zr_meeting=findViewById(R.id.zr_meeting);
        RelativeLayout r_bottom=findViewById(R.id.r_bottom);
        if(isAdmin){
            r_bottom.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(mavatar)
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(avatar);
        yc_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new MeetingZDialog(context,mname,"将其移出会议?","#FF2D2D","0","",R.style.DialogTheme,mavatar,mConfrId,mTalkers,mId,admin).show();
            }
        });
        zr_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new MeetingZDialog(context,mname,"转让房主给TA","#0076FF","1","",R.style.DialogTheme,mavatar,mConfrId,mTalkers,mId,admin).show();
            }
        });
        add_gz_r=findViewById(R.id.add_gz_r);
        add_gz=findViewById(R.id.add_gz);
        add_gz_t=findViewById(R.id.add_gz_t);
        if(cId==null){
            add_gz.setVisibility(View.VISIBLE);
            add_gz_t.setTextColor(Color.parseColor("#ffffff"));
            add_gz_t.setText("关注");
            add_gz_r.setBackgroundResource(R.drawable.button_red_shape);
        }else {
            add_gz.setVisibility(View.GONE);
            add_gz_t.setTextColor(Color.parseColor("#666666"));
            add_gz_t.setText("已关注");
            add_gz_r.setBackgroundResource(R.drawable.button_m1_shape);
        }
        add_gz_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_collect();
            }
        });
    }
    private void s_collect() {
        if(cId==null){
            Map<String,String> map=new HashMap<>();
            map.put("collectUserId", userId);
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.ADDCOLLECTIONSUSER)
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
                    UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                    if(userBean.isSuccess()){
                        UIHelper.ToastMessageCenter(context, "关注成功", 200);
                        add_gz.setVisibility(View.GONE);
                        add_gz_t.setTextColor(Color.parseColor("#666666"));
                        add_gz_t.setText("已关注");
                        add_gz_r.setBackgroundResource(R.drawable.button_m1_shape);
                        cId=userId;
                    }else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(context, LoginActivity.class);
                        }
                    }
                }
            });
        }else {
            OkHttpUtils.post()
                    .url(URLs.IMGEURL + URLs.DELETECOLLECTIONSUSER)
                    .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                    .addParams("cId",cId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                    if(userBean.isSuccess()){
                        UIHelper.ToastMessageCenter(context, "取消关注成功", 200);
                        add_gz.setVisibility(View.VISIBLE);
                        add_gz_t.setTextColor(Color.parseColor("#ffffff"));
                        add_gz_t.setText("关注");
                        add_gz_r.setBackgroundResource(R.drawable.button_red_shape);
                        cId=null;
                    }else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(context,LoginActivity.class);
                        }
                    }
                }
            });
        }
    }
}
