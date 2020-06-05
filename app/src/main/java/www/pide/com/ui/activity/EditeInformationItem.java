package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.UserBean;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/3/27.
 */
public class EditeInformationItem extends BaseActivity {
    private TextView title_name;
    private Intent intent;
    private RelativeLayout back;
    private TextView t_right, t_left;
    private EditText input;
    private int mnumber = 0;
    private Context context;
    private Button save;
    private String t_input;
    private String param_name;
    private String sign = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edite_information_item);
        intent = getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText(intent.getStringExtra("titleName"));
        mnumber = Integer.parseInt(intent.getStringExtra("num"));
        param_name = intent.getStringExtra("param_name");
        back = (RelativeLayout) findViewById(R.id.back);
        t_right = (TextView) findViewById(R.id.t_right);
        t_left = (TextView) findViewById(R.id.t_left);
        input = (EditText) findViewById(R.id.input);
        save = (Button) findViewById(R.id.save);
        input.addTextChangedListener(watcher);
        t_right.setText("/" + mnumber);
        if (intent.getStringExtra("input_t") == null || intent.getStringExtra("input_t").equals("")) {
            input.setHint(intent.getStringExtra("hint"));
        } else {
            input.setText(intent.getStringExtra("input_t"));
        }
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        finish();
                        break;
                    case R.id.save:
                        save();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        save.setOnClickListener(listener);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int minput = input.getText().toString().trim().length();
            t_left.setText(String.valueOf(minput));
            if (minput > mnumber) {
                UIHelper.ToastMessageCenter(context, "字数超过限制", 200);
                t_left.setTextColor(Color.parseColor("#FF0000"));
            } else {
                t_left.setTextColor(Color.parseColor("#999999"));
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
// TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
// TODO Auto-generated method stub

        }
    };

    private void save() {
        Util.showLoadingDialog(this,"加载中");
        t_input = input.getText().toString().trim();
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        if (t_input.length() > 0 && t_input.length() <= mnumber) {
            if (intent.getStringExtra("name") == null && (intent.getStringExtra("titleName").equals("拥有资源") || intent.getStringExtra("titleName").equals("需要资源"))) {
                Util.closeLoadingDialog(context);
                EventBus.getDefault().post(new Sticky(intent.getStringExtra("titleName"), t_input));
                finish();
            } else {
                Map<String,String> map=new HashMap<>();
                map.put("currentTime",mcurrentTime);
                map.put("sign", sign);
                map.put(param_name, t_input);
                OkHttpUtils.postString().url(URLs.IMGEURL + URLs.EDIT)
                        .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(new Gson().toJson(map))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e) {
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(EditeInformationItem.this, "保存失败", 2000);
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, String response) {
                                UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                                if (userBean.isSuccess()) {
                                    Util.closeLoadingDialog(context);
                                    UIHelper.ToastMessageCenter(EditeInformationItem.this, "保存成功", 2000);
                                    EventBus.getDefault().post(new Sticky(intent.getStringExtra("titleName"),t_input));
                                    Intent intent = new Intent();
                                    setResult(2, intent);
                                    finish();
                                }else {
                                    Util.closeLoadingDialog(context);
                                    UIHelper.ToastMessageCenter(EditeInformationItem.this, userBean.getMsg(), 2000);
                                    if(userBean.getCode().equals("401")){
                                        EMClient.getInstance().logout(true);
                                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                        UIHelper.OpenActivity(context,LoginActivity.class);
                                    }
                                }
                            }
                        });
            }
        } else {
            Util.closeLoadingDialog(context);
            if (t_input.length() > mnumber) {
                UIHelper.ToastMessageCenter(EditeInformationItem.this, "字数超过限制", 2000);
            } else {
                UIHelper.ToastMessageCenter(EditeInformationItem.this, "请填写信息后再保存", 2000);
            }
        }
    }
}
