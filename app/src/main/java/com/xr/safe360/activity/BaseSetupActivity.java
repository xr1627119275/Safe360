package com.xr.safe360.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xr.safe360.R;

/**
 * Created by 16271 on 2018/3/20.
 */

abstract class BaseSetupActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getRawX() - e2.getRawX() > 100) {
                    showNextPage();
                }
                if (e2.getRawX() - e1.getRawX() > 100) {
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    public abstract void showNextPage();
    public abstract void showPrePage();

    public  void nextPage(View view){
        showNextPage();
    }
    public  void prePage(View view){
        showPrePage();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);

    }
}
