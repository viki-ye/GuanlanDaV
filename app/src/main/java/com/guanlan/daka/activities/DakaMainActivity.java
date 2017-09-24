package com.guanlan.daka.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guanlan.daka.bean.FansBean;
import com.guanlan.daka.bean.JsonBean;
import com.guanlan.daka.bean.UserBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.guanlan.daka.constant.Constant.DAKA_PERMISSION;
import static com.guanlan.daka.constant.Constant.SERVER_APP_URL;


public class DakaMainActivity extends AppCompatActivity {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json
    private List<FansBean> lists;
    private ProgressDialog mpDialog;
    private List<FansBean> records;
    private String returnCode;
    private String returnMsg;
    private List<FansBean> returnInfo;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daka_main);

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, DAKA_PERMISSION[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
    }

    //这个handler只有一个作用，接受到消息后关闭提示进度框
    Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            mpDialog.dismiss();
        };
    };

    Handler toastHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "批量导出完成！", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "导出失败！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        };
    };

    Handler fansHandler = new Handler() {
        //接收粉丝信息
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();

            returnCode = bundle.getString("returnCode");
            returnMsg = bundle.getString("returnMsg");
            ArrayList rlist = bundle.getParcelableArrayList("returnInfo");
            returnInfo = (List<FansBean>) rlist.get(0);

            Log.i("System.out", returnCode+returnMsg);

            if(returnCode.equals("S006")){ //获取粉丝成功
//                Toast.makeText(DakaMainActivity.this,
//                        returnMsg, Toast.LENGTH_SHORT)
//                        .show();
                //弹出提示进度框
                mpDialog=ProgressDialog.show(DakaMainActivity.this, "粉丝获取成功", "添加通讯录中，请等待...");

                //开启一个新线程
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Message msg=new Message();
                        //在新的线程执行添加操作
                        if (returnInfo!=null&&returnInfo.size()!=0) {
                            CopyFansRecords(returnInfo);
                            //弹出一句话，提示批量操作完成
                            msg.what=1;
                            toastHandler.sendMessage(msg);
                            //当添加操作完成后，给提示进度框发送一个消息，让进度框关闭
                            handler.sendEmptyMessage(0);


                        }else{
                            msg.what=2;
                            toastHandler.sendMessage(msg);
                            handler.sendEmptyMessage(0);

                        }

                    }
                }).start();


            }else {
                Toast.makeText(DakaMainActivity.this,
                        returnCode+": "+returnMsg, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
    };

    public void getFans(View view) {
        String fansUrl = SERVER_APP_URL+"&m=Fans&a=getFans"; //短信url
        Intent intent = getIntent();
        String myPhone = intent.getStringExtra("myPhone");

        HashMap<String,String> myData = new HashMap<String, String>();
        myData.put("myPhone",myPhone);

        Gson gson = new Gson();
        String smsStr = gson.toJson(myData);

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient 对象
        RequestBody body = RequestBody.create(JSON, smsStr);
        Request request = new Request.Builder()
                .url(fansUrl)
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
                    JsonBean result = gson1.fromJson(str,JsonBean.class);
                    
                    Bundle bunlde = new Bundle();
                    bunlde.putString("returnCode",result.getReturnCode());
                    bunlde.putString("returnMsg",result.getReturnMsg());
                    ArrayList list = new ArrayList();
                    list.add(result.getReturnInfo());

                    bunlde.putParcelableArrayList("returnInfo",list);
                    Message msg = fansHandler.obtainMessage();
                    msg.setData(bunlde);
                    //                    backgroundThreadShortToast(RegisterActivity.this,bunlde.getString("returnCode"));

                    fansHandler.sendMessage(msg);



                }
            }
        });


    }

    /**
     * 将数据保存到通讯录中的方法
     * @param list
     */
    public  void CopyFansRecords(List<FansBean> list){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex;
        if (list==null||list.size()==0) {
            return ;
        }
        for (int i = 0; i < list.size(); i++) {
            rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,list.get(i).getName())
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_MOBILE)
                    .withYieldAllowed(true)
                    .build());

        }
        try {
            //这里才调用的批量添加
            this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.no_permission_title))
                .setMessage(getResources().getString(R.string.no_permission_contacts_tip))
                .setPositiveButton(getResources().getString(R.string.permission_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.permission_refuse), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, DAKA_PERMISSION, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许支付宝使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, DAKA_PERMISSION[1]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}


