package www.pide.com.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.UserBean;
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

import www.pide.com.ui.view.BottomTimeWindow;

/**
 * Created by Administrator on 2020/4/2.
 */
public class CreatMeetingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back, time_p;
    private Button creatMeeting;
    private TextView meeting_code, date;
    private BottomTimeWindow bottomWindow;
    private EditText meeting_title, meeting_demo;
    private ConferenceSession conferenceSession;
    private EMConferenceManager.EMConferenceRole conferenceRole;
    private int position = 0;
    private String meeting_pw = "";
    private String mdate = "30";
    private Intent intent;
    private String mmeeting_code = "";
    private String sign = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creat_meeting_activity);
        //注册订阅者
        EventBus.getDefault().register(this);
        intent = getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        creatMeeting = (Button) findViewById(R.id.creatMeeting);
        meeting_code = findViewById(R.id.meeting_code);
        time_p = findViewById(R.id.time_p);
        meeting_title = findViewById(R.id.meeting_title);
        meeting_demo = findViewById(R.id.meeting_demo);
        date = findViewById(R.id.date);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        Util.showLoadingDialog(this, "加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.CREATEROOMNUM)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                Util.closeLoadingDialog(context);
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    String roomNum = u.getUserD().getRoomNum();
                    if (roomNum != null) {
                        meeting_code.setText(roomNum);
                        meeting_pw = u.getUserD().getmPassword();
                    }
                } else {
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if (u.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(CreatMeetingActivity.this, LoginActivity.class);
                    }
                }
            }
        });
        if (intent.getStringExtra("meeting_code") != null) {
            Util.showLoadingDialog(this, "加载中");
            String mcurrentTime = QTime.QTime();
            sign = QTime.QSign(mcurrentTime);
            Map<String, String> map = new HashMap<>();
            map.put("currentTime", mcurrentTime);
            map.put("sign", sign);
            map.put("roomNum", intent.getStringExtra("meeting_code"));
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
                    Util.closeLoadingDialog(context);
                    MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                    if (meetingDBean.getSuccess()) {
                        MeetingDBean.MeetingD meetingD = meetingDBean.getData();
                        meeting_title.setText(meetingD.getmName());
                        if(meetingD.getRemark()!=null){
                            meeting_demo.setText(meetingD.getRemark());
                        }
                    } else {
                        UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                        if (meetingDBean.getCode().equals("401")) {
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context, "LtAreaPeople");
                            UIHelper.OpenActivity(context, LoginActivity.class);
                        }
                    }
                }
            });
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
                    case R.id.creatMeeting:
                        if (FloatingView.get().getView() == null) {
                            creatMeeting();
                        }else {
                            UIHelper.ToastMessageCenter(context, "您已有会议进行中", 200);
                        }
                        break;
                    case R.id.time_p:
                        BottomTime();
                        break;
                    case R.id.meeting_code:
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", meeting_code.getText().toString().trim());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        UIHelper.ToastMessageCenter(context, "已拷贝", 200);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        creatMeeting.setOnClickListener(listener);
        time_p.setOnClickListener(listener);
        meeting_code.setOnClickListener(listener);
    }

    private void creatMeeting() {
        ConferenceInfo.getInstance().Init();
        if(conferenceSession.getConferenceProfiles() != null){
            conferenceSession.getConferenceProfiles().clear();
        }
        DemoHelper.getInstance().setGlobalListeners();
        Util.showLoadingDialog(this, "加载中");
        String mmeeting_title = meeting_title.getText().toString().trim();
        String mmeeting_code = meeting_code.getText().toString().trim();
        switch (position) {
            case 0:
                mdate = "30";
                break;
            case 1:
                mdate = "60";
                break;
            case 2:
                mdate = "90";
                break;
            case 3:
                mdate = "120";
                break;
            case 4:
                mdate = "180";
                break;
        }
        String mmeeting_demo = meeting_demo.getText().toString().trim();
        if (mmeeting_title.length() > 0 && mmeeting_code.length() > 0) {
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
                e.printStackTrace();
            }
            EMClient.getInstance().conferenceManager().joinRoom(mmeeting_code, meeting_pw, conferenceRole, roomConfig, new EMValueCallBack<EMConference>() {
                @Override
                public void onSuccess(EMConference value) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mName", mmeeting_title);
                    map.put("durationTime", Integer.valueOf(mdate));
                    map.put("mType", "0");
                    map.put("roomNum", mmeeting_code);
                    map.put("remark", mmeeting_demo);
                    map.put("confrId", value.getConferenceId());
                    map.put("mPassword", value.getPassword());
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
                                ConferenceInfo.getInstance().setRoomname(mmeeting_code);
                                ConferenceInfo.getInstance().setPassword(meeting_pw);
                                ConferenceInfo.getInstance().setCurrentrole(value.getConferenceRole());
                                ConferenceInfo.getInstance().setConference(value);
                                conferenceSession.setConfrId(value.getConferenceId());
                                conferenceSession.setConfrPwd(value.getPassword());
                                conferenceSession.setSelfUserId(PreferenceManager.getInstance().getCurrentUsername());
                                conferenceSession.setStreamParam(value);
                                SharedPreferencesUtil.write(context, "LtAreaPeople", "ROOMNAME", mmeeting_code);
                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFERENCEID", conferenceSession.getConfrId());
                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CONFRPWD", conferenceSession.getConfrPwd());
                                SharedPreferencesUtil.write(context, "LtAreaPeople", "CHATROOMID", meetingBean.getData().getChatRoomId());
                                Util.closeLoadingDialog(context);
                                Intent intent = new Intent(CreatMeetingActivity.this, MainMeetingActivity.class);
                                intent.putExtra("mId", meetingBean.getData().getmId() + "");
                                startActivity(intent);
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
            if (mmeeting_title.length() == 0) {
                UIHelper.ToastMessageCenter(context, "请输入会议主题", 200);
            } else if (mmeeting_code.length() == 0) {
                UIHelper.ToastMessageCenter(context, "房间号不能为空", 200);
            }
        }
    }

    private void BottomTime() {
        List<String> listData = new ArrayList<>();
        listData.add("30分钟");
        listData.add("60分钟");
        listData.add("1小时30分钟");
        listData.add("2小时");
        listData.add("3小时");
        bottomWindow = new BottomTimeWindow(CreatMeetingActivity.this, listData, itemsOnClick, "会议时长", position);
        //设置弹窗位置
        bottomWindow.showAtLocation(CreatMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
        }

    };

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if (userEvent.msg.equals("0")) {
            date.setText(userEvent.obj);
            position = userEvent.position;
        }
    }
}
