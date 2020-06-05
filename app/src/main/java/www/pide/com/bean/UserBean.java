package www.pide.com.bean;

public class UserBean {
    private String msg;
    private String code;
    private boolean isSuccess;
    private UserD data;

    public UserD getUserD() {
        return data;
    }

    public void setUserD(UserD data) {
        this.data = data;
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

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
