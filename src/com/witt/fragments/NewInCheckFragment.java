package com.witt.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.witt.mybills.R;
import com.witt.tools.MySQLiteOpenHelper;

import java.util.Calendar;

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

    //数据库操作对象
    private MySQLiteOpenHelper DBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                        date_inCheck.setText(year+":"+(monthOfYear+1)+":"+dayOfMonth);
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
        String count = count_inCheck.getText().toString();
        if(date_inCheck.getText().toString()==""){
            Toast.makeText(getActivity(),"请选择时间！",Toast.LENGTH_SHORT).show();
            return;
        }
        String date = date_inCheck.getText().toString();
        String detail = detail_inCheck.getText().toString();

        DBHelper = new MySQLiteOpenHelper(getActivity());
        //获取各个文本框的内容,并将内容插入数据库
        boolean flag = DBHelper.execData("insert into tb_inCheck(type,count,date,detail) values (?,?,?,?)",new String[]{type,count,date,detail});
        if(flag) {
            Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT);
        }
    }

}