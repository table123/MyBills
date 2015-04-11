package com.witt.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.lidroid.xutils.db.sqlite.DbModelSelector;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.witt.model.Tb_OutCheck;
import com.witt.model.Tb_OutCheckType;
import com.witt.mybills.R;
import com.witt.tools.MySQLiteOpenHelper;

import java.util.*;

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
    private DbUtils dbUtils;
    //支出父类型
    private final int[] arrId = new int[]{R.array.type1,R.array.type2,R.array.type3,R.array.type4,R.array.type5};
    private int index;
    //spinner2的数据源
    private List<String> subTypeList;

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
        //1.获取表中记录的个数
        try {
            DbModel dbModel = dbUtils.findDbModelFirst(DbModelSelector.from(Tb_OutCheckType.class)
            .select("count(*)"));
            int count = 0;
            if(dbModel!=null){
                HashMap<String,String> dataMap =dbModel.getDataMap();
                count = Integer.parseInt(dataMap.get("count(*)"));
                Log.i("count","=========="+count);
            }
            if(count==0){
                Tb_OutCheckType outCheckType = new Tb_OutCheckType();
                for(int i=0;i<arrId.length;i++) {
                    String[] subTitle = getResources().getStringArray(arrId[i]);
                    for(int j=0;j<subTitle.length;j++){
                        outCheckType.setCType(subTitle[j]);
                        outCheckType.setMainType(i);
                        dbUtils.save(outCheckType);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }


//        DBHelper = new MySQLiteOpenHelper(getActivity());
//        int count = DBHelper.selectCount("select * from tb_outCheckType",null);
//        if(count==0) {
//            for(int i=0;i<arrId.length;i++) {
//                String[] subTitle = getResources().getStringArray(arrId[i]);
//                for(int j=0;j<subTitle.length;j++){
//                    DBHelper.execData("insert into tb_outCheckType(detailType,mainType)values(?,?)",
//                            new String[]{subTitle[j],i+""});
//                }
//            }
//        }

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
                subTypeList.addAll(getDataBaseData(position));
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
                        date_outCheck.setText(year + "-"+month + "-" + day);
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
        subTypeList=getDataBaseData(0);
        arrayAdapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,subTypeList);
        subType_outCheck.setAdapter(arrayAdapter1);
    }


    //从数据库中获取第二个spinner的数据
    public List<String> getDataBaseData(int mainType){
        //TODO 获取类型表的子类型
        WhereBuilder builder = WhereBuilder.b();
        builder.expr("mainType="+mainType);
        List<Tb_OutCheckType> outCheckTypes=null;
        try {
            outCheckTypes = dbUtils.findAll(Selector.from(Tb_OutCheckType.class)
                    .where(builder));

        } catch (DbException e) {
            e.printStackTrace();
        }

        List<String> list = listChange(outCheckTypes);
        return  list;
    }

    //将List<Map<String,String>>转换为list<String>
    private List<String> listChange(List<Tb_OutCheckType> listTypes){
        List<String> list = new ArrayList<String>();
        if (listTypes != null) {
            for(Tb_OutCheckType outCheckType:listTypes) {
                list.add(outCheckType.getCType());
            }
        }
        return list;
    }

    //确定按钮的监听事件
    @OnClick(R.id.submit_outCheck)
    public void btn_submit(View v){
        //获取输入框数据
        int type = (int)type_outCheck.getSelectedItemId();
        //获取子类型，通过访问数据库查到Id
        String subType = subType_outCheck.getSelectedItem().toString();
//        Tb_OutCheckType outCheckType =new Tb_OutCheckType();
//        outCheckType.setCType(subType);

        WhereBuilder builder = WhereBuilder.b();
        builder.expr("cType = "+"'"+subType+"'");
        List<Tb_OutCheckType> outCheckTypes=null;
        try {
             outCheckTypes = dbUtils.findAll(Selector.from(Tb_OutCheckType.class)
            .where(builder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(count_outCheck.getText().toString().equals("")){
            Toast.makeText(getActivity(),"请输入支出的钱数！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(date_outCheck.getText().toString().equals("")){
            Toast.makeText(getActivity(),"请选择时间！",Toast.LENGTH_SHORT).show();
            return;
        }
        float money = Float.parseFloat(count_outCheck.getText().toString());

        String date = date_outCheck.getText().toString();
        String detail = detail_outCheck.getText().toString();
        //添加一条支出信息
        Tb_OutCheck outCheck = new Tb_OutCheck();
        outCheck.setMainType(type);
        outCheck.setMoney(money);
        outCheck.setDate(date);
        outCheck.setOutCheckType(outCheckTypes.get(0));
        outCheck.setDetail(detail);

        try {
            dbUtils.save(outCheck);
        } catch (DbException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT);
        }
        Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();
    }

    //"取消"按钮的监听事件
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
                //将一条子类型添加到数据库

                String subType = subType_edit.getText().toString();
                if (!subType.equals("")) {
                    Tb_OutCheckType outCheckType = new Tb_OutCheckType();
                    outCheckType.setCType(subType);
                    outCheckType.setMainType(index);
                    try {
                        dbUtils.save(outCheckType);
                    } catch (DbException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"添加失败！",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(),"添加成功！",Toast.LENGTH_SHORT).show();
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
