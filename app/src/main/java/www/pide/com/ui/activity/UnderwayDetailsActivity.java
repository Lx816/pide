package www.pide.com.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * Created by Administrator on 2020/4/2.
 */
public class UnderwayDetailsActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private TextView more, m_title, userName, joinCount, roomNum, mweek, start_time, remark;
    private Button jion_meeting;
    private Intent intent;
    private String meeting_code = "";
    private String sign = "";
    private ConferenceSession conferenceSession;
    private EMConferenceManager.EMConferenceRole conferenceRole;
    private int mid;
    private LinearLayout l_remark;
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
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.underway_details_activity);
        intent = getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        more = (TextView) findViewById(R.id.more);
        m_title = (TextView) findViewById(R.id.m_title);
        userName = (TextView) findViewById(R.id.userName);
        joinCount = (TextView) findViewById(R.id.joinCount);
        roomNum = (TextView) findViewById(R.id.roomNum);
        mweek = (TextView) findViewById(R.id.mweek);
        start_time = (TextView) findViewById(R.id.start_time);
        jion_meeting = (Button) findViewById(R.id.jion_meeting);
        meeting_code = intent.getStringExtra("meeting_code");
        remark = findViewById(R.id.remark);
        l_remark = findViewById(R.id.l_remark);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMeetingHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initData() {
        Util.showLoadingDialog(this,"加载中");
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
                    if(!meetingD.getmStatus().equals("2")){
                        m_title.setText(meetingD.getmName());
                        userName.setText(meetingD.getUserName());
                        joinCount.setText(meetingD.getJoinCount() + "人参加");
                        roomNum.setText(meetingD.getRoomNum());
                        if (meetingD.getRemark() != null) {
                            if (meetingD.getRemark().length() > 0) {
                                l_remark.setVisibility(View.VISIBLE);
                                remark.setText(meetingD.getRemark());
                            }
                        }
                        mid = meetingD.getmId();
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
                        Util.closeLoadingDialog(context);
                    }else {
                        Util.closeLoadingDialog(context);
                        UIHelper.ToastMessageCenter(context, "会议已结束", 200);
                        EventBus.getDefault().post(new Sticky("XS"));
                        finish();
                    }
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if(meetingDBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
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
                    case R.id.more:
                        Intent intent = new Intent(UnderwayDetailsActivity.this, ParticipantActivity.class);
                        intent.putExtra("mId", mid + "");
                        intent.putExtra("name", "参与人");
                        startActivity(intent);
                        break;
                    case R.id.jion_meeting:
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
        more.setOnClickListener(listener);
        jion_meeting.setOnClickListener(listener);
        roomNum.setOnClickListener(listener);
    }
    private void jion_meeting() {
        ConferenceInfo.getInstance().Init();
        if(conferenceSession.getConferenceProfiles() != null){
            conferenceSession.getConferenceProfiles().clear();
        }
        DemoHelper.getInstance().setGlobalListeners();
        Util.showLoadingDialog(this,"加载中");
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", meeting_code);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/joinMeetByRoomNum")
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
                    if (meetingDBean.getData().getRoleType().equals("0")) {
                        conferenceRole = EMConferenceManager.EMConferenceRole.Admin;
                        ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Admin);
                    } else {
                        conferenceRole = EMConferenceManager.EMConferenceRole.Talker;
                        ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Talker);
                    }
                    if (meetingD.getConfrId() != null) {
                        EMClient.getInstance().conferenceManager().getConferenceInfo(meetingD.getConfrId(), meetingD.getmPassword(), new EMValueCallBack<EMConference>() {
                            @Override
                            public void onSuccess(EMConference value) {
                                HashSet<String> hs = new HashSet<String>(Arrays.asList(value.getTalkers()));
                                List<String> mTalkers = new ArrayList<>(hs);
                                if (mTalkers.size()>19) {
                                    Util.closeLoadingDialog(context);
                                    UIHelper.ToastMessageCenter(context, "房间已满", 200);
                                } else {
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
                                        extStr = extStr.replace("\\","");
                                        roomConfig.setExt(extStr);
                                    }catch (JSONException e) {
                                        Util.closeLoadingDialog(context);
                                        e.printStackTrace();
                                    }
                                    EMClient.getInstance().conferenceManager().joinRoom(meetingDBean.getData().getRoomNum(), meetingDBean.getData().getmPassword(), conferenceRole,roomConfig, new EMValueCallBack<EMConference>() {
                                        @Override
                                        public void onSuccess(EMConference value) {
                                            Log.d("3265346dsgg",JsonUtil.toJson(value));
                                            String mcurrentTime = QTime.QTime();
                                            sign = QTime.QSign(mcurrentTime);
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("currentTime", mcurrentTime);
                                            map.put("sign", sign);
                                            map.put("mId", meetingDBean.getData().getmId());
                                            map.put("roleType", meetingDBean.getData().getRoleType());
                                            OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/addJoinMeetRecord")
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
                                                        ConferenceInfo.getInstance().setRoomname(meetingDBean.getData().getRoomNum());
                                                        ConferenceInfo.getInstance().setPassword(meetingDBean.getData().getmPassword());
                                                        ConferenceInfo.getInstance().setCurrentrole(value.getConferenceRole());
                                                        ConferenceInfo.getInstance().setConference(value);
                                                        conferenceSession.setConfrId(value.getConferenceId());
                                                        conferenceSession.setConfrPwd(value.getPassword());
                                                        conferenceSession.setSelfUserId(PreferenceManager.getInstance().getCurrentUsername());
                                                        conferenceSession.setStreamParam(value);
                                                        Util.closeLoadingDialog(context);
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME",meetingDBean.getData().getRoomNum());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID",conferenceSession.getConfrId());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD",conferenceSession.getConfrPwd());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingD.getChatRoomId());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                        Intent intent = new Intent(UnderwayDetailsActivity.this, MainMeetingActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Util.closeLoadingDialog(context);
                                                        UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                                        if(meetingBean.getCode().equals("401")){
                                                            EMClient.getInstance().logout(true);
                                                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                            UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
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

                                }
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
                            extStr = extStr.replace("\\","");
                            roomConfig.setExt(extStr);
                        }catch (JSONException e) {
                            Util.closeLoadingDialog(context);
                            e.printStackTrace();
                        }
                        EMClient.getInstance().conferenceManager().joinRoom(meetingDBean.getData().getRoomNum(), meetingDBean.getData().getmPassword(), conferenceRole,roomConfig, new EMValueCallBack<EMConference>() {
                            @Override
                            public void onSuccess(EMConference value) {
                                String mcurrentTime = QTime.QTime();
                                sign = QTime.QSign(mcurrentTime);
                                Map<String, Object> map = new HashMap<>();
                                map.put("currentTime", mcurrentTime);
                                map.put("sign", sign);
                                map.put("mId", meetingDBean.getData().getmId());
                                map.put("roleType", meetingDBean.getData().getRoleType());
                                OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/addJoinMeetRecord")
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
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("confrId", value.getConferenceId());
                                            map.put("mId", meetingDBean.getData().getmId());
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
                                                        ConferenceInfo.getInstance().setRoomname(meetingDBean.getData().getRoomNum());
                                                        ConferenceInfo.getInstance().setPassword(meetingDBean.getData().getmPassword());
                                                        ConferenceInfo.getInstance().setCurrentrole(value.getConferenceRole());
                                                        ConferenceInfo.getInstance().setConference(value);
                                                        conferenceSession.setConfrId(value.getConferenceId());
                                                        conferenceSession.setConfrPwd(value.getPassword());
                                                        conferenceSession.setSelfUserId(PreferenceManager.getInstance().getCurrentUsername());
                                                        conferenceSession.setStreamParam(value);
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME",meetingDBean.getData().getRoomNum());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID",conferenceSession.getConfrId());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD",conferenceSession.getConfrPwd());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingD.getChatRoomId());
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                        Util.closeLoadingDialog(context);
                                                        Intent intent = new Intent(UnderwayDetailsActivity.this, MainMeetingActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Util.closeLoadingDialog(context);
                                                        UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                                        if(meetingBean.getCode().equals("401")){
                                                            EMClient.getInstance().logout(true);
                                                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                            UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Util.closeLoadingDialog(context);
                                            UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                            if(meetingBean.getCode().equals("401")){
                                                EMClient.getInstance().logout(true);
                                                SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
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
                    }
                } else {
                    if (meetingDBean.getCode().equals("")) {
                        String mcurrentTime = QTime.QTime();
                        sign = QTime.QSign(mcurrentTime);
                        Map<String, String> map = new HashMap<>();
                        map.put("currentTime", mcurrentTime);
                        map.put("sign", sign);
                        map.put("mId", mid + "");
                        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/endMeet")
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
                                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                                if (meetingDBean.getSuccess()) {
                                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                    EventBus.getDefault().post(new Sticky("XS"));
                                    finish();
                                } else {
                                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                    if(meetingDBean.getCode().equals("401")){
                                        EMClient.getInstance().logout(true);
                                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                        UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
                                    }
                                }
                            }
                        });
                    } else {
                        Util.closeLoadingDialog(context);
                        UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                        if(meetingDBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(UnderwayDetailsActivity.this,LoginActivity.class);
                        }else if(meetingDBean.getCode().equals("30022")){
                            EventBus.getDefault().post(new Sticky("XS"));
                            finish();
                        }else if(meetingDBean.getCode().equals("30014")){
                            EventBus.getDefault().post(new Sticky("XS"));
                            finish();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
