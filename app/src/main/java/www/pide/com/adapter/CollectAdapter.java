package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;
import www.pide.com.bean.User;
import www.pide.com.ui.view.ZQImageViewRoundOval;

/**
 * Created by Administrator on 2020/3/29.
 */
public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.MyViewHolder> {
    private Context context;
    private List<User> list = new ArrayList<>();
    private OnPriceClickListener onPriceClickListener;

    public CollectAdapter(Context context, List<User> list, OnPriceClickListener onPriceClickListener) {
        this.context = context;
        this.list = list;
        this.onPriceClickListener = onPriceClickListener;
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ZQImageViewRoundOval p_img;
        ImageView collect;
        TextView name, company;
        RelativeLayout r_collect, r_item;

        public MyViewHolder(View convertView) {
            super(convertView);
            this.name = (TextView) convertView.findViewById(R.id.name);
            this.company = (TextView) convertView.findViewById(R.id.company);
            this.p_img = (ZQImageViewRoundOval) convertView.findViewById(R.id.p_img);
            this.collect = (ImageView) convertView.findViewById(R.id.collect);
            this.r_collect = (RelativeLayout) convertView.findViewById(R.id.r_collect);
            this.r_item = (RelativeLayout) convertView.findViewById(R.id.r_item);
        }
    }
    @Override
    public CollectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_item,null);
        CollectAdapter.MyViewHolder myViewHolder=new CollectAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(CollectAdapter.MyViewHolder holder, int position) {
        CollectAdapter.MyViewHolder myViewHolder = (CollectAdapter.MyViewHolder) holder;
        final User user = list.get(position);
        myViewHolder.name.setText(user.getName());
        myViewHolder.company.setText(user.getCompany());
        myViewHolder.collect.setVisibility(View.VISIBLE);
        if(user.getcId()==null){
            myViewHolder.collect.setImageResource(R.mipmap.collect);
        }else {
            myViewHolder.collect.setImageResource(R.mipmap.collect_onclick);
        }
        myViewHolder.p_img.setType(ZQImageViewRoundOval.TYPE_ROUND);
        myViewHolder.p_img.setRoundRadius(4);//矩形凹行大小
        Glide.with(context)
                .load(user.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(myViewHolder.p_img);
        myViewHolder.r_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPriceClickListener != null) {
                    String mcid="";
                    if(user.getcId()!=null){
                        mcid=user.getcId();
                    }
                    onPriceClickListener.onChangeData(user.getUserId(),mcid);
                }
            }
        });
        myViewHolder.r_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPriceClickListener != null) {
                    onPriceClickListener.onClickItem(user.getUserId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnPriceClickListener {
        //通知activtiy更新数据
        void onChangeData(int id, String status);
        void onClickItem(int id);
    }
}
