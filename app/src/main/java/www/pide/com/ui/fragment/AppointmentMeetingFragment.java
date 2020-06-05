package www.pide.com.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.adapter.AllMeetingAdapter;
import www.pide.com.bean.MeetingBean;
import www.pide.com.bean.Sticky;
import www.pide.com.utils.GridItemDecoration;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.activity.AppointmentDetailsActivity;
import www.pide.com.ui.activity.LoginActivity;

/**
 * Created by Administrator on 2020/4/1.
 * 会议记录（预约中）
 */
public class AppointmentMeetingFragment extends BasePadFragment {
    View mRootView;
    private RelativeLayout no_w;
    private XRecyclerView appointment_listview;
    private AllMeetingAdapter allMeetingAdapter;
    private Context context;
    private List<MeetingBean.MeetingBeanRecords> list=new ArrayList<>();
    private List<MeetingBean.MeetingBeanRecords> mList=new ArrayList<>();
    private String sign="";
    private int times = 1;
    //是否可见
    public boolean isVisible = false;
    //是否初始化完成
    public boolean isInit = false;
    private MyHandler initMeetingHandler=new MyHandler(getActivity());

    private static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;
        public MyHandler(Activity activity) {
            mWeakReference=new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {

        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        initMeetingHandler=new MyHandler(getActivity()){
            @Override
            public void handleMessage(Message msg) {
                final Activity activity=mWeakReference.get();
                if(activity!=null) {
                    switch (msg.what) {
                        case 0:
                            initData();
                            break;
                        case 1:
                            initData1(msg.arg1);
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        };
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.appointment_meeting_fragment, container, false);
            context=getActivity();
            initView();
            isInit = true;
            if (isInit && isVisible) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initMeetingHandler.sendEmptyMessage(0);
                    }
                }).start();
            }
            //注册订阅者
            EventBus.getDefault().register(this);
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisible = isVisibleToUser;
        if (isInit && isVisible) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initMeetingHandler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    private void initView() {
        no_w= (RelativeLayout) mRootView.findViewById(R.id.no_w);
        //no_w.setVisibility(View.VISIBLE);
        appointment_listview= (XRecyclerView) mRootView.findViewById(R.id.appointment_listview);
        //1)初始化RecyclerView设置
        appointment_listview.setPullRefreshEnabled(true);
        appointment_listview.setLoadingMoreEnabled(true);
        appointment_listview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        appointment_listview.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        //2)添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        appointment_listview.setLayoutManager(layoutManager);
        //添加自定义的分割线
        GridItemDecoration divider = new GridItemDecoration(context, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider));
        appointment_listview.addItemDecoration(divider);
        //5)实现 下拉刷新和加载更多 接口
        appointment_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                times = 1;
                mList.clear(); //先要清掉数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.what=1;
                        message.arg1=0;
                        initMeetingHandler.sendMessage(message);
                    }
                }).start();

            }

            @Override
            public void onLoadMore() {
                if (times < 20) {//加载20次后，就不再加载更多
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message=new Message();
                            message.what=1;
                            message.arg1=1;
                            initMeetingHandler.sendMessage(message);
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message=new Message();
                            message.what=1;
                            message.arg1=2;
                            initMeetingHandler.sendMessage(message);
                        }
                    }).start();
                }
                times++;
            }
        });
    }
    private void initData() {
        try {
            Util.showLoadingDialog(context,"加载中");
        }catch (Exception e){}
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String,String> map=new HashMap<>();
        map.put("currentTime",mcurrentTime);
        map.put("sign", sign);
        map.put("mStatus", "0");
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/myList?pageNum=1&pageSize=10")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingBean bean= JsonUtil.fromJson(response,MeetingBean.class);
                if(bean.getSuccess()){
                    list=new ArrayList<>();
                    list=bean.getData().getRecords();
                    mList=new ArrayList<>();
                    mList.addAll(list);
                    if(list.size()>0){
                        Util.closeLoadingDialog(context);
                        appointment_listview.setVisibility(View.VISIBLE);
                        no_w.setVisibility(View.GONE);
                        allMeetingAdapter=new AllMeetingAdapter(context,mList,"appointment");
                        appointment_listview.setAdapter(allMeetingAdapter);
                        allMeetingAdapter.setOnItemClickListener(new AllMeetingAdapter.ClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Intent intent=new Intent(context, AppointmentDetailsActivity.class);
                                intent.putExtra("meeting_code",mList.get(position).getRoomNum());
                                startActivity(intent);
                            }
                        });
                    }else {
                        Util.closeLoadingDialog(context);
                        appointment_listview.setVisibility(View.GONE);
                        no_w.setVisibility(View.VISIBLE);
                    }
                }else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, bean.getMsg(), 200);
                    if(bean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }
    private void initData1(int mtype) {
        String mcurrentTime=QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        Map<String,String> map=new HashMap<>();
        map.put("currentTime",mcurrentTime);
        map.put("sign", sign);
        map.put("mStatus", "0");
        OkHttpUtils.postString().url(URLs.IMGEURL + "meet/auth/myList?pageNum="+times+"&pageSize=10")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                MeetingBean bean= JsonUtil.fromJson(response,MeetingBean.class);
                if(bean.getSuccess()){
                    list=new ArrayList<>();
                    list=bean.getData().getRecords();
                    switch (mtype){
                        case 0:
                            if(list.size()>0){
                                mList.addAll(list); //再将数据插入到前面
                                allMeetingAdapter.notifyDataSetChanged();
                                appointment_listview.refreshComplete(); //下拉刷新完成
                            }else {
                                appointment_listview.refreshComplete(); //下拉刷新完成
                            }
                            break;
                        case 1:
                            if(list.size()>0){
                                mList.addAll(list); //直接将数据追加到后面
                                appointment_listview.loadMoreComplete();
                                allMeetingAdapter.notifyDataSetChanged();
                            }else {
                                appointment_listview.loadMoreComplete();
                            }
                            break;
                        case 2:
                            if(list.size()>0){
                                mList.addAll(list); //将数据追加到后面
                                allMeetingAdapter.notifyDataSetChanged();
                                appointment_listview.setNoMore(true);
                            }else {
                                appointment_listview.loadMoreComplete();
                            }
                            break;
                    }
                }else {
                    UIHelper.ToastMessageCenter(context, bean.getMsg(), 200);
                    if(bean.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context,LoginActivity.class);
                    }
                }
            }
        });
    }
    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("All_j")){
            initData();
        }
    }

    @Override
    public void onDestroy() {
        initMeetingHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
