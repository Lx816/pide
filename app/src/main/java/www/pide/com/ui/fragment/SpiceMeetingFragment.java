package www.pide.com.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;

import www.pide.com.R;
import www.pide.com.ui.activity.AllMeetingActivity;
import www.pide.com.ui.activity.AppointmentMeetingActivity;
import www.pide.com.ui.activity.CreatMeetingActivity;
import www.pide.com.ui.activity.JionMeetingActivity;
import www.pide.com.utils.UIHelper;

public class SpiceMeetingFragment extends Fragment {
    View mRootView;
    private RelativeLayout all_meeting,r1,r2;
    private Context context;
    private Button b1,b2,b3;
    private ImageView close1,close2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.spice_meeting_fragment, container, false);
        context=getActivity();
        initView();
        initOnclick();
        return mRootView;
    }

    private void initView() {
        all_meeting = (RelativeLayout) mRootView.findViewById(R.id.all_meeting);
        b1= (Button) mRootView.findViewById(R.id.b1);
        b2= (Button) mRootView.findViewById(R.id.b2);
        b3= (Button) mRootView.findViewById(R.id.b3);
        close1=mRootView.findViewById(R.id.close1);
        close2=mRootView.findViewById(R.id.close2);
        r1=mRootView.findViewById(R.id.r1);
        r2=mRootView.findViewById(R.id.r2);
    }

    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.all_meeting:
                        UIHelper.OpenActivity(context, AllMeetingActivity.class);
                        break;
                    case R.id.b1:
                        UIHelper.OpenActivity(context, CreatMeetingActivity.class);
                        break;
                    case R.id.b2:
                        UIHelper.OpenActivity(context, JionMeetingActivity.class);
                        break;
                    case R.id.b3:
                        UIHelper.OpenActivity(context, AppointmentMeetingActivity.class);
                        break;
                    case R.id.close1:
                        r1.setVisibility(View.GONE);
                        break;
                    case R.id.close2:
                        r2.setVisibility(View.GONE);
                        break;
                }
            }
        };
        all_meeting.setOnClickListener(listener);
        b1.setOnClickListener(listener);
        b2.setOnClickListener(listener);
        b3.setOnClickListener(listener);
        close1.setOnClickListener(listener);
        close2.setOnClickListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
