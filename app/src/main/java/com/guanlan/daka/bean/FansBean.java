package com.guanlan.daka.bean;

/**
 * Created by yepengfei on 17/9/24.
 */

public class FansBean {
    private  int id;
    private String name;
    private String phone;


    public void FansBean(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
