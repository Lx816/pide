package www.pide.com.utils;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import www.pide.com.R;
import www.pide.com.bean.MicroCardsBean;
import www.pide.com.ui.activity.AddCardActivity;
import www.pide.com.ui.view.CustomProgressDialog;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/3/13.
 */
public class UIHelper {
    public static void OpenActivity(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
    public static void OpenActivity(Context context, Class clazz,String str) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("titleName",(Serializable) str);
        context.startActivity(intent);
    }

    public static void OpenActivity(Context context, Class<AddCardActivity> clazz, MicroCardsBean microCardsBean) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("microCardsBean",(Serializable) microCardsBean );
        context.startActivity(intent);
    }
    /**
     * 弹出ToastCenter消息
     *
     * @param msg
     */
    static Toast t1 = null;
    public static void ToastMessageCenter(Context cont, String msg, int time) {
        if(cont==null){
            return;
        }
        LayoutInflater flater=LayoutInflater.from(cont);
        View v=flater.inflate(R.layout.toast_tv2, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_toast);
        tv.setText(msg);
        if (t1 == null) {
            t1 = new Toast(cont);
            t1.setView(v);
            t1.setGravity(Gravity.CENTER, 0, -50);
            t1.setDuration(time);
        } else {
            tv.setText(msg);
            t1.setView(v);
            t1.setDuration(time);
        }

        t1.show();
    }
    /**
     * 弹出ToastCenter消息
     *
     * @param msg
     */
    static Toast t2 = null;
    public static void ToastMessageTop(Context cont, String msg, int time) {
        if(cont==null){
            return;
        }
        LayoutInflater flater=LayoutInflater.from(cont);
        View v=flater.inflate(R.layout.toast_tv3, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_toast);
        tv.setText(msg);
        if (t2 == null) {
            t2 = new Toast(cont);
            t2.setView(v);
            t2.setGravity(Gravity.TOP, 0, -50);
            t2.setDuration(time);
        } else {
            tv.setText(msg);
            t2.setView(v);
            t2.setDuration(time);
        }
        t2.show();
    }
    public static CustomProgressDialog showdialog(Context context, String mes){
        CustomProgressDialog proDialog = null;
        if(proDialog==null){
            proDialog=CustomProgressDialog.createDialog(context);
            proDialog.setMessagebotom(mes);
        }
        //设置进度条是否可以按退回键取消
        proDialog.setCancelable(true);
        //设置点击进度对话框外的区域对话框不消失
        proDialog.setCanceledOnTouchOutside(false);
        proDialog.show();
        return  proDialog;
    }
}
