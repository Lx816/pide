package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.pide.com.R;
import www.pide.com.bean.SendBean;

public class SendAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<SendBean> data;
    private static final int TYPEONE = 1;
    private static final int TYPETWO = 2;

    public SendAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<SendBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case TYPEONE:
                View view = LayoutInflater.from(context).inflate(R.layout.item_left,parent,false);
                holder = new OneViewHolder(view);
                break;
            case TYPETWO:
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_right,parent,false);
                holder = new TwoViewHolder(view1);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType){
            case TYPEONE:
                OneViewHolder oneViewHolder = (OneViewHolder) holder;
//                oneViewHolder.tv1.setText(data.get(position).getData());
//                oneViewHolder.name1.setText(data.get(position).getName());
//                oneViewHolder.time1.setText(data.get(position).getTime());
                break;
            case TYPETWO:
                TwoViewHolder twoViewHolder = (TwoViewHolder) holder;
//                twoViewHolder.tv2.setText(data.get(position).getData());
//                twoViewHolder.name2.setText(data.get(position).getName());
//                twoViewHolder.time2.setText(data.get(position).getTime());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return  data != null && data.size() > 0 ? data.size() : 0;
    }

    class OneViewHolder extends RecyclerView.ViewHolder{
        private TextView tv1;
        private TextView name1,time1;
        public OneViewHolder(View itemView) {
            super(itemView);
//            tv1 = (TextView) itemView.findViewById(R.id.tv);
//            name1 = (TextView) itemView.findViewById(R.id.tv_name);
//            time1 = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    class TwoViewHolder extends RecyclerView.ViewHolder{
        private TextView tv2;
        private TextView name2,time2;
        public TwoViewHolder(View itemView) {
            super(itemView);
//            tv2 = (TextView) itemView.findViewById(R.id.tv2);
//            name2 = (TextView) itemView.findViewById(R.id.tv_name2);
//            time2 = (TextView) itemView.findViewById(R.id.tv_time2);
        }
    }

}
