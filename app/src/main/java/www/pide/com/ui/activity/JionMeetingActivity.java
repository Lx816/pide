package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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

import org.json.JSONException;
import org.json.JSONObject;

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

import www.pide.com.ui.view.MeetingJDialog;

/**
 * Created by Administrator on 2020/4/2.
 */
public class JionMeetingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private Button sure;
    private EditText meeting_code;
    private String mmeeting_code = "";
    private String sign = "";
    private ConferenceSession conferenceSession;
    private EMConferenceManager.EMConferenceRole conferenceRole;
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
                    if (!isFinishing()) {
                        initData();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jion_meeting);
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        sure = (Button) findViewById(R.id.sure);
        meeting_code = findViewById(R.id.meeting_code);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        finish();
                        break;
                    case R.id.sure:
                        mmeeting_code = meeting_code.getText().toString().trim();
                        if (mmeeting_code.length() > 0) {
                            if (FloatingView.get().getView() == null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initMeetingHandler.sendEmptyMessage(0);
                                    }
                                }).start();
                            } else {
                                UIHelper.ToastMessageCenter(context, "您已有会议进行中", 200);
                            }
                        } else {
                            UIHelper.ToastMessageCenter(context, "请输入会议号", 200);
                        }
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        sure.setOnClickListener(listener);
    }

    private void initData() {
        Util.showLoadingDialog(this, "加载中");
        ConferenceInfo.getInstance().Init();
        if (conferenceSession.getConferenceProfiles() != null) {
            conferenceSession.getConferenceProfiles().clear();
        }
        DemoHelper.getInstance().setGlobalListeners();
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", mmeeting_code);
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
                Log.d("asfdsg234235df", JsonUtil.toJson(response));
                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                if (meetingDBean.getSuccess()) {
                    MeetingDBean.MeetingD meetingD = meetingDBean.getData();
                    if (meetingD.getConfrId() != null) {
                        EMClient.getInstance().conferenceManager().getConferenceInfo(meetingD.getConfrId(), meetingD.getmPassword(), new EMValueCallBack<EMConference>() {
                            @Override
                            public void onSuccess(EMConference value) {
                                HashSet<String> hs = new HashSet<String>(Arrays.asList(value.getTalkers()));
                                List<String> mTalkers = new ArrayList<>(hs);
                                if (mTalkers.size()>19) {
                                    Util.closeLoadingDialog(context);
                                    new MeetingJDialog(JionMeetingActivity.this, "会议房间已满").show();
                                } else {
                                    jion_meeting(meetingDBean);
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
                        jion_meeting(meetingDBean);
                    }
                } else {
                    Util.closeLoadingDialog(context);
                    new MeetingJDialog(JionMeetingActivity.this, "会议已过期或还未开始").show();
                }
            }
        });
    }

    private void jion_meeting(MeetingDBean meetingDBean) {
        MeetingDBean.MeetingD meetingD = meetingDBean.getData();
        if (meetingDBean.getData().getRoleType().equals("0")) {
            conferenceRole = EMConferenceManager.EMConferenceRole.Admin;
            ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Admin);
        } else {
            conferenceRole = EMConferenceManager.EMConferenceRole.Talker;
            ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Talker);
        }
        if (meetingD.getConfrId() != null) {
            Log.d("asfsdgdhg", meetingD.getConfrId() + "hhh");
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
                            extStr = extStr.replace("\\", "");
                            roomConfig.setExt(extStr);
                        } catch (JSONException e) {
                            Util.closeLoadingDialog(context);
                            e.printStackTrace();
                        }
                        EMClient.getInstance().conferenceManager().joinRoom(meetingDBean.getData().getRoomNum(), meetingDBean.getData().getmPassword(), conferenceRole, roomConfig, new EMValueCallBack<EMConference>() {
                            @Override
                            public void onSuccess(EMConference value) {
                                Log.d("asfsdgdhg", JsonUtil.toJson(value));
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
                                        Log.d("sfds5676fgfdh", JsonUtil.toJson(response));
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
                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME", meetingDBean.getData().getRoomNum());
                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID", conferenceSession.getConfrId());
                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD", conferenceSession.getConfrPwd());
                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingD.getChatRoomId());
                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                            Intent intent = new Intent(JionMeetingActivity.this, MainMeetingActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Util.closeLoadingDialog(context);
                                            UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                            if (meetingBean.getCode().equals("401")) {
                                                EMClient.getInstance().logout(true);
                                                SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                                UIHelper.OpenActivity(JionMeetingActivity.this, LoginActivity.class);
                                            }

                                        }
                                    }
                                    });
                                }

                                @Override
                                public void onError ( final int error, final String errorMsg){
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
                    public void onError ( final int error, final String errorMsg){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Util.closeLoadingDialog(context);
                                Log.d("asfsdgdhg", errorMsg + "3");
                            }
                        });
                    }
                });
            } else{
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
                                            Log.d("asfsdgdfhfjuhtyui",JsonUtil.toJson(meetingD));
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
                                                SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME", meetingDBean.getData().getRoomNum());
                                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID", conferenceSession.getConfrId());
                                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD", conferenceSession.getConfrPwd());
                                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingD.getChatRoomId());
                                                SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                Util.closeLoadingDialog(context);
                                                Intent intent = new Intent(JionMeetingActivity.this, MainMeetingActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Util.closeLoadingDialog(context);
                                                UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                                if (meetingBean.getCode().equals("401")) {
                                                    EMClient.getInstance().logout(true);
                                                    SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                                    UIHelper.OpenActivity(JionMeetingActivity.this, LoginActivity.class);
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Util.closeLoadingDialog(context);
                                    UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                    if (meetingBean.getCode().equals("401")) {
                                        EMClient.getInstance().logout(true);
                                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                                        UIHelper.OpenActivity(JionMeetingActivity.this, LoginActivity.class);
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
                                Log.d("asfsdgdhg", errorMsg + "///////");
                            }
                        });
                    }
                });
            }
        }
    }
