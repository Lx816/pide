package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import www.pide.com.adapter.SearchAdapter;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.RecordSQLiteOpenHelper;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/3/29.
 */
public class SearchConnectionItemActivity extends BaseActivity {
    private EditText input;
    private Intent intent;
    private RelativeLayout back;
    private ImageView search;
    private Context context;
    private SearchAdapter searchAdapter;
    private XRecyclerView listView;
    private List<User> list=new ArrayList<User>();
    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);;
    private SQLiteDatabase db;
    private LinearLayout no_w;
    private int times = 1;
    private  List<User>  mList=new ArrayList<>();
    private String sign="";
    private UserBean userBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context,R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_connection_item);
        intent=getIntent();
        initView();
        initOnclick();
    }

    private void initView() {
        input= (EditText) findViewById(R.id.input);
        back= (RelativeLayout) findViewById(R.id.back);
        search= (ImageView) findViewById(R.id.search);
        listView= (XRecyclerView) findViewById(R.id.listView);
        no_w= (LinearLayout) findViewById(R.id.no_w);
        if(intent.getStringExtra("name")==""){
            input.setText("");
            input.setHint("搜索想找的人");
        }else {
            input.setText(intent.getStringExtra("name"));
            search();
        }
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return false;
            }
        });
        //1)初始化RecyclerView设置
        listView.setPullRefreshEnabled(true);
        listView.setLoadingMoreEnabled(true);
        listView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        listView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        //2)添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchConnectionItemActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        //5)实现 下拉刷新和加载更多 接口
        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                times = 1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.clear(); //先要清掉数据
                        search1(0);
                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                if (times < 20) {//加载20次后，就不再加载更多
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            search1(1);
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            search1(2);
                        }
                    }, 1000);
                }
                times++;
            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        finish();
                        break;
                    case R.id.search:
                        search();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        search.setOnClickListener(listener);
    }

    private void search() {
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        String minput=input.getText().toString().trim();
        if(minput.length()>0){
            Util.showLoadingDialog(this,"加载中");
            boolean hasData = hasData(input.getText().toString().trim());
            if (!hasData) {
                insertData(input.getText().toString().trim());
                EventBus.getDefault().post(new Sticky("3"));
            }
            Map<String,String> map=new HashMap<>();
            map.put("currentTime",mcurrentTime);
            map.put("sign", sign);
            map.put("name", minput);
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.USETLIST+"?pageNum=1&pageSize=10")
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(map))
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    userBean = JsonUtil.fromJson(response, UserBean.class);
                    if(userBean.isSuccess()){
                        list = new ArrayList<User>();
                        list = userBean.getUserD().getRecords();
                        mList=new ArrayList<>();
                        mList.addAll(list);
                        if(list.size()>0) {
                            Util.closeLoadingDialog(context);
                            no_w.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            searchAdapter = new SearchAdapter(context, mList);
                            listView.setAdapter(searchAdapter);
                            searchAdapter.setOnItemClickListener(new SearchAdapter.ClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    String userId= SharedPreferencesUtil.read(context, "LtAreaPeople", "USERID");
                                    if(mList.get(position-1).getUserId()==Integer.parseInt(userId)){
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
                            listView.setVisibility(View.GONE);
                        }
                    }else {
                        Util.closeLoadingDialog(context);
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                    }
                }
            });
        }
    }
    private void search1(int mtype) {
        String minput=input.getText().toString().trim();
        if(minput.length()>0){
            boolean hasData = hasData(input.getText().toString().trim());
            if (!hasData) {
                insertData(input.getText().toString().trim());
                EventBus.getDefault().post(new Sticky("3"));
            }
            String mcurrentTime=QTime.QTime();
            sign = QTime.QSign(mcurrentTime);
            Map<String,String> map=new HashMap<>();
            map.put("currentTime",mcurrentTime);
            map.put("sign", sign);
            map.put("name", minput);
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.USETLIST+"?pageNum="+times+"&pageSize=10")
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(map))
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    userBean = JsonUtil.fromJson(response, UserBean.class);
                    if(userBean.isSuccess()){
                        list = new ArrayList<User>();
                        list = userBean.getUserD().getRecords();
                        switch (mtype){
                            case 0:
                                if(list.size()>0){
                                    mList.addAll(list); //再将数据插入到前面
                                    searchAdapter.notifyDataSetChanged();
                                    listView.refreshComplete(); //下拉刷新完成
                                }else {
                                    listView.refreshComplete(); //下拉刷新完成
                                }
                                break;
                            case 1:
                                if(list.size()>0){
                                    mList.addAll(list); //直接将数据追加到后面
                                    listView.loadMoreComplete();
                                    searchAdapter.notifyDataSetChanged();
                                }else {
                                    listView.loadMoreComplete();
                                }
                                break;
                            case 2:
                                if(list.size()>0){
                                    mList.addAll(list); //将数据追加到后面
                                    searchAdapter.notifyDataSetChanged();
                                    listView.setNoMore(true);
                                }else {
                                    listView.loadMoreComplete();
                                }
                                break;
                        }
                    }else {
                        UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                    }
                }
            });
        }
    }
    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }
}
