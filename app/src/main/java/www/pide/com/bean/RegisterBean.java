package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/24.
 */
public class RegisterBean {
    private  String access_token;
    private Boolean isSuccess;
    private String msg;
    private Token data;

    public Token getData() {
        return data;
    }

    public void setData(Token data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
