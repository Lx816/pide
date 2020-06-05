package www.pide.com.chart.easeui.easeui.ui;

import com.hyphenate.EMChatRoomChangeListener;

import java.util.List;

/**
 * Created by linan on 17/2/9.
 */

public abstract class EaseChatRoomListener implements EMChatRoomChangeListener {

    @Override
    public void onChatRoomDestroyed(final String roomId, final String roomName) {

    }

    @Override
    public void onMemberJoined(final String roomId, final String participant) {

    }

    @Override
    public void onMemberExited(final String roomId, final String roomName, final String participant) {

    }

    @Override
    public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {

    }

    @Override
    public void onMuteListAdded(final String chatRoomId, final List<String> mutes, final long expireTime) {

    }

    @Override
    public void onMuteListRemoved(final String chatRoomId, final List<String> mutes) {

    }

    @Override
    public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {

    }

    @Override
    public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {

    }

    @Override
    public void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {

    }

    @Override
    public void onAdminAdded(final String chatRoomId, final String admin) {

    }

    @Override
    public void onAdminRemoved(final String chatRoomId, final String admin) {

    }

    @Override
    public void onOwnerChanged(final String chatRoomId, final String newOwner, final String oldOwner) {

    }

    @Override
    public void onAnnouncementChanged(final String chatroomId, final String announcement) {

    }
}