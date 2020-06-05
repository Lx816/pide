package www.pide.com.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.pide.com.R;

/**
 * Created by Administrator on 2020/4/1.
 */
public class ChartFragment extends BasePadFragment {
    View mRootView;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.chart_fragment, container, false);
        context=getActivity();
        initView();
        return mRootView;
    }

    private void initView() {

    }

}
