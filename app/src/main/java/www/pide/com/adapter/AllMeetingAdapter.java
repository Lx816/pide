package www.pide.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.pide.com.R;
import www.pide.com.bean.MeetingBean;
import www.pide.com.utils.QTime;

/**
 * Created by Administrator on 2020/3/29.
 */
public class AllMeetingAdapter extends RecyclerView.Adapter<AllMeetingAdapter.MyViewHolder> {
    private Context context;
    private List<MeetingBean.MeetingBeanRecords> list = new ArrayList<>();
    private String end = "";
    private AllMeetingAdapter.ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(AllMeetingAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView meeting_title, meeting_date, name, date, c_name;
        RelativeLayout ybm;

        public MyViewHolder(View convertView) {
            super(convertView);
            this.meeting_title = (TextView) convertView.findViewById(R.id.meeting_title);
            this.meeting_date = (TextView) convertView.findViewById(R.id.meeting_date);
            this.name = (TextView) convertView.findViewById(R.id.name);
            this.date = (TextView) convertView.findViewById(R.id.date);
            this.c_name = (TextView) convertView.findViewById(R.id.c_name);
            this.ybm = (RelativeLayout) convertView.findViewById(R.id.ybm);
        }
    }

    public AllMeetingAdapter(Context context, List<MeetingBean.MeetingBeanRecords> list, String end) {
        this.context = context;
        this.list = list;
        this.end = end;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_meeting_fragment_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(AllMeetingAdapter.MyViewHolder holder, int position) {
        MeetingBean.MeetingBeanRecords meetingBean = list.get(position);
        AllMeetingAdapter.MyViewHolder myViewHolder = (AllMeetingAdapter.MyViewHolder) holder;
        myViewHolder.meeting_title.setText(meetingBean.getmName());
        if (end.equals("appointment")) {
            String nowtime = QTime.QTime();
            String starttime = null;
            try {
                starttime = QTime.dateToStamp(meetingBean.getStartTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            myViewHolder.meeting_date.setVisibility(View.VISIBLE);
            if (Long.valueOf(starttime) - Long.valueOf(nowtime) > 0) {
                myViewHolder.meeting_date.setText(QTime.testPassedTime(Long.valueOf(starttime), Long.valueOf(nowtime)));
            }
        }
        myViewHolder.name.setText(meetingBean.getUserName());
        myViewHolder.date.setText(meetingBean.getStartTime());
        String c_name = "";
        if(meetingBean.getMeetingUserInfoVOList()!=null){
            if(meetingBean.getMeetingUserInfoVOList().size()>0){
                for (int i = 0; i < meetingBean.getMeetingUserInfoVOList().size(); i++) {
                    MeetingBean.MeetingUserInfoVOList b = meetingBean.getMeetingUserInfoVOList().get(i);
                    if (i == 0) {
                        if(b.getMeetUserName()!=null){
                            c_name = b.getMeetUserName();
                        }
                    } else {
                        if(b.getMeetUserName()!=null) {
                            c_name = c_name + "、" + b.getMeetUserName();
                        }
                    }
                }
                if(c_name.length()>17){
                    c_name = formatText(c_name, 15)+meetingBean.getMeetingUserInfoVOList().size()+"人";
                }
                myViewHolder.c_name.setText(c_name);
            }
        }
        if (meetingBean.getMiId() != null && end.equals("appointment")) {
            myViewHolder.ybm.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                clickListener.onItemClick(holder.itemView, pos - 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private static String regEx = "[\u4e00-\u9fa5]"; // 中文范围
    /**
     * 格式化字符串
     *
     * @param string   原始输入字符串
     * @param maxCount 最大字符限制，中文算作2个字符，其他都算1个字符
     * @return
     */
    private static String formatText(String string, int maxCount) {
        if (string.length() > maxCount) {
            string = subStrByLen(string, maxCount);
        }
        return string;
    }
    /**
     * 截取字符串，超出最大字数截断并显示"..."
     *
     * @param str    原始字符串
     * @param length 最大字数限制（以最大字数限制7个为例，当含中文时，length应设为2*7，不含中文时设为7）
     * @return 处理后的字符串
     */
    public static String subStrByLen(String str, int length) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int chCnt = getStrLen(str);
        // 超出进行截断处理
        if (chCnt > length) {
            int cur = 0;
            int cnt = 0;
            StringBuilder sb = new StringBuilder();
            while (cnt <= length && cur < str.length()) {
                char nextChar = str.charAt(cur);
                cnt++;
                if (cnt <= length) {
                    sb.append(nextChar);
                } else {
                    return sb.toString() + "...";
                }
                cur++;
            }
            return sb.toString() + "...";
        }
        // 未超出直接返回
        return str;
    }
    /**
     * 获取字符串中的中文字数
     */
    private static int getChCount(String str) {
        int cnt = 0;
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        ;
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 获取字符长度，中文算作2个字符，其他都算1个字符
     */
    public static int getStrLen(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        return str.length() + getChCount(str);
    }
}
