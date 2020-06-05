package www.pide.com.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import www.pide.com.R;
import www.pide.com.base.BaseActivity;
import www.pide.com.ui.fragment.AllAppointmentMeetingFragment;
import www.pide.com.ui.fragment.AllEndMeetingFragment;
import www.pide.com.ui.fragment.AllUnderwayMeetingFragment;

/**
 * Created by Administrator on 2020/4/2.
 */
public class AllMeetingActivity extends BaseActivity {
    private Context context;
    private ViewPager meetingFrameLayout;
    private RelativeLayout back;
    private RelativeLayout r0,r1,r2;
    private TextView t0,t1,t2,t00,t11,t22;
    private List<Fragment> fragmentList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_meeting);
        initView();
        initOnclick();
    }

    private void initView() {
        meetingFrameLayout= (ViewPager) findViewById(R.id.meetingFrameLayout);
        back= (RelativeLayout) findViewById(R.id.back);
        r0= (RelativeLayout) findViewById(R.id.r0);
        r1= (RelativeLayout) findViewById(R.id.r1);
        r2= (RelativeLayout) findViewById(R.id.r2);
        t0= (TextView) findViewById(R.id.t0);
        t1= (TextView) findViewById(R.id.t1);
        t2= (TextView) findViewById(R.id.t2);
        t00= (TextView) findViewById(R.id.t00);
        t11= (TextView) findViewById(R.id.t11);
        t22= (TextView) findViewById(R.id.t22);
        AllUnderwayMeetingFragment f1 = new AllUnderwayMeetingFragment();
        AllAppointmentMeetingFragment f2 = new AllAppointmentMeetingFragment();
        AllEndMeetingFragment f3 = new AllEndMeetingFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(f1);
        fragmentList.add(f2);
        fragmentList.add(f3);
        meetingFrameLayout.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(),fragmentList));
        meetingFrameLayout.setCurrentItem(0);
        meetingFrameLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        initColor();
                        t0.setTextColor(Color.parseColor("#0076FF"));
                        t00.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        initColor();
                        t1.setTextColor(Color.parseColor("#0076FF"));
                        t11.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        initColor();
                        t2.setTextColor(Color.parseColor("#0076FF"));
                        t22.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initOnclick() {
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 switch (view.getId()){
                     case R.id.back:
                         finish();
                         break;
                     case R.id.r0:
                         initColor();
                         t0.setTextColor(Color.parseColor("#0076FF"));
                         t00.setVisibility(View.VISIBLE);
                         meetingFrameLayout.setCurrentItem(0);
                         break;
                     case R.id.r1:
                         initColor();
                         t1.setTextColor(Color.parseColor("#0076FF"));
                         t11.setVisibility(View.VISIBLE);
                         meetingFrameLayout.setCurrentItem(1);
                         break;
                     case R.id.r2:
                         initColor();
                         t2.setTextColor(Color.parseColor("#0076FF"));
                         t22.setVisibility(View.VISIBLE);
                         meetingFrameLayout.setCurrentItem(2);
                         break;
                 }
            }
        };
        back.setOnClickListener(listener);
        r0.setOnClickListener(listener);
        r1.setOnClickListener(listener);
        r2.setOnClickListener(listener);
    }

    private void initColor() {
        t0.setTextColor(Color.parseColor("#9B9B9B"));
        t1.setTextColor(Color.parseColor("#9B9B9B"));
        t2.setTextColor(Color.parseColor("#9B9B9B"));
        t00.setVisibility(View.GONE);
        t11.setVisibility(View.GONE);
        t22.setVisibility(View.GONE);
    }
    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public MyViewPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {//必须实现
            return mFragments.get(position);
        }

        @Override
        public int getCount() {//必须实现
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {//选择性实现
            return mFragments.get(position).getClass().getSimpleName();
        }

    }
}
