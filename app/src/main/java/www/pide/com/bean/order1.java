package www.pide.com.bean;

import java.util.Comparator;

public class order1 implements Comparator<UserData> {

    @Override
    public int compare(UserData u1, UserData u2) {
        // TODO Auto-generated method stub
        return Integer.parseInt(u1.getUserId()) - Integer.parseInt(u2.getUserId());
    }
}
