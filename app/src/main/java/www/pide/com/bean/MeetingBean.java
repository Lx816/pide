package www.pide.com.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/4/3.
 */
public class MeetingBean {
    private String msg;
    private String code;
    private String ok;
    private MeetingBeandata data;
    private Boolean isSuccess;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public class MeetingBeandata {
        private List<MeetingBeanRecords> records;
        private String roomNum;
        private String mType;
        private String mPassword;
        private int mId;
        private String chatRoomId;

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }

        public String getmType() {
            return mType;
        }

        public void setmType(String mType) {
            this.mType = mType;
        }

        public String getmPassword() {
            return mPassword;
        }

        public void setmPassword(String mPassword) {
            this.mPassword = mPassword;
        }

        public List<MeetingBeanRecords> getRecords() {
            return records;
        }

        public void setRecords(List<MeetingBeanRecords> records) {
            this.records = records;
        }
    }
    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public MeetingBeandata getData() {
        return data;
    }

    public void setData(MeetingBeandata data) {
        this.data = data;
    }

    public class MeetingBeanRecords {
        private int mId;
        private String mName;
        private String roomNum;
        private String startTime;
        private String userName;
        private List<MeetingUserInfoVOList> meetingUserInfoVOList;
        private String miId;

        public String getMiId() {
            return miId;
        }

        public void setMiId(String miId) {
            this.miId = miId;
        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<MeetingUserInfoVOList> getMeetingUserInfoVOList() {
            return meetingUserInfoVOList;
        }

        public void setMeetingUserInfoVOList(List<MeetingUserInfoVOList> meetingUserInfoVOList) {
            this.meetingUserInfoVOList = meetingUserInfoVOList;
        }
    }
    public class MeetingUserInfoVOList {
        private String meetUserName;
        private String meetUserAvatar;

        public String getMeetUserName() {
            return meetUserName;
        }

        public void setMeetUserName(String meetUserName) {
            this.meetUserName = meetUserName;
        }

        public String getMeetUserAvatar() {
            return meetUserAvatar;
        }

        public void setMeetUserAvatar(String meetUserAvatar) {
            this.meetUserAvatar = meetUserAvatar;
        }
    }
}
