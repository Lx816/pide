package www.pide.com.bean;

public class MeetingDBean {
    private String msg;
    private String code;
    private MeetingD data;
    private Boolean isSuccess;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MeetingD getData() {
        return data;
    }

    public void setData(MeetingD data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public class MeetingD {
        private int mId;
        private String mName;
        private String roomNum;
        private String startTime;
        private String userName;
        private String mStatus;
        private String userId;
        private int isCreateType;
        private int joinCount;
        private String remark;
        private String confrId;
        private String mPassword;
        private String roleType;//0是群主，1是成员
        private int isMakeType;
        private String informTime;
        private String durationTime;
        private String endTime;
        private String chatRoomId;

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(String durationTime) {
            this.durationTime = durationTime;
        }

        public String getInformTime() {
            return informTime;
        }

        public void setInformTime(String informTime) {
            this.informTime = informTime;
        }

        public int getIsMakeType() {
            return isMakeType;
        }

        public void setIsMakeType(int isMakeType) {
            this.isMakeType = isMakeType;
        }

        public String getRoleType() {
            return roleType;
        }

        public void setRoleType(String roleType) {
            this.roleType = roleType;
        }

        public String getmPassword() {
            return mPassword;
        }

        public void setmPassword(String mPassword) {
            this.mPassword = mPassword;
        }

        public String getConfrId() {
            return confrId;
        }

        public void setConfrId(String confrId) {
            this.confrId = confrId;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
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

        public String getmStatus() {
            return mStatus;
        }

        public void setmStatus(String mStatus) {
            this.mStatus = mStatus;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getIsCreateType() {
            return isCreateType;
        }

        public void setIsCreateType(int isCreateType) {
            this.isCreateType = isCreateType;
        }

        public int getJoinCount() {
            return joinCount;
        }

        public void setJoinCount(int joinCount) {
            this.joinCount = joinCount;
        }
    }
}
