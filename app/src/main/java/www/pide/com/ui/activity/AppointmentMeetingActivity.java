package www.pide.com.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.UserBean;
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
public class AppointmentMeetingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private RelativeLayout time_p;
    private LinearLayout date_p;
    private BottomTimeWindow bottomWindow;
    private TextView date, date_h;
    private Button send_appointment;
    private RelativeLayout time_h;
    private int mposition = 0;
    private int mposition1 = 0;
    private EditText m_title, demo;
    private TextView room_code;
    private String date_time = "";
    private String meeting_pw = "";
    private TextView date_year, date_mouth, date_day, date_times;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_meeting_activity);
        //注册订阅者
        EventBus.getDefault().register(this);
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        time_p = findViewById(R.id.time_p);
        date_p = (LinearLayout) findViewById(R.id.date_p);
        date = (TextView) findViewById(R.id.date);
        send_appointment = (Button) findViewById(R.id.send_appointment);
        time_h = findViewById(R.id.time_h);
        date_h = findViewById(R.id.date_h);
        m_title = findViewById(R.id.m_title);
        date_year = findViewById(R.id.date_year);
        date_mouth = findViewById(R.id.date_mouth);
        date_day = findViewById(R.id.date_day);
        date_times = findViewById(R.id.date_times);
        room_code = findViewById(R.id.room_code);
        demo = findViewById(R.id.demo);
        Util.showLoadingDialog(this, "加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.CREATEROOMNUM)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    String roomNum = u.getUserD().getRoomNum();
                    if (roomNum != null) {
                        room_code.setText(roomNum);
                        meeting_pw = u.getUserD().getmPassword();
                    }
                    Util.closeLoadingDialog(context);
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if (u.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        date_time = formatter.format(curDate);
        String mstart_time = formatter.format(curDate);
        try {
            int year = Integer.parseInt(QTime.StringToTimeN(mstart_time));
            //月
            int month = Integer.parseInt(QTime.StringToTimeY(mstart_time));
            //日
            int day = Integer.parseInt(QTime.StringToTimeR(mstart_time));
            int hour = Integer.parseInt(QTime.StringToTimeS(mstart_time));
            int min = Integer.parseInt(QTime.StringToTimeF(mstart_time));
            date_year.setText(year + "");
            String mmonth = month + "";
            if (month < 10) {
                mmonth = "0" + month;
            }
            date_mouth.setText(mmonth);
            String mday = day + "";
            if (day < 10) {
                mday = "0" + day;
            }
            date_day.setText(mday);
            String mhour = hour + "";
            if (hour < 10) {
                mhour = "0" + hour;
            }
            String mmin = min + "";
            if (min < 10) {
                mmin = "0" + min;
            }
            date_times.setText(mhour + ":" + mmin);
        } catch (ParseException e) {
            e.printStackTrace();
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
                    case R.id.send_appointment:
                        appointment();
                        break;
                    case R.id.time_h:
                        BottomTime_h();
                        break;
                    case R.id.room_code:
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", room_code.getText().toString().trim());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        UIHelper.ToastMessageCenter(context, "已拷贝", 200);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        time_p.setOnClickListener(listener);
        date_p.setOnClickListener(listener);
        send_appointment.setOnClickListener(listener);
        time_h.setOnClickListener(listener);
        room_code.setOnClickListener(listener);
    }

    private void appointment() {
        //Util.showLoadingDialog(this, "加载中");
        String mm_title = m_title.getText().toString().trim();
        String mdate_time = date_time;
        String mroom_code = room_code.getText().toString().trim();
        String mdemo = demo.getText().toString().trim();
        String mdate_h = "30";
        switch (mposition1) {
            case 0:
                mdate_h = "30";
                break;
            case 1:
                mdate_h = "60";
                break;
            case 2:
                mdate_h = "90";
                break;
            case 3:
                mdate_h = "120";
                break;
            case 4:
                mdate_h = "180";
                break;
        }
        String mdate = "0";
        switch (mposition) {
            case 0:
                mdate = "0";
                break;
            case 1:
                mdate = "5";
                break;
            case 2:
                mdate = "15";
                break;
            case 3:
                mdate = "60";
                break;
            case 4:
                mdate = "1440";
                break;
        }
        if (mm_title.length() > 0 && mroom_code.length() > 0 && mdate_time.length() > 0) {
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
                    Map<String, Object> map = new HashMap<>();
                    map.put("mName", mm_title);
                    map.put("startTime", mdate_time);
                    map.put("durationTime", Integer.valueOf(mdate_h));
                    map.put("informTime", mdate);
                    map.put("mType", "1");
                    map.put("roomNum", mroom_code);
                    map.put("remark", mdemo);
                    map.put("mPassword", meeting_pw);
                    OkHttpUtils.postString().url(URLs.IMGEURL + URLs.ADD)
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
                            MeetingBean meetingBean = JsonUtil.fromJson(response, MeetingBean.class);
                            if (meetingBean.getSuccess()) {
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(context, "会议预约成功", 200);
                                finish();
                            } else {
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                if (meetingBean.getCode().equals("401")) {
                                    EMClient.getInstance().logout(true);
                                    SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                    UIHelper.OpenActivity(context, LoginActivity.class);
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
            if (mm_title.length() == 0) {
                UIHelper.ToastMessageCenter(context, "请输入会议主题", 200);
            }
            if (mroom_code.length() == 0) {
                UIHelper.ToastMessageCenter(context, "请获取房间号", 200);
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
        bottomWindow = new BottomTimeWindow(AppointmentMeetingActivity.this, listData, itemsOnClick, "", mposition);
        //设置弹窗位置
        bottomWindow.showAtLocation(AppointmentMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void BottomTime_h() {
        List<String> listData = new ArrayList<>();
        listData.add("30分钟");
        listData.add("60分钟");
        listData.add("1小时30分钟");
        listData.add("2小时");
        listData.add("3小时");
        bottomWindow = new BottomTimeWindow(AppointmentMeetingActivity.this, listData, itemsOnClick, "会议时长", mposition1);
        //设置弹窗位置
        bottomWindow.showAtLocation(AppointmentMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
        }

    };

    private void datePicker() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String mstart_time = formatter.format(curDate);
        int year = Integer.parseInt(QTime.StringToTimeN(mstart_time)) - 1;
        //月
        int month = Integer.parseInt(QTime.StringToTimeY(mstart_time)) - 1;
        //日
        int day = Integer.parseInt(QTime.StringToTimeR(mstart_time)) - 1;
        int hour = Integer.parseInt(QTime.StringToTimeS(mstart_time));
        int min = Integer.parseInt(QTime.StringToTimeF(mstart_time));
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        DatePickerDialog chooseDialog = builder.setSelectYear(year).setSelectMonth(month).setSelectDay(day).setSelectHour(hour).setSelectMin(min).setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @Override
            public void onDateSelected(TimeBean dates) {
                String mmonth = "" + dates.getCurrMonth();
                if (mmonth.length() > 1) {
                    mmonth = "" + dates.getCurrMonth();
                } else {
                    mmonth = "0" + dates.getCurrMonth();
                }
                String mday = "" + dates.getCurrDay();
                if (mday.length() > 1) {
                    mday = "" + dates.getCurrDay();
                } else {
                    mday = "0" + dates.getCurrDay();
                }
                String mhour = "" + dates.getCurrHour();
                if (mhour.length() > 1) {
                    mhour = "" + dates.getCurrHour();
                } else {
                    mhour = "0" + dates.getCurrHour();
                }
                String mmin = "" + dates.getCurrMin();
                if (mmin.length() > 1) {
                    mmin = "" + dates.getCurrMin();
                } else {
                    mmin = "0" + dates.getCurrMin();
                }
                String msec = "" + dates.getCurrSec();
                if (msec.length() > 1) {
                    msec = "" + dates.getCurrSec();
                } else {
                    msec = "0" + dates.getCurrSec();
                }
                date_time = dates.getCurrYear() + "-" + mmonth + "-" + mday + " " + mhour + ":" + mmin + ":" + msec;
                date_year.setText(dates.getCurrYear() + "");
                date_mouth.setText(mmonth);
                date_day.setText(mday);
                date_times.setText(mhour + ":" + mmin);
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
        if (userEvent.msg.equals("0")) {
            date_h.setText(userEvent.obj);
            mposition1 = userEvent.position;
        } else {
            date.setText(userEvent.obj);
            mposition = userEvent.position;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
