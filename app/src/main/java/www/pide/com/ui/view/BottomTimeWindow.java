package www.pide.com.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import www.pide.com.R;
import www.pide.com.adapter.TimeAdapter;
import www.pide.com.bean.Sticky;

/**
 * Created by Administrator on 2020/3/26.
 */
public class BottomTimeWindow extends PopupWindow {
    private View menuview;
    private TimeAdapter adapter;
    private TextView h_t;

    /**
     * @param context
     * @param itemsOnClick
     */
    public BottomTimeWindow(final Activity context, final List<String> listData, View.OnClickListener itemsOnClick,String h_time,int position) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuview = inflater.inflate(R.layout.item_time_popupwindows, null);
        adapter=new TimeAdapter(context,listData,position);
        final ListView listView = (ListView) menuview.findViewById(R.id.time_listview);
        h_t=menuview.findViewById(R.id.h_t);
        h_t.setText(h_time);
        listView.setAdapter(adapter);
        TextView cancel= (TextView) menuview.findViewById(R.id.cancel);
        cancel.setOnClickListener(itemsOnClick);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter=new TimeAdapter(context,listData,i);
                listView.setAdapter(adapter);
                if(h_time.length()>0){
                    EventBus.getDefault().post(new Sticky("0",listData.get(i),i));
                }else {
                    EventBus.getDefault().post(new Sticky("1",listData.get(i),i));
                }
                dismiss();
            }
        });
        //设置SelectPicPopupWindow的View
        this.setContentView(menuview);
        //设置SelectPicPopupWindow**弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        //修改高度显示，解决被手机底部虚拟键挡住的问题 by黄海杰 at:2015-4-30
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //menuview添加ontouchlistener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        menuview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = menuview.findViewById(R.id.ll_popup).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
