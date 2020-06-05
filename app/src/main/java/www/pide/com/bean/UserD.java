package www.pide.com.bean;

import java.util.List;

public class UserD {
    private List<User> records;
    private String roomNum;
    private String mPassword;

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public List<User> getRecords() {
        return records;
    }

    public void setRecords(List<User> records) {
        this.records = records;
    }
}
