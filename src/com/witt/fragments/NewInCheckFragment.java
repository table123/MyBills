package com.witt.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.witt.model.Tb_InCheck;
import com.witt.mybills.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by IDEA
 * user:$ {USER}
 * date:$ {DATE}.
 */
public class NewInCheckFragment extends Fragment {

    @ViewInject(R.id.type_inCheck)
    private Spinner spinner_inCheck;
    @ViewInject(R.id.count_inCheck)
    private EditText count_inCheck;
    @ViewInject(R.id.date_inCheck)
    private EditText date_inCheck;
    @ViewInject(R.id.detail_inCheck)
    private EditText detail_inCheck;
    //使用DbUtils对象操作数据库
    private DbUtils dbUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //数据库的创建
        dbUtils = DbUtils.create(getActivity(),
                "myBills.db",
                1,
                new DbUtils.DbUpgradeListener(){
                    @Override
                    public void onUpgrade(DbUtils dbUtils, int i, int i1) {
                        //此处用于数据库升级
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_incheck_fragment,null);
        ViewUtils.inject(this,view);
        initView();
        return view;
    }

    //view的初始化
    public void initView(){
        //spinner的初始化
        String[] arrType = getResources().getStringArray(R.array.arrType_inCheck);
        ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,arrType);
        spinner_inCheck.setAdapter(arrayAdapter);

        //时间选择窗口的点击事件
        date_inCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String month = null;
                            String day = null;
                            if(monthOfYear<9) {
                                    month = "0" + (monthOfYear+1);
                                } else {
                                month = (monthOfYear+1)+"";
                                }
                            if(dayOfMonth<10) {
                                day = "0" + dayOfMonth;
                            }else {
                                day = dayOfMonth+"";
                            }

                            date_inCheck.setText(year + "-"+month + "-" + day);
                        }

                },calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });
    }

    //按钮的监听事件
    @OnClick(R.id.submit_inCheck)
    public void btn(View v){
        //获取输入框数据
        String type = spinner_inCheck.getSelectedItem().toString();
        if(count_inCheck.getText().toString().equals("")){
            Toast.makeText(getActivity(),"请输入收入的钱数！",Toast.LENGTH_SHORT).show();
        }
        if(date_inCheck.getText().toString().equals("")){
            Toast.makeText(getActivity(),"请选择时间！",Toast.LENGTH_SHORT).show();
            return;
        }

        float money = Float.parseFloat(count_inCheck.getText().toString());
        String date = date_inCheck.getText().toString();
        String detail = detail_inCheck.getText().toString();

        //向数据库中条件收入
        Tb_InCheck inCheck = new Tb_InCheck();
        inCheck.setType(type);
        inCheck.setMoney(money);
        inCheck.setDate(date);
        inCheck.setDetail(detail);
        try {
            dbUtils.save(inCheck);
        } catch (DbException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT);
        }
        Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();

    }

    //碎片消失时，关闭数据库连接
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbUtils.close();
    }
}