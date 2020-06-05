package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
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

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.adapter.CollectAdapter;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/3/29.
 */
public class CollectActivity extends BaseActivity implements CollectAdapter.OnPriceClickListener {
    private XRecyclerView collect_list;
    private Context context;
    private CollectAdapter collectAdapter;
    private List<User> list;
    private RelativeLayout back;
    private CollectAdapter.OnPriceClickListener onPriceClickListener;
    private Boolean isFrist = false;
    private LinearLayout no_w;
    private int times = 1;
    private List<User> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);
        //注册订阅者
        EventBus.getDefault().register(this);
        onPriceClickListener = this;
        initView();
        initData();
    }

    private void initView() {
        collect_list = (XRecyclerView) findViewById(R.id.collect_list);
        back = (RelativeLayout) findViewById(R.id.back);
        no_w = (LinearLayout) findViewById(R.id.no_w);
        //1)初始化RecyclerView设置
        collect_list.setPullRefreshEnabled(true);
        collect_list.setLoadingMoreEnabled(true);
        collect_list.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        collect_list.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        //2)添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(CollectActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        collect_list.setLayoutManager(layoutManager);
        //5)实现 下拉刷新和加载更多 接口
        collect_list.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                times = 1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.clear(); //先要清掉数据
                        initData1(0);
                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                if (times < 20) {//加载20次后，就不再加载更多
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            initData1(1);
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            initData1(2);
                        }
                    }, 1000);
                }
                times++;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isFrist = true;
    }

    private void initData() {
        Util.showLoadingDialog(this,"加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.LISTCOLLECTIONSUSER + "?isAsc=DESC&orderByColumn=1&pageNum=1&pageSize=10")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    Util.closeLoadingDialog(context);
                    list = new ArrayList<>();
                    list = u.getUserD().getRecords();
                    if (list.size() > 0) {
                        collect_list.setVisibility(View.VISIBLE);
                        no_w.setVisibility(View.GONE);
                        collectAdapter = new CollectAdapter(context, list, onPriceClickListener);
                        collect_list.setAdapter(collectAdapter);
                    } else {
                        collect_list.setVisibility(View.GONE);
                        no_w.setVisibility(View.VISIBLE);
                    }
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if(u.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(CollectActivity.this,LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initData1(int mtype) {
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.LISTCOLLECTIONSUSER + "?isAsc=DESC&orderByColumn=1&pageNum=" + times + "&pageSize=10")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    list = new ArrayList<>();
                    list = u.getUserD().getRecords();
                    switch (mtype){
                        case 0:
                            if (list.size() > 0) {
                                mList.addAll(list); //再将数据插入到前面
                                collectAdapter.notifyDataSetChanged();
                                collect_list.refreshComplete(); //下拉刷新完成
                            }else {
                                collect_list.refreshComplete(); //下拉刷新完成
                            }
                            break;
                        case 1:
                            if (list.size() > 0) {
                                mList.addAll(list); //直接将数据追加到后面
                                collect_list.loadMoreComplete();
                                collectAdapter.notifyDataSetChanged();
                            }else {
                                collect_list.loadMoreComplete();
                            }
                            break;
                        case 2:
                            if (list.size() > 0) {
                                mList.addAll(list); //将数据追加到后面
                                collectAdapter.notifyDataSetChanged();
                                collect_list.setNoMore(true);
                            }else {
                                collect_list.loadMoreComplete();
                            }
                            break;
                    }
                } else {
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if(u.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(CollectActivity.this,LoginActivity.class);
                    }
                }
            }
        });
    }

    @Override
    public void onChangeData(int id, String cid) {
        Util.showLoadingDialog(this,"加载中");
        if (cid.length() == 0) {
            Map<String, String> map = new HashMap<>();
            map.put("collectUserId", id + "");
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.ADDCOLLECTIONSUSER)
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
                    UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                    if (userBean.isSuccess()) {
                        UIHelper.ToastMessageCenter(context, "收藏成功", 200);
                        initData();
                    } else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(CollectActivity.this,LoginActivity.class);
                        }
                    }
                }
            });
        } else {
            OkHttpUtils.post()
                    .url(URLs.IMGEURL + URLs.DELETECOLLECTIONSUSER)
                    .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                    .addParams("cId", cid)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    Util.closeLoadingDialog(context);
                    UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                    if (userBean.isSuccess()) {
                        UIHelper.ToastMessageCenter(context, "取消收藏成功", 200);
                        initData();
                    } else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                        if(userBean.getCode().equals("401")){
                            EMClient.getInstance().logout(true);
                            SharedPreferencesUtil.delete(context,"LtAreaPeople");
                            UIHelper.OpenActivity(CollectActivity.this,LoginActivity.class);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClickItem(int id) {
        Intent intent = new Intent(CollectActivity.this, CompleteInformationFK.class);
        intent.putExtra("id", String.valueOf(id));
        startActivity(intent);
    }

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if (userEvent.msg.equals("4")) {
            isFrist = false;
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
