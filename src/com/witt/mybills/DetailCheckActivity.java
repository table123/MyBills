package com.witt.mybills;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.witt.fragments.DetailCheckFragment;

/**
 * 账单详情的展示页面包括：日收支明细，周收支明细，月收支明细，年收支明细。
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */
@ContentView(R.layout.layout_detailcheck_activity)
public class DetailCheckActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        //动态创建碎片
        Bundle bundle = getIntent().getExtras();
        DetailCheckFragment fragment = new DetailCheckFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        transaction.replace(R.id.container_fragment,fragment);
        transaction.commit();
    }


}