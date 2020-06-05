package www.pide.com.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import okhttp3.MediaType;
import www.pide.com.adapter.MineCardListViewAdapter;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.bean.UserBean1;
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

public class MineCardFragment extends BasePadFragment {
    View mRootView;
    private XRecyclerView mine_card_listview;
    private MineCardListViewAdapter mineCardListViewAdapter;
    private Context context;
    private UserBean userBean=new UserBean();
    private  List<User>  microCardsBean=new ArrayList<>();
    private  List<User>  mList=new ArrayList<>();
    private LinearLayout no_w;
    private int times = 1;
    private String sign="";
    private int userId;
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
        mRootView = inflater.inflate(R.layout.mine_card_fragment, container, false);
        context=getContext();
        initView();
        //注册订阅者
        EventBus.getDefault().register(this);
        return mRootView;
    }

    private void initView() {
        mine_card_listview= (XRecyclerView) mRootView.findViewById(R.id.mine_card_listview);
        no_w= (LinearLayout) mRootView.findViewById(R.id.no_w);
        //1)初始化RecyclerView设置
        mine_card_listview.setPullRefreshEnabled(true);
        mine_card_listview.setLoadingMoreEnabled(true);
        mine_card_listview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mine_card_listview.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        //2)添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mine_card_listview.setLayoutManager(layoutManager);
        initMeetingHandler=new MyHandler(getActivity()){
            @Override
            public void handleMessage(Message msg) {
                final Activity activity=mWeakReference.get();
                if(activity!=null) {
                    switch (msg.what) {
                        case 0:
                            initDataUser();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMeetingHandler.sendEmptyMessage(0);
            }
        }).start();
        //5)实现 下拉刷新和加载更多 接口
        mine_card_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
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
                            message.what=2;
                            message.arg1=0;
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
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.USETLIST+"?pageNum=1&pageSize=10")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
               userBean = JsonUtil.fromJson(response,UserBean.class);
               if(userBean.isSuccess()){
                   microCardsBean=new ArrayList<>();
                   microCardsBean = userBean.getUserD().getRecords();
                   mList=new ArrayList<>();
                   mList.addAll(microCardsBean);
                   if(microCardsBean.size()>0){
                       Util.closeLoadingDialog(context);
                       no_w.setVisibility(View.GONE);
                       mine_card_listview.setVisibility(View.VISIBLE);
                       mineCardListViewAdapter = new MineCardListViewAdapter(getContext(), mList);
                       mine_card_listview.setAdapter(mineCardListViewAdapter);
                       mineCardListViewAdapter.setOnItemClickListener(new MineCardListViewAdapter.ClickListener() {
                           @Override
                           public void onItemClick(View v, int position) {
                               if(mList.get(position-1).getUserId()==userId){
                                   Intent intent=new Intent(context, CompleteInformation.class);
                                   intent.putExtra("id",String.valueOf(mList.get(position-1).getUserId()));
                                   startActivity(intent);
                               }else {
                                   Intent intent=new Intent(context, CompleteInformationFK.class);
                                   intent.putExtra("id",String.valueOf(mList.get(position-1).getUserId()));
                                   startActivity(intent);
                               }
                           }
                       });
                   }else {
                       Util.closeLoadingDialog(context);
                       no_w.setVisibility(View.VISIBLE);
                       mine_card_listview.setVisibility(View.GONE);
                   }
               }else {
                   Util.closeLoadingDialog(context);
                   UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
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
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.USETLIST+"?pageNum="+times+"&pageSize=10")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                userBean = JsonUtil.fromJson(response,UserBean.class);
                if(userBean.isSuccess()){
                    microCardsBean=new ArrayList<>();
                    microCardsBean = userBean.getUserD().getRecords();
                    switch (mtype){
                        case 0:
                            if(microCardsBean.size()>0){
                                mList.addAll(microCardsBean); //再将数据插入到前面
                                mineCardListViewAdapter.notifyDataSetChanged();
                                mine_card_listview.refreshComplete(); //下拉刷新完成
                            }else {
                                mine_card_listview.refreshComplete(); //下拉刷新完成
                            }
                            break;
                        case 1:
                            if(microCardsBean.size()>0){
                                mList.addAll(microCardsBean); //直接将数据追加到后面
                                mine_card_listview.loadMoreComplete();
                                mineCardListViewAdapter.notifyDataSetChanged();
                            }else {
                                mine_card_listview.loadMoreComplete();
                            }
                            break;
                        case 2:
                            if(microCardsBean.size()>0){
                                mList.addAll(microCardsBean); //将数据追加到后面
                                mineCardListViewAdapter.notifyDataSetChanged();
                                mine_card_listview.setNoMore(true);
                            }else {
                                mine_card_listview.loadMoreComplete();
                            }
                            break;
                    }
                }else {
                    UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                }
            }
        });
    }
    private void initDataUser() {
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYDETAIL)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean1 user0 = JsonUtil.fromJson(response, UserBean1.class);
                if(user0.isSuccess()) {
                    userId=user0.getData().getUserId();
                }else {
                    UIHelper.ToastMessageCenter(context, user0.getMsg(), 200);
                    if(user0.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }
    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("1")){
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        initMeetingHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
        //注销注册
        EventBus.getDefault().unregister(this);
    }

}
