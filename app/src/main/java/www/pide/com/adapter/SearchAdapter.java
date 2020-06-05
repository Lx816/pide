package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private Context context;
    private List<User> list=new ArrayList<>();
    private SearchAdapter.ClickListener clickListener;
    private String yqm="";
    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(SearchAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public SearchAdapter(Context context, List<User> list) {
      this.context=context;
        this.list=list;
    }
    public SearchAdapter(Context context, List<User> list,String yqm) {
      this.context=context;
        this.list=list;
        this.yqm=yqm;
    }
    static class MyViewHolder extends RecyclerView.ViewHolder{
        ZQImageViewRoundOval p_img;
        TextView name,company;
        ImageView collect;
        public MyViewHolder(View convertView) {
            super(convertView);
            this.name = (TextView) convertView.findViewById(R.id.name);
            this.company = (TextView) convertView.findViewById(R.id.company);
            this.p_img = (ZQImageViewRoundOval) convertView.findViewById(R.id.p_img);
            this.collect=convertView.findViewById(R.id.yqm);
        }
    }
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_item,null);
        SearchAdapter.MyViewHolder myViewHolder=new SearchAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.MyViewHolder holder, int position) {
        User user=list.get(position);
        SearchAdapter.MyViewHolder myViewHolder = (SearchAdapter.MyViewHolder) holder;
        myViewHolder.name.setText(user.getName());
        myViewHolder.company.setText(user.getCompany());
        myViewHolder.p_img.setType(ZQImageViewRoundOval.TYPE_ROUND);
        myViewHolder.p_img.setRoundRadius(4);//矩形凹行大小
        Glide.with(context)
                .load(user.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(myViewHolder.p_img);
        if(yqm.length()>0){
           myViewHolder.collect.setVisibility(View.VISIBLE);
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                clickListener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
