package www.pide.com.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMRoomConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.Sticky;
import www.pide.com.hxsdk.ConferenceInfo;
import www.pide.com.hxsdk.ConferenceSession;
import www.pide.com.hxsdk.DemoHelper;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.ui.activity.AppointmentDetailsActivity;
import www.pide.com.ui.activity.LoginActivity;
import www.pide.com.ui.activity.MainMeetingActivity;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import www.pide.com.R;

public class SendMassageDialog extends Dialog {
    private String mname;
    private Activity context;
    private String msgType;
    private String roomNum;
    private ConferenceSession conferenceSession;
    private String sign,mid;
    private EMConferenceManager.EMConferenceRole conferenceRole;
    public SendMassageDialog(Activity mcontext, String t1, String msgType, String roomNum,String mid,int dialogTheme) {
        super(mcontext,dialogTheme);
        context=mcontext;
        mname=t1;
        this.msgType=msgType;
        this.roomNum=roomNum;
        this.mid=mid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        setContentView(R.layout.center_send_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView name=findViewById(R.id.name);
        name.setText(mname);
        RelativeLayout r1=findViewById(R.id.r1);
        RelativeLayout r2=findViewById(R.id.r2);
        if(!msgType.equals("0")){
            r1.setVisibility(View.VISIBLE);
        }
        Button cancel=findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button sure=findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent=new Intent();
                switch (msgType){
                    case "1":
                    case "4":
                        intent=new Intent(context, AppointmentDetailsActivity.class);
                        intent.putExtra("meeting_code",roomNum);
                        context.startActivity(intent);
                        break;
                    case "2":
                    case "3":
                        jion_meeting();
                        break;
                }
            }
        });
    }
    private void jion_meeting() {
        ConferenceInfo.getInstance().Init();
        if(conferenceSession.getConferenceProfiles() != null){
            conferenceSession.getConferenceProfiles().clear();
        }
        DemoHelper.getInstance().setGlobalListeners();
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", roomNum);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/joinMeetByRoomNum")
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
                                if (value.getMemberNum() >= 20) {
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
                                                        if (meetingDBean.getData().getRoleType().equals("0")) {
                                                            EMClient.getInstance().conferenceManager().getConferenceInfo(meetingD.getConfrId(), meetingD.getmPassword(), new EMValueCallBack<EMConference>() {
                                                                @Override
                                                                public void onSuccess(EMConference value) {
                                                                    String[] alladmin = value.getAdmins();
                                                                    for (int m = 0; m < alladmin.length; m++) {
                                                                        if (!alladmin[m].equals(URLs.APPKEY+"_" + PreferenceManager.getInstance().getCurrentUsername())) {
                                                                            conferenceRole = EMConferenceManager.EMConferenceRole.Talker;
                                                                            ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Talker);
                                                                            EMConferenceMember member = new EMConferenceMember(alladmin[m], "", "");
                                                                            EMClient.getInstance().conferenceManager().grantRole(meetingD.getConfrId(), member, conferenceRole, new EMValueCallBack<String>() {
                                                                                @Override
                                                                                public void onSuccess(String value) {
                                                                                    SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME",meetingDBean.getData().getRoomNum());
                                                                                    SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID",conferenceSession.getConfrId());
                                                                                    SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD", conferenceSession.getConfrPwd());
                                                                                    SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                                                    Intent intent = new Intent(context, MainMeetingActivity.class);
                                                                                    context.startActivity(intent);
                                                                                }

                                                                                @Override
                                                                                public void onError(int error, String errorMsg) {
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(final int error, final String errorMsg) {
                                                                }
                                                            });
                                                        }else {
                                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME",meetingDBean.getData().getRoomNum());
                                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID",conferenceSession.getConfrId());
                                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD",conferenceSession.getConfrPwd());
                                                            SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                            Intent intent = new Intent(context, MainMeetingActivity.class);
                                                            context.startActivity(intent);
                                                        }
                                                    } else {
                                                        UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                                        if(meetingBean.getCode().equals("401")){
                                                            EMClient.getInstance().logout(true);
                                                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                            UIHelper.OpenActivity(context, LoginActivity.class);
                                                        }
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(final int error, final String errorMsg) {
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onError(final int error, final String errorMsg) {
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
                                                        SharedPreferencesUtil.write(context, "LtAreaPeople", "ROLEID", meetingDBean.getData().getUserId());
                                                        Intent intent = new Intent(context, MainMeetingActivity.class);
                                                        intent.putExtra("mId", meetingDBean.getData().getmId() + "");
                                                        context.startActivity(intent);
                                                    } else {
                                                        UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                                        if(meetingBean.getCode().equals("401")){
                                                            EMClient.getInstance().logout(true);
                                                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                            UIHelper.OpenActivity(context,LoginActivity.class);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            UIHelper.ToastMessageCenter(context, meetingBean.getMsg(), 200);
                                            if(meetingBean.getCode().equals("401")){
                                                EMClient.getInstance().logout(true);
                                                SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                                UIHelper.OpenActivity(context,LoginActivity.class);
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final int error, final String errorMsg) {
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
                                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, String response) {
                                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                                if (meetingDBean.getSuccess()) {
                                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                    EventBus.getDefault().post(new Sticky("XS"));
                                    context.finish();
                                } else {
                                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                    if(meetingDBean.getCode().equals("401")){
                                        EMClient.getInstance().logout(true);
                                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                        UIHelper.OpenActivity(context, LoginActivity.class);
                                    }
                                }
                            }
                        });
                    } else {
                        UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                        if(meetingDBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(context,LoginActivity.class);
                        }
                    }
                }
            }
        });
    }
}
