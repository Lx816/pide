package www.pide.com.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import www.pide.com.chart.easeui.easeui.ui.EaseChatFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.Sticky;
import www.pide.com.hxsdk.ConferenceSession;
import www.pide.com.hxsdk.DemoHelper;
import www.pide.com.hxsdk.PreferenceManager;
import www.pide.com.hxsdk.runtimepermissions.PermissionsManager;
import www.pide.com.ui.fragment.ChartPeopleFragment;
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
public class ChartMeetingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout back;
    private ViewPager chartFrameLayout;
    private RelativeLayout r0,r1;
    private TextView t0,t00,t1,t11;
    private List<Fragment> fragmentList;
    private String sign="";
    private ConferenceSession conferenceSession;
    private TimerTask timerTask;
    private Timer timer1;
    private String startTime;
    private TextView durationTime0,durationTime,roomNum;
    private String admin = "";
    private String mId="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_meeting);
        //注册订阅者
        EventBus.getDefault().register(this);
        initView();
        initOnclick();
    }

    private void initView() {
        timer1 = new Timer();
        back= (RelativeLayout) findViewById(R.id.back);
        chartFrameLayout= (ViewPager) findViewById(R.id.chartFrameLayout);
        r0= (RelativeLayout) findViewById(R.id.r0);
        r1= (RelativeLayout) findViewById(R.id.r1);
        t0= (TextView) findViewById(R.id.t0);
        durationTime0= (TextView) findViewById(R.id.durationTime0);
        durationTime= (TextView) findViewById(R.id.durationTime);
        roomNum= (TextView) findViewById(R.id.roomNum);
        t00= (TextView) findViewById(R.id.t00);
        t1= (TextView) findViewById(R.id.t1);
        t11= (TextView) findViewById(R.id.t11);
        EaseChatFragment f1 = new EaseChatFragment();
        ChartPeopleFragment f2 = new ChartPeopleFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(f1);
        fragmentList.add(f2);
        chartFrameLayout.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        chartFrameLayout.setCurrentItem(0);
        chartFrameLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        initColor();
                        t0.setTextColor(Color.parseColor("#0076FF"));
                        t00.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        initColor();
                        t1.setTextColor(Color.parseColor("#0076FF"));
                        t11.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
        initMeeting();
    }
    private void initMeeting() {
        Util.showLoadingDialog(this,"加载中");
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
                    startTime=meetingD.getStartTime();

                    EMClient.getInstance().conferenceManager().getConferenceInfo(SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFERENCEID"), SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFRPWD"), new EMValueCallBack<EMConference>() {
                        @Override
                        public void onSuccess(EMConference value) {
                            String[] mAdmin = value.getAdmins();
                            String[] mTalkersz = value.getTalkers();
                           List<String> mTalkers=new ArrayList<>();
                            admin = mAdmin[0].replace(URLs.APPKEY+"_", "");
                            HashSet<String> hs = new HashSet<String>(Arrays.asList(mTalkersz));
                            mTalkers = new ArrayList<>(hs);
                            timerTask = new TimerTask() {
                                int cnt = 0;
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            long time = System.currentTimeMillis();
                                            try {
                                                long mstartTime=Long.valueOf(QTime.dateToStamp(startTime));
                                                long mendTime=Long.valueOf(QTime.dateToStamp(meetingD.getEndTime()));
                                                cnt=QTime.testPassedTime1(time,mstartTime);
                                                int hytime=QTime.testPassedTime2(mendTime,time);
                                                if(Integer.parseInt(meetingD.getDurationTime())-hytime==15){
                                                    UIHelper.ToastMessageCenter(context, "会议还有十五分钟结束", 200);
                                                }
                                                if(hytime==0){
                                                    UIHelper.ToastMessageCenter(context, "会议已结束", 200);
                                                    if(admin.equals(PreferenceManager.getInstance().getCurrentUsername())){
                                                        destroyMeeting();
                                                    }else {
                                                        outMeeting("您已成功退出当前会议！");
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            durationTime0.setText(getStringTime(cnt++));
                                            durationTime.setText(":"+getStringTime1(cnt++));
                                        }
                                    });
                                }
                            };
                            timer1.schedule(timerTask,0,1000);
                            List<String> finalMTalkers = mTalkers;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    t1.setText("成员（"+ finalMTalkers.size()+"人）");
                                    Util.closeLoadingDialog(context);
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
                    if(meetingDBean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(ChartMeetingActivity.this,LoginActivity.class);
                    }
                }
            }
        });
    }
    private String getStringTime(int cnt) {
        int min = cnt % 3600 / 60;
        return String.format(Locale.CHINA,"%02d",min);
    }
    private String getStringTime1(int cnt) {
        int second = cnt % 60;
        return String.format(Locale.CHINA,"%02d",second);
    }
    private void initOnclick() {
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   switch (view.getId()){
                       case R.id.back:
                           finish();
                           break;
                       case R.id.r0:
                           initColor();
                           t0.setTextColor(Color.parseColor("#0076FF"));
                           t00.setVisibility(View.VISIBLE);
                           chartFrameLayout.setCurrentItem(0);
                           break;
                       case R.id.r1:
                           initColor();
                           t1.setTextColor(Color.parseColor("#0076FF"));
                           t11.setVisibility(View.VISIBLE);
                           chartFrameLayout.setCurrentItem(1);
                           break;
                   }
            }
        };
        back.setOnClickListener(listener);
        r0.setOnClickListener(listener);
        r1.setOnClickListener(listener);
    }

    private void initColor() {
        t0.setTextColor(Color.parseColor("#939393"));
        t1.setTextColor(Color.parseColor("#939393"));
        t00.setVisibility(View.GONE);
        t11.setVisibility(View.GONE);
    }
    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
    private void destroyMeeting() {
        Util.showLoadingDialog(this,"加载中");
        EMClient.getInstance().conferenceManager().destroyConference(new EMValueCallBack() {
            @Override
            public void onSuccess(Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, String response) {
                                MeetingDBean meetingDBean = JsonUtil.fromJson(response, MeetingDBean.class);
                                if (meetingDBean.getSuccess()) {
                                    Util.closeLoadingDialog(context);
                                    if (!timerTask.cancel()){
                                        timerTask.cancel();
                                        timer1.cancel();
                                    }
                                    UIHelper.ToastMessageCenter(context, "会议已结束", 200);
                                    EventBus.getDefault().post(new Sticky("XS"));
                                    finish();
                                } else {
                                    Util.closeLoadingDialog(context);
                                    UIHelper.ToastMessageCenter(context, meetingDBean.getMsg(), 200);
                                    if(meetingDBean.getCode().equals("401")){
                                        EMClient.getInstance().logout(true);
                                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                                        UIHelper.OpenActivity(ChartMeetingActivity.this,LoginActivity.class);
                                    }
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.closeLoadingDialog(context);
                        Toast.makeText(getApplicationContext(), "会议销毁失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void outMeeting(String msg) {
        EMClient.getInstance().conferenceManager().exitConference(new EMValueCallBack() {
            @Override
            public void onSuccess(Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!timerTask.cancel()){
                            timerTask.cancel();
                            timer1.cancel();
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new Sticky("XS"));
                        finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                finish();
            }
        });
    }

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("JION")){
            initMeeting();
        }else if(userEvent.msg.equals("CHATEND")){
            UIHelper.ToastMessageCenter(context, "会议已结束", 200);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        //注销注册
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
