package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetingDBean;
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
public class EndDetailsActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private TextView more;
    private Intent intent;
    private String meeting_code="";
    private String sign="";
    private TextView m_title,userName,joinCount,mweek,start_time,remark,informTime,reMeeting;
    private int mid;
    private LinearLayout l_informTime,l_remark;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_details_activity);
        intent=getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back= (RelativeLayout) findViewById(R.id.back);
        more= (TextView) findViewById(R.id.more);
        m_title= (TextView) findViewById(R.id.m_title);
        userName= (TextView) findViewById(R.id.userName);
        joinCount= (TextView) findViewById(R.id.joinCount);
        mweek= (TextView) findViewById(R.id.mweek);
        start_time= (TextView) findViewById(R.id.start_time);
        informTime= (TextView) findViewById(R.id.informTime);
        reMeeting= (TextView) findViewById(R.id.reMeeting);
        remark=findViewById(R.id.remark);
        l_informTime=findViewById(R.id.l_informTime);
        l_remark=findViewById(R.id.l_remark);
        meeting_code=intent.getStringExtra("meeting_code");
        initData();
    }
    private void initData() {
        Util.showLoadingDialog(this,"加载中");
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String,String> map=new HashMap<>();
        map.put("currentTime",mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", meeting_code);
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.SELECTBYROOMNUM)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingDBean meetingDBean= JsonUtil.fromJson(response,MeetingDBean.class);
                if(meetingDBean.getSuccess()){
                    MeetingDBean.MeetingD meetingD=meetingDBean.getData();
                    mid=meetingD.getmId();
                    m_title.setText(meetingD.getmName());
                    userName.setText(meetingD.getUserName());
                    joinCount.setText(meetingD.getJoinCount()+"人参加");
                    if(meetingD.getRemark()!=null){
                        if(meetingD.getRemark().length()>0){
                            l_remark.setVisibility(View.VISIBLE);
                            remark.setText(meetingD.getRemark());
                        }
                    }
                    try {
                        mweek.setText(QTime.StringToDate(meetingD.getStartTime())+" "+QTime.getWeek(QTime.StrToDate(meetingD.getStartTime())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        start_time.setText(QTime.StringToTime(meetingD.getStartTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(meetingD.getInformTime()!=null){
                        l_informTime.setVisibility(View.VISIBLE);
                        String mdateT="";
                        switch (meetingD.getInformTime()){
                            case "0":
                                mdateT="无提醒";
                                break;
                            case "5":
                                mdateT="5分钟前";
                                break;
                            case "15":
                                mdateT="15分钟前";
                                break;
                            case "60":
                                mdateT="1小时前";
                                break;
                            case "1440":
                                mdateT="1天前";
                                break;
                        }
                        informTime.setText(mdateT);
                    }
                    if(meetingD.getIsCreateType()==1){
                        reMeeting.setVisibility(View.VISIBLE);
                    }
                    Util.closeLoadingDialog(context);
                }else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if(meetingDBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context,LoginActivity.class);
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
                    case R.id.more:
                        Intent intent=new Intent(EndDetailsActivity.this,ParticipantActivity.class);
                        intent.putExtra("mId",mid+"");
                        intent.putExtra("name", "参与人");
                        startActivity(intent);
                        break;
                    case R.id.reMeeting:
                        Intent intentC=new Intent(EndDetailsActivity.this,CreatMeetingActivity.class);
                        intentC.putExtra("meeting_code",meeting_code);
                        startActivity(intentC);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        more.setOnClickListener(listener);
        reMeeting.setOnClickListener(listener);
    }
}
