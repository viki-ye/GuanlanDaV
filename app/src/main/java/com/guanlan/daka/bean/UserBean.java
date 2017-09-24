package com.guanlan.daka.bean;

/**
 * Created by yepengfei on 17/9/15.
 */

public class UserBean {
    private String phone;
    private String pwd;
    private String registerPhone;
    private String registerPwd;
    private String returnCode;
    private String returnMsg;
    private String smsCode;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPwd(){
        return pwd;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public String getReturnCode(){
        return returnCode;
    }

    public void setReturnCode(String returnCode){
        this.returnCode = returnCode;
    }

    public String getReturnMsg(){
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg){
        this.returnMsg = returnMsg;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getRegisterPhone() {
        return registerPhone;
    }

    public void setRegisterPhone(String registerPhone) {
        this.registerPhone = registerPhone;
    }

    public String getRegisterPwd() {
        return registerPwd;
    }

    public void setRegisterPwd(String registerPwd) {
        this.registerPwd = registerPwd;
    }
}
