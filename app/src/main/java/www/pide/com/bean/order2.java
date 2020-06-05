package www.pide.com.bean;

import java.util.Comparator;
import www.pide.com.utils.URLs;

public class order2 implements Comparator<String> {

    @Override
    public int compare(String lhs, String rhs) {
        // TODO Auto-generated method stub
        return Integer.parseInt(lhs.replace(URLs.APPKEY + "_","")) - Integer.parseInt(rhs.replace(URLs.APPKEY + "_",""));
    }
}
