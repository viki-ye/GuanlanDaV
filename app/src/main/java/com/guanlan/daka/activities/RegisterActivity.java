package com.guanlan.daka.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guanlan.daka.bean.UserBean;
import com.guanlan.daka.utils.CountDownButtonHelper;

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

public class RegisterActivity extends AppCompatActivity {


    MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json
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

            if(returnCode.equals("S001")){ //注册成功的情况下返回登录页面
                Toast.makeText(RegisterActivity.this,
                        returnMsg, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);

            }else {
                Toast.makeText(RegisterActivity.this,
                        returnCode+": "+returnMsg, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daka_register);


    }

    public static void backgroundThreadShortToast(final Context context,
                                                  final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void getSMSCode(View view) {
        EditText registerPhone = (EditText) findViewById(R.id.registerPhone);
        final Button codeButton = (Button) findViewById(R.id.getCode);
//        EditText registerPwd = (EditText)findViewById(R.id.registerPwd);
//        EditText registerCode = (EditText)findViewById(R.id.registerCode);

        CountDownButtonHelper helper = new CountDownButtonHelper(codeButton, "倒计时", 60, 1);

        helper.setOnFinishListener(new CountDownButtonHelper.OnFinishListener() {
            @Override
            public void finish() {
                // Toast.makeText(RegisterActivity.this,"倒计时结束",Toast.LENGTH_SHORT).show();
                codeButton.setText("再次获取");
            }
        });

        helper.start();


        String smsUrl = SERVER_APP_URL+"&m=User&a=getSMSCode"; //短信url
        String phone = registerPhone.getText().toString().trim();

        if ("".equals(phone) || phone == null) {
            Toast.makeText(RegisterActivity.this,
                    getResources().getString(R.string.phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        HashMap<String,String> smsData = new HashMap<String, String>();
        smsData.put("registerPhone",phone);

        Gson gson = new Gson();
        String smsStr = gson.toJson(smsData);

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
        RequestBody body = RequestBody.create(JSON, smsStr);
        Request request = new Request.Builder()
                .url(smsUrl)
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

                   String resStr = result.getReturnCode()+":"+result.getReturnMsg();


                    backgroundThreadShortToast(RegisterActivity.this,resStr);

                }
            }
        });
    }

    public void goRegister(View view) {
        EditText registerPhone = (EditText) findViewById(R.id.registerPhone);
        EditText registerCode= (EditText)findViewById(R.id.smsCode);
        EditText registerPwd = (EditText)findViewById(R.id.registerPwd);

       String phone  =  registerPhone.getText().toString().trim();
        String pwd  =  registerPwd.getText().toString().trim();
        String code  =  registerCode.getText().toString().trim();


        if ("".equals(phone) || phone == null) {
            Toast.makeText(RegisterActivity.this,
                    getResources().getString(R.string.phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }else if (!(phone.matches(phone_regex))) {
            Toast.makeText(
                    RegisterActivity.this,
                    getResources().getString(R.string.phone_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }else if ("".equals(pwd) || pwd == null) {
            Toast.makeText(
                    RegisterActivity.this,
                    getResources().getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }else if (!(pwd.matches(pwd_regex))) {
            Toast.makeText(
                    RegisterActivity.this,
                    getResources().getString(R.string.pwd_regex_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }else if ("".equals(code) || code == null){
            Toast.makeText(
                    RegisterActivity.this,
                    getResources().getString(R.string.code_empty),
                    Toast.LENGTH_SHORT).show();
            return;

        }else{

            String registerUrl = SERVER_APP_URL+"&m=User&a=register"; //短信url

            HashMap<String,String> registerData = new HashMap<String, String>();
            registerData.put("smsCode",code);
            registerData.put("registerPhone",phone);
            registerData.put("registerPwd",pwd);


            Gson gson = new Gson();
            String registerStr = gson.toJson(registerData);

            OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
            RequestBody body = RequestBody.create(JSON, registerStr);
            Request request = new Request.Builder()
                    .url(registerUrl)
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


//    public void checkCode(String code){
//        String checkUrl = SERVER_APP_URL+"&m=User&a=getSMSCode"; //短信url
//
//        HashMap<String,String> checkData = new HashMap<String, String>();
//        checkData.put("smsCode",code);
//
//        Gson gson = new Gson();
//        String smsStr = gson.toJson(checkData);
//
//        OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
//        RequestBody body = RequestBody.create(JSON, smsStr);
//        Request request = new Request.Builder()
//                .url(checkUrl)
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                if(response.isSuccessful()){//回调的方法执行在子线程。
//                    String str = response.body().string();
//                    Gson gson1 = new Gson();
//                    UserBean result = gson1.fromJson(str,UserBean.class);
//
//                    Bundle bunlde = new Bundle();
//                    bunlde.putString("returnCode",result.getReturnCode());
//                    bunlde.putString("returnMsg",result.getReturnMsg());
//                    Message msg = handler.obtainMessage();
//                    msg.setData(bunlde);
////                    backgroundThreadShortToast(RegisterActivity.this,bunlde.getString("returnCode"));
//                    handler.sendMessage(msg);
//                }
//            }
//        });


//    }
}
