package com.witt.mybills;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.witt.fragments.NewInCheckFragment;
import com.witt.fragments.NewOutCheckFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */

@ContentView(R.layout.layout_newcheck_activity)
public class NewCheckActivity extends FragmentActivity {

    /**
     * 用户添加收入账单的界面
     * @param savedInstanceState
     */
    private final String[] arrTitle = new String[]{"添加收入","添加支出"};
    private List<Fragment> fragmentList;
    private ActionBar actionBar;
    @ViewInject(R.id.viewPager_newCheck)
    private ViewPager viewPager_newCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initView();
    }

    //页面的初始化
    private void initView() {
        //actionBar的各项设置
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for(int i=0;i<arrTitle.length;i++) {
            ActionBar.Tab tab1 = actionBar.newTab();
            tab1.setText(arrTitle[i]);
            tab1.setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    viewPager_newCheck.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            });
            actionBar.addTab(tab1);
        }

        //viewPager的各项设置
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new NewInCheckFragment());
        fragmentList.add(new NewOutCheckFragment());
        viewPager_newCheck.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager_newCheck.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    //viewPager的适配器
    public class ViewPagerAdapter extends FragmentPagerAdapter
    {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}