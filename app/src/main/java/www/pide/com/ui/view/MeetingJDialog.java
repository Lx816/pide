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

import www.pide.com.R;

public class MeetingJDialog extends Dialog {
    private String mm_title;
    private Activity activity;
    public MeetingJDialog(Activity mcontext, String t1) {
        super(mcontext);
        mm_title=t1;
        activity=mcontext;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_jdialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView m_title=findViewById(R.id.m_title);
        m_title.setText(mm_title);
        Button sure=findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
