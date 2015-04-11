package com.witt.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 创建支出
 * Created by IDEA
 * user:witt
 * date:2015/4/12.
 */
@Table(name="Tb_OutCheck")
public class Tb_OutCheck {
    @Id(column = "_id")
    private long id;
    //表中的列名mainType,money,date,cId,detail
    @Column(column = "mainType")
    private int mainType;
    @Column(column = "money")
    private float money;
    @Column(column = "date")
    private String date;
    //设置为外键
    @Foreign(column = "cId",foreign = "_id")
    private Tb_OutCheckType outCheckType;
    @Column(column = "detail")
    private String detail;

    public void setId(long id) {
        this.id = id;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setOutCheckType(Tb_OutCheckType outCheckType) {
        this.outCheckType = outCheckType;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getId() {
        return id;
    }

    public int getMainType() {
        return mainType;
    }

    public float getMoney() {
        return money;
    }

    public String getDate() {
        return date;
    }

    public Tb_OutCheckType getOutCheckType() {
        return outCheckType;
    }

    public String getDetail() {
        return detail;
    }
}
