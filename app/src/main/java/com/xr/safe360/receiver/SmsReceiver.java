package com.xr.safe360.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.xr.safe360.R;
import com.xr.safe360.service.LocationService;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.ToastUtil;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        boolean open_security = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if(open_security){
            for (Object obj:pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                if(messageBody.contains("#*alarm*#")){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }else if(messageBody.contains("#*location*#")){
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }
            }
        }
    }
}
