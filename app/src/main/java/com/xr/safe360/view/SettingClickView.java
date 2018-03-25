package com.xr.safe360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xr.safe360.R;

;

/**
 * Created by 16271 on 2018/3/17.
 */

public class SettingClickView extends RelativeLayout {

    private TextView tv_des;


    private TextView tv_title;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_click_item, this);

        tv_title = this.findViewById(R.id.tv_title);
        tv_des = this.findViewById(R.id.tv_des);
    }
    public void setTitle(String title){
        tv_title.setText(title);
    }
    public void setDes(String des){
        tv_des.setText(des);
    }

}
