package www.pide.com.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import www.pide.com.R;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean1;
import www.pide.com.ui.activity.BindPhoneActivity;
import www.pide.com.ui.activity.CollectActivity;
import www.pide.com.ui.activity.CompleteInformation;
import www.pide.com.ui.activity.EditeInformation;
import www.pide.com.ui.activity.JionSpiceMeetingActivity;
import www.pide.com.ui.activity.LoginActivity;
import www.pide.com.ui.activity.SearchConnectionActivity;
import www.pide.com.ui.activity.YQCodeActivity;
import www.pide.com.ui.view.XCRoundImageView;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

public class MineFragment extends Fragment {
    View mRootView;
    private Button complete_information;
    private Context context;
    private RelativeLayout t_login;
    private XCRoundImageView user_img;
    private RelativeLayout r1,r2;
    private TextView name,company,date,phone,yq_code;
    private RelativeLayout bind_phone,collect,search_connection,yqm;
    private User user=new User();
    private RelativeLayout spice_meeting;
    private UserBean1 user1;
    private MyHandler initMeetingHandler=new MyHandler(getActivity());

    private static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;
        public MyHandler(Activity activity) {
            mWeakReference=new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {

        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.mine_fragment, container, false);
        context=getActivity();
        //注册订阅者
        EventBus.getDefault().register(this);
        initView();
        initMeetingHandler=new MyHandler(getActivity()){
            @Override
            public void handleMessage(Message msg) {
                final Activity activity=mWeakReference.get();
                if(activity!=null) {
                    switch (msg.what) {
                        case 0:
                            initData();
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMeetingHandler.sendEmptyMessage(0);
            }
        }).start();
        initOnclick();
        return mRootView;
    }

    private void initView() {
        complete_information = (Button) mRootView.findViewById(R.id.complete_information);
        t_login= (RelativeLayout) mRootView.findViewById(R.id.t_login);
        user_img= (XCRoundImageView) mRootView.findViewById(R.id.user_img);
        r1= (RelativeLayout) mRootView.findViewById(R.id.r1);
        r2= (RelativeLayout) mRootView.findViewById(R.id.r2);
        name= (TextView) mRootView.findViewById(R.id.name);
        company= (TextView) mRootView.findViewById(R.id.company);
        date= (TextView) mRootView.findViewById(R.id.date);
        bind_phone= (RelativeLayout) mRootView.findViewById(R.id.bind_phone);
        collect= (RelativeLayout) mRootView.findViewById(R.id.collect);
        search_connection= (RelativeLayout) mRootView.findViewById(R.id.search_connection);
        phone= (TextView) mRootView.findViewById(R.id.phone);
        spice_meeting= (RelativeLayout) mRootView.findViewById(R.id.spice_meeting);
        yqm= (RelativeLayout) mRootView.findViewById(R.id.yqm);
        yq_code=mRootView.findViewById(R.id.yq_code);
    }

    private void initOnclick() {
        View.OnClickListener listener;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.complete_information:
                        UIHelper.OpenActivity(context, EditeInformation.class);
                        break;
                    case R.id.t_login:
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context, LoginActivity.class);
                        getActivity().finish();
                        break;
                    case R.id.bind_phone:
                        Intent intent = new Intent(context, BindPhoneActivity.class);
                        intent.putExtra("phone", user.getPhone());
                        startActivity(intent);
                        break;
                    case R.id.r1:
                        UIHelper.OpenActivity(context, CompleteInformation.class);
                        break;
                    case R.id.collect:
                        UIHelper.OpenActivity(context, CollectActivity.class);
                        break;
                    case R.id.search_connection:
                        UIHelper.OpenActivity(context, SearchConnectionActivity.class);
                        break;
                    case R.id.spice_meeting:
                        UIHelper.OpenActivity(context, JionSpiceMeetingActivity.class);
                        break;
                    case R.id.yqm:
                        Intent intenty=new Intent(context,YQCodeActivity.class);
                        intenty.putExtra("recommend_code",user.getRecommend_code());
                        intenty.putExtra("user_img",user.getAvatar());
                        getActivity().startActivity(intenty);
                        break;
                }
            }
        };
        complete_information.setOnClickListener(listener);
        t_login.setOnClickListener(listener);
        bind_phone.setOnClickListener(listener);
        r1.setOnClickListener(listener);
        collect.setOnClickListener(listener);
        search_connection.setOnClickListener(listener);
        spice_meeting.setOnClickListener(listener);
        yqm.setOnClickListener(listener);
    }

    private void initData() {
        try {
            Util.showLoadingDialog(context,"加载中");
        }catch (Exception e){}
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYDETAIL)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                user1 = JsonUtil.fromJson(response, UserBean1.class);
                if(user1.isSuccess()){
                    user = user1.getData();
                    SharedPreferencesUtil.write(getActivity(), "LtAreaPeople", "USERID", user.getUserId()+"");
                    if(user.getAvatar()==null){
                        Util.closeLoadingDialog(context);
                        r1.setVisibility(View.GONE);
                        r2.setVisibility(View.VISIBLE);
                        yq_code.setText(user.getRecommend_code());
                        phone.setText(user.getPhone());
                    }else {
                        Util.closeLoadingDialog(context);
                        r1.setVisibility(View.VISIBLE);
                        r2.setVisibility(View.GONE);
                        SharedPreferencesUtil.write(getActivity(), "LtAreaPeople", URLs.USER_ID, user.getId()+"");
                        Glide.with(context)
                                .load(user.getAvatar())
                                .apply(new RequestOptions().placeholder(R.drawable.error))
                                .apply(new RequestOptions().error(R.drawable.error))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                .into(user_img);
                        name.setText(user.getName());
                        company.setText(user.getCompany());
                        String[] mdate=(user.getUpdated_at()).split(" ")[0].split("-");
                        String updated_at="上次更新时间："+mdate[0]+"年"+mdate[1]+"月"+mdate[2]+"日";
                        date.setText(updated_at);
                        yq_code.setText(user.getRecommend_code());
                        phone.setText(user.getPhone());
                    }
                }else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, user1.getMsg(), 200);
                    if(user1.getCode().equals("401")){
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context,"LtAreaPeople");
                        UIHelper.OpenActivity(context,LoginActivity.class);
                    }
                }
            }
        });
    }
    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent){
        if(userEvent.msg.equals("2")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initMeetingHandler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    @Override
    public void onDestroyView() {
        initMeetingHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
        //注销注册
        EventBus.getDefault().unregister(this);
    }
}
