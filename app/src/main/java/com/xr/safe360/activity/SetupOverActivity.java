package com.xr.safe360.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;

public class SetupOverActivity extends AppCompatActivity {

    private TextView tv_safe_phone;
    private TextView tv_reset_setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setup_over);
            initUI();
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initUI() {
        tv_safe_phone = findViewById(R.id.tv_safe_phone);
        String phone = SpUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
        tv_safe_phone.setText(phone);
        tv_reset_setup = findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Setup1Activity.class));
                finish();
            }
        });
    }
}
