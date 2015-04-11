package com.witt.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 创建支出类型表
 * Created by IDEA
 * user:witt
 * date:2015/4/12.
 */
@Table(name = "Tb_OutCheckType")
public class Tb_OutCheckType {
    @Id(column = "_id")
    private long Id;
    //列名cType,mainType
    @Column(column = "cType")
    private String cType;
    @Column(column = "mainType")
    private int mainType;

    public void setId(long id) {
        Id = id;
    }

    public void setCType(String cType) {
        this.cType = cType;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    public long getId() {
        return Id;
    }

    public String getCType() {
        return cType;
    }

    public int getMainType() {
        return mainType;
    }
}
