package www.pide.com.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMStreamParam;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.media.EMCallSurfaceView;
import com.imuxuan.floatingview.FloatingMagnetView;
import com.imuxuan.floatingview.FloatingView;
import com.imuxuan.floatingview.MagnetViewListener;
import com.superrtc.sdk.VideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity1;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.SendBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean1;
import www.pide.com.bean.UserBean2;
import www.pide.com.bean.UserData;
import www.pide.com.bean.order1;
import www.pide.com.bean.order2;
import www.pide.com.chart.easeui.easeui.domain.EaseEmojicon;
import www.pide.com.chart.easeui.easeui.utils.EaseCommonUtils;
import www.pide.com.chart.easeui.easeui.utils.EaseSmileUtils;
import www.pide.com.chart.easeui.easeui.widget.EaseChatInputMenu1;
import www.pide.com.hxsdk.ConferenceInfo;
import www.pide.com.hxsdk.ConferenceSession;
import www.pide.com.hxsdk.DemoHelper;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.hxsdk.runtimepermissions.PermissionsManager;
import www.pide.com.hxsdk.runtimepermissions.PermissionsResultAction;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;
import www.pide.com.R;
import www.pide.com.ui.view.BottomPhoneWindow;
import www.pide.com.ui.view.CircleRelativeLayout;
import www.pide.com.ui.view.CircleRelativeLayout1;
import www.pide.com.ui.view.MeetingDialog;
import www.pide.com.ui.view.MeetingDialogT;
import www.pide.com.ui.view.MeetingZDialog;
import www.pide.com.ui.view.ProlongMeetDialog;
import www.pide.com.ui.view.XCRoundImageView;

/**
 * Created by Administrator on 2020/4/2.
 */
public class MainMeetingActivity extends BaseActivity1 implements EMConferenceListener {
    private Context context;
    private ImageView back, finish, meeting_pelpeo;
    private CircleRelativeLayout c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20;
    private ImageView statement_b;
    private RelativeLayout down_b;
    private CircleRelativeLayout1 v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20;
    private TextView t_v;
    private EditText edit_t, edit_t1;
    private ImageView text_img1, text_img2;
    private RelativeLayout r_b1, r_b2, r_b0, r_b3, send_r, send_r1;
    private int r_height = 1;
    private ImageView send;
    private TextView send_t;
    private String regEx = "[\u4e00-\u9fa5]"; // 中文范围
    private RelativeLayout mMainLy;
    private RelativeLayout mitem;
    private RelativeLayout text_img;
    private RelativeLayout r1;
    private ActionBar actionBar;
    private RelativeLayout l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12, l13, l14, l15, l16, l17, l18, l19, l20;
    private BottomPhoneWindow bottomWindow;
    private RelativeLayout checked_fz, volide_l;
    private LinearLayout checked_fz_item;
    private CheckBox img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16, img17, img18, img19, img20;
    private Button cancel_b, sure_b;
    private Handler mHandler = null;
    private EMConferenceListener conferenceListener;
    private EMConference conference;
    private ConferenceSession conferenceSession;
    private String sign = "";
    private XCRoundImageView XCRoundImageView0, XCRoundImageView1, XCRoundImageView2, XCRoundImageView3, XCRoundImageView4, XCRoundImageView5, XCRoundImageView6, XCRoundImageView7, XCRoundImageView8, XCRoundImageView9, XCRoundImageView10, XCRoundImageView11, XCRoundImageView12, XCRoundImageView13, XCRoundImageView14, XCRoundImageView15, XCRoundImageView16, XCRoundImageView17, XCRoundImageView18, XCRoundImageView19;
    private ImageView f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19;
    private TextView t_name0, t_name1, t_name2, t_name3, t_name4, t_name5, t_name6, t_name7, t_name8, t_name9, t_name10, t_name11, t_name12, t_name13, t_name14, t_name15, t_name16, t_name17, t_name18, t_name19;
    private String mId;
    private String userId = "";
    private TextView roomNum;
    private TextView durationTime;
    private TextView durationTime0;
    private User mUser;
    private String[] mTalkersz;
    private String mTalker;
    private String mConfrId;
    private RelativeLayout zr;
    private TimerTask timerTask = null;
    private Timer timer1;
    private String startTime;
    private List<String> mTalkers = new ArrayList<>();
    private EMConferenceManager.EMConferenceRole conferenceRole;
    private AudioManager audioManager;
    private List<String> list;
    private XCRoundImageView mavatar;
    private CircleRelativeLayout c_t;
    private TextView mt_text;
    private ImageView add_img;
    private EaseChatInputMenu1 inputMenu;
    private Boolean isDown = false;
    private TextView t_time;
    private Timer mtimer_t;
    private Boolean isSmall = false;
    private List<EMConferenceStream> streamList=new ArrayList<>();
    private Boolean isFirst = false;
    // 声明一个Handler对象
    private Handler handler = new Handler();
    private String roleid = "";
    private String endTime = "";
    private String mdurationTime = "";
    private MyHandler initMeetingHandler = new MyHandler(this);
    private List<UserData> userData = new ArrayList<>();
    private UserData mimgUser=new UserData();
    private ImageView dj_time;
    private LinearLayout large_preview;

    private static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    private int v = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(this, R.color.black);
        setLightStatusBar(this, false, false);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_meeting_activity);
        mMainLy = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.main_meeting_activity, null);
        setContentView(mMainLy);
        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_custom);//设置自定义的布局：actionbar_custom
        }
        requestPermissions();
        ConferenceInfo.Initflag = false;
        initMeetingHandler = new MyHandler(this) {
            public void handleMessage(Message msg) {
                final Activity activity = mWeakReference.get();
                if (activity != null) {
                    switch (msg.what) {
                        case 0:
                            initMeeting();
                            break;
                        case 1:
                            initData(list);
                            break;
                        case 2:
                            int cnt = 0;
                            long time = System.currentTimeMillis();
                            try {
                                long mstartTime = Long.valueOf(QTime.dateToStamp(startTime));
                                long mendTime = Long.valueOf(QTime.dateToStamp(endTime));
                                cnt = QTime.testPassedTime1(time, mstartTime);
                                int hytime = QTime.testPassedTime2(mendTime, time);
                                if (hytime == 10 * 60) {
                                    if (roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                                        try {
                                            new ProlongMeetDialog(MainMeetingActivity.this, R.style.DialogTheme).show();
                                        }catch (Exception e){}
                                    } else {
                                        UIHelper.ToastMessageCenter(context, "会议还有十分钟就结束了", 1500);
                                    }
                                }
                                Util.closeLoadingDialog(context);
                                if (hytime == 0) {
                                    UIHelper.ToastMessageCenter(context, "会议已结束", 1500);
                                    if (roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                                        destroyMeeting();
                                    }
                                }
                            } catch (ParseException e) {
                                Util.closeLoadingDialog(context);
                                e.printStackTrace();
                            }
                            durationTime0.setText(getStringTime(cnt++));
                            durationTime.setText(":" + getStringTime1(cnt++));
                            break;
                        case 3:
                            Util.closeLoadingDialog(context);
                            break;
                        case 4:
                            UserData jionuser = JsonUtil.fromJson(JsonUtil.toJson(msg.obj), UserData.class);
                            EventBus.getDefault().post(new Sticky("JION"));
                            if (!isSmall) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!userData.contains(jionuser)) {
                                            userData.add(jionuser);
                                            HashSet<UserData> hs = new HashSet<UserData>(userData);
                                            userData = new ArrayList<>(userData);
                                            Collections.sort(userData, new order1());
                                            mTalkers.add(URLs.APPKEY+"_"+jionuser.getUserId());
                                            list.add(jionuser.getUserId());
                                            Collections.sort(mTalkers, new order2());
                                            Collections.sort(list, new order2());
                                            initMeetingHandler.sendEmptyMessage(14);
                                            Looper.prepare();
                                            UIHelper.ToastMessageCenter(context, jionuser.getName() + "-加入会议", 200);
                                            Looper.loop();
                                        }
                                    }
                                }).start();
                            }
                            break;
                        case 5:
                            UserData exiteduser = JsonUtil.fromJson(JsonUtil.toJson(msg.obj), UserData.class);
                            EMClient.getInstance().conferenceManager().getConferenceInfo(SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFERENCEID"), SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFRPWD"), new EMValueCallBack<EMConference>() {
                                @Override
                                public void onSuccess(EMConference value) {
                                    if (Collections.frequency(Arrays.asList(value.getTalkers()), (URLs.APPKEY + "_" + exiteduser.getUserId())) < 1) {
                                        EventBus.getDefault().post(new Sticky("JION"));
                                        if (!isSmall) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (userData.contains(exiteduser)) {
                                                        userData.remove(exiteduser);
                                                        HashSet<UserData> hs = new HashSet<UserData>(userData);
                                                        userData = new ArrayList<>(userData);
                                                        Collections.sort(userData, new order1());
                                                        mTalkers.remove(URLs.APPKEY+"_"+exiteduser.getUserId());
                                                        list.remove(exiteduser.getUserId());
                                                        Collections.sort(mTalkers, new order2());
                                                        Collections.sort(list, new order2());
                                                        initMeetingHandler.sendEmptyMessage(14);
                                                        Looper.prepare();
                                                        UIHelper.ToastMessageCenter(context, exiteduser.getName() + "-退出会议", 200);
                                                        Looper.loop();
                                                    }
                                                }
                                            }).start();
                                        }
                                    }
                                }

                                @Override
                                public void onError(int error, String errorMsg) {
                                }
                            });
                            break;
                        case 6:
                            EMConferenceStream stream = JsonUtil.fromJson(JsonUtil.toJson(msg.obj), EMConferenceStream.class);
                            EMCallSurfaceView memberView = new EMCallSurfaceView(DemoHelper.getInstance().getContext());
                            memberView.setZOrderMediaOverlay(true);
                            memberView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

                            EMClient.getInstance().conferenceManager().subscribe(stream, memberView, new EMValueCallBack<String>() {
                                @Override
                                public void onSuccess(String value) {
                                }

                                @Override
                                public void onError(int error, String errorMsg) {

                                }
                            });
                            if (mTalkers.size() > 0) {
                                int retval = mTalkers.indexOf(stream.getMemberName());
                                if (retval != -1) {
                                    mMainLy.findViewById(context.getResources().getIdentifier("v" + (retval + 1), "id", context.getPackageName())).setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case 7:
                            EMConferenceStream mstream = JsonUtil.fromJson(JsonUtil.toJson(msg.obj), EMConferenceStream.class);
                            int retval = mTalkers.indexOf(mstream.getMemberName());
                            if (retval != -1) {
                                mMainLy.findViewById(context.getResources().getIdentifier("v" + (retval + 1), "id", context.getPackageName())).setVisibility(View.GONE);
                            }
                            break;
                        case 8:
                            List<EMMessage> messages = (List<EMMessage>) msg.obj;
                            for (EMMessage message : messages) {
                                EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
                                SendBean sendBean = new SendBean();
                                final String action = body.action();//获取自定义action
                                sendBean = JsonUtil.fromJson(action, SendBean.class);
                                if (sendBean.getMsgType().equals("5") && SharedPreferencesUtil.read(context, "LtAreaPeople", "ROOMNAME").equals(sendBean.getRoomNum())) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            initMeetingHandler.sendEmptyMessage(0);
                                        }
                                    }).start();
                                } else if (sendBean.getMsgType().equals("6") && SharedPreferencesUtil.read(context, "LtAreaPeople", "ROOMNAME").equals(sendBean.getRoomNum()) && sendBean.getUserId().equals(PreferenceManager.getInstance().getCurrentUsername())) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            UIHelper.ToastMessageCenter(context, "您已被移出房间", 1500);
                                            Looper.loop();
                                            finish();
                                        }
                                    }).start();
                                } else if (sendBean.getMsgType().equals("7") && SharedPreferencesUtil.read(context, "LtAreaPeople", "ROOMNAME").equals(sendBean.getRoomNum())) {
                                    endTime = sendBean.getEndTime();
                                    SendBean finalSendBean = sendBean;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            UIHelper.ToastMessageCenter(context, finalSendBean.getMsg(), 1500);
                                            Looper.loop();
                                            initMeetingHandler.sendEmptyMessage(13);
                                        }
                                    }).start();
                                }
                            }
                            break;
                        case 9:
                            if (!roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                                outMeeting("会议已结束");
                                if (FloatingView.get().getView() != null) {
                                    FloatingView.get().remove();
                                }
                                EventBus.getDefault().post(new Sticky("CHATEND"));
                            }
                            break;
                        case 10:
                            List<EMMessage> mmessages = (List<EMMessage>) msg.obj;
                            for (EMMessage message : mmessages) {
                                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                                if (body.getMessage().length() > 0) {
                                    queryUser(message.getFrom(), body.getMessage());
                                }
                            }
                            EventBus.getDefault().post(new Sticky("SEND"));
                            break;
                        case 11:
                            if (!isSmall) {
                                // 停止监听说话者
                                EMClient.getInstance().conferenceManager().stopMonitorSpeaker();
                                EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
                                //注销注册
                                EventBus.getDefault().unregister(this);
                                if (timerTask != null && !timerTask.cancel()) {
                                    timerTask.cancel();
                                    timer1.cancel();
                                }
                                if (FloatingView.get().getView() != null) {
                                    FloatingView.get().remove();
                                }
                            }
                            UIHelper.ToastMessageCenter(context, msg.obj.toString(), 200);
                            EventBus.getDefault().post(new Sticky("XS"));
                            finish();
                            break;
                        case 12:
                            prolongMeet();
                            break;
                        case 13:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initMeetingHandler.sendEmptyMessage(2);
                                }
                            }).start();
                            break;
                        case 14:
                            initUser();
                            break;

                    }
                }
            }

        };
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        //注册订阅者
        EventBus.getDefault().register(this);
        mHandler = new Handler();
        timer1 = new Timer();
        mtimer_t = new Timer();
        conference = ConferenceInfo.getInstance().getConference();
        conferenceListener = this;
        isFirst = true;
        EMClient.getInstance().conferenceManager().addConferenceListener(conferenceListener);
        onChatRoomViewCreation();
        initView();
        initMe();
        initOnclick();
    }

    private void initUser() {
        try {
            initC();
        } catch (Exception e) {
        }
        for (int i = 0; i < userData.size(); i++) {
            try {
                if (userData.get(i).getAvatar() != null) {
                    Glide.with(context).load(userData.get(i).getAvatar()).into(initV(i));
                } else {
                    Glide.with(context).load(R.mipmap.meeting_no_p).into(initV(i));
                }
            } catch (Exception e) {

            }
            initTextView(i).setText(userData.get(i).getName());
            if (roleid.equals(userData.get(i).getUserId() + "")) {
                initImageView(i).setVisibility(View.VISIBLE);
            }
            if (PreferenceManager.getInstance().getCurrentUsername().equals(userData.get(i).getUserId() + "")) {
                initCircleRelativeLayout(i + 1).setColor(context.getResources().getColor(R.color.zb));
                if (isDown) {
                    mMainLy.findViewById(context.getResources().getIdentifier("v" + (i + 1), "id", context.getPackageName())).setVisibility(View.VISIBLE);
                }
            }
        }
        if (!ConferenceInfo.Initflag) {
            streamList = new ArrayList<>();
            streamList = ConferenceInfo.getInstance().getConferenceStreamList();
            for (int n = 0; n < streamList.size(); n++) {
                onStreamAdded(streamList.get(n));
            }
            ConferenceInfo.Initflag=true;
        }
    }

    private void prolongMeet() {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId);
        map.put("prolongTime", 30);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/prolongMeet")
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
                    endTime = meetingD.getEndTime();
                    if (timerTask != null && !timerTask.cancel()) {
                        timerTask.cancel();
                        timer1.cancel();
                    }
                    timer1=new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initMeetingHandler.sendEmptyMessage(2);
                                }
                            }).start();
                        }
                    };
                    timer1.schedule(timerTask, 0, 1000);
                } else {
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                }
            }
        });
    }

    private void initMe() {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", SharedPreferencesUtil.read(context, "LtAreaPeople", "ROOMNAME"));
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
                    roomNum.setText(meetingD.getRoomNum());
                    mId = meetingD.getmId() + "";
                    startTime = meetingD.getStartTime();
                    endTime = meetingD.getEndTime();
                    mdurationTime = meetingD.getDurationTime();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initMeetingHandler.sendEmptyMessage(0);
                        }
                    }).start();
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                    if (meetingDBean.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(MainMeetingActivity.this, LoginActivity.class);
                    }
                }
            }
        });
    }

    protected void onChatRoomViewCreation() {
        EMClient.getInstance().chatroomManager().joinChatRoom(SharedPreferencesUtil.read(context, "LtAreaPeople", "CHATROOMID"), new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
            }

            @Override
            public void onError(final int error, String errorMsg) {
            }
        });
    }

    @Override
    public void onResume() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        //EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onPause();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 10;
                    message.obj = messages;
                    initMeetingHandler.sendMessage(message);
                }
            }).start();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 8;
                    message.obj = messages;
                    initMeetingHandler.sendMessage(message);
                }
            }).start();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private void queryUser(String userid, String msg) {
        List<String> ml = new ArrayList<>();
        ml.add(userid);
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("userIds", ml);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/getMeetUserByUserId")
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
                UserBean2 meetingDBean = JsonUtil.fromJson(response, UserBean2.class);
                if (meetingDBean.isSuccess()) {
                    List<User> user = new ArrayList<>();
                    user = meetingDBean.getData();
                    try {
                        mUser = new User();
                        mUser = user.get(0);
                        mitem = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.barrageview_item, null);
                        mMainLy.addView(mitem);
                        send_t = mitem.findViewById(R.id.send_t);
                        send_r = mitem.findViewById(R.id.send_r);
                        send_r1 = mitem.findViewById(R.id.send_r1);
                        mavatar = mitem.findViewById(R.id.avatar);
                        c_t = mitem.findViewById(R.id.c_t);
                        mt_text = mitem.findViewById(R.id.t_text);
                        send_t.append(EaseSmileUtils.getSmiledText(context, msg));
                        Glide.with(context)
                                .load(mUser.getAvatar())
                                .apply(new RequestOptions().placeholder(R.drawable.error))
                                .apply(new RequestOptions().error(R.drawable.error))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                .into(mavatar);
                    } catch (Exception e) {
                    }
                    int retval = list.indexOf(userid);
                    if (retval != -1) {
                        mt_text.setText((retval + 1) + "");
                    }
                    send_r1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.OpenActivity(MainMeetingActivity.this, ChartMeetingActivity.class);
                        }
                    });
                    displayWholeAnimation();
                }
            }
        });
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    private void initMeeting() {
        try {
            Util.showLoadingDialog(context, "加载中");
            initC();
        } catch (Exception e) {
        }
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("roomNum", SharedPreferencesUtil.read(context, "LtAreaPeople", "ROOMNAME"));
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/getMeetOwner")
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
                MeetingDBean meetingdbean = JsonUtil.fromJson(response, MeetingDBean.class);
                if (meetingdbean.getSuccess()) {
                    roleid = meetingdbean.getData().getUserId();
                    EMClient.getInstance().conferenceManager().getConferenceInfo(SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFERENCEID"), SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFRPWD"), new EMValueCallBack<EMConference>() {
                        @Override
                        public void onSuccess(EMConference value) {
                            Log.d("235347657", JsonUtil.toJson(value));
                            mConfrId = value.getConferenceId();
                            mTalkersz = value.getTalkers();
                            list = new ArrayList<>();
                            HashSet<String> hs = new HashSet<String>(Arrays.asList(mTalkersz));
                            mTalkers = new ArrayList<>(hs);
                            list = JsonUtil.fromJson(mTalkers.toString().replaceAll(URLs.APPKEY + "_", ""), new TypeToken<List<String>>() {
                            }.getType());
                            Collections.sort(mTalkers, new order2());
                            Collections.sort(list, new order2());
                            Log.d("32534fdhfgjh657548", JsonUtil.toJson(list) + "////////");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initMeetingHandler.sendEmptyMessage(1);
                                }
                            }).start();
                            if (timerTask != null && !timerTask.cancel()) {
                                timerTask.cancel();
                                timer1.cancel();
                            }
                            timer1=new Timer();
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            initMeetingHandler.sendEmptyMessage(2);
                                        }
                                    }).start();
                                }
                            };
                            timer1.schedule(timerTask, 0, 1000);
                        }

                        @Override
                        public void onError(final int error, final String errorMsg) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initMeetingHandler.sendEmptyMessage(3);
                                }
                            }).start();
                        }
                    });
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, meetingdbean.getMsg(), 200);
                    if (meetingdbean.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initC() {
        for (int m = 0; m < 20; m++) {
            Glide.with(context).load(R.mipmap.meeting_nop).into(initV(m));
            initCircleRelativeLayout(m + 1).setColor(context.getResources().getColor(R.color.colorAccent));
            initImageView(m).setVisibility(View.GONE);
            TextView t = mMainLy.findViewById(context.getResources().getIdentifier("t_name" + (m), "id", context.getPackageName()));
            t.setText("");
            mMainLy.findViewById(context.getResources().getIdentifier("v" + (m + 1), "id", context.getPackageName())).setVisibility(View.GONE);
        }
    }

    private String getStringTime(int cnt) {
        int min = cnt % 3600 / 60;
        return String.format(Locale.CHINA, "%02d", min);
    }

    private String getStringTime1(int cnt) {
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d", second);
    }

    private void initData(List<String> mlist) {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("userIds", mlist);
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/getMeetUserByUserId")
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
                UserBean2 meetingDBean = JsonUtil.fromJson(response, UserBean2.class);
                if (meetingDBean.isSuccess()) {
                    userData = new ArrayList<>();
                    userData = JsonUtil.fromJson(JsonUtil.toJson(meetingDBean.getData()), new TypeToken<List<UserData>>() {
                    }.getType());
                    for (int i = 0; i < userData.size(); i++) {
                        try {
                            if (userData.get(i).getAvatar() != null) {
                                Glide.with(context).load(userData.get(i).getAvatar()).into(initV(i));
                            } else {
                                Glide.with(context).load(R.mipmap.meeting_no_p).into(initV(i));
                            }
                        } catch (Exception e) {

                        }
                        initTextView(i).setText(userData.get(i).getName());
                        if (roleid.equals(userData.get(i).getUserId() + "")) {
                            initImageView(i).setVisibility(View.VISIBLE);
                        }
                        if (PreferenceManager.getInstance().getCurrentUsername().equals(userData.get(i).getUserId() + "")) {
                            initCircleRelativeLayout(i + 1).setColor(context.getResources().getColor(R.color.zb));
                            if (isDown) {
                                mMainLy.findViewById(context.getResources().getIdentifier("v" + (i + 1), "id", context.getPackageName())).setVisibility(View.VISIBLE);
                            }
                        }
                        if (!ConferenceInfo.Initflag) {
                            streamList = new ArrayList<>();
                            streamList = ConferenceInfo.getInstance().getConferenceStreamList();
                            for (int n = 0; n < streamList.size(); n++) {
                                onStreamAdded(streamList.get(n));
                            }
                            ConferenceInfo.Initflag=true;
                        }
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

    private void qData(int n) {
        if (mTalkers.size() >= n + 1) {
            userId = mTalkers.get(n).replace(URLs.APPKEY + "_", "");
            String mcurrentTime = QTime.QTime();
            sign = QTime.QSign(mcurrentTime);
            Map<String, Object> map = new HashMap<>();
            map.put("currentTime",mcurrentTime);
            map.put("sign", sign);
            map.put("userId", userId);
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.SELECTBYID)
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
                    UserBean1 meetingDBean = JsonUtil.fromJson(response, UserBean1.class);
                    if (meetingDBean.isSuccess()) {
                        mUser = new User();
                        mUser = meetingDBean.getData();
                        Boolean isAdmin = false;
                        if (roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                            isAdmin = true;
                        }
                        if (!PreferenceManager.getInstance().getCurrentUsername().equals(userId)) {
                            new MeetingDialog(MainMeetingActivity.this, mUser.getName(), mUser.getCompany(), mUser.getPosition(), isAdmin, R.style.DialogTheme, userId, mUser.getAvatar(), mConfrId, mTalkers.get(n), mId, roleid,mUser.getcId()).show();
                        }
                    } else {
                        UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                        if (meetingDBean.getCode().equals("401")) {
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context, "LtAreaPeople");
                            UIHelper.OpenActivity(MainMeetingActivity.this, LoginActivity.class);
                        }
                    }
                }
            });
        }
    }

    private void ZData(int n) {
        mimgUser = new UserData();
        mimgUser = userData.get(n);
    }

    private XCRoundImageView initV(int i) {
        XCRoundImageView mImageView = null;
        mImageView=mMainLy.findViewById(context.getResources().getIdentifier("XCRoundImageView" + i, "id", context.getPackageName()));
        return mImageView;
    }

    private ImageView initImageView(int i) {
        ImageView mImageView = null;
        mImageView=mMainLy.findViewById(context.getResources().getIdentifier("f" + i, "id", context.getPackageName()));
        return mImageView;
    }

    private TextView initTextView(int i) {
        TextView mImageView = null;
        mImageView=mMainLy.findViewById(context.getResources().getIdentifier("t_name" + i, "id", context.getPackageName()));
        return mImageView;
    }

    private CircleRelativeLayout initCircleRelativeLayout(int i) {
        CircleRelativeLayout mImageView = null;
        mImageView=mMainLy.findViewById(context.getResources().getIdentifier("c" + i, "id", context.getPackageName()));
        return mImageView;
    }

    private void initView() {
        back = (ImageView) actionBar.getCustomView().findViewById(R.id.back);
        finish = (ImageView) actionBar.getCustomView().findViewById(R.id.finish);
        roomNum = (TextView) actionBar.getCustomView().findViewById(R.id.roomNum);
        durationTime = (TextView) actionBar.getCustomView().findViewById(R.id.durationTime);
        durationTime0 = (TextView) actionBar.getCustomView().findViewById(R.id.durationTime0);
        dj_time = (ImageView) actionBar.getCustomView().findViewById(R.id.dj_time);
        add_img = (ImageView) mMainLy.findViewById(R.id.add_img);
        large_preview = (LinearLayout) mMainLy.findViewById(R.id.large_preview);
        large_preview.setBackgroundColor(Color.parseColor("#333333"));
        inputMenu = (EaseChatInputMenu1) mMainLy.findViewById(R.id.input_menu);
        inputMenu.init(null);
        zr = (RelativeLayout) mMainLy.findViewById(R.id.zr);
        r1 = actionBar.getCustomView().findViewById(R.id.r1);
        meeting_pelpeo = (ImageView) actionBar.getCustomView().findViewById(R.id.meeting_pelpeo);
        statement_b = mMainLy.findViewById(R.id.statement_b);
        volide_l = mMainLy.findViewById(R.id.volide_l);
        cancel_b = mMainLy.findViewById(R.id.cancel_b);
        sure_b = mMainLy.findViewById(R.id.sure_b);
        down_b = mMainLy.findViewById(R.id.down_b);
        t_v = mMainLy.findViewById(R.id.t_v);
        edit_t = mMainLy.findViewById(R.id.edit_t);
        edit_t1 = mMainLy.findViewById(R.id.edit_t1);
        text_img1 = mMainLy.findViewById(R.id.text_img1);
        text_img2 = mMainLy.findViewById(R.id.text_img2);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        r_b1 = mMainLy.findViewById(R.id.r_b1);
        r_b2 = mMainLy.findViewById(R.id.r_b2);
        r_b0 = mMainLy.findViewById(R.id.r_b0);
        r_b3 = mMainLy.findViewById(R.id.r_b3);
        send = mMainLy.findViewById(R.id.send);
        r_b0.setBackgroundColor(Color.parseColor("#000000"));
        r1.setBackgroundColor(Color.parseColor("#000000"));
        l1 = mMainLy.findViewById(R.id.l1);
        l2 = mMainLy.findViewById(R.id.l2);
        l3 = mMainLy.findViewById(R.id.l3);
        l4 = mMainLy.findViewById(R.id.l4);
        l5 = mMainLy.findViewById(R.id.l5);
        l6 = mMainLy.findViewById(R.id.l6);
        l7 = mMainLy.findViewById(R.id.l7);
        l8 = mMainLy.findViewById(R.id.l8);
        l9 = mMainLy.findViewById(R.id.l9);
        l10 = mMainLy.findViewById(R.id.l10);
        l11 = mMainLy.findViewById(R.id.l11);
        l12 = mMainLy.findViewById(R.id.l12);
        l13 = mMainLy.findViewById(R.id.l13);
        l14 = mMainLy.findViewById(R.id.l14);
        l15 = mMainLy.findViewById(R.id.l15);
        l16 = mMainLy.findViewById(R.id.l16);
        l17 = mMainLy.findViewById(R.id.l17);
        l18 = mMainLy.findViewById(R.id.l18);
        l19 = mMainLy.findViewById(R.id.l19);
        l20 = mMainLy.findViewById(R.id.l20);
        checked_fz = mMainLy.findViewById(R.id.checked_fz);
        checked_fz_item = mMainLy.findViewById(R.id.checked_fz_item);
        img1 = mMainLy.findViewById(R.id.img1);
        img2 = mMainLy.findViewById(R.id.img2);
        img3 = mMainLy.findViewById(R.id.img3);
        img4 = mMainLy.findViewById(R.id.img4);
        img5 = mMainLy.findViewById(R.id.img5);
        img6 = mMainLy.findViewById(R.id.img6);
        img7 = mMainLy.findViewById(R.id.img7);
        img8 = mMainLy.findViewById(R.id.img8);
        img9 = mMainLy.findViewById(R.id.img9);
        img10 = mMainLy.findViewById(R.id.img10);
        img11 = mMainLy.findViewById(R.id.img11);
        img12 = mMainLy.findViewById(R.id.img12);
        img13 = mMainLy.findViewById(R.id.img13);
        img14 = mMainLy.findViewById(R.id.img14);
        img15 = mMainLy.findViewById(R.id.img15);
        img16 = mMainLy.findViewById(R.id.img16);
        img17 = mMainLy.findViewById(R.id.img17);
        img18 = mMainLy.findViewById(R.id.img18);
        img19 = mMainLy.findViewById(R.id.img19);
        img20 = mMainLy.findViewById(R.id.img20);
        text_img = mMainLy.findViewById(R.id.text_img);
        t_time = mMainLy.findViewById(R.id.t_time);

        edit_t.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.length() > 0) {
                    showInput(edit_t1);
                    r_b1.setVisibility(View.GONE);
                    r_b2.setVisibility(View.VISIBLE);
                    r_b3.setVisibility(View.VISIBLE);
                    edit_t1.setText(input);
                    edit_t1.setSelection(edit_t1.getText().length());
                }
            }
        });
        edit_t1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.length() == 0) {
                    r_b1.setVisibility(View.VISIBLE);
                    r_b2.setVisibility(View.GONE);
                    r_b3.setVisibility(View.GONE);
                    edit_t.setText("");
                    edit_t.requestFocus();
                }
            }
        });
        edit_t1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    r_b0.setBackgroundColor(Color.parseColor("#ff000000"));
                    r_height = r_height + 1;
                    if (r_height > 3) {
                        r_height = 1;
                        return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                    }

                }
                return false;
            }
        });
        openSpeaker();
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu1.ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onSendMessage(String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mitem = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.barrageview_item, null);
                            mMainLy.addView(mitem);
                            send_t = mitem.findViewById(R.id.send_t);
                            send_r = mitem.findViewById(R.id.send_r);
                            send_r1 = mitem.findViewById(R.id.send_r1);
                            mavatar = mitem.findViewById(R.id.avatar);
                            c_t = mitem.findViewById(R.id.c_t);
                            mt_text = mitem.findViewById(R.id.t_text);
                            if (content.length() > 0) {
                                Glide.with(context)
                                        .load(SharedPreferencesUtil.read(context, "LtAreaPeople", "AVATAR"))
                                        .apply(new RequestOptions().placeholder(R.drawable.error))
                                        .apply(new RequestOptions().error(R.drawable.error))
                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                        .into(mavatar);
                                c_t.setColor(getResources().getColor(R.color.zb));
                                int retval = list.indexOf(PreferenceManager.getInstance().getCurrentUsername());
                                if (retval != -1) {
                                    mt_text.setText((retval + 1) + "");
                                }
                                send_t.append(EaseSmileUtils.getSmiledText(context, content));
                                EMMessage message = EMMessage.createTxtSendMessage(content, SharedPreferencesUtil.read(context, "LtAreaPeople", "CHATROOMID"));
                                sendMessage(message);
                                displayWholeAnimation();
                            }
                            send_r1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UIHelper.OpenActivity(MainMeetingActivity.this, ChartMeetingActivity.class);
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(SharedPreferencesUtil.read(context, "LtAreaPeople", "CHATROOMID"), name, identityCode);
        sendMessage(message);
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        isSmall = true;
                        FloatingView.get().icon(R.mipmap.meeting_small);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.BOTTOM | Gravity.END;
                        params.setMargins(params.leftMargin, params.topMargin, 48, 216);
                        FloatingView.get().layoutParams(params);
                        FloatingView.get().add();
                        FloatingView.get().listener(new MagnetViewListener() {
                            @Override
                            public void onRemove(FloatingMagnetView magnetView) {
                            }

                            @Override
                            public void onClick(FloatingMagnetView magnetView) {
                                Log.d("34265347547dsgdfhdsgdfh",JsonUtil.toJson(ConferenceInfo.getInstance().getLocalStream()));
                                if (FloatingView.get().getView() != null) {
                                    FloatingView.get().remove();
                                }
                                UIHelper.OpenActivity(MainMeetingActivity.this, MainMeetingActivity.class);
                            }
                        });
                        finish();
                        break;
                    case R.id.finish:
                        if (roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                            BottomPhoneWindow();
                        } else {
                            new MeetingDialogT(MainMeetingActivity.this, "是否退出会议", R.style.DialogTheme).show();
                        }
                        break;
                    case R.id.meeting_pelpeo:
                        UIHelper.OpenActivity(MainMeetingActivity.this, ChartMeetingActivity.class);
                        break;
                    case R.id.text_img1:
                        r_b1.setVisibility(View.VISIBLE);
                        r_b2.setVisibility(View.GONE);
                        down_b.setVisibility(View.GONE);
                        edit_t.setVisibility(View.VISIBLE);
                        text_img1.setVisibility(View.GONE);
                        text_img2.setVisibility(View.VISIBLE);
                        edit_t.setText("");
                        break;
                    case R.id.text_img2:
                        r_b1.setVisibility(View.VISIBLE);
                        r_b2.setVisibility(View.GONE);
                        down_b.setVisibility(View.VISIBLE);
                        edit_t.setVisibility(View.GONE);
                        text_img1.setVisibility(View.VISIBLE);
                        text_img2.setVisibility(View.GONE);
                        r_b3.setVisibility(View.GONE);
                        hideInput();
                        break;
                    case R.id.send:
                        mitem = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.barrageview_item, null);
                        mMainLy.addView(mitem);
                        send_t = mitem.findViewById(R.id.send_t);
                        send_r = mitem.findViewById(R.id.send_r);
                        send_r1 = mitem.findViewById(R.id.send_r1);
                        mavatar = mitem.findViewById(R.id.avatar);
                        c_t = mitem.findViewById(R.id.c_t);
                        mt_text = mitem.findViewById(R.id.t_text);
                        if (edit_t1.getText().toString().trim().length() > 0) {
                            Glide.with(context)
                                    .load(SharedPreferencesUtil.read(context, "LtAreaPeople", "AVATAR"))
                                    .apply(new RequestOptions().placeholder(R.drawable.error))
                                    .apply(new RequestOptions().error(R.drawable.error))
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                    .into(mavatar);
                            c_t.setColor(getResources().getColor(R.color.zb));
                            int retval = list.indexOf(PreferenceManager.getInstance().getCurrentUsername());
                            if (retval != -1) {
                                mt_text.setText((retval + 1) + "");
                            }
                            send_t.append(EaseSmileUtils.getSmiledText(context, edit_t1.getText().toString().trim()));
                            EMMessage message = EMMessage.createTxtSendMessage(edit_t1.getText().toString().trim(), SharedPreferencesUtil.read(context, "LtAreaPeople", "CHATROOMID"));
                            sendMessage(message);
                            displayWholeAnimation();
                        }
                        send_r1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIHelper.OpenActivity(MainMeetingActivity.this, ChartMeetingActivity.class);
                            }
                        });
                        break;
                    case R.id.l1:
                        qData(0);
                        break;
                    case R.id.l2:
                        qData(1);
                        break;
                    case R.id.l3:
                        qData(2);
                        break;
                    case R.id.l4:
                        qData(3);
                        break;
                    case R.id.l5:
                        qData(4);
                        break;
                    case R.id.l6:
                        qData(5);
                        break;
                    case R.id.l7:
                        qData(6);
                        break;
                    case R.id.l8:
                        qData(7);
                        break;
                    case R.id.l9:
                        qData(8);
                        break;
                    case R.id.l10:
                        qData(9);
                        break;
                    case R.id.l11:
                        qData(10);
                        break;
                    case R.id.l12:
                        qData(11);
                        break;
                    case R.id.l13:
                        qData(12);
                        break;
                    case R.id.l14:
                        qData(13);
                        break;
                    case R.id.l15:
                        qData(14);
                        break;
                    case R.id.l16:
                        qData(15);
                        break;
                    case R.id.l17:
                        qData(16);
                        break;
                    case R.id.l18:
                        qData(17);
                        break;
                    case R.id.l19:
                        qData(18);
                        break;
                    case R.id.l20:
                        qData(19);
                        break;
                    case R.id.img1:
                        initColor();
                        if (img1.isChecked()) {
                            mTalker = mTalkers.get(0);
                            ZData(0);
                            img1.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img1.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img2:
                        initColor();
                        if (img2.isChecked()) {
                            mTalker = mTalkers.get(1);
                            ZData(1);
                            img2.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img2.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img3:
                        initColor();
                        if (img3.isChecked()) {
                            mTalker = mTalkers.get(2);
                            ZData(2);
                            img3.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img3.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img4:
                        initColor();
                        if (img4.isChecked()) {
                            mTalker = mTalkers.get(3);
                            ZData(3);
                            img4.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img4.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img5:
                        initColor();
                        if (img5.isChecked()) {
                            mTalker = mTalkers.get(4);
                            ZData(4);
                            img5.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img5.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img6:
                        initColor();
                        if (img6.isChecked()) {
                            mTalker = mTalkers.get(5);
                            ZData(5);
                            img6.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img6.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img7:
                        initColor();
                        if (img7.isChecked()) {
                            mTalker = mTalkers.get(6);
                            ZData(6);
                            img7.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img7.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img8:
                        initColor();
                        if (img8.isChecked()) {
                            mTalker = mTalkers.get(7);
                            ZData(7);
                            img8.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img8.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img9:
                        initColor();
                        if (img9.isChecked()) {
                            mTalker = mTalkers.get(8);
                            ZData(8);
                            img9.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img9.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img10:
                        initColor();
                        if (img10.isChecked()) {
                            mTalker = mTalkers.get(9);
                            ZData(9);
                            img10.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img10.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img11:
                        initColor();
                        if (img11.isChecked()) {
                            mTalker = mTalkers.get(10);
                            ZData(10);
                            img11.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img11.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img12:
                        initColor();
                        if (img12.isChecked()) {
                            mTalker = mTalkers.get(11);
                            ZData(11);
                            img12.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img12.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img13:
                        initColor();
                        if (img13.isChecked()) {
                            mTalker = mTalkers.get(12);
                            ZData(12);
                            img13.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img13.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img14:
                        initColor();
                        if (img14.isChecked()) {
                            mTalker = mTalkers.get(13);
                            ZData(13);
                            img14.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img14.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img15:
                        initColor();
                        if (img15.isChecked()) {
                            mTalker = mTalkers.get(14);
                            ZData(14);
                            img15.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img15.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img16:
                        initColor();
                        if (img16.isChecked()) {
                            mTalker = mTalkers.get(15);
                            ZData(15);
                            img16.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img16.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img17:
                        initColor();
                        if (img17.isChecked()) {
                            mTalker = mTalkers.get(16);
                            ZData(16);
                            img17.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img17.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img18:
                        initColor();
                        if (img18.isChecked()) {
                            mTalker = mTalkers.get(17);
                            ZData(17);
                            img18.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img18.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img19:
                        initColor();
                        if (img19.isChecked()) {
                            mTalker = mTalkers.get(18);
                            ZData(18);
                            img19.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img19.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.img20:
                        initColor();
                        if (img20.isChecked()) {
                            mTalker = mTalkers.get(19);
                            ZData(19);
                            img20.setBackground(getResources().getDrawable(R.mipmap.img_checked));
                        } else {
                            img20.setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
                        }
                        break;
                    case R.id.cancel_b:
                        checked_fz.setVisibility(View.GONE);
                        checked_fz_item.setVisibility(View.GONE);
                        zr.setVisibility(View.GONE);
                        r_b0.setVisibility(View.VISIBLE);
                        text_img.setVisibility(View.VISIBLE);
                        inputMenu.setVisibility(View.VISIBLE);
                        break;
                    case R.id.sure_b:
                        checked_fz.setVisibility(View.GONE);
                        checked_fz_item.setVisibility(View.GONE);
                        zr.setVisibility(View.GONE);
                        r_b0.setVisibility(View.VISIBLE);
                        text_img.setVisibility(View.VISIBLE);
                        inputMenu.setVisibility(View.VISIBLE);
                        new MeetingZDialog(MainMeetingActivity.this, mimgUser.getName(), "转让房主给TA", "#0076FF", "1", "onclick", R.style.DialogTheme, mimgUser.getAvatar(), mConfrId, mTalker, mId, roleid).show();
                        break;
                    case R.id.dj_time:
                        new ProlongMeetDialog(MainMeetingActivity.this, R.style.DialogTheme).show();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        finish.setOnClickListener(listener);
        meeting_pelpeo.setOnClickListener(listener);
        text_img1.setOnClickListener(listener);
        text_img2.setOnClickListener(listener);
        send.setOnClickListener(listener);
        l1.setOnClickListener(listener);
        l2.setOnClickListener(listener);
        l3.setOnClickListener(listener);
        l4.setOnClickListener(listener);
        l5.setOnClickListener(listener);
        l6.setOnClickListener(listener);
        l7.setOnClickListener(listener);
        l8.setOnClickListener(listener);
        l9.setOnClickListener(listener);
        l10.setOnClickListener(listener);
        l11.setOnClickListener(listener);
        l12.setOnClickListener(listener);
        l13.setOnClickListener(listener);
        l14.setOnClickListener(listener);
        l15.setOnClickListener(listener);
        l16.setOnClickListener(listener);
        l17.setOnClickListener(listener);
        l18.setOnClickListener(listener);
        l19.setOnClickListener(listener);
        l20.setOnClickListener(listener);
        img1.setOnClickListener(listener);
        img2.setOnClickListener(listener);
        img3.setOnClickListener(listener);
        img4.setOnClickListener(listener);
        img5.setOnClickListener(listener);
        img6.setOnClickListener(listener);
        img7.setOnClickListener(listener);
        img8.setOnClickListener(listener);
        img9.setOnClickListener(listener);
        img10.setOnClickListener(listener);
        img11.setOnClickListener(listener);
        img12.setOnClickListener(listener);
        img13.setOnClickListener(listener);
        img14.setOnClickListener(listener);
        img15.setOnClickListener(listener);
        img16.setOnClickListener(listener);
        img17.setOnClickListener(listener);
        img18.setOnClickListener(listener);
        img19.setOnClickListener(listener);
        img20.setOnClickListener(listener);
        cancel_b.setOnClickListener(listener);
        sure_b.setOnClickListener(listener);
        dj_time.setOnClickListener(listener);
    }

    protected void sendMessage(EMMessage message) {
        message.setChatType(EMMessage.ChatType.ChatRoom);
        // Send message.
        message.setAttribute("avatar", SharedPreferencesUtil.read(context, "LtAreaPeople", "AVATAR"));
        message.setAttribute("name", SharedPreferencesUtil.read(context, "LtAreaPeople", "NAME"));
        message.setAttribute("userId", PreferenceManager.getInstance().getCurrentUsername());
        EMClient.getInstance().chatManager().sendMessage(message);

    }

    private void initColor() {
        for (int i = 0; i < 20; i++) {
            mMainLy.findViewById(getResources().getIdentifier("img" + (i + 1), "id", getPackageName())).setBackground(getResources().getDrawable(R.mipmap.img_no_checked));
        }
    }

    private void BottomPhoneWindow() {
        bottomWindow = new BottomPhoneWindow(MainMeetingActivity.this, itemsOnClick);
        //设置弹窗位置
        bottomWindow.showAtLocation(MainMeetingActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.zr:
                    initColor();
                    for (int i = 0; i < mTalkers.size(); i++) {
                        if (!mTalkers.get(i).replace(URLs.APPKEY + "_", "").equals(PreferenceManager.getInstance().getCurrentUsername())) {
                            mMainLy.findViewById(getResources().getIdentifier("img" + (i + 1), "id", getPackageName())).setVisibility(View.VISIBLE);
                            checked_fz.setVisibility(View.VISIBLE);
                            checked_fz_item.setVisibility(View.VISIBLE);
                            zr.setVisibility(View.VISIBLE);
                            r_b0.setVisibility(View.GONE);
                            text_img.setVisibility(View.GONE);
                            inputMenu.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.js:
                    new MeetingDialogT(MainMeetingActivity.this, "是否结束会议", R.style.DialogTheme).show();
                    break;
                case R.id.close_r:
                    checked_fz.setVisibility(View.GONE);
                    checked_fz_item.setVisibility(View.GONE);
                    zr.setVisibility(View.GONE);
                    r_b0.setVisibility(View.VISIBLE);
                    text_img.setVisibility(View.VISIBLE);
                    inputMenu.setVisibility(View.VISIBLE);
                    break;
            }
        }

    };

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void displayWholeAnimation() {
        startAppearanceAnimation();
    }

    private void startAppearanceAnimation() {
        /**
         * 核心类 AnimationSet 顾名思义，可以简单理解为将多种动画放在一个set集合里面
         *    产生渐渐显示+位移动画，将加速小火箭渐渐显示出来;
         *
         */
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        ObjectAnimator.ofFloat(send_r, "translationY", -200.0f, -Float.parseFloat(heightPixels + ""))
                .setDuration(6000)
                .start();
    }

    /**
     * 格式化字符串
     *
     * @param string   原始输入字符串
     * @param maxCount 最大字符限制，中文算作2个字符，其他都算1个字符
     * @return
     */
    private String formatText(String string, int maxCount) {
        if (string.length() > maxCount) {
            string = subStrByLen(string, maxCount);
        }
        return string;
    }

    /**
     * 截取字符串，超出最大字数截断并显示"..."
     *
     * @param str    原始字符串
     * @param length 最大字数限制（以最大字数限制7个为例，当含中文时，length应设为2*7，不含中文时设为7）
     * @return 处理后的字符串
     */
    public String subStrByLen(String str, int length) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int chCnt = getStrLen(str);
        // 超出进行截断处理
        if (chCnt > length) {
            int cur = 0;
            int cnt = 0;
            StringBuilder sb = new StringBuilder();
            while (cnt <= length && cur < str.length()) {
                char nextChar = str.charAt(cur);
                cnt++;
                if (cnt <= length) {
                    sb.append(nextChar);
                } else {
                    return sb.toString() + "...";
                }
                cur++;
            }
            return sb.toString() + "...";
        }
        // 未超出直接返回
        return str;
    }

    /**
     * 获取字符串中的中文字数
     */
    private int getChCount(String str) {
        int cnt = 0;
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        ;
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 获取字符长度，中文算作2个字符，其他都算1个字符
     */
    public int getStrLen(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        return str.length() + getChCount(str);
    }

    private void pubLocalStream() {
        EMStreamParam param = new EMStreamParam();
        param.setStreamType(EMConferenceStream.StreamType.NORMAL);
        param.setVideoOff(true);
        param.setAudioOff(false);

        EMClient.getInstance().conferenceManager().publish(param, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String streamId) {
                conference.setPubStreamId(streamId, EMConferenceStream.StreamType.NORMAL);
            }

            @Override
            public void onError(int error, String errorMsg) {
//                EMLog.e(TAG, "publish failed: error=" + error + ", msg=" + errorMsg);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int retval = mTalkers.indexOf(URLs.APPKEY + "_" + PreferenceManager.getInstance().getCurrentUsername());
                if (retval != -1) {
                    mMainLy.findViewById(getResources().getIdentifier("v" + (retval + 1), "id", getPackageName())).setVisibility(View.VISIBLE);
                    volide_l.setVisibility(View.VISIBLE);
                    if(mTimerTask!=null && !mTimerTask.cancel()){
                        mTimerTask.cancel();
                        mtimer_t.cancel();
                    }
                    mtimer_t = new Timer();
                    mTimerTask = new TimerTask() {
                        int cnt = 0;

                        @Override
                        public void run() {
                            cnt++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    t_time.setText(cnt + "s");
                                }
                            });
                        }
                    };
                    mtimer_t.schedule(mTimerTask, 0, 1000);
                }
            }
        }, 1000);
    }

    private void colsepubLocalStream() {
        EMClient.getInstance().conferenceManager().unpublish(conference.getPubStreamId(EMConferenceStream.StreamType.NORMAL), new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String streamId) {
                //closeSpeaker();
            }

            @Override
            public void onError(int error, String errorMsg) {
//                EMLog.e(TAG, "publish failed: error=" + error + ", msg=" + errorMsg);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int retval = mTalkers.indexOf(URLs.APPKEY + "_" + PreferenceManager.getInstance().getCurrentUsername());
                if (retval != -1) {
                    mMainLy.findViewById(getResources().getIdentifier("v" + (retval + 1), "id", getPackageName())).setVisibility(View.GONE);
                    if (mTimerTask != null && !mTimerTask.cancel()) {
                        mTimerTask.cancel();
                        mtimer_t.cancel();
                    }
                    t_time.setText("");
                    volide_l.setVisibility(View.GONE);
                }
            }
        }, 200);
    }

    /**
     * 打开扬声器
     * 主要是通过扬声器的开关以及设置音频播放模式来实现
     * 1、MODE_NORMAL：是正常模式，一般用于外放音频
     * 2、MODE_IN_CALL：
     * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
     * 4、MODE_RINGTONE：铃声模式
     */
    public void openSpeaker() {
        // 检查是否已经开启扬声器
        if (!audioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            audioManager.setSpeakerphoneOn(true);
            audioManager.setBluetoothScoOn(true);
            audioManager.startBluetoothSco();
        }
        // 开启了扬声器之后，因为是进行通话，声音的模式也要设置成通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public void closeSpeaker() {
        // 检查是否已经开启扬声器
        if (audioManager.isSpeakerphoneOn()) {
            // 关闭扬声器
            audioManager.setSpeakerphoneOn(false);
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
        }
        // 设置声音模式为通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * --------------------------------------------------------------------
     * 多人音视频会议回调方法
     */
    @Override
    public void onMemberJoined(EMConferenceMember member) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 4;
                message.obj = JsonUtil.fromJson(member.extension, User.class);
                initMeetingHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onMemberExited(EMConferenceMember member) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 5;
                message.obj = JsonUtil.fromJson(member.extension, User.class);
                initMeetingHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onStreamAdded(EMConferenceStream stream) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 6;
                message.obj = stream;
                initMeetingHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onStreamRemoved(EMConferenceStream stream) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 7;
                message.obj = stream;
                initMeetingHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onStreamUpdate(EMConferenceStream stream) {
    }

    @Override
    public void onPassiveLeave(int error, String message) {
        switch (error) {
            case -411:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initMeetingHandler.sendEmptyMessage(9);
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onConferenceState(ConferenceState state) {
    }

    @Override
    public void onStreamStatistics(EMStreamStatistics statistics) {
    }

    @Override
    public void onStreamSetup(String streamId) {
    }

    @Override
    public void onSpeakers(List<String> speakers) {
    }

    @Override
    public void onReceiveInvite(String confId, String password, String extension) {
    }

    @Override
    public void onRoleChanged(EMConferenceManager.EMConferenceRole role) {

    }

    @Override
    protected void onDestroy() {
        if (!isSmall) {
            // 停止监听说话者
            EMClient.getInstance().conferenceManager().stopMonitorSpeaker();
            EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
            //注销注册
            EventBus.getDefault().unregister(this);
            if (timerTask != null && !timerTask.cancel()) {
                timerTask.cancel();
                timer1.cancel();
            }
            closeSpeaker();
            if (FloatingView.get().getView() != null) {
                FloatingView.get().remove();
            }
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setMicrophoneMute(false);
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
            initMeetingHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (roleid.equals(PreferenceManager.getInstance().getCurrentUsername())) {
                if (mTalkers.size() > 1) {
                    BottomPhoneWindow();
                } else {
                    new MeetingDialogT(MainMeetingActivity.this, "是否结束会议", R.style.DialogTheme).show();
                }
            } else {
                if (mTalkers.size() > 1) {
                    new MeetingDialogT(MainMeetingActivity.this, "是否退出会议", R.style.DialogTheme).show();
                } else {
                    new MeetingDialogT(MainMeetingActivity.this, "是否结束会议", R.style.DialogTheme).show();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void outMeeting(String msg) {
        EMClient.getInstance().conferenceManager().exitConference(new EMValueCallBack() {
            @Override
            public void onSuccess(Object value) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 11;
                        message.obj = msg;
                        initMeetingHandler.sendMessage(message);
                    }
                }).start();
            }

            @Override
            public void onError(int error, String errorMsg) {
                UIHelper.ToastMessageCenter(context, msg, 200);
                EventBus.getDefault().post(new Sticky("XS"));
                finish();
            }
        });
    }

    private void destroyMeeting() {
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String, String> map = new HashMap<>();
        map.put("currentTime", mcurrentTime);
        map.put("sign", sign);
        map.put("mId", mId);
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
                    if (!isSmall) {
                        // 停止监听说话者
                        EMClient.getInstance().conferenceManager().stopMonitorSpeaker();
                        EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
                        //注销注册
                        EventBus.getDefault().unregister(this);
                        if (timerTask != null && !timerTask.cancel()) {
                            timerTask.cancel();
                            timer1.cancel();
                        }
                        if (FloatingView.get().getView() != null) {
                            FloatingView.get().remove();
                        }
                    }
                    UIHelper.ToastMessageCenter(context, "会议已结束", 200);
                    EventBus.getDefault().post(new Sticky("XS"));
                    EventBus.getDefault().post(new Sticky("CHATEND"));
                    finish();
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

    private TimerTask mTimerTask;

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if (userEvent.msg.equals("是否退出会议")) {
            outMeeting("您已成功退出当前会议！");
        } else if (userEvent.msg.equals("是否结束会议")) {
            destroyMeeting();
        } else if (userEvent.msg.equals("V_X")) {
            isDown = true;
            down_b.setBackground(getResources().getDrawable(R.drawable.edit_meeting_shape2));
            t_v.setText("松开结束发言");
            statement_b.setVisibility(View.GONE);
            pubLocalStream();
        } else if (userEvent.msg.equals("V_N")) {
            isDown = false;
            down_b.setBackground(getResources().getDrawable(R.drawable.edit_meeting_shape1));
            t_v.setText("按住发言");
            statement_b.setVisibility(View.VISIBLE);
            // 放开处理
            colsepubLocalStream();
        } else if (userEvent.msg.equals("ProlongMeet")) {
            dj_time.setVisibility(View.GONE);
            initMeetingHandler.sendEmptyMessage(12);
        }else if(userEvent.msg.equals("NoProlongMeet")){
            dj_time.setVisibility(View.VISIBLE);
        }
    }
}
