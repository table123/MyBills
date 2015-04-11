package com.witt.mybills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * 监听主页主页面中的点击事件，跳转到不同的内容页面
     */
    public void textViewClick(View view){

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        switch (view.getId()){
            //跳转到添加页面
            case R.id.btn_newInCheck:
                intent.setClass(this,NewCheckActivity.class);
                break;
            //跳转到明细页面
            case R.id.btn_dayDetailCheck:
                bundle.putInt("type", 0);
                intent.putExtras(bundle);
                intent.setClass(this,DetailCheckActivity.class);
                break;
            case R.id.btn_weekDetailCheck:
                bundle.putInt("type", 1);
                intent.putExtras(bundle);
                intent.setClass(this,DetailCheckActivity.class);
                break;
            case R.id.btn_mouthDetailCheck:
                bundle.putInt("type", 2);
                intent.putExtras(bundle);
                intent.setClass(this,DetailCheckActivity.class);
                break;

            //每周明细（先不考虑）
            case R.id.btn_yearDetailCheck:
                bundle.putInt("type", 3);
                intent.putExtras(bundle);
                intent.setClass(this,DetailCheckActivity.class);
                break;

            //跳转到图形统计页面
            case R.id.btn_checkStatus:
                intent.setClass(this,StatusCheck_Activity.class);
                break;
        }
        startActivity(intent);
    }
}
