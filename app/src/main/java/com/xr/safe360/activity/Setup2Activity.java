package com.xr.safe360.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;
import com.xr.safe360.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        init();
    }

    @Override
    public void showNextPage() {
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if(!TextUtils.isEmpty(sim_number)){
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);
            finish();
        }else{
            ToastUtil.show(this,"请绑定SIM卡");
        }
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);


    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);

    }

    private void init() {
        siv_sim_bound = findViewById(R.id.siv_sim_bound);
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = siv_sim_bound.isCheck();
                siv_sim_bound.setCheck(!ischeck);
                if (!ischeck) {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    SpUtils.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                }else {
                    SpUtils.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }



}
