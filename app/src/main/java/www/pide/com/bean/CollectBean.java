package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/29.
 */
public class CollectBean {
    private int id;
    private int user_id;
    private int collect_user_id;
    private int status;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCollect_user_id() {
        return collect_user_id;
    }

    public void setCollect_user_id(int collect_user_id) {
        this.collect_user_id = collect_user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
