package com.witt.mybills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.zxing.activity.CaptureActivity;

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
                startActivity(intent);
                break;
            //跳转到明细页面
            case R.id.btn_dayDetailCheck:
                bundle.putInt("type", 0);
                intent.putExtras(bundle);
                intent.setClass(this, DetailCheckActivity.class);
                startActivity(intent);
                break;

            //扫描2
            case R.id.btn_weekDetailCheck:
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(this,CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;

            case R.id.btn_mouthDetailCheck:
                bundle.putInt("type", 2);
                intent.putExtras(bundle);
                intent.setClass(this, DetailCheckActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_yearDetailCheck:
                bundle.putInt("type", 3);
                intent.putExtras(bundle);
                intent.setClass(this, DetailCheckActivity.class);
                startActivity(intent);
                break;

            //跳转到图形统计页面
            case R.id.btn_checkStatus:
                intent.setClass(this, StatusCheck_Activity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Toast.makeText(this,scanResult,Toast.LENGTH_SHORT);
        }
    }

}
