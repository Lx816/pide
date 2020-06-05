package www.pide.com.ui.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMRoomConfig;
import com.imuxuan.floatingview.FloatingView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.Sticky;
import www.pide.com.hxsdk.ConferenceInfo;
import www.pide.com.hxsdk.ConferenceSession;
import www.pide.com.hxsdk.DemoHelper;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.view.BottomMeetingWindow;

/**
 * 预约会议详情
 */
public class AppointmentDetailsActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back, b1, b2;
    private TextView tq_meeting, more_g;
    private ImageView more;
    private BottomMeetingWindow bottomWindow;
    private Intent intent;
    private String meeting_code = "";
    private String sign = "";
    private TextView m_title, userName, joinCount, roomNum, mweek, start_time, remark, t1, informTime;
    private ConferenceSession conferenceSession;
    private EMConferenceManager.EMConferenceRole conferenceRole;
    private Button fq_meeting;
    private int mId;
    private LinearLayout l_remark;
    private String mRoomNum = "", mPassword = "";
    private int mjoinCount = 0;
    Handler initMeetingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            /**
             * Looper回调此方法，覆写这个方法
             */
            switch (msg.what) {
                case 0:
                    initData();
                    break;
                case 1:
                    jion_meeting();
                    break;
                case 2:
                    fq_meeting();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_details_activity);
        //注册订阅者
        EventBus.getDefault().register(this);
        intent = getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        b1 = (RelativeLayout) findViewById(R.id.b1);
        b2 = (RelativeLayout) findViewById(R.id.b2);
        tq_meeting = (TextView) findViewById(R.id.tq_meeting);
        more = (ImageView) findViewById(R.id.more);
        more_g = (TextView) findViewById(R.id.more_g);
        m_title = (TextView) findViewById(R.id.m_title);
        userName = (TextView) findViewById(R.id.userName);
        joinCount = (TextView) findViewById(R.id.joinCount);
        roomNum = (TextView) findViewById(R.id.roomNum);
        mweek = (TextView) findViewById(R.id.mweek);
        start_time = (TextView) findViewById(R.id.start_time);
        informTime = (TextView) findViewById(R.id.informTime);
        l_remark = findViewById(R.id.l_remark);
        t1 = findViewById(R.id.t1);
        meeting_code = intent.getStringExtra("meeting_code");
        remark = findViewById(R.id.remark);
        fq_meeting = findViewById(R.id.fq_meeting);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMeetingHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initData() {
        Util.showLoadingDialog(this, "加载中");
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", meeting_code);
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.SELECTBYROOMNUM)
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
                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                if (meetingDBean.getSuccess()) {
                    MeetingDBean.MeetingD meetingD = meetingDBean.getData();
                    if (meetingD.getmStatus().equals("0")) {
                        mId = meetingD.getmId();
                        mRoomNum = meetingD.getRoomNum();
                        mPassword = meetingD.getmPassword();
                        m_title.setText(meetingD.getmName());
                        userName.setText(meetingD.getUserName());
                        joinCount.setText(meetingD.getJoinCount() + "人报名");
                        mjoinCount = meetingD.getJoinCount();
                        roomNum.setText(meetingD.getRoomNum());
                        if (meetingD.getRemark() != null) {
                            if (meetingD.getRemark().length() > 0) {
                                l_remark.setVisibility(View.VISIBLE);
                                remark.setText(meetingD.getRemark());
                            }
                        }
                        try {
                            mweek.setText(QTime.StringToDate(meetingD.getStartTime()) + " " + QTime.getWeek(QTime.StrToDate(meetingD.getStartTime())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            start_time.setText(QTime.StringToTime(meetingD.getStartTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (meetingD.getIsCreateType() == 1) {
                            b1.setVisibility(View.VISIBLE);
                            more.setVisibility(View.VISIBLE);
                        } else {
                            more.setVisibility(View.GONE);
                            if (meetingD.getIsMakeType() == 1) {
                                t1.setVisibility(View.VISIBLE);
                                fq_meeting.setText("您已报名");
                                fq_meeting.setClickable(false);
                            } else {
                                fq_meeting.setText("我要报名");
                                fq_meeting.setClickable(true);
                            }
                            b2.setVisibility(View.VISIBLE);
                        }
                        String mdateT = "";
                        switch (meetingD.getInformTime()) {
                            case "0":
                                mdateT = "无提醒";
                                break;
                            case "5":
                                mdateT = "5分钟前";
                                break;
                            case "15":
                                mdateT = "15分钟前";
                                break;
                            case "60":
                                mdateT = "1小时前";
                                break;
                            case "1440":
                                mdateT = "1天前";
                                break;
                        }
                        informTime.setText(mdateT);
                        Util.closeLoadingDialog(context);
                    }else if (meetingD.getmStatus().equals("1")){
                        UIHelper.ToastMessageCenter(context, "该会议正在进行中", 200);
                        EventBus.getDefault().post(new Sticky("All_j"));
                        finish();
                    }else if (meetingD.getmStatus().equals("2")){
                        UIHelper.ToastMessageCenter(context, "该会议已结束", 200);
                        EventBus.getDefault().post(new Sticky("All_j"));
                        finish();
                    }
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if (meetingDBean.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(AppointmentDetailsActivity.this, LoginActivity.class);
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
                    case R.id.tq_meeting:
                        initDialog();
                        break;
                    case R.id.more:
                        BottomMeetingWindow();
                        break;
                    case R.id.more_g:
                        Intent intent = new Intent(AppointmentDetailsActivity.this, ParticipantActivity.class);
                        intent.putExtra("mId", mId + "");
                        intent.putExtra("name", "报名人");
                        startActivity(intent);
                        break;
                    case R.id.fq_meeting:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initMeetingHandler.sendEmptyMessage(2);
                            }
                        }).start();
                        break;
                    case R.id.roomNum:
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", roomNum.getText().toString().trim());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        UIHelper.ToastMessageCenter(context, "已拷贝", 200);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        tq_meeting.setOnClickListener(listener);
        more.setOnClickListener(listener);
        more_g.setOnClickListener(listener);
        fq_meeting.setOnClickListener(listener);
        roomNum.setOnClickListener(listener);
    }

    private void fq_meeting() {
        Util.showLoadingDialog(this, "加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + "meet/auth/makeMeet")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .addParams("mId", String.valueOf(mId))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                if (meetingDBean.getSuccess()) {
                    MeetingDBean.MeetingD meetingD = meetingDBean.getData();
                    joinCount.setText((mjoinCount + 1) + "人报名");
                    t1.setVisibility(View.VISIBLE);
                    fq_meeting.setText("报名成功");
                    fq_meeting.setClickable(false);
                    Util.closeLoadingDialog(context);
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if (meetingDBean.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(AppointmentDetailsActivity.this, LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initDialog() {
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_dialog, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(AppointmentDetailsActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        TextView dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) layout.findViewById(R.id.dialog_cancel);
        TextView dialog_sure = (TextView) layout.findViewById(R.id.dialog_sure);
        dialog_title.setText("立即开始会议");
        dialog_cancel.setText("取消");
        dialog_sure.setText("确认");
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (FloatingView.get().getView() == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initMeetingHandler.sendEmptyMessage(1);
                        }
                    }).start();
                }else {
                    UIHelper.ToastMessageCenter(context, "您已有会议进行中", 200);
                }
            }
        });
    }

    private void jion_meeting() {
        ConferenceInfo.getInstance().Init();
        if (conferenceSession.getConferenceProfiles() != null) {
            conferenceSession.getConferenceProfiles().clear();
        }
        DemoHelper.getInstance().setGlobalListeners();
        Util.showLoadingDialog(this, "加载中");
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId + "");
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/beginMeet")
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
                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                if (meetingDBean.getSuccess()) {
                    conferenceRole = EMConferenceManager.EMConferenceRole.Admin;
                    ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Admin);
                    EMRoomConfig roomConfig = new EMRoomConfig();
                    roomConfig.setNickName(SharedPreferencesUtil.read(context, "LtAreaPeople", "NAME"));
                    try {
                        JSONObject extobject = new JSONObject();
                        extobject.putOpt("userId", SharedPreferencesUtil.read(context, "LtAreaPeople", "USERID"));
                        extobject.putOpt("avatar", SharedPreferencesUtil.read(context, "LtAreaPeople", "AVATAR"));
                        extobject.putOpt("company", SharedPreferencesUtil.read(context, "LtAreaPeople", "COMPANY"));
                        extobject.putOpt("position", SharedPreferencesUtil.read(context, "LtAreaPeople", "POSITION"));
                        extobject.putOpt("name", SharedPreferencesUtil.read(context, "LtAreaPeople", "NAME"));
                        String extStr = extobject.toString();
                        extStr = extStr.replace("\\", "");
                        roomConfig.setExt(extStr);
                    } catch (JSONException e) {
                        Util.closeLoadingDialog(context);
                        e.printStackTrace();
                    }
                    EMClient.getInstance().conferenceManager().joinRoom(meetingDBean.getData().getRoomNum(), meetingDBean.getData().getmPassword(), conferenceRole, roomConfig, new EMValueCallBack<EMConference>() {
                        @Override
                        public void onSuccess(EMConference value) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("confrId", value.getConferenceId());
                            map.put("mId", mId);
                            OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/editConfrId")
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
                                        ConferenceInfo.getInstance().setRoomname(mRoomNum);
                                        ConferenceInfo.getInstance().setPassword(mPassword);
                                        ConferenceInfo.getInstance().setCurrentrole(value.getConferenceRole());
                                        ConferenceInfo.getInstance().setConference(value);
                                        conferenceSession.setConfrId(value.getConferenceId());
                                        conferenceSession.setConfrPwd(value.getPassword());
                                        conferenceSession.setSelfUserId(PreferenceManager.getInstance().getCurrentUsername());
                                        conferenceSession.setStreamParam(value);
                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME", mRoomNum);
                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID", conferenceSession.getConfrId());
                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD", conferenceSession.getConfrPwd());
                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingDBean.getData().getChatRoomId());
                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", PreferenceManager.getInstance().getCurrentUsername());
                                        Util.closeLoadingDialog(context);
                                        Intent intent = new Intent(AppointmentDetailsActivity.this, MainMeetingActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Util.closeLoadingDialog(context);
                                        UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                        if (meetingBean.getCode().equals("401")) {
                                            EMClient.getInstance().logout(true);
                                            SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                            UIHelper.OpenActivity(AppointmentDetailsActivity.this, LoginActivity.class);
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(final int error, final String errorMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.closeLoadingDialog(context);
                                }
                            });
                        }
                    });

                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if (meetingDBean.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(AppointmentDetailsActivity.this, LoginActivity.class);
                    }
                }
            }
        });
    }

    private void BottomMeetingWindow() {
        bottomWindow = new BottomMeetingWindow(AppointmentDetailsActivity.this, itemsOnClick);
        //设置弹窗位置
        bottomWindow.showAtLocation(AppointmentDetailsActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.edit_meeting:
                    Intent intent = new Intent(AppointmentDetailsActivity.this, UpdateMeetingActivity.class);
                    intent.putExtra("meeting_code", meeting_code);
                    startActivity(intent);
                    break;
                case R.id.cancel_meeting:
                    cancel_meeting();
                    break;
                default:
                    break;
            }
        }

    };

    private void cancel_meeting() {
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_dialog, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(AppointmentDetailsActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        TextView dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) layout.findViewById(R.id.dialog_cancel);
        TextView dialog_sure = (TextView) layout.findViewById(R.id.dialog_sure);
        dialog_sure.setTextColor(Color.parseColor("#FF3B34"));
        dialog_title.setText("确定取消会议？");
        dialog_cancel.setText("暂不取消");
        dialog_sure.setText("取消会议");
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Util.showLoadingDialog(context, "加载中");
                OkHttpUtils.post().url(URLs.IMGEURL + "meet/auth/delete")
                        .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                        .addParams("mId", String.valueOf(mId))
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Util.closeLoadingDialog(context);
                        UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, String response) {
                        MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                        if (meetingDBean.getSuccess()) {
                            Util.closeLoadingDialog(context);
                            EventBus.getDefault().post(new Sticky("All_j"));
                            UIHelper.ToastMessageCenter(context, "会议取消成功", 200);
                            finish();
                        } else {
                            Util.closeLoadingDialog(context);
                            UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                            if (meetingDBean.getCode().equals("401")) {
                                EMClient.getInstance().logout(true);
                                SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                UIHelper.OpenActivity(AppointmentDetailsActivity.this, LoginActivity.class);
                            }
                        }
                    }
                });
            }
        });
    }

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if (userEvent.msg.equals("update")) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
