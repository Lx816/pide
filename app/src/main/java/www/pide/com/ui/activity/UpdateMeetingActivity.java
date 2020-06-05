package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.example.liangmutian.mypicker.DatePickerDialog;
import www.example.liangmutian.mypicker.TimeBean;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.Sticky;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.view.BottomTimeWindow;

/**
 * Created by Administrator on 2020/4/2.
 */
public class UpdateMeetingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private RelativeLayout time_p;
    private LinearLayout date_p;
    private BottomTimeWindow bottomWindow;
    private TextView date;
    private int mposition1=0;
    private int mposition=0;
    private Intent intent;
    private String meeting_code="";
    private String sign="";
    private TextView m_title,date_h;
    private EditText remark;
    private Button sure;
    private int mId;
    private RelativeLayout time_h;
    private String date_time="";
    private TextView date_year,date_mouth,date_day,date_times;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_meeting_activity);
        //注册订阅者
        EventBus.getDefault().register(this);
        intent=getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        time_p = (RelativeLayout) findViewById(R.id.time_p);
        date_p = (LinearLayout) findViewById(R.id.date_p);
        date = (TextView) findViewById(R.id.date);
        m_title=findViewById(R.id.m_title);
        date_year=findViewById(R.id.date_year);
        date_mouth=findViewById(R.id.date_mouth);
        date_day=findViewById(R.id.date_day);
        date_times=findViewById(R.id.date_times);
        meeting_code=intent.getStringExtra("meeting_code");
        remark=findViewById(R.id.remark);
        sure=findViewById(R.id.sure);
        time_h=findViewById(R.id.time_h);
        date_h=findViewById(R.id.date_h);
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
                    m_title.setText(meetingD.getmName());
                    mId=meetingD.getmId();
                    date_time=meetingD.getStartTime();
                    String mstart_time=date_time;
                    try {
                        int year = Integer.parseInt(QTime.StringToTimeN(mstart_time));
                        //月
                        int month = Integer.parseInt(QTime.StringToTimeY(mstart_time));
                        //日
                        int day = Integer.parseInt(QTime.StringToTimeR(mstart_time));
                        int hour = Integer.parseInt(QTime.StringToTimeS(mstart_time));
                        int min = Integer.parseInt(QTime.StringToTimeF(mstart_time));
                        date_year.setText(year+"");
                        String mmonth=month+"";
                        if(month<10){
                            mmonth="0"+month;
                        }
                        date_mouth.setText(mmonth);
                        String mday=day+"";
                        if(day<10){
                            mday="0"+day;
                        }
                        date_day.setText(mday);
                        String mhour=hour+"";
                        if(hour<10){
                            mhour="0"+hour;
                        }
                        String mmin=min+"";
                        if(min<10){
                            mmin="0"+min;
                        }
                        date_times.setText(mhour+":"+mmin);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String mdateT="";
                    switch (meetingD.getInformTime()){
                        case "0":
                            mdateT="无提醒";
                            mposition1=0;
                            break;
                        case "5":
                            mdateT="5分钟前";
                            mposition1=1;
                            break;
                        case "15":
                            mdateT="15分钟前";
                            mposition1=2;
                            break;
                        case "60":
                            mdateT="1小时前";
                            mposition1=3;
                            break;
                        case "1440":
                            mdateT="1天前";
                            mposition1=4;
                            break;
                    }
                    date.setText(mdateT);
                    remark.setText(meetingD.getRemark());
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
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        finish();
                        break;
                    case R.id.time_p:
                        BottomTime();
                        break;
                    case R.id.date_p:
                        try {
                            datePicker();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.sure:
                        if(m_title.getText().toString().length()>0){
                            sure();
                        }else {
                            UIHelper.ToastMessageCenter(context, "请输入会议主题", 200);
                        }
                        break;
                    case R.id.time_h:
                        BottomTime_h();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        time_p.setOnClickListener(listener);
        date_p.setOnClickListener(listener);
        sure.setOnClickListener(listener);
        time_h.setOnClickListener(listener);
    }

    private void sure() {
        Util.showLoadingDialog(this,"加载中");
        String mdate="0";
        switch (mposition){
            case 0:
                mdate="0";
                break;
            case 1:
                mdate="5";
                break;
            case 2:
                mdate="15";
                break;
            case 3:
                mdate="60";
                break;
            case 4:
                mdate="1440";
                break;
        }
        String mdate_h="30";
        switch (mposition1){
            case 0:
                mdate_h="30";
                break;
            case 1:
                mdate_h="60";
                break;
            case 2:
                mdate_h="90";
                break;
            case 3:
                mdate_h="120";
                break;
            case 4:
                mdate_h="180";
                break;
        }
        if (m_title.getText().toString().trim().length() > 0) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String mstart_time0 = formatter.format(curDate);
                String mmsg="";
                if(mdate!="0"){
                    mmsg="减去提醒时间";
                }else {
                    mmsg="";
                }
                if (Long.valueOf(QTime.dateToStamp1(date_time)) - Long.valueOf(QTime.dateToStamp1(mstart_time0))-Long.valueOf(mdate) > 0) {
                    Map<String,Object> map=new HashMap<>();
                    map.put("informTime",mdate);
                    map.put("mId", String.valueOf(mId));
                    map.put("mName", m_title.getText().toString().trim());
                    map.put("remark", remark.getText().toString().trim());
                    map.put("durationTime", Integer.valueOf(mdate_h));
                    map.put("startTime", date_time);
                    OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/edit")
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
                            Util.closeLoadingDialog(context);
                            MeetingDBean meetingDBean= JsonUtil.fromJson(response,MeetingDBean.class);
                            if(meetingDBean.getSuccess()){
                                UIHelper.ToastMessageCenter(context, "会议修改成功", 200);
                                EventBus.getDefault().post(new Sticky("update"));
                                finish();
                            }else {
                                UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                if(meetingDBean.getCode().equals("401")){
                                    EMClient.getInstance().logout(true);
                                    SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                    UIHelper.OpenActivity(context,LoginActivity.class);
                                }
                            }
                        }
                    });
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, "预约时间"+mmsg+"不能小于当前时间", 200);
                }
            } catch (ParseException e) {
                Util.closeLoadingDialog(context);
                e.printStackTrace();
            }
        } else {
            Util.closeLoadingDialog(context);
            if (m_title.getText().toString().trim().length() == 0) {
                UIHelper.ToastMessageCenter(context, "请输入会议主题", 200);
            }
        }
    }

    private void BottomTime() {
        List<String> listData = new ArrayList<>();
        listData.add("无提醒");
        listData.add("5分钟前");
        listData.add("15分钟前");
        listData.add("1小时前");
        listData.add("1天前");
        bottomWindow = new BottomTimeWindow(UpdateMeetingActivity.this, listData, itemsOnClick,"",mposition);
        //设置弹窗位置
        bottomWindow.showAtLocation(UpdateMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void BottomTime_h() {
        List<String> listData = new ArrayList<>();
        listData.add("30分钟");
        listData.add("60分钟");
        listData.add("1小时30分钟");
        listData.add("2小时");
        listData.add("3小时");
        bottomWindow = new BottomTimeWindow(UpdateMeetingActivity.this, listData, itemsOnClick,"会议时长",mposition1);
        //设置弹窗位置
        bottomWindow.showAtLocation(UpdateMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
        }

    };
    private void datePicker() throws ParseException {
        SimpleDateFormat formatter=new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");
        Date curDate=new Date(System.currentTimeMillis());
        String mstart_time=formatter.format(curDate);
        int year = Integer.parseInt(QTime.StringToTimeN(mstart_time))-1;
        //月
        int month = Integer.parseInt(QTime.StringToTimeY(mstart_time))-1;
        //日
        int day = Integer.parseInt(QTime.StringToTimeR(mstart_time))-1;
        int hour = Integer.parseInt(QTime.StringToTimeS(mstart_time));
        int min = Integer.parseInt(QTime.StringToTimeF(mstart_time));
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        DatePickerDialog chooseDialog = builder.setSelectYear(year).setSelectMonth(month).setSelectDay(day).setSelectHour(hour).setSelectMin(min).setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @Override
            public void onDateSelected(TimeBean dates) {
                String mmonth=""+dates.getCurrMonth();
                if(mmonth.length()>1){
                    mmonth=""+dates.getCurrMonth();
                }else {
                    mmonth="0"+dates.getCurrMonth();
                }
                String mday=""+dates.getCurrDay();
                if(mday.length()>1){
                    mday=""+dates.getCurrDay();
                }else {
                    mday="0"+dates.getCurrDay();
                }
                String mhour=""+dates.getCurrHour();
                if(mhour.length()>1){
                     mhour=""+dates.getCurrHour();
                }else {
                    mhour="0"+dates.getCurrHour();
                }
                String mmin=""+dates.getCurrMin();
                if(mmin.length()>1){
                    mmin=""+dates.getCurrMin();
                }else {
                    mmin="0"+dates.getCurrMin();
                }
                String msec=""+dates.getCurrSec();
                if(msec.length()>1){
                    msec=""+dates.getCurrSec();
                }else {
                    msec="0"+dates.getCurrSec();
                }
                date_time=dates.getCurrYear()+"-"+mmonth+"-"+mday+" "+mhour+":"+mmin+":"+msec;
                date_year.setText(dates.getCurrYear()+"");
                date_mouth.setText(mmonth);
                date_day.setText(mday);
                date_times.setText(mhour+":"+mmin);
            }

            @Override
            public void onCancel() {

            }
        }).create();
        chooseDialog.show();
    }

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if(userEvent.msg.equals("0")){
            date_h.setText(userEvent.obj);
            mposition1=userEvent.position;
        }else {
            date.setText(userEvent.obj);
            mposition=userEvent.position;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
