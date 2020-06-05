package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/27.
 */
public class User {
    private int id;
    private int userId;
    private String name;
    private int image_id;
    private String avatar;
    private String phone;
    private String position;
    private int professionId;
    private String professionName;
    private String company;
    private String needResources;
    private String possessionResources;
    private String recommendCode;//推荐码
    private String introduce;//介绍
    private Boolean bound_phone;
    private Boolean bound_wechat;
    private String updateTime;
    private String address;
    private String gender;//用户的性别，0-未知，1-男，2-女

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String cId;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUpdated_at() {
        return updateTime;
    }

    public void setUpdated_at(String updated_at) {
        this.updateTime = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProfession_id() {
        return professionId;
    }

    public void setProfession_id(int profession_id) {
        this.professionId = profession_id;
    }

    public String getProfession_name() {
        return professionName;
    }

    public void setProfession_name(String profession_name) {
        this.professionName = profession_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNeed_resources() {
        return needResources;
    }

    public void setNeed_resources(String need_resources) {
        this.needResources = need_resources;
    }

    public String getPossession_resources() {
        return possessionResources;
    }

    public void setPossession_resources(String possession_resources) {
        this.possessionResources = possession_resources;
    }

    public String getRecommend_code() {
        return recommendCode;
    }

    public void setRecommend_code(String recommend_code) {
        this.recommendCode = recommend_code;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Boolean getBound_phone() {
        return bound_phone;
    }

    public void setBound_phone(Boolean bound_phone) {
        this.bound_phone = bound_phone;
    }

    public Boolean getBound_wechat() {
        return bound_wechat;
    }

    public void setBound_wechat(Boolean bound_wechat) {
        this.bound_wechat = bound_wechat;
    }
}
