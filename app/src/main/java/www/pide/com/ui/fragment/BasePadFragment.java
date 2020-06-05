package www.pide.com.ui.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BasePadFragment extends Fragment {

    public Context mContext;

    public void startToFragment(Context context,int container,Fragment newFragment){

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(container,newFragment);
        transaction.addToBackStack(context.getClass().getName());
        transaction.commit();
    }

}