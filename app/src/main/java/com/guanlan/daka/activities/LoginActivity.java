package com.guanlan.daka.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.guanlan.daka.bean.UserBean;


import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.guanlan.daka.constant.Constant.SERVER_APP_URL;



public class LoginActivity extends AppCompatActivity {

    private String phone_regex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
    private String pwd_regex = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{6,16}$"; //密码规则 包含数字字母 6-16位
    private String returnCode;
    private String returnMsg;


    Handler handler = new Handler() {
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();

            returnCode = bundle.getString("returnCode");
            returnMsg = bundle.getString("returnMsg");
            Log.i("System.out", returnCode+returnMsg);

            if(returnCode.equals("S002")){ //注册成功的情况下返回登录页面
                Toast.makeText(LoginActivity.this,
                        returnMsg, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.putExtra("myPhone","18616945251");
                intent.setClass(LoginActivity.this,DakaMainActivity.class);
                startActivity(intent);

            }else {
                Toast.makeText(LoginActivity.this,
                        returnCode+": "+returnMsg, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daka_login);

    }

    public void goRegister(View v){


        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }


    public void login(View v){
//        NetWorkService net = new NetWorkService();
//        net.isConnect();

        String strUrlPath = SERVER_APP_URL+"&m=User&a=login" ; //登录地址
        //Toast.makeText(LoginActivity.this, strUrlPath, Toast.LENGTH_SHORT).show();
        EditText userName = (EditText)findViewById(R.id.userPhone);
        EditText userPwd = (EditText)findViewById(R.id.userPwd);


        String phone = userName.getText().toString().trim();
        String pwd = userPwd.getText().toString();

        if ("".equals(phone) || phone == null) {
            Toast.makeText(LoginActivity.this,
                    getResources().getString(R.string.phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }else if (!(phone.matches(phone_regex))) {
                Toast.makeText(
                        LoginActivity.this,
                        getResources().getString(R.string.phone_error),
                        Toast.LENGTH_SHORT).show();
                return;
        }else if ("".equals(pwd) || pwd == null) {
            Toast.makeText(
                    LoginActivity.this,
                    getResources().getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }else if (!(pwd.matches(pwd_regex))) {
            Toast.makeText(
                    LoginActivity.this,
                    getResources().getString(R.string.pwd_regex_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }else{

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json
            HashMap<String,String> loginData = new HashMap<String, String>();
            loginData.put("userphone",phone);
            loginData.put("userpwd",pwd);

            Gson gson = new Gson();
            String loginStr = gson.toJson(loginData);

//            Toast.makeText(LoginActivity.this, loginStr, Toast.LENGTH_SHORT).show();

            OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
            RequestBody body = RequestBody.create(JSON, loginStr);
            Request request = new Request.Builder()
                    .url(strUrlPath)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if(response.isSuccessful()){//回调的方法执行在子线程。
                        String str = response.body().string();
                        Gson gson1 = new Gson();
                        UserBean result = gson1.fromJson(str,UserBean.class);

                        Bundle bunlde = new Bundle();
                        bunlde.putString("returnCode",result.getReturnCode());
                        bunlde.putString("returnMsg",result.getReturnMsg());
                        Message msg = handler.obtainMessage();
                        msg.setData(bunlde);
                        //                    backgroundThreadShortToast(RegisterActivity.this,bunlde.getString("returnCode"));
                        handler.sendMessage(msg);

                    }
                }
            });

        }



    }


}
