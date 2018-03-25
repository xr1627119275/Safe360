package com.xr.safe360.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.xr.safe360.db.dao.BlackNumberDao;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlackNumberService extends Service {

    private InnerSmsReceiver mInnerSmsReceiver;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private BlackNumberDao mDao;
    private MyContentObserver mContentObserver;

    public BlackNumberService() {
    }

    @Override
    public void onCreate() {
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(2147483647);
        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);


        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String originatingAddress = smsMessage.getOriginatingAddress();
                int mode = mDao.getMode(originatingAddress);
                if (mode == 1 || mode == 3) {
                    abortBroadcast();
                }
            }
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                    endCall(incomingNumber);
                    break;
            }

        }
    }

    private void endCall(final String phone) {

        int mode = mDao.getMode(phone);

        if (mode == 2 || mode == 3) {
            try {
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                Method method = clazz.getMethod("getService", String.class);
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

//            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new String[]{phone});
            mContentObserver = new MyContentObserver(new Handler(), phone);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mContentObserver);
        }


    }

    class MyContentObserver extends ContentObserver {

        private final String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
            super.onChange(selfChange);
        }
    }
}
