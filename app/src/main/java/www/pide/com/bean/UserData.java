package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/27.
 */
public class UserData {
    private String userId;
    private String name;
    private String avatar;
    private String position;
    private String company;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserData other = (UserData) obj;
        if (other.getUserId() == null) {
            return false;
        }
        if (userId == null) {
            if (other.getUserId() != null)
                return false;
        } else if (!userId.equals(other.getUserId()))
            return false;
        return true;
    }
}
