package www.pide.com.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

/**
 * Created by bo on 2016/7/8.
 */
public class SystemSetStatusBar {




    /**
     * 全屏模式
     * @param activity
     */
    public static void setFistItem(Activity activity){
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        //首先使 ChildView 不预留空间
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        Rect rect = new Rect();
        int statusBarHeight = rect.top;//状态栏高度
        //需要设置这个 flag 才能设置状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //避免多次调用该方法时,多次移除了 View
        if (mChildView != null && mChildView.getLayoutParams() != null && mChildView.getLayoutParams().height == statusBarHeight) {
            //移除假的 View.
            mContentView.removeView(mChildView);
            mChildView = mContentView.getChildAt(0);
        }
        if (mChildView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
            //清除 ChildView 的 marginTop 属性
            if (lp != null && lp.topMargin >= statusBarHeight) {
                lp.topMargin -= statusBarHeight;
                mChildView.setLayoutParams(lp);
            }
        }
    }


   public static void SetZS(Activity activity,int statusColor){
       Window window = activity.getWindow();
       ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

       //First translucent status bar.
       window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
       Rect rect = new Rect();
       int statusBarHeight = rect.top;//状态栏高度


       View mChildView = mContentView.getChildAt(0);
       if (mChildView != null) {
           FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
           //如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
           if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
               //不预留系统空间
               ViewCompat.setFitsSystemWindows(mChildView, false);
               lp.topMargin += statusBarHeight;
               mChildView.setLayoutParams(lp);
           }
       }

       View statusBarView = mContentView.getChildAt(0);
       if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
           //避免重复调用时多次添加 View
           statusBarView.setBackgroundColor(statusColor);
           return;
       }
       statusBarView = new View(activity);
       ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
       statusBarView.setBackgroundColor(statusColor);
       //向 ContentView 中添加假 View
       mContentView.addView(statusBarView, 0, lp);
   }

}
