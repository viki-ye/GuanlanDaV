package com.guanlan.daka.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.AndroidTestCase;

/**
 * Created by yepengfei on 17/9/18.
 */

public class ConstactsHelper extends AndroidTestCase {
    public void insertConstacts() {

        ContentValues values = new ContentValues();
        Uri rawContactUri = this.getContext().getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);//EPApplication是我定义的Application的子类，getContextObject方法返回的是context
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 表插入姓名数据
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);// 内容类型
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "维拉报警电话");
        mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                values);

        //写入电话
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "95213176");
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);


    }


}
