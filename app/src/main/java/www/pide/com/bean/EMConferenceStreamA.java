package www.pide.com.bean;

import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.util.EasyUtils;
import com.superrtc.mediamanager.EMediaStream;

public class EMConferenceStreamA {
    private String streamId;
    private String streamName;
    private String memberName;
    private String username;
    private String extension;
    private boolean videoOff;
    private boolean audioOff;
    private EMConferenceStream.StreamType streamType;

    public EMConferenceStreamA() {
    }

    public void init(EMediaStream var1) {
        this.setStreamId(var1.streamId);
        this.setStreamName(var1.streamName);
        this.setMemberName(var1.memberName);
        String var2 = EasyUtils.useridFromJid(this.memberName);
        this.setUsername(var2);
        this.setExtension(var1.extension);
        this.setVideoOff(var1.videoOff);
        this.setAudioOff(var1.audioOff);
        this.setStreamType(var1.streamType.val);
    }

    public String getStreamId() {
        return this.streamId;
    }

    public void setStreamId(String var1) {
        this.streamId = var1;
    }

    public String getStreamName() {
        return this.streamName;
    }

    public void setStreamName(String var1) {
        this.streamName = var1;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public void setMemberName(String var1) {
        this.memberName = var1;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String var1) {
        this.username = var1;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String var1) {
        this.extension = var1;
    }

    public boolean isVideoOff() {
        return this.videoOff;
    }

    public void setVideoOff(boolean var1) {
        this.videoOff = var1;
    }

    public boolean isAudioOff() {
        return this.audioOff;
    }

    public void setAudioOff(boolean var1) {
        this.audioOff = var1;
    }

    public EMConferenceStream.StreamType getStreamType() {
        return this.streamType;
    }

    public void setStreamType(int var1) {
        if (var1 == 1) {
            this.streamType = EMConferenceStream.StreamType.DESKTOP;
        } else {
            this.streamType = EMConferenceStream.StreamType.NORMAL;
        }

    }

    public static enum StreamType {
        NORMAL,
        DESKTOP;

        private StreamType() {
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EMConferenceStreamA other = (EMConferenceStreamA) obj;
        if (other.getUsername() == null) {
            return false;
        }
        if (username == null) {
            if (other.getUsername() != null)
                return false;
        } else if (!username.equals(other.getUsername()))
            return false;
        return true;
    }
}
