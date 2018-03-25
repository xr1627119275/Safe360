package com.xr.safe360.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xr.safe360.R;
import com.xr.safe360.engine.AddressDao;
import com.xr.safe360.receiver.BootReceiver;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class AddressService extends Service {
    private static final String TAG = "AddressService";
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWindowManager;
    private String address;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(address);
        }
    };
    private TextView tv_toast;
    private int[] mDrawableIds;
    private int mScreenWidth;
    private int mScreenHeight;
    private InnerOutCallRevecier innerOutCallRecevier;

    public AddressService() {
    }

    @Override
    public void onCreate() {

        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        innerOutCallRecevier = new InnerOutCallRevecier();
        registerReceiver(innerOutCallRecevier,intentFilter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (mTM != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:

                    if(mWindowManager!=null&&mViewToast!=null){
                        mWindowManager.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {
//        Toast.makeText(getApplicationContext(),incomingNumber,Toast.LENGTH_LONG).show();
        mParams.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        mParams.y = SpUtils.getInt(getApplicationContext(),ConstantValue.LOCATION_Y,0);
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            mParams.gravity = Gravity.LEFT + Gravity.TOP;

        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = mViewToast.findViewById(R.id.tv_toast);
        mDrawableIds = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        int toastStyleIndex = SpUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
        mParams.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        mParams.y = SpUtils.getInt(getApplicationContext(),ConstantValue.LOCATION_Y,0);
        mWindowManager.addView(mViewToast,mParams);
        query(incomingNumber);
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        mParams.x += disX;
                        mParams.y += disY;

                        if(mParams.x<0){
                            mParams.x = 0;
                        }
                        if(mParams.y<0){
                            mParams.y = 0;
                        }
                        if(mParams.x > mScreenWidth-mViewToast.getWidth()){
                            mParams.x = mScreenWidth-mViewToast.getWidth();
                        }
                        if(mParams.y > mScreenHeight-mViewToast.getHeight()-22){
                            mParams.y = mScreenHeight-mViewToast.getHeight()-22;
                        }
                        mWindowManager.updateViewLayout(mViewToast,mParams);
                        startX = (int)event.getRawX();
                        startY = (int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, mParams.x);
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, mParams.y);
                        break;
                }
                return true;
            }
        });
    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                address = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
                super.run();
            }
        }.start();
    }

    private class InnerOutCallRevecier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = getResultData();
            showToast(phone);
        }
    }
}
