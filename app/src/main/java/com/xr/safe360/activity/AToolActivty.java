package com.xr.safe360.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.engine.SmsBackUp;

import java.io.File;

public class AToolActivty extends AppCompatActivity {

    private TextView tv_query_phone_address;
    private TextView tv_sms_backup;
    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool_activty);
        initAddress();
        initSmsBackup();
    }

    private void initSmsBackup() {
        tv_sms_backup = findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        pb_bar = findViewById(R.id.pb_bar);
        new Thread() {
            @Override
            public void run() {
                SmsBackUp.backup(getApplicationContext(), Environment.getExternalStorageDirectory() + "/sms.xml", new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int progress) {
                        progressDialog.setProgress(progress);
                    }
                });
            }
        }.start();
    }

    private void initAddress() {
        tv_query_phone_address = findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
