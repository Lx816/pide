package www.pide.com.hxsdk;

import android.content.Context;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceAttribute;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.util.EMLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/15/2020
 */

public class DemoHelper {

    protected static final String TAG = "DemoHelper";

	private static DemoHelper instance = null;
    private ExecutorService executor;
	private Context appContext;
	private ConferenceSession conferenceSession;
    private EMConferenceListener conferenceListener;

	private DemoHelper() {
        executor = Executors.newCachedThreadPool();
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}

	public  ConferenceSession getConferenceSession(){
		if (conferenceSession == null) {
			conferenceSession = new ConferenceSession();
		}
		return conferenceSession;
	}

	/**
	 * init helper
	 *
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
	    EMOptions options = initChatOptions(context);
        appContext = context;
		EMClient.getInstance().init(context, options);
		PreferenceManager.init(context);
	}

	public Context getContext(){
		return appContext;
	}

    private EMOptions initChatOptions(Context context){

		EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        return options;
    }


	/**
	 * logout
	 *
	 * @param unbindDeviceToken
	 *            whether you need unbind your device token
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {
			@Override
			public void onSuccess() {
				if (callback != null) {
					callback.onSuccess();
				}
			}
			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}
			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}


	/**
	 * set global listener
	 */
	public void setGlobalListeners(){

	    if(conferenceListener != null){
            EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
            conferenceListener = null;
        }
        conferenceListener = new EMConferenceListener() {
			@Override public void onMemberJoined(EMConferenceMember member) {
				EMLog.i(TAG, String.format("member joined username: %s, member: %d", member.memberName,
						EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
				if(!ConferenceInfo.Initflag){
					if(!ConferenceInfo.getInstance().getConferenceMemberList().contains(member)){
						ConferenceInfo.getInstance().getConferenceMemberList().add(member);
					}
				}
			}

			@Override public void onMemberExited(EMConferenceMember member) {
				EMLog.i(TAG, String.format("member exited username: %s, member size: %d", member.memberName,
						EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
			}

			@Override public void onStreamAdded(EMConferenceStream stream) {
				EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
						stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
						stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
				EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
						EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
						EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
				if(!ConferenceInfo.Initflag){
					if(!ConferenceInfo.getInstance().getConferenceStreamList().contains(stream)){
							ConferenceInfo.getInstance().getConferenceStreamList().add(stream);
					}
				}
			}

			@Override public void onStreamRemoved(EMConferenceStream stream) {
				EMLog.i(TAG, String.format("Stream removed streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
						stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
						stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
				EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
						EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
						EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
			}

			@Override public void onStreamUpdate(EMConferenceStream stream) {
				EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
						stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
						stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
				EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
						EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
						EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
			}

			@Override public void onPassiveLeave(int error, String message) {
				EMLog.i(TAG, String.format("passive leave code: %d, message: %s", error, message));
			}

			@Override public void onConferenceState(ConferenceState state) {
				EMLog.i(TAG, String.format("State code=%d", state.ordinal()));
			}

			@Override public void onStreamStatistics(EMStreamStatistics statistics) {
				EMLog.d(TAG, statistics.toString());
			}

			@Override public void onStreamSetup(String streamId) {
				EMLog.i(TAG, String.format("Stream id - %s", streamId));
			}

			@Override
			public void onSpeakers(List<String> speakers) {}

			@Override public void onReceiveInvite(String confId, String password, String extension) {
				EMLog.i(TAG, String.format("Receive conference invite confId: %s, password: %s, extension: %s", confId, password, extension));
				//goConference(confId, password, extension);
			}

			@Override
			public void onRoleChanged(EMConferenceManager.EMConferenceRole role) {
			}

			@Override
			public void onAttributesUpdated(EMConferenceAttribute[] attributes) {

			}
			@Override
			public void onAdminAdded(String memName){

			}
			@Override
			public void onAdminRemoved(String memName){

			}

			@Override
            public  void onPubStreamFailed(int error, String message){

			}

			@Override
            public  void onUpdateStreamFailed(int error, String message){

			}
		};

		EMClient.getInstance().conferenceManager().addConferenceListener(conferenceListener);

	}
	public void removeGlobalListeners(){
		EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
        conferenceListener = null;
	}
	/**
	 * if ever logged in
	 *
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}
}
