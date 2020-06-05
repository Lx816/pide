package www.pide.com.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/3/24.
 */
public class MicroCardsBean implements Serializable {
    private int id;
    private int user_id;
    private int image_id;
    private String name;
    private String avatar;
    private String position;
    private String company;
    private String phone;
    private String wechat;
    private int profession_id;
    private String profession_name;
    private String address;
    private String possession_resources;
    private String need_resources;

    public String getProfession_name() {
        return profession_name;
    }

    public void setProfession_name(String profession_name) {
        this.profession_name = profession_name;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNeed_resources() {
        return need_resources;
    }

    public void setNeed_resources(String need_resources) {
        this.need_resources = need_resources;
    }

    public String getPossession_resources() {
        return possession_resources;
    }

    public void setPossession_resources(String possession_resources) {
        this.possession_resources = possession_resources;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(int profession_id) {
        this.profession_id = profession_id;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
