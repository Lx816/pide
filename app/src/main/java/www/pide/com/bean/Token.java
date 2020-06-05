package www.pide.com.bean;

public class Token {
    private String jwtToken;
    private String hxUuid;
    private String hxPassword;
    private String hxUserName;

    public String getHxUuid() {
        return hxUuid;
    }

    public void setHxUuid(String hxUuid) {
        this.hxUuid = hxUuid;
    }

    public String getHxPassword() {
        return hxPassword;
    }

    public void setHxPassword(String hxPassword) {
        this.hxPassword = hxPassword;
    }

    public String getHxUserName() {
        return hxUserName;
    }

    public void setHxUserName(String hxUserName) {
        this.hxUserName = hxUserName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
