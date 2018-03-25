package com.xr.safe360.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();


    }

    @Override
    public void showNextPage() {
        boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if(open_security) {
            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putBoolean(this, ConstantValue.SETUP_OVER, true);
        }else {
            ToastUtil.show(this,"请开启防盗保护设置");
        }
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);

    }

    private void initUI() {
        cb_box = findViewById(R.id.cb_box);
        final boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            cb_box.setChecked(true);
            cb_box.setText("安全设置已开启");
        }
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                if (isChecked) {
                    cb_box.setText("安全设置已开启");
                } else {
                    cb_box.setText("你没有设置防盗保护");
                }
            }
        });

    }

}
