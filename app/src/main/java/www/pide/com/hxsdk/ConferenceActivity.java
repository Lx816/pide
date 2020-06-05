package www.pide.com.hxsdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamParam;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.media.EMCallSurfaceView;
import com.superrtc.sdk.VideoView;
import java.util.List;
import www.pide.com.R;
import www.pide.com.utils.JsonUtil;

public class ConferenceActivity extends Activity implements EMConferenceListener {
    private EMConferenceListener conferenceListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_main);
        conferenceListener=this;
        EMClient.getInstance().conferenceManager().addConferenceListener(conferenceListener);
        init();
    }
    /*
     初始化
     */
    private void init() {
        pubLocalStream();
    }
    private void pubLocalStream() {
        EMStreamParam param = new EMStreamParam();
        param.setStreamType(EMConferenceStream.StreamType.NORMAL);
        param.setVideoOff(true);
        param.setAudioOff(false);

        EMClient.getInstance().conferenceManager().publish(param, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String streamId) {
            }

            @Override
            public void onError(int error, String errorMsg) {
//                EMLog.e(TAG, "publish failed: error=" + error + ", msg=" + errorMsg);
            }
        });
    }
    /**
     * --------------------------------------------------------------------
     * 多人音视频会议回调方法
     */
    @Override
    public void onMemberJoined(EMConferenceMember member) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("safdsgfdh", JsonUtil.toJson(member));
                Toast.makeText(ConferenceActivity.this, member.memberName + "有新成员加入会议!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMemberExited(EMConferenceMember member) {

    }

    @Override
    public void onStreamAdded(EMConferenceStream stream) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EMCallSurfaceView memberView = new EMCallSurfaceView(DemoHelper.getInstance().getContext());
                memberView.setZOrderMediaOverlay(true);
                memberView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

                EMClient.getInstance().conferenceManager().subscribe(stream, memberView, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.d("safdsgfdh", JsonUtil.toJson(value)+"//////////////");
                    }

                    @Override
                    public void onError(int error, String errorMsg) {

                    }
                });
            }
        });
    }

    @Override
    public void onStreamRemoved(EMConferenceStream stream) {

    }

    @Override
    public void onStreamUpdate(EMConferenceStream stream) {

    }

    @Override
    public void onPassiveLeave(int error, String message) {

    }

    @Override
    public void onConferenceState(ConferenceState state) {

    }

    @Override
    public void onStreamStatistics(EMStreamStatistics statistics) {

    }

    @Override
    public void onStreamSetup(String streamId) {

    }

    @Override
    public void onSpeakers(List<String> speakers) {

    }

    @Override
    public void onReceiveInvite(String confId, String password, String extension) {

    }

    @Override
    public void onRoleChanged(EMConferenceManager.EMConferenceRole role) {

    }
}
