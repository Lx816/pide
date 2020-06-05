package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class ChartPeopleAdapter extends BaseAdapter {
    private Context context;
    private List<User> list = new ArrayList<>();

    public ChartPeopleAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.participant_item, null);
            myViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            myViewHolder.company = (TextView) convertView.findViewById(R.id.company);
            myViewHolder.p_img = (ZQImageViewRoundOval) convertView.findViewById(R.id.p_img);
            myViewHolder.code_top = (RelativeLayout) convertView.findViewById(R.id.code_top);
            myViewHolder.code = (TextView) convertView.findViewById(R.id.code);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        User bean = list.get(position);
        myViewHolder.name.setText(bean.getName());
        myViewHolder.company.setText(bean.getCompany());
        myViewHolder.p_img.setType(ZQImageViewRoundOval.TYPE_ROUND);
        myViewHolder.p_img.setRoundRadius(4);//矩形凹行大小
        Glide.with(context)
                .load(bean.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.error))
                .apply(new RequestOptions().error(R.drawable.error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(myViewHolder.p_img);
        myViewHolder.code_top.setVisibility(View.GONE);
        myViewHolder.code.setText((position + 1) + "");
        return convertView;
    }

    class MyViewHolder {
        TextView name, company, code;
        ZQImageViewRoundOval p_img;
        RelativeLayout code_top;
    }
}
