package www.pide.com.bean;
import www.pide.com.R;
import www.pide.com.ui.fragment.MineCardFragment;
import www.pide.com.ui.fragment.MineFragment;
import www.pide.com.ui.fragment.SpiceMeetingFragment;

/**
 * Created by bo on 2016/7/15.
 */
public enum BottomEnum {
    MINECARD(0, R.string.mineCard, R.drawable.mine_card, MineCardFragment.class),
    SPICEMEETING(1,R.string.spiceMeeting, R.drawable.spice_meeting, SpiceMeetingFragment.class),
    MINE(2,R.string.mine, R.drawable.mine, MineFragment.class);
    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;
    BottomEnum(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
