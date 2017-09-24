package com.guanlan.daka.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yepengfei on 17/9/24.
 */

public class JsonBean {
    private String returnCode;
    private String returnMsg;
    private ArrayList<FansBean> returnInfo;

    public String getReturnCode(){
        return returnCode;
    }

    public void setReturnCode(String returnCode){
        this.returnCode = returnCode;
    }


    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public ArrayList<FansBean> getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(ArrayList<FansBean> returnInfo) {
        this.returnInfo = returnInfo;
    }
}
