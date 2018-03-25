package com.xr.safe360.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.engine.AddressDao;

public class QueryAddressActivity extends AppCompatActivity {

    private EditText et_phone;
    private Button bt_query;
    private String mAddress;
    private TextView tv_query_resuslt;
    private Handler mHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_query_resuslt.setText(mAddress);
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        initUI();
    }

    private void initUI() {
        et_phone = findViewById(R.id.et_phone);
        bt_query = findViewById(R.id.bt_query);
        tv_query_resuslt = findViewById(R.id.tv_query_resuslt);

        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                if(!TextUtils.isEmpty(phone)){
                    query(phone);
                }else{
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
//                    shake.setInterpolator(new Interpolator() {
//                        @Override
//                        public float getInterpolation(float input) {
//
//                            return 0;
//                        }
//                    });
                    et_phone.startAnimation(shake);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    vibrator.vibrate(new long[]{200,1000},-1);
                }
            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_phone.getText().toString().trim();
                query(phone);
            }
        });
    }

    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                mHander.sendEmptyMessage(0);
                super.run();
            }
        }.start();
    }
}
