package www.pide.com.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.CollectBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.bean.UserBean1;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.view.BottomPhoneWindow;
import www.pide.com.ui.view.RoundImageView;

/**
 * Created by Administrator on 2020/3/25.
 */
public class CompleteInformationFK extends BaseActivity {
    private RelativeLayout back;
    private Context context;
    private TextView name, company, profession_name, address, introduce, possession_resources, need_resources, company_name, profession_name_m;
    private RoundImageView avatar;
    private User user = new User();
    private RelativeLayout r3, r4, r2, r1, r_a1, r_a2;
    private BottomPhoneWindow bottomWindow;
    private Button call_tel;
    private Intent intent;
    private String id;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private ImageView s_collect;
    private CollectBean bean = new CollectBean();
    private String sign = "";
    private UserBean1 user1;
    private TextView position,line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_information_fk);
        intent = getIntent();
        context = this;
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        name = (TextView) findViewById(R.id.name);
        company = (TextView) findViewById(R.id.company);
        profession_name = (TextView) findViewById(R.id.profession_name);
        address = (TextView) findViewById(R.id.address);
        introduce = (TextView) findViewById(R.id.introduce);
        possession_resources = (TextView) findViewById(R.id.possession_resources);
        need_resources = (TextView) findViewById(R.id.need_resources);
        avatar = (RoundImageView) findViewById(R.id.avatar);
        r3 = (RelativeLayout) findViewById(R.id.r3);
        r4 = (RelativeLayout) findViewById(R.id.r4);
        call_tel = (Button) findViewById(R.id.call_tel);
        profession_name_m = (TextView) findViewById(R.id.profession_name_m);
        company_name = (TextView) findViewById(R.id.company_name);
        r2 = (RelativeLayout) findViewById(R.id.r2);
        r1 = (RelativeLayout) findViewById(R.id.r1);
        r_a1 = (RelativeLayout) findViewById(R.id.r_a1);
        r_a2 = (RelativeLayout) findViewById(R.id.r_a2);
        id = intent.getStringExtra("id");
        s_collect = (ImageView) findViewById(R.id.s_collect);
        position=findViewById(R.id.position);
        line=findViewById(R.id.line);
        initData();
    }

    private void initData() {
        Util.showLoadingDialog(this,"加载中");
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String,String> map=new HashMap<>();
        map.put("currentTime",mcurrentTime);
        map.put("sign", sign);
        map.put("userId", id);
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.SELECTBYID)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                user1 = JsonUtil.fromJson(response, UserBean1.class);
                if (user1.isSuccess()) {
                    user = new User();
                    user = user1.getData();
                    name.setText(user.getName());
                    company.setText(user.getCompany());
                    profession_name.setText(user.getProfession_name());
                    address.setText(user.getAddress());
                    if(user.getPosition()!=null && user.getPosition().length()>0){
                        position.setText(user.getPosition());
                        line.setBackgroundColor(Color.parseColor("#000000"));
                    }else {
                        line.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                    Glide.with(context)
                            .load(user.getAvatar())
                            .apply(new RequestOptions().placeholder(R.drawable.error))
                            .apply(new RequestOptions().error(R.drawable.error))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(avatar);
                    if (user.getIntroduce() == null) {
                        r1.setVisibility(View.GONE);
                    } else {
                        r1.setVisibility(View.VISIBLE);
                        introduce.setText(user.getIntroduce());
                    }
                    profession_name_m.setText(user.getProfession_name());
                    company_name.setText(user.getCompany());
                    if (user.getPossession_resources() == null) {
                        r3.setVisibility(View.GONE);
                        r_a1.setVisibility(View.VISIBLE);
                    } else {
                        r3.setVisibility(View.VISIBLE);
                        r_a1.setVisibility(View.GONE);
                        possession_resources.setText(user.getPossession_resources());
                    }
                    if (user.getNeed_resources() == null) {
                        r4.setVisibility(View.GONE);
                        r_a2.setVisibility(View.VISIBLE);
                    } else {
                        r4.setVisibility(View.VISIBLE);
                        r_a2.setVisibility(View.GONE);
                        need_resources.setText(user.getNeed_resources());
                    }
                    if(user.getcId()==null){
                        s_collect.setImageResource(R.mipmap.s_collect);
                    }else {
                        s_collect.setImageResource(R.mipmap.collect_onclick);
                    }
                    Util.closeLoadingDialog(context);
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, user1.getMsg(), 200);
                    if(user1.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(CompleteInformationFK.this,LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        finish();
                        break;
                    case R.id.call_tel:
                        BottomPhoneWindow(user.getPhone());
                        break;
                    case R.id.s_collect:
                        s_collect();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        call_tel.setOnClickListener(listener);
        s_collect.setOnClickListener(listener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                initData();
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void BottomPhoneWindow(String phone) {
        bottomWindow = new BottomPhoneWindow(CompleteInformationFK.this, itemsOnClick, phone, "fk");
        //设置弹窗位置
        bottomWindow.showAtLocation(CompleteInformationFK.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.phone:
                    call(user.getPhone());
                    break;
                case R.id.close_r:
                    break;
            }
        }

    };

    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请允许拨号权限后再试", Toast.LENGTH_SHORT).show();
                } else {//成功
                    call(user.getPhone());
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     *
     * @param telPhone 电话
     */
    public void call(String telPhone) {
        if (checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telPhone));
            startActivity(intent);
        }
    }

    private void s_collect() {
        if(user.getcId()==null){
            Map<String,String> map=new HashMap<>();
            map.put("collectUserId", user.getUserId()+"");
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
                        UIHelper.ToastMessageCenter(context, "收藏成功", 200);
                        EventBus.getDefault().post(new Sticky("4"));
                        initData();
                    }else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(CompleteInformationFK.this,LoginActivity.class);
                        }
                    }
                }
            });
        }else {
            OkHttpUtils.post()
                    .url(URLs.IMGEURL + URLs.DELETECOLLECTIONSUSER)
                    .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                    .addParams("cId",user.getcId())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                    if(userBean.isSuccess()){
                        UIHelper.ToastMessageCenter(context, "取消收藏成功", 200);
                        EventBus.getDefault().post(new Sticky("4"));
                        initData();
                    }else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(CompleteInformationFK.this,LoginActivity.class);
                        }
                    }
                }
            });
        }
    }
}
