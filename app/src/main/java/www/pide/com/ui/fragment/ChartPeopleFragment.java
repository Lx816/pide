package www.pide.com.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.adapter.ChartPeopleAdapter;
import www.pide.com.bean.MeetingDBean;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean2;
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

import www.pide.com.ui.activity.CompleteInformation;
import www.pide.com.ui.activity.CompleteInformationFK;
import www.pide.com.ui.activity.LoginActivity;

/**
 * Created by Administrator on 2020/4/1.
 */
public class ChartPeopleFragment extends BasePadFragment {
    View mRootView;
    private Context context;
    private ChartPeopleAdapter adapter;
    private ListView listview;
    private String sign="";
    private ConferenceSession conferenceSession;
    private List<User> user;
    //是否可见
    public boolean isVisible = false;
    //是否初始化完成
    public boolean isInit = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.chart_people_fragment, container, false);
            context=getActivity();
            initView();
            isInit = true;
            if (isInit && isVisible) {
                initData();
            }
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisible = isVisibleToUser;
        if (isInit && isVisible) {
            initData();
        }
    }

    private void initData() {
        initMeeting();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(PreferenceManager.getInstance().getCurrentUsername().equals(user.get(i).getUserId()+"")){
                    Intent intent=new Intent(context, CompleteInformation.class);
                    intent.putExtra("id",String.valueOf(user.get(i).getUserId()));
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(context, CompleteInformationFK.class);
                    intent.putExtra("id",String.valueOf(user.get(i).getUserId()));
                    startActivity(intent);
                }
            }
        });
    }

    private void initView() {
        listview= (ListView) mRootView.findViewById(R.id.listview);
        conferenceSession = DemoHelper.getInstance().getConferenceSession();
    }
    private void initMeeting() {
        try {
            Util.showLoadingDialog(context,"加载中");
        }catch (Exception e){}
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
                    EMClient.getInstance().conferenceManager().getConferenceInfo(SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFERENCEID"), SharedPreferencesUtil.read(context, "LtAreaPeople", "CONFRPWD"), new EMValueCallBack<EMConference>() {
                        @Override
                        public void onSuccess(EMConference value) {
                           String[] mTalkersz = value.getTalkers();
                            List<String> mTalkers=new ArrayList<>();
                            List<String> list = new ArrayList<>();
                            for (int i=0; i<mTalkersz.length; i++) {
                                if(!mTalkers.contains(mTalkersz[i])) {
                                    String userId = mTalkersz[i].replace(URLs.APPKEY+"_", "");
                                    mTalkers.add(mTalkersz[i]);
                                    list.add(userId);
                                }
                            }
                            initData(list);
                        }

                        @Override
                        public void onError(final int error, final String errorMsg) {
                            getActivity().runOnUiThread(new Runnable() {
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
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
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
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                Util.closeLoadingDialog(context);
                UserBean2 meetingDBean = JsonUtil.fromJson(response, UserBean2.class);
                if (meetingDBean.isSuccess()) {
                    user = new ArrayList<>();
                    user = meetingDBean.getData();
                    adapter=new ChartPeopleAdapter(context,user);
                    listview.setAdapter(adapter);
                } else {
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
}
