package com.xr.safe360.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_phone_number;
    private Button bt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();

    }

    @Override
    public void showNextPage() {
        String contact_phone = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        String phone = et_phone_number.getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);
            finish();
            SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE,phone);
        }else {
            ToastUtil.show(this,"联系人不能为空");
        }
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);

    }

    private void initUI() {
        et_phone_number = findViewById(R.id.et_phone_number);
        bt_select_number = findViewById(R.id.bt_select_number);
        String phone = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if(!TextUtils.isEmpty(phone)){et_phone_number.setText(phone);}
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
            SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE,phone);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
