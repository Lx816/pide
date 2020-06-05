package www.pide.com.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

public class Util {


    private static ProgressDialog processDia;
    private static ZLoadingDialog dialog;

    /**
     * 显示加载中对话框
     *
     * @param context
     */
    public static void showLoadingDialog(Context context, String message) {
        if(dialog==null){
            dialog = new ZLoadingDialog(context);
            dialog.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#0076FF"))//颜色
                    .setHintText("Loading...")
                    .show();
        }
    }

    /**
     * 关闭加载对话框
     */
    public static void closeLoadingDialog(Context context) {
        if (dialog!=null){
            dialog.cancel();
            dialog = null;
        }
    }

}
