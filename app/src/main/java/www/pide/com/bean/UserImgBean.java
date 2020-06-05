package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/24.
 */
public class UserImgBean {
    private ImageUrl data;
    private String msg;

    public ImageUrl getData() {
        return data;
    }

    public void setData(ImageUrl data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private Boolean isSuccess;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

}
