package com.witt.mybills;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.witt.model.Tb_InCheck;
import com.witt.model.Tb_OutCheck;
import com.witt.myview.ChartView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 绘制图片的页面
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */

@ContentView(R.layout.layout_statuscheck_activity)
public class StatusCheck_Activity extends Activity {
    /**
     * 从数据库中获取数据，并绘制如下饼图：
     *  1.收入比例图。
     *  2.支出比例图（本周，本月，本年）。
     *
     */
    @ViewInject(R.id.spinner_drawType)
    private Spinner spinner_drawType;
    @ViewInject(R.id.spinner_drawOutCheck)
    private Spinner spinner_drawOutCheck;
    @ViewInject(R.id.layOut_outCheck)
    private LinearLayout linearLayout;
    @ViewInject(R.id.chartView)
    private ChartView chartView;

    //数据库查询
    private DbUtils dbUtils;
    //收入支出类型
    private String  type;
    private int searchType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initView();
    }

    //初始化view
    private void initView(){
        //初始化数据库对象
        dbUtils = DbUtils.create(this,
                "myBills.db",
                1,
                new DbUtils.DbUpgradeListener(){
                    @Override
                    public void onUpgrade(DbUtils dbUtils, int i, int i1) {
                        //此处用于数据库升级
                    }
                });

        //初始化spinner1
        String[] drawTypeArr = getResources().getStringArray(R.array.searchType);
        ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,drawTypeArr);
        spinner_drawType.setAdapter(arrayAdapter);
        spinner_drawType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = spinner_drawType.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化spinner2
        String[] outCheckArr = getResources().getStringArray(R.array.outCheckType);
        ArrayAdapter<String> arrayAdapter1  = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,outCheckArr);
        spinner_drawOutCheck.setAdapter(arrayAdapter1);
        spinner_drawOutCheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchType = (int)spinner_drawOutCheck.getSelectedItemId();
                initData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //初始化数据
    private void initData(){
        switch (searchType) {
            //查询当天收支
            case 0:
                //读取数据库，获取listView中显示的数据,每次查取20条
                WhereBuilder builder = WhereBuilder.b();
                builder.expr("date = date('now')");
                queryInput(builder);
                if("收入".equals(type)){
                    queryInput(builder);
                }else{
                    queryOutput(builder);
                }
                break;

            //查询当月收支
            case 1:
                //获取时间
                Date date =new Date();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
                String dateStr = fmt.format(date);
                //获取数据
                WhereBuilder builder1 = WhereBuilder.b();
                builder1.expr("date","like",dateStr+"%");
                queryOutput(builder1);
                if("收入".equals(type)){
                    queryInput(builder1);
                }else{
                    queryOutput(builder1);
                }
                break;

            //查询当年收支比例
            case 2:
                //获取时间
                Date date1 =new Date();
                SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy");
                String dateStr1 = fmt1.format(date1);
                //获取数据
                WhereBuilder builder2 = WhereBuilder.b();
                builder2.expr("date","like",dateStr1+"%");
                queryOutput(builder2);
                if("收入".equals(type)){
                    queryInput(builder2);
                }else{
                    queryOutput(builder2);
                }
                break;
        }
    }

    //查询收入明细的方法
    private void queryInput(WhereBuilder builder){
        String[] types_in = getResources().getStringArray(R.array.arrType_inCheck);
        float[] money_in = new float[]{0,0,0};
        float all_in = 0f;

        try {
            List<Tb_InCheck> inChecks = dbUtils.findAll(Selector.from(Tb_InCheck.class)
                            .where(builder)
            );

            if (inChecks != null && inChecks.size() != 0) {
                for (Tb_InCheck income : inChecks) {
                    float incomeMoney = income.getMoney();
                    all_in += incomeMoney;
                    if("工资".equals(income.getType()))
                        money_in[0]+=incomeMoney;
                    if("外快".equals(income.getType()))
                        money_in[1] += incomeMoney;
                    else{
                        money_in[2] += incomeMoney;
                    }
                }
                money_in = setFloat(money_in, all_in);
            }
            //绘制图片
            chartView.setData(money_in,types_in);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //查询支出明细的方法
    private void queryOutput(WhereBuilder builder){
        String[] types_out = getResources().getStringArray(R.array.arrType_outCheck);
        float[] money_out = new float[]{0,0,0,0,0};
        float all_out = 0f;

        try {
            List<Tb_OutCheck> outChecks = dbUtils.findAll(Selector.from(Tb_OutCheck.class)
                            .where(builder)
            );
            if (outChecks != null && outChecks.size() != 0) {
                for (Tb_OutCheck e : outChecks) {
                    float money = e.getMoney();
                    all_out += money;
                    switch (e.getMainType()) {
                        case 0:
                            money_out[0] += money;
                            break;
                        case 1:
                            money_out[1] += money;
                            break;
                        case 2:
                            money_out[2] += money;
                            break;
                        case 3:
                            money_out[3] += money;
                            break;
                        case 4:
                            money_out[4] += money;
                            break;
                    }
                }
                money_out = setFloat(money_out, all_out);
            }
            chartView.setData(money_out,types_out);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private float[] setFloat(float[] money, float all) {
        if(all==0){
            money[0]=-1;
        }else {
            for (int i = 0; i < money.length; i++) {
                money[i] = money[i] / all;
                Log.i("2222", money[i] + "");
            }
        }
        return money;
    }

}