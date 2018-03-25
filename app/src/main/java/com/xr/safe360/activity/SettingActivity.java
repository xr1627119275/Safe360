package com.xr.safe360.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xr.safe360.R;
import com.xr.safe360.service.AddressService;
import com.xr.safe360.service.BlackNumberService;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.ServiceUtil;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.view.SettingClickView;
import com.xr.safe360.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private SettingClickView sic_toast_style;
    private String[] mToastStyleDes;
    private int mToast_style_item;
    private SettingClickView scv_location;
    private SettingItemView siv_blacknumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
        initAddress();
        initToastStyle();
        initLocation();
        initBlacknumber();
    }

    private void initBlacknumber() {
        siv_blacknumber = findViewById(R.id.siv_blacknumber);
        boolean running = ServiceUtil.isRunning(this, "com.xr.safe360.service.BlackNumberService");
        siv_blacknumber.setCheck(running);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_blacknumber.isCheck();
                siv_blacknumber.setCheck(!check);
                Intent intent = new Intent(getApplicationContext(), BlackNumberService.class);
                if (!check) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    private void initLocation() {
        scv_location = findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        sic_toast_style = findViewById(R.id.scv_toast_style);
        sic_toast_style.setTitle("电话归属地样式选择");
        mToastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        mToast_style_item = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        sic_toast_style.setDes(mToastStyleDes[mToast_style_item]);
        sic_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastDialog();
            }
        });
    }

    private void showToastDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择归属地的样式");
        builder.setSingleChoiceItems(mToastStyleDes, mToast_style_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mToast_style_item = which;
                sic_toast_style.setDes(mToastStyleDes[which]);
                SpUtils.putInt(getApplicationContext(),ConstantValue.TOAST_STYLE,which);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initAddress() {
        final SettingItemView siv_update = findViewById(R.id.siv_address);
        boolean running = ServiceUtil.isRunning(this, "com.xr.safe360.service.AddressService");
        siv_update.setCheck(running);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_update.isCheck();
                siv_update.setCheck(!check);
                Intent intent = new Intent(getApplicationContext(), AddressService.class);
                if (!check) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    private void initUpdate() {
        final SettingItemView siv_update = findViewById(R.id.siv_update);
        boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}
