package www.pide.com.bean;

import com.hyphenate.chat.EMConferenceMember;

import java.util.Comparator;

import www.pide.com.utils.JsonUtil;

public class order implements Comparator<EMConferenceMember> {

    @Override
    public int compare(EMConferenceMember lhs, EMConferenceMember rhs) {
        // TODO Auto-generated method stub
        MeetingMainBean m1= JsonUtil.fromJson(lhs.extension,MeetingMainBean.class);
        MeetingMainBean m2= JsonUtil.fromJson(rhs.extension,MeetingMainBean.class);
        return Integer.parseInt( m2.getUserId()) - Integer.parseInt(m1.getUserId());
    }
}
