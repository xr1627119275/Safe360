package com.xr.safe360.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.xr.safe360.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
        TextView textView= new TextView(this);
        textView.setText("TestAvtivity");
        setContentView(textView);
    }
}
