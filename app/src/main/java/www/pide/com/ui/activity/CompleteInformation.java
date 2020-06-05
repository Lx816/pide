package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import www.pide.com.R;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean1;
import www.pide.com.ui.view.BottomPhoneWindow;
import www.pide.com.ui.view.RoundImageView;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

/**
 * Created by Administrator on 2020/3/25.
 */
public class CompleteInformation extends BaseActivity {
    private RelativeLayout back;
    private TextView complete;
    private Context context;
    private TextView name,company,profession_name,address,introduce,possession_resources,need_resources,company_name,profession_name_m;
    private RoundImageView avatar;
    private User user=new User();
    private RelativeLayout r3,r4,r2,r1,r_a1,r_a2;
    private BottomPhoneWindow bottomWindow;
    private Button call_tel;
    private UserBean1 user1;
    private TextView position,line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_information);
        //注册订阅者
        EventBus.getDefault().register(this);
        context=this;
        initView();
        initOnclick();
    }

    private void initView() {
        back= (RelativeLayout) findViewById(R.id.back);
        complete= (TextView) findViewById(R.id.complete);
        name= (TextView) findViewById(R.id.name);
        company= (TextView) findViewById(R.id.company);
        profession_name= (TextView) findViewById(R.id.profession_name);
        address= (TextView) findViewById(R.id.address);
        introduce= (TextView) findViewById(R.id.introduce);
        possession_resources= (TextView) findViewById(R.id.possession_resources);
        need_resources= (TextView) findViewById(R.id.need_resources);
        avatar= (RoundImageView) findViewById(R.id.avatar);
        r3= (RelativeLayout) findViewById(R.id.r3);
        r4= (RelativeLayout) findViewById(R.id.r4);
        call_tel= (Button) findViewById(R.id.call_tel);
        profession_name_m= (TextView) findViewById(R.id.profession_name_m);
        company_name= (TextView) findViewById(R.id.company_name);
        r2= (RelativeLayout) findViewById(R.id.r2);
        r1= (RelativeLayout) findViewById(R.id.r1);
        r_a1= (RelativeLayout) findViewById(R.id.r_a1);
        r_a2= (RelativeLayout) findViewById(R.id.r_a2);
        position=findViewById(R.id.position);
        line=findViewById(R.id.line);
        initData();
    }

    private void initData() {
        Util.showLoadingDialog(this,"加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYDETAIL)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                user1 = JsonUtil.fromJson(response, UserBean1.class);
                if(user1.isSuccess()) {
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
                        Bitmap bitmap = BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.edite);
                        ImageSpan imgSpan = new ImageSpan(getApplicationContext(), bitmap);
                        SpannableString spanString = new SpannableString("icon");
                        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        introduce.setText("");
                        introduce.append(user.getIntroduce());
                        introduce.append(spanString);
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
                    Util.closeLoadingDialog(context);
                }else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, user1.getMsg(), 200);
                    if(user1.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(CompleteInformation.this,LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        finish();
                        break;
                    case R.id.complete:
                        UIHelper.OpenActivity(context,EditeInformation.class);
                        break;
                    case R.id.r3:
                        Intent intent = new Intent(CompleteInformation.this, EditeInformationItem.class);
                        intent.putExtra("titleName", "拥有资源");
                        intent.putExtra("name", user.getName());
                        intent.putExtra("num", "60");
                        intent.putExtra("param_name", "possessionResources");
                        intent.putExtra("input_t", user.getPossession_resources());
                        startActivityForResult(intent,2);
                        break;
                    case R.id.r4:
                        Intent intent1 = new Intent(CompleteInformation.this, EditeInformationItem.class);
                        intent1.putExtra("titleName", "需要资源");
                        intent1.putExtra("name", user.getName());
                        intent1.putExtra("num", "60");
                        intent1.putExtra("param_name", "needResources");
                        intent1.putExtra("input_t", user.getNeed_resources());
                        startActivityForResult(intent1,2);
                        break;
                    case R.id.call_tel:
                        BottomPhoneWindow(user.getPhone());
                        break;
                    case R.id.r2:
                        UIHelper.OpenActivity(context,EditeInformation.class);
                        break;
                    case R.id.r1:
                        Intent intent2 = new Intent(CompleteInformation.this, EditeInformationItem.class);
                        intent2.putExtra("titleName", "自我介绍");
                        intent2.putExtra("name", user.getName());
                        intent2.putExtra("num", "100");
                        intent2.putExtra("param_name", "introduce");
                        intent2.putExtra("input_t", user.getIntroduce());
                        intent2.putExtra("hint", "一句话介绍自己的独特之处");
                        startActivityForResult(intent2, 2);
                        break;
                    case R.id.r_a1:
                        Intent intent3 = new Intent(CompleteInformation.this, EditeInformationItem.class);
                        intent3.putExtra("titleName", "拥有资源");
                        intent3.putExtra("name", user.getName());
                        intent3.putExtra("num", "60");
                        intent3.putExtra("param_name", "possessionResources");
                        intent3.putExtra("input_t", user.getPossession_resources());
                        intent3.putExtra("hint", "填写自己拥有的资源");
                        startActivityForResult(intent3,2);
                        break;
                    case R.id.r_a2:
                        Intent intent4 = new Intent(CompleteInformation.this, EditeInformationItem.class);
                        intent4.putExtra("titleName", "需要资源");
                        intent4.putExtra("name", user.getName());
                        intent4.putExtra("num", "60");
                        intent4.putExtra("param_name", "needResources");
                        intent4.putExtra("input_t", user.getNeed_resources());
                        intent4.putExtra("hint", "填写自己需要的资源");
                        startActivityForResult(intent4,2);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        complete.setOnClickListener(listener);
        r3.setOnClickListener(listener);
        r4.setOnClickListener(listener);
        call_tel.setOnClickListener(listener);
        r2.setOnClickListener(listener);
        r1.setOnClickListener(listener);
        r_a1.setOnClickListener(listener);
        r_a2.setOnClickListener(listener);
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
        bottomWindow = new BottomPhoneWindow(CompleteInformation.this, itemsOnClick,phone,"");
        //设置弹窗位置
        bottomWindow.showAtLocation(CompleteInformation.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.phone:
                    UIHelper.ToastMessageCenter(context, "不能给自己打电话", 200);
                    break;
                case R.id.item_edit:
                    UIHelper.OpenActivity(CompleteInformation.this,EditeInformation.class);
                    break;
                case R.id.close_r:
                    break;
            }
        }

    };
    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("2")){
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
