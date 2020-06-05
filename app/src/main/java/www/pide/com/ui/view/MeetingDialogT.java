package www.pide.com.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import www.pide.com.R;
import www.pide.com.bean.Sticky;

public class MeetingDialogT extends Dialog {
    private String mname;
    private Activity context;
    public MeetingDialogT(Activity mcontext, String t1, int dialogTheme) {
        super(mcontext,dialogTheme);
        context=mcontext;
        mname=t1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_t_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView name=findViewById(R.id.name);
        name.setText(mname);
        Button cancel=findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button sure=findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new Sticky(mname));
                context.finish();
            }
        });
    }
}
