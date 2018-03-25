package com.xr.safe360.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.Md5Util;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initData() {
        mTitleStr = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "浏览统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools,
                R.drawable.home_settings
        };
        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(),AToolActivty.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");

        if(TextUtils.isEmpty(psd)){
            showSetPsdDialog();
        }else{
            showConfirmPsdDialog();
        }
    }
    /**
     * 确认密码的对话框
     */
    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_confirm_psd =view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString();
                if(!TextUtils.isEmpty(confirmPsd)){
                    String psd = SpUtils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    if(Md5Util.encoder(confirmPsd).equals(psd)){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }else{
                        ToastUtil.show(getApplicationContext(),"密码错误");
                    }
                }else{
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置密码的对话框
     */
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd =view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                if(!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(confirmPsd)){
                    if(psd.trim().equals(confirmPsd.trim())){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        SpUtils.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(psd));
                    }else{
                        ToastUtil.show(getApplicationContext(),"两次密码不一致");
                    }
                }else{
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        gv_home = findViewById(R.id.gv_home);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            TextView tv_title = view.findViewById(R.id.tv_title);
            tv_title.setText(mTitleStr[position]);
            iv_icon.setImageResource(mDrawableIds[position]);
            return view;
        }
    }
}
