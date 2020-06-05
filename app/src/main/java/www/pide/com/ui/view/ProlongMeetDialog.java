package www.pide.com.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import www.pide.com.R;
import www.pide.com.bean.Sticky;

public class ProlongMeetDialog extends Dialog {
    private Activity context;
    public ProlongMeetDialog(Activity mcontext, int dialogTheme) {
        super(mcontext,dialogTheme);
        context=mcontext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prolong_meet);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        Button cancel=findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new Sticky("NoProlongMeet"));
            }
        });
        Button sure=findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new Sticky("ProlongMeet"));
            }
        });
    }
}
