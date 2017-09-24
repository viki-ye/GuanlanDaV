package com.guanlan.daka.base;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guanlan.daka.activities.R;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yepengfei on 17/9/17.
 */

public class BaseActivity extends Activity {
    ConnectivityManager manager;

    public Boolean isConnect() {

        manager = (ConnectivityManager) getSystemService(BaseActivity.this.CONNECTIVITY_SERVICE);

        if (manager.getActiveNetworkInfo() != null) {

            if (manager.getActiveNetworkInfo().isAvailable()) {

                if (manager.getActiveNetworkInfo().isConnected()) {

                    return true;
                }
            }
        }

        return false;
    }

    public boolean isConnect_() {

        if (!isConnect()) {
            Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public String postData(String url,HashMap<String,String> data){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder()
                .url(url)
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
                    String res = response.body().string();


                   // Log.i("aaaaa",result.getReturnCode());

                }
            }
        });
        return "1";

    }



}
