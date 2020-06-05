package www.pide.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import www.pide.com.R;
import www.pide.com.ui.activity.SearchConnectionItemActivity;
import www.pide.com.utils.RecordSQLiteOpenHelper;

/**
 * Created by Administrator on 2020/3/29.
 */
public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<String> list = new ArrayList<>();
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private OnPriceClickListener onPriceClickListener;

    public HistoryAdapter(Context context, List<String> list, RecordSQLiteOpenHelper helper, SQLiteDatabase db,OnPriceClickListener listener) {
        this.context = context;
        this.list = list;
        this.helper = helper;
        this.db = db;
        this.onPriceClickListener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder = null;
        if (convertView == null) {
            myViewHolder = new MyViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.history, null);
            myViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            myViewHolder.clear = (ImageView) convertView.findViewById(R.id.clear);
            myViewHolder.r_item= (RelativeLayout) convertView.findViewById(R.id.r_item);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        final String name = list.get(position);
        myViewHolder.name.setText(name);
        myViewHolder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = helper.getWritableDatabase();
                db.execSQL("delete from records where name ='" + name+"'");
                db.close();
                if (onPriceClickListener != null) {
                    onPriceClickListener.onChangeData();
                }
            }
        });
        myViewHolder.r_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,SearchConnectionItemActivity.class);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class MyViewHolder {
        ImageView clear;
        TextView name;
        RelativeLayout r_item;
    }
    public interface OnPriceClickListener {
        //通知activtiy更新数据
        void onChangeData();
    }
}
