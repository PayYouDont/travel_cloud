package com.gospell.travel.ui.device;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.ui.fragment.BackListener;
import com.gospell.travel.ui.fragment.CollectFragment;
import com.gospell.travel.ui.fragment.DocumentFragment;
import com.gospell.travel.ui.fragment.MusicFragment;
import com.gospell.travel.ui.fragment.PictureFragment;
import com.gospell.travel.ui.fragment.RecentFragment;
import com.gospell.travel.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class FileListFragment extends BaseFragment {
    @RootView(R.layout.fragment_file_list)
    private View root;
    @ViewById(R.id.tab_layout)
    private TabLayout tabLayout;
    @ViewById(R.id.back_layout)
    private LinearLayout backLayout;
    @ViewById(R.id.selectAll_layout)
    private LinearLayout selectAllLayout;
    @ViewById(R.id.view_pager)
    private ViewPager viewPager;
    private String [] tabTexts = {"最近","图片","文档","视频","音乐","收藏","更多"};
    private List<Fragment> fragmentList = new ArrayList<> ();
    private PagerAdapter adapter;
    @Override
    protected void onCreateView() {
        for(int i=0;i<tabTexts.length;i++){
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(tabTexts[i]);
            tabLayout.addTab(tab);
        }
        fragmentList.add (new RecentFragment ());
        fragmentList.add (new PictureFragment ());
        fragmentList.add (new DocumentFragment ());
        fragmentList.add (new VideoFragment ());
        fragmentList.add (new MusicFragment ());
        fragmentList.add (new CollectFragment ());
        adapter = new PagerAdapter (getFragmentManager (),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter (adapter);
        adapter.notifyDataSetChanged ();
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager (viewPager);
        tabLayout.getTabAt (1).select ();
        backLayout.setOnClickListener (v -> {
            Fragment fragment = fragmentList.get (viewPager.getCurrentItem ());
            if(fragment instanceof BackListener){
                ((BackListener)fragment).onBack ();
            }

        });
        selectAllLayout.setOnClickListener (v -> {

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        adapter.clear (viewPager);
    }

    private class PagerAdapter extends FragmentPagerAdapter{
        private FragmentTransaction mCurTransaction;
        private FragmentManager fragmentManager;
        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super (fm, behavior);
            this.fragmentManager = fm;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get (position);
        }

        @Override
        public int getCount() {
            return fragmentList.size ();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTexts[position];
        }
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem (container, position, object);
        }
        /**
         * 清除缓存fragment
         * @param container ViewPager
         */
        public void clear(ViewGroup container){
            if (this.mCurTransaction == null) {
                this.mCurTransaction = fragmentManager.beginTransaction();
            }

            for(int i=0;i<fragmentList.size();i++){
                long itemId = this.getItemId(i);
                String name = makeFragmentName(container.getId(), itemId);
                Fragment fragment = getFragmentManager ().findFragmentByTag(name);
                if(fragment != null){//根据对应的ID，找到fragment，删除
                    mCurTransaction.remove(fragment);
                }
            }
            mCurTransaction.commit ();
        }
    }
    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
