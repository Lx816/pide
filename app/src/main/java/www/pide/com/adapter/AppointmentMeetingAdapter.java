package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;
import www.pide.com.bean.MeetingBean;

/**
 * Created by Administrator on 2020/3/29.
 */
public class AppointmentMeetingAdapter extends BaseAdapter {
    private Context context;
    private List<MeetingBean> list=new ArrayList<>();
    private String end="";
    public AppointmentMeetingAdapter(Context context, List<MeetingBean> list,String end) {
      this.context=context;
        this.list=list;
        this.end=end;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.appointment_meeting_fragment_item, null);
            myViewHolder.meeting_title= (TextView) convertView.findViewById(R.id.meeting_title);
            myViewHolder.meeting_date= (TextView) convertView.findViewById(R.id.meeting_date);
            myViewHolder.name= (TextView) convertView.findViewById(R.id.name);
            myViewHolder.date= (TextView) convertView.findViewById(R.id.date);
            myViewHolder.c_name= (TextView) convertView.findViewById(R.id.c_name);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
       /* MeetingBean meetingBean=list.get(position);
        myViewHolder.meeting_title.setText(meetingBean.getMeeting_title());
        if(end.equals("")){
            myViewHolder.meeting_date.setVisibility(View.VISIBLE);
            myViewHolder.meeting_date.setText(meetingBean.getMeeting_date());
        }
        myViewHolder.name.setText(meetingBean.getName());
        myViewHolder.date.setText(meetingBean.getDate());
        myViewHolder.c_name.setText(meetingBean.getC_name());*/
        return convertView;
    }
    class MyViewHolder {
        TextView meeting_title,meeting_date,name,date,c_name;
    }
}
