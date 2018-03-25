package com.xr.safe360.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private static final String TAG = "ContactListActivity";
    private ListView lv_contcat;
    private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                myadapter = new Myadapter();
                lv_contcat.setAdapter(myadapter);
            }
        }
    };
    private Myadapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), new String[]{"contact_id"}, null, null, null);
                contactList.clear();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
//                    Log.i(TAG, "id= "+id);
                    Cursor cursor1 = contentResolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (cursor1.moveToNext()) {
                        String data = cursor1.getString(0);
                        String mimetype = cursor1.getString(1);
//                        Log.i(TAG, "data="+data+"mimetype="+mimetype);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            if (!TextUtils.isEmpty(data))
                                hashMap.put("phone", data);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    cursor1.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        lv_contcat = findViewById(R.id.lv_contcat);
        lv_contcat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hashMap = myadapter.getItem(position);
                String phone = hashMap.get("phone");
                Intent intent = new Intent();
                intent.putExtra("phone",phone);
                setResult(0,intent);
                finish();
            }
        });
    }

    private class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_phone = view.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
