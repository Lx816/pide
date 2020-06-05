package www.pide.com.bean;

import java.util.List;

public class MeetUserBean {
    private String msg;
    private String code;
    private List<UserBean> data;
    private boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

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

    public List<UserBean> getData() {
        return data;
    }

    public void setData(List<UserBean> data) {
        this.data = data;
    }

    public class UserBean {
        private int userId;
        private String meetUserName;
        private String meetUserAvatar;
        private String company;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

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

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }
    }
}
