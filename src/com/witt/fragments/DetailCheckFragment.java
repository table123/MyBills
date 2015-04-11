package com.witt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.witt.mybills.R;
import com.witt.tools.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */

public class DetailCheckFragment extends Fragment {

    /**
     * 获取查询类型Spinner对话框
     */
    @ViewInject(R.id.spinner_searchType)
    private Spinner spinner_searchType;
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
    private MySQLiteOpenHelper DBHelper;
    private MyAdapter adapter;
    private List<Map<String,String>> totalList;
    private int type;
    private String sql;
    //listView数据的分页显示
    private int startIndex=0;
    private int pagerSize=20;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = bundle.getInt("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_detailcheck_fragment,null);
        ViewUtils.inject(this,view);
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

        //初始换listView
        totalList = new ArrayList<Map<String, String>>();
        adapter = new MyAdapter();
        detail_listView.setAdapter(adapter);
        detail_listView.setEmptyView(nothing_textView);

        //获取数据
        DBHelper = new MySQLiteOpenHelper(getActivity());
        List<Map<String,String>> list = null;
        switch (type) {
            //每天收入情况
            case 0:
                sql = "select * from tb_inCheck order by date desc limit(?,?)";
                list = getDataBaseData(sql);
                startIndex = pagerSize+1;
                break;
            case 1:
                //gone掉"天"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);

                break;
            case 2:
                //gone掉"天","月"的输入框
                day_editText.setVisibility(View.GONE);
                day_textView.setVisibility(View.GONE);
                mouth_editText.setVisibility(View.GONE);
                mouth_textView.setVisibility(View.GONE);

                break;

            case 3:

                break;
        }
        reLoadListView(list);

    }

    //获取数据库的数据
    private List<Map<String,String>> getDataBaseData(String sqlStr){
        List<Map<String,String>> list = DBHelper.selectList(sqlStr,
                new String[]{startIndex+"",pagerSize+""});
        return list;
    }


    //刷新数据
    private void reLoadListView(List<Map<String,String>> list){
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    //查询按钮的监听
    @OnClick(R.id.btn_searchCheck)
    public void btn_search(){

        //if("收入".equals(spinner_searchType.getSelectedItem().toString())) {}
    }

    //自定义适配器显示数据
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
            String type = spinner_searchType.getSelectedItem().toString();
            if("收入".equals(type)){
                holder.type_textView.setText(totalList.get(position).get("type"));
                holder.count_textView.setText("收入："+totalList.get(position).get("count"));

            }else{
                holder.type_textView.setText(totalList.get(position).get("type")+
                ":"+totalList.get(position).get("detailType"));
                holder.count_textView.setText("支出："+totalList.get(position).get("count"));
            }
            holder.date_textView.setText(totalList.get(position).get("date"));
            holder.detail_textView.setText("原因："+totalList.get(position).get("detail"));

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
