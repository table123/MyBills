package com.witt.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */

public class NewOutCheckFragment extends Fragment {

    @ViewInject(R.id.type_outCheck)
    private Spinner type_outCheck;
    @ViewInject(R.id.subType_outCheck)
    private Spinner subType_outCheck;
    @ViewInject(R.id.count_outCheck)
    private EditText count_outCheck;
    @ViewInject(R.id.date_outCheck)
    private EditText date_outCheck;
    @ViewInject(R.id.detail_outCheck)
    private EditText detail_outCheck;
    private ArrayAdapter<String> arrayAdapter1;
    //数据库操作对象
    private MySQLiteOpenHelper DBHelper;
    private final int[] arrId = new int[]{R.array.type1,R.array.type2,R.array.type3,R.array.type4,R.array.type5};
    private int index;
    //spinner2的数据源
    private List<String> subTypeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获取并初始化碎片布局
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_outcheck_fragment,null);
        ViewUtils.inject(this,view);
        initView();
        return view;
    }

    /**
     * 碎片初始化的内部函数
     */
    private void initView(){
        //判断第二个支出表中是否有数据，如果没有则初始化数据
        DBHelper = new MySQLiteOpenHelper(getActivity());
        int count = DBHelper.selectCount("select * from tb_outCheckType",null);
        if(count==0) {
            for(int i=0;i<arrId.length;i++) {
                String[] subTitle = getResources().getStringArray(arrId[i]);
                for(int j=0;j<subTitle.length;j++){
                    DBHelper.execData("insert into tb_outCheckType(detailType,mainType)values(?,?)",
                            new String[]{subTitle[j],i+""});
                }
            }
        }

        //初始化spinner1
        String[] arrType = getResources().getStringArray(R.array.arrType_outCheck);
        ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,arrType);
        type_outCheck.setAdapter(arrayAdapter);

        //初始化spinner2
       initSpinner2();

        //对spinner1设置监听,spinner1改变，spinner2也改变。
        type_outCheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subTypeList.clear();
                subTypeList.addAll(getDataBaseData(position+""));
                arrayAdapter1.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //时间选择窗口的点击事件
        date_outCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date_outCheck.setText(year+":"+(monthOfYear+1)+":"+dayOfMonth);
                    }
                },calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });
    }

    //初始化spinner2
    public void initSpinner2()
    {
        subTypeList=getDataBaseData("0");
        arrayAdapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,subTypeList);
        subType_outCheck.setAdapter(arrayAdapter1);
    }


    //从数据库中获取第二个spinner的数据
    public List<String> getDataBaseData(String id){
        List<Map<String,String>> arrSubType = DBHelper.selectList("select detailType from tb_outCheckType where mainType=?",
                new String[]{id});
        List<String> list = listChangeToArray(arrSubType);
        return  list;
    }

    //将List<Map<String,String>>转换为String[]
    private List<String> listChangeToArray(List<Map<String,String>> listMap){
        List<String> list = new ArrayList<String>();
        int i = 0;
        for(Map<String,String> map:listMap) {
            list.add(map.get("detailType"));
        }
        return list;
    }

    //确定按钮的监听事件
    @OnClick(R.id.submit_outCheck)
    public void btn_submit(View v){
        //获取输入框数据
        String type = type_outCheck.getSelectedItem().toString();
        String subType = subType_outCheck.getSelectedItem().toString();

        String count = count_outCheck.getText().toString();
        if(date_outCheck.getText().toString()==""){
            Toast.makeText(getActivity(),"请选择时间！",Toast.LENGTH_SHORT).show();
            return;
        }
        String date = date_outCheck.getText().toString();
        String detail = detail_outCheck.getText().toString();

        DBHelper = new MySQLiteOpenHelper(getActivity());
        //获取各个文本框的内容,并将内容插入数据库
        boolean flag = DBHelper.execData("insert into tb_outCheck(mainType,count,date,detailType,detail) values (?,?,?,?,?)",
                new String[]{type,count,date,subType,detail});
        if(flag) {
            Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT);
        }
    }

    //取消按钮的监听事件
    @OnClick(R.id.cancel_outCheck)
    public void btn_cancel(View view)
    {
        getActivity().finish();
    }

    //添加新子项的按钮
    @OnClick(R.id.btn_addSubType)
    public void btn_addSubType(View v){
        //通过自定义对话框实现子项的添加
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加支出子项：");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.layout_addsubtype_dialog,null);

        //初始化Spinner
        final Spinner mainType = (Spinner)view.findViewById(R.id.spinner_addSubType_dialog);
        final EditText subType_edit = (EditText)view.findViewById(R.id.subType_edit);

        String[] arrType = getResources().getStringArray(R.array.arrType_outCheck);
        ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,arrType);
        mainType.setAdapter(arrayAdapter);
        //监听Spinner获取索引
        mainType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(view);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean flag  = DBHelper.execData("insert into tb_outCheckType(detailTitle,mainType)values(?,?)",
                        new String[]{subType_edit.getText().toString(),index+""});
                if(flag){
                    Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT).show();
                }
                initSpinner2();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

}
