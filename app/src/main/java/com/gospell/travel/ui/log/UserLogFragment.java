package com.gospell.travel.ui.log;

import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gospell.travel.MainActivity;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;

public class UserLogFragment extends BaseFragment {
    @RootView(R.layout.fragment_log_user)
    private View root;
    @ViewById(R.id.custom_nav_back)
    private LinearLayout backImage;
    @Override
    protected void onCreateView(){
        backImage.setOnClickListener (v ->  onBack());
    }
    public void onBack(){
        Fragment fragment = getChildFragmentManager ().getFragments ().get (0).getChildFragmentManager ().getFragments ().get (0);
        if(fragment instanceof LogInfoFragment){
            getChildFragmentManager ().popBackStack ();
        }else {
            getFragmentManager ().popBackStack (((MainActivity)getActivity ()).getSelectedItem (), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}

