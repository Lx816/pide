package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/23.
 */
public class ProfessionsBean {
    private String msg;
    private ProfessionsBean1 data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ProfessionsBean1 getData() {
        return data;
    }

    public void setData(ProfessionsBean1 data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    private Boolean isSuccess;
}
