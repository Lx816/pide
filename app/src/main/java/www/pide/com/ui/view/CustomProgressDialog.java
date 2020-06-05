package www.pide.com.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import www.pide.com.R;

/**
 * 自定义的加载提示框
 * @author Administrator
 *
 */
public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context){
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context){
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.dialog_coustom);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        RelativeLayout relativeLayout= (RelativeLayout) customProgressDialog.findViewById(R.id.re_dialog);
        relativeLayout.getBackground().setAlpha(235);
        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus){

        if (customProgressDialog == null){
            return;
        }


    }

    /**
     *
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
        return customProgressDialog;
    }

    public CustomProgressDialog setMessagebotom(String strMessage){
        TextView tv_msg = (TextView)customProgressDialog.findViewById(R.id.tv_msg);
        if (tv_msg != null){
            tv_msg.setText(strMessage);
        }

        return customProgressDialog;
    }
}
