package com.xr.safe360.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
;import com.xr.safe360.R;

/**
 * Created by 16271 on 2018/3/17.
 */

public class SettingItemView extends RelativeLayout {
    private static final String TAG = "SettingItemView";
    private CheckBox cb_box;
    private TextView tv_des;
    private String NAMESPACE = "http://schemas.android.com/apk/res/com.xr.safe360";
    private String mDestitle;
    private String mDesoff;
    private String mDeson;
    private TextView tv_title;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_item, this);
        initAttrs(attrs);
        tv_title = this.findViewById(R.id.tv_title);
        tv_des = this.findViewById(R.id.tv_des);
        cb_box = this.findViewById(R.id.cb_box);
        tv_title.setText(mDestitle);
        tv_des.setText(mDesoff);
    }

    private void initAttrs(AttributeSet attrs) {
        Log.d(TAG, "initAttrs: "+attrs.getAttributeCount());
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    public boolean isCheck() {
        return cb_box.isChecked();
    }

    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if(isCheck){
            tv_des.setText(mDeson);
        }else{
            tv_des.setText(mDesoff);
        }
    }


}
