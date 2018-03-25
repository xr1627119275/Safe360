package com.xr.safe360.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;

public class ToastLocationActivity extends Activity {


    private Button iv_drag;
    private Button bt_top;
    private Button bt_bottom;
    private WindowManager mWindowManager;
    private int mScreenWidth;
    private int mScreenHeight;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);


        initUI();

    }

    private void initUI() {
        iv_drag = findViewById(R.id.bt_drag);
        bt_top = findViewById(R.id.bt_top);
        bt_bottom = findViewById(R.id.bt_bottom);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        int location_x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
        int location_y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = location_x;
        layoutParams.topMargin = location_y;
        iv_drag.setLayoutParams(layoutParams);
        if (location_y > mScreenHeight / 2) {
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        } else {
            bt_top.setVisibility(View.INVISIBLE);
            bt_bottom.setVisibility(View.VISIBLE);
        }
        iv_drag.setOnTouchListener(new View.OnTouchListener() {

            private int startX;
            private int startY;
            private int moveX;
            private int moveY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = (int) event.getRawX();
                        moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        int left = iv_drag.getLeft() + disX;
                        int top = iv_drag.getTop() + disY;
                        int right = iv_drag.getRight() + disX;
                        int bottom = iv_drag.getBottom() + disY;

                        if (left < 0) {
                            return true;
                        }
                        if (right > mScreenWidth) {
                            return true;
                        }
                        if (top < 0) {
                            return true;
                        }
                        if (bottom > mScreenHeight - 22) {
                            return true;
                        }
                        if (top > mScreenHeight / 2) {
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        } else {
                            bt_top.setVisibility(View.INVISIBLE);
                            bt_bottom.setVisibility(View.VISIBLE);
                        }
                        iv_drag.layout(left, top, right, bottom);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                        break;
                }
                return false;
            }
        });

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    int left = mScreenWidth / 2 - iv_drag.getWidth() / 2;
                    int top = mScreenHeight / 2 - iv_drag.getHeight() / 2;
                    int right = mScreenWidth / 2 + iv_drag.getWidth() / 2;
                    int bottom = mScreenHeight / 2 + iv_drag.getHeight() / 2;

                    iv_drag.layout(left, top, right, bottom);
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());

                }
            }
        });

    }
}
