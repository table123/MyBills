package com.witt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.witt.model.Tb_InCheck;
import com.witt.model.Tb_OutCheck;
import com.witt.mybills.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IDEA
 * user:witt
 * date:2015/4/13.
 */
public class DetailChartFragment extends Fragment{

    /**
     * 获取查询类型Spinner对话框
     */
    @ViewInject(R.id.spinner_searchType)
    private Spinner spinner_searchType;
    @ViewInject(R.id.queryAll_detail)
    private Button btn_query;
    /**
     * 年月日的编辑框。
     */
    @ViewInject(R.id.day_edit)
    private EditText day_editText;
    @ViewInject(R.id.day_textView)
    private TextView day_textView;

    @ViewInject(R.id.mouth_edit)
    private EditText mouth_editText;
    @ViewInject(R.id.mouth_textView)
    private TextView mouth_textView;

    @ViewInject(R.id.year_edit)
    private EditText year_editText;
    @ViewInject(R.id.detail_listView)
    private ListView detail_listView;
    @ViewInject(R.id.nothing_textView)
    private TextView nothing_textView;
    //访问数据库对象
    private DbUtils dbUtils;

    private MyAdapter adapter;
    private List<Object> totalList;
    private int searchType;
    private String sql;
    //listView数据的分页显示
    private int startIndex = 0;
    private int pagerSize = 20;
    //收入、支出类型的数组
    private String[] inType;
    private String[] outType;
    //查询的是收入还是支出？
    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        searchType = bundle.getInt("type");
        inType = getActivity().getResources().getStringArray(R.array.arrType_inCheck);
        outType =getActivity().getResources().getStringArray(R.array.arrType_outCheck);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_detailcheck_fragment,null);
        ViewUtils.inject(this, view);
        init();
        return view;
    }

    //初始化页面
    private void init(){

        //初始化spinner
        String[] arrType = getResources().getStringArray(R.array.searchType);
        ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,arrType);
        spinner_searchType.setAdapter(arrayAdapter);
        type = spinner_searchType.getSelectedItem().toString();
        spinner_searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = spinner_searchType.getSelectedItem().toString();
                initData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //初始换listView
        totalList = new ArrayList<Object>();
        adapter = new MyAdapter();
        detail_listView.setAdapter(adapter);
        detail_listView.setEmptyView(nothing_textView);
        //获取数据
        dbUtils = DbUtils.create(getActivity(),
                "myBills.db",
                1,
                new DbUtils.DbUpgradeListener(){
                    @Override
                    public void onUpgrade(DbUtils dbUtils, int i, int i1) {
                        //此处用于数据库升级
                    }
                });
        initData();
    }

    //初始化数据
    private void initData(){
        switch (searchType) {
            //每天收入明细
            case 0:
                btn_query.setText("查询当天");
                //读取数据库，获取listView中显示的数据,每次查取20条
                WhereBuilder builder = WhereBuilder.b();
                builder.expr("date = date('now')");
                if(type.equals("收入")){
                    queryInput(builder);
                }else{
                    queryOutput(builder);
                }
                break;
            case 1:
                //gone掉"天"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);
                break;

            case 2:
                btn_query.setText("查询当月");
                //gone掉"天"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);
                //获取时间
                Date date =new Date();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
                String dateStr = fmt.format(date);
                //获取数据
                WhereBuilder builder1 = WhereBuilder.b();
                builder1.expr("date","like",dateStr+"%");
                if(type.equals("收入")){
                    queryInput(builder1);
                }else{
                    queryOutput(builder1);
                }
                break;

            case 3:
                btn_query.setText("查询当年");
                //gone掉"天","月"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);
                mouth_editText.setVisibility(View.GONE);
                mouth_textView.setVisibility(View.GONE);

                //获取时间
                Date date1 =new Date();
                SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy");
                String dateStr1 = fmt1.format(date1);
                //获取数据
                WhereBuilder builder2 = WhereBuilder.b();
                builder2.expr("date","like",dateStr1+"%");
                if(type.equals("收入")){
                    queryInput(builder2);
                }else{
                    queryOutput(builder2);
                }

                break;
        }
    }

    //查询收入明细的方法
    private void queryInput(WhereBuilder builder){
        try {
            List<Object> inChecks = dbUtils.findAll(Selector.from(Tb_InCheck.class)
                            .where(builder)
                            .limit(pagerSize)
                            .offset(startIndex)
            );
            if (inChecks != null) {
                reLoadListView(inChecks);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //查询支出明细的方法
    private void queryOutput(WhereBuilder builder){
        try {
            List<Object> outChecks = dbUtils.findAll(Selector.from(Tb_OutCheck.class)
                            .where(builder)
                            .limit(pagerSize)
                            .offset(startIndex)
            );
            if (outChecks != null) {
                reLoadListView(outChecks);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //刷新数据
    private void reLoadListView(List<Object> list){
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    //监听查询所有按钮
    @OnClick(R.id.queryAll_detail)
    public void btn_searchAll(View view){
        initData();
    }

    //查询按钮的监听
    @OnClick(R.id.btn_searchCheck)
    public void btn_search(View view){
        String year = year_editText.getText().toString();
        String mouth = mouth_editText.getText().toString();
        String day = day_editText.getText().toString();

        String expression="[0-9]{4}";
        Pattern pattern = Pattern.compile(expression);
        boolean flag = pattern.matcher(year).matches();
        if(!flag){
            Toast.makeText(getActivity(),"年份必须为4位整数！",Toast.LENGTH_SHORT).show();
            return;
        }

        String expression1="[0-9]{2}";
        Pattern pattern1 = Pattern.compile(expression1);


        switch (searchType) {
            //每天收入明细
            case 0:
                boolean flag1 = pattern1.matcher(mouth).matches();
                if(!flag1){
                    Toast.makeText(getActivity(),"月份必须为2位整数！",Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean flag2 = pattern1.matcher(day).matches();
                if(!flag2){
                    Toast.makeText(getActivity(),"日期必须为2位整数！",Toast.LENGTH_SHORT).show();
                    return;
                }

                //读取数据库，获取listView中显示的数据,每次查取20条
                String date = year+"-"+mouth+"-"+day;
                WhereBuilder builder = WhereBuilder.b();
                builder.expr("date = "+date);
                if(type.equals("收入")){
                    queryInput(builder);
                }else{
                    queryOutput(builder);
                }
                break;
            case 1:
                //gone掉"天"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);
                break;

            case 2:
                //获取时间
                String date2 =year+"-"+mouth;
                boolean flag3 = pattern1.matcher(mouth).matches();
                if(!flag3){
                    Toast.makeText(getActivity(),"月份必须为2位整数！",Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取数据
                WhereBuilder builder1 = WhereBuilder.b();
                builder1.expr("date","like",date2+"%");
                if(type.equals("收入")){
                    queryInput(builder1);
                }else{
                    queryOutput(builder1);
                }
                break;

            case 3:
                //获取时间
                String date3 = year;
                //获取数据
                WhereBuilder builder2 = WhereBuilder.b();
                builder2.expr("date","like",date3+"%");
                if(type.equals("收入")){
                    queryInput(builder2);
                }else{
                    queryOutput(builder2);
                }
                break;
        }
    }

    //自定义适配器显示数据.
    public class MyAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return totalList.size();
        }

        @Override
        public Object getItem(int position) {
            return totalList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder holder = null;
            if(convertView==null){
                holder = new MyHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_detailfragment,null);
                holder.type_textView = (TextView) convertView.findViewById(R.id.type_item_textView);
                holder.count_textView = (TextView) convertView.findViewById(R.id.count_item_textView);
                holder.date_textView = (TextView) convertView.findViewById(R.id.date_item_textView);
                holder.detail_textView = (TextView) convertView.findViewById(R.id.detail_item_textView);
                convertView.setTag(holder);
            }else{
                holder = (MyHolder) convertView.getTag();
            }
            //设置数据
            if("收入".equals(type)){
                holder.type_textView.setText(((Tb_InCheck) totalList.get(position)).getType()+"  ");
                holder.count_textView.setText("收入："+((Tb_InCheck)totalList.get(position)).getMoney()+"元");
                holder.date_textView.setText("时间:"+((Tb_InCheck)totalList.get(position)).getDate());
                holder.detail_textView.setText("备注："+(((Tb_InCheck) totalList.get(position)).getDetail()));

            }else{
                holder.type_textView.setText(
                        outType[((Tb_OutCheck) totalList.get(position)).getMainType()]+"  ");
                holder.count_textView.setText("支出："+((Tb_OutCheck) totalList.get(position)).getMoney()+"元");
                holder.date_textView.setText("时间:"+((Tb_OutCheck)totalList.get(position)).getDate());
                holder.detail_textView.setText("备注："+((Tb_OutCheck)totalList.get(position)).getDetail());
            }
            return convertView;
        }

        class MyHolder{
            private TextView type_textView;
            private TextView count_textView;
            private TextView date_textView;
            private TextView detail_textView;
        }

    }
}
