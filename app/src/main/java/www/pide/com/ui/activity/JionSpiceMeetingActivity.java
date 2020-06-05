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
import www.pide.com.ui.fragment.AppointmentMeetingFragment;
import www.pide.com.ui.fragment.EndMeetingFragment;

/**
 * Created by Administrator on 2020/4/1.
 */
public class JionSpiceMeetingActivity extends BaseActivity {
    private ViewPager spiceFrameLayout;
    private List<Fragment> fragmentList;
    private TextView t1,t11,t2,t22;
    private RelativeLayout back;
    private RelativeLayout r1,r2;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context,R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jion_spice_meeting);
        initView();
        initOnclick();
    }

    private void initOnclick() {
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 switch (view.getId()){
                     case R.id.back:
                         finish();
                         break;
                     case R.id.r1:
                         t1.setTextColor(Color.parseColor("#0076FF"));
                         t2.setTextColor(Color.parseColor("#9B9B9B"));
                         spiceFrameLayout.setCurrentItem(0);
                         t11.setVisibility(View.VISIBLE);
                         t22.setVisibility(View.GONE);
                         break;
                     case R.id.r2:
                         t1.setTextColor(Color.parseColor("#9B9B9B"));
                         t2.setTextColor(Color.parseColor("#0076FF"));
                         spiceFrameLayout.setCurrentItem(1);
                         t11.setVisibility(View.GONE);
                         t22.setVisibility(View.VISIBLE);
                         break;
                 }
            }
        };
        back.setOnClickListener(listener);
        r1.setOnClickListener(listener);
        r2.setOnClickListener(listener);
    }

    private void initView() {
        spiceFrameLayout = (ViewPager) findViewById(R.id.spiceFrameLayout);
        back= (RelativeLayout) findViewById(R.id.back);
        t1= (TextView) findViewById(R.id.t1);
        t11= (TextView) findViewById(R.id.t11);
        t2= (TextView) findViewById(R.id.t2);
        t22= (TextView) findViewById(R.id.t22);
        t1= (TextView) findViewById(R.id.t1);
        r1= (RelativeLayout) findViewById(R.id.r1);
        r2= (RelativeLayout) findViewById(R.id.r2);
        AppointmentMeetingFragment f1 = new AppointmentMeetingFragment();
        EndMeetingFragment f2 = new EndMeetingFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(f1);
        fragmentList.add(f2);
        spiceFrameLayout.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        spiceFrameLayout.setCurrentItem(0);
        spiceFrameLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        t1.setTextColor(Color.parseColor("#0076FF"));
                        t2.setTextColor(Color.parseColor("#9B9B9B"));
                        t11.setVisibility(View.VISIBLE);
                        t22.setVisibility(View.GONE);
                        break;
                    case 1:
                        t1.setTextColor(Color.parseColor("#9B9B9B"));
                        t2.setTextColor(Color.parseColor("#0076FF"));
                        t11.setVisibility(View.GONE);
                        t22.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }
}
