package www.pide.com.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by Administrator on 2020/3/30.
 */
public class ShengBean implements IPickerViewData {
    public String title;
    public String ad_code;
    public List<Shi> child;
    public static class Shi{
        public String title;
        public String ad_code;
        public List<Qu> child;

    }
    public static class Qu{
        public String title;
        public String ad_code;

    }
    //  这个要返回省的名字
    @Override
    public String getPickerViewText() {
        return this.title;
    }
}
