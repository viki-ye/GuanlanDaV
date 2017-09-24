package com.guanlan.daka.bean;

/**
 * Created by yepengfei on 17/9/19.
 */

public class UpdateBean {
    private String version;
    private String describe;
    private String url;

    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
