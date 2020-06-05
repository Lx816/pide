package www.pide.com.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;
import www.pide.com.adapter.SearchAdapter;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.ui.view.RoundImageView;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

public class YQCodeActivity extends BaseActivity {
    private RoundImageView user_img;
    private RelativeLayout back;
    private XRecyclerView listview;
    private SearchAdapter searchAdapter;
    private Context context;
    private List<User> list = new ArrayList<>();
    private Intent intent;
    private TextView recommend_code;
    private int times = 1;
    private List<User> mList = new ArrayList<>();
    private LinearLayout no_listview;
    private TextView people_number;
    private Button fz_recommend_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_code_activity);
        context = this;
        intent = getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        user_img = findViewById(R.id.user_img);
        back = findViewById(R.id.back);
        listview = findViewById(R.id.listview);
        recommend_code = findViewById(R.id.recommend_code);
        no_listview = findViewById(R.id.no_listview);
        people_number = findViewById(R.id.people_number);
        fz_recommend_code = findViewById(R.id.fz_recommend_code);
        //1)初始化RecyclerView设置
        listview.setPullRefreshEnabled(true);
        listview.setLoadingMoreEnabled(true);
        listview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        listview.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        //2)添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(YQCodeActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listview.setLayoutManager(layoutManager);
        initData();

        //5)实现 下拉刷新和加载更多 接口
        listview.setLoadingListener(new XRecyclerView.LoadingListener() {
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
        initData();
        if (intent.getExtras() != null) {
            recommend_code.setText(intent.getStringExtra("recommend_code"));
            Glide.with(context)
                    .load(intent.getStringExtra("user_img"))
                    .apply(new RequestOptions().placeholder(R.drawable.error))
                    .apply(new RequestOptions().error(R.drawable.error))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(user_img);
        }
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back:
                        finish();
                        break;
                    case R.id.fz_recommend_code:
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", recommend_code.getText().toString().trim());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        UIHelper.ToastMessageCenter(context, "已拷贝", 200);
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        fz_recommend_code.setOnClickListener(listener);
    }

    private void initData() {
        Util.showLoadingDialog(this, "加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYINVITEUSERLIST + "?pageNum=1&pageSize=10")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    list = new ArrayList<>();
                    list = u.getUserD().getRecords();
                    mList = new ArrayList<>();
                    mList.addAll(list);
                    if (list.size() > 0) {
                        Util.closeLoadingDialog(context);
                        no_listview.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchAdapter(context, mList, "yqm");
                        listview.setAdapter(searchAdapter);
                        searchAdapter.setOnItemClickListener(new SearchAdapter.ClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                String userId = SharedPreferencesUtil.read(context, "LtAreaPeople", "USERID");
                                if (mList.get(position - 1).getUserId() == Integer.parseInt(userId)) {
                                    Intent intent = new Intent(context, CompleteInformation.class);
                                    intent.putExtra("id", String.valueOf(mList.get(position - 1).getUserId()));
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, CompleteInformationFK.class);
                                    intent.putExtra("id", String.valueOf(mList.get(position - 1).getUserId()));
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        Util.closeLoadingDialog(context);
                        no_listview.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    }
                    people_number.setText("（共" + list.size() + "人）");
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if (u.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }

    private void initData1(int mtype) {
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYINVITEUSERLIST + "?pageNum=" + times + "&pageSize=10")
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                UserBean u = JsonUtil.fromJson(response, UserBean.class);
                if (u.isSuccess()) {
                    list = new ArrayList<>();
                    list = u.getUserD().getRecords();
                    switch (mtype) {
                        case 0:
                            if (list.size() > 0) {
                                mList.addAll(list); //再将数据插入到前面
                                searchAdapter.notifyDataSetChanged();
                                listview.refreshComplete(); //下拉刷新完成
                            } else {
                                listview.refreshComplete(); //下拉刷新完成
                            }
                            break;
                        case 1:
                            if (list.size() > 0) {
                                mList.addAll(list); //直接将数据追加到后面
                                listview.loadMoreComplete();
                                searchAdapter.notifyDataSetChanged();
                            } else {
                                listview.loadMoreComplete();
                            }
                            break;
                        case 2:
                            if (list.size() > 0) {
                                mList.addAll(list); //将数据追加到后面
                                searchAdapter.notifyDataSetChanged();
                                listview.setNoMore(true);
                            } else {
                                listview.loadMoreComplete();
                            }
                            break;
                    }
                    people_number.setText("（共" + list.size() + "人）");
                } else {
                    UIHelper.ToastMessageCenter(context, u.getMsg(), 200);
                    if (u.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                    }
                }
            }
        });
    }

}
