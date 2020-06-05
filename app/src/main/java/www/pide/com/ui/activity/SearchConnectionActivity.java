package www.pide.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;
import www.pide.com.adapter.HistoryAdapter;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.Sticky;
import www.pide.com.utils.RecordSQLiteOpenHelper;

/**
 * Created by Administrator on 2020/3/29.
 */
public class SearchConnectionActivity extends BaseActivity implements HistoryAdapter.OnPriceClickListener {
    private RelativeLayout back;
    private ImageView search,close;
    private EditText input;
    private ListView listView;
    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);;
    private SQLiteDatabase db;
    private HistoryAdapter historyAdapter;
    private Context context;
    private TextView clear;
    private RelativeLayout w_no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context,R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_connection);
        //注册订阅者
        EventBus.getDefault().register(this);
        initView();
        initOnclick();
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        search = (ImageView) findViewById(R.id.search);
        input= (EditText) findViewById(R.id.input);
        input.addTextChangedListener(watcher);
        close= (ImageView) findViewById(R.id.close);
        listView= (ListView) findViewById(R.id.listView);
        clear= (TextView) findViewById(R.id.clear);
        w_no= (RelativeLayout) findViewById(R.id.w_no);
        queryData("");
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return false;
            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        finish();
                        break;
                    case R.id.search:
                        search();
                        break;
                    case R.id.close:
                        close.setVisibility(View.GONE);
                        input.setText("");
                        input.setHint("搜索想找的人");
                        break;
                    case R.id.clear:
                        deleteData();
                        queryData("");
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        search.setOnClickListener(listener);
        close.setOnClickListener(listener);
        clear.setOnClickListener(listener);
    }

    private void search() {
        String mi=input.getText().toString().trim();
        if(mi.length()>0){
            boolean hasData = hasData(mi);
            if (!hasData) {
                insertData(mi);
                queryData("");
            }
            Intent intent=new Intent(SearchConnectionActivity.this,SearchConnectionItemActivity.class);
            intent.putExtra("name",mi);
            startActivity(intent);
        }
    }
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          if(input.getText().toString().trim().length()>0){
              close.setVisibility(View.VISIBLE);
          }else {
              close.setVisibility(View.GONE);
              input.setHint("搜索想找的人");
          }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
// TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
// TODO Auto-generated method stub

        }
    };
    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象
        List<String> list=new ArrayList<>();
        while (cursor.moveToNext()) {
           String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(name);
        }
        if(list.size()>0){
            w_no.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            historyAdapter = new HistoryAdapter(context,list,helper,db,this);
            // 设置适配器
            listView.setAdapter(historyAdapter);
            historyAdapter.notifyDataSetChanged();
        }else {
            w_no.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
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
    /**
     * 清空数据
     */
    private void deleteDataItem(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records where name ="+tempName);
        db.close();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    @Override
    public void onChangeData() {
        queryData("");
    }
    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("3")){
            queryData("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
