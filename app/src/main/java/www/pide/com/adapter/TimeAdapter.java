package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/3/29.
 */
public class TimeAdapter extends BaseAdapter {
    private Context context;
    private List<String> list = new ArrayList<>();
    private int m=-1;

    public TimeAdapter(Context context, List<String> list, int i) {
        this.context = context;
        this.list = list;
        m=i;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_time_item, null);
            myViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            myViewHolder.sure= (ImageView) convertView.findViewById(R.id.sure);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        final String name = list.get(position);
        myViewHolder.name.setText(name);
        if(m==position){
            myViewHolder.sure.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.sure.setVisibility(View.GONE);
        }
        return convertView;
    }

    class MyViewHolder {
        TextView name;
        ImageView sure;
    }
}
