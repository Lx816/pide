package www.pide.com.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/3/26.
 */
public class CenterWindow extends PopupWindow {
    private View menuview;
    private Button to_login;
    private TextView t1,t2;

    /**
     * @param context
     * @param itemsOnclick
     */
    public CenterWindow(Activity context, View.OnClickListener itemsOnclick, String mt1, String mt2, String mt3) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuview = inflater.inflate(R.layout.center_popupwindows, null);
        to_login= (Button) menuview.findViewById(R.id.to_login);
        t1=(TextView) menuview.findViewById(R.id.t1);
        t2=(TextView) menuview.findViewById(R.id.t2);
        t1.setText(mt1);
        t2.setText(mt2);
        to_login.setText(mt3);
        to_login.setOnClickListener(itemsOnclick);
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
