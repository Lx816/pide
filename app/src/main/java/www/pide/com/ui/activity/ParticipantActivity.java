package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.adapter.ParticipantAdapter;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetUserBean;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/4/2.
 */
public class ParticipantActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private ListView listview;
    private ParticipantAdapter adapter;
    private List<MeetUserBean.UserBean> listData;
    private Intent intent;
    private TextView title;
    private String title_name="";
    private String mId;
    private String sign="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_activity);
        intent=getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        listview= (ListView) findViewById(R.id.listview);
        title= (TextView) findViewById(R.id.title);
        title_name=intent.getStringExtra("name");
        mId=intent.getStringExtra("mId");
        title.setText(title_name);
        initData();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(PreferenceManager.getInstance().getCurrentUsername().equals(listData.get(i).getUserId()+"")){
                    Intent intent=new Intent(ParticipantActivity.this,CompleteInformation.class);
                    intent.putExtra("id",listData.get(i).getUserId()+"");
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(ParticipantActivity.this,CompleteInformationFK.class);
                    intent.putExtra("id",listData.get(i).getUserId()+"");
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        Util.showLoadingDialog(this,"加载中");
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String,String> map=new HashMap<>();
        map.put("currentTime",mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/getMeetUserList")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                Util.closeLoadingDialog(context);
                MeetUserBean meetingDBean = JsonUtil.fromJson(response, MeetUserBean.class);
                if (meetingDBean.isSuccess()) {
                    listData=new ArrayList<>();
                    listData=meetingDBean.getData();
                    adapter=new ParticipantAdapter(context,listData);
                    listview.setAdapter(adapter);
                } else {
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if(meetingDBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(ParticipantActivity.this,LoginActivity.class);
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
                }
            }
        };
        back.setOnClickListener(listener);
    }
}
