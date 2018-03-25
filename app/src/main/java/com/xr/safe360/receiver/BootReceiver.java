package com.xr.safe360.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "开机了卡刷卡单到那时快登记卡几十块就肯定不能看你说可能");

        String SpsimSerialNumber = SpUtils.getString(context, ConstantValue.SIM_NUMBER, "");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber()+"xxx";
        if(!SpsimSerialNumber.equals(simSerialNumber)){
            SmsManager sm = SmsManager.getDefault();
            String phone = SpUtils.getString(context, ConstantValue.CONTACT_PHONE, "");
            sm.sendTextMessage(phone,null,"sim change!!!",null,null);
        }
    }
}
