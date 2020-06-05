package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import www.pide.com.R;
import www.pide.com.bean.User;
import www.pide.com.ui.view.XCRoundImageView;

import java.util.List;
public class MineCardListViewAdapter extends RecyclerView.Adapter<MineCardListViewAdapter.MyViewHolder> {
    private Context context;
    private List<User> items;
    private ClickListener clickListener;
    public interface ClickListener {
                void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,company,position,possession_resources,need_resources,address,phone,professions;
        public LinearLayout ll_item;
        XCRoundImageView user_img;
        public MyViewHolder(View convertView) {
            super(convertView);
            this.name= (TextView) convertView.findViewById(R.id.name);
            this.company= (TextView) convertView.findViewById(R.id.company);
            this.position= (TextView) convertView.findViewById(R.id.position);
            this.possession_resources= (TextView) convertView.findViewById(R.id.possession_resources);
            this.need_resources= (TextView) convertView.findViewById(R.id.need_resources);
            this.address= (TextView) convertView.findViewById(R.id.address);
            this.phone= (TextView) convertView.findViewById(R.id.phone);
            this.professions= (TextView) convertView.findViewById(R.id.professions);
            this.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            this.user_img = (XCRoundImageView) convertView.findViewById(R.id.user_img);
        }
    }

    public MineCardListViewAdapter(Context context, List<User> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mine_card_listview_item,null);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User User = items.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.name.setText(User.getName());
        myViewHolder.company.setText(User.getCompany());
        myViewHolder.position.setText(User.getPosition());
        myViewHolder.professions.setText(User.getProfession_name());
        if(User.getPossession_resources()==null){
            myViewHolder.possession_resources.setText("无");
        }else {
            myViewHolder.possession_resources.setText(User.getPossession_resources());
        }
        if(User.getNeed_resources()==null){
            myViewHolder.need_resources.setText("无");
        }else {
            myViewHolder.need_resources.setText(User.getNeed_resources());
        }
        if(User.getAddress()==null || User.getAddress().length()==0){
            myViewHolder.address.setText("无");
        }else {
            myViewHolder.address.setText(User.getAddress());
        }
        myViewHolder.phone.setText(User.getPhone());
        Glide.with(context)
                .load(User.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(myViewHolder.user_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                clickListener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
