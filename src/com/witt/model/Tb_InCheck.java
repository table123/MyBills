package com.witt.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 创建收入表
 * Created by IDEA
 * user:witt
 * date:2015/4/12.
 */

@Table(name="Tb_InCheck")
public class Tb_InCheck {
    //表的主键
    @Id(column = "_id")
    private long id;
    //表中的列type,money,date,detail
    @Column(column = "type")
    private String type;
    @Column(column = "money")
    private float money;
    @Column(column = "date")
    private String date;
    @Column(column = "detail")
    private String detail;

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public float getMoney() {
        return money;
    }

    public String getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }
}
