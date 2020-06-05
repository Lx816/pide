package www.pide.com.bean;

/**
 * Created by Administrator on 2020/3/25.
 */
public class Sticky {
    public String msg;
    public String obj;
    public int position;

    public Sticky(String msg) {
        this.msg = msg;
    }
    public Sticky(String msg,String obj) {
        this.msg = msg;
        this.obj = obj;
    }
    public Sticky(String msg,String obj,int position) {
        this.msg = msg;
        this.obj = obj;
        this.position=position;
    }
}
