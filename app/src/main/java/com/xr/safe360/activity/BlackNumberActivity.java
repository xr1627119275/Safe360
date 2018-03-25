package com.xr.safe360.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.db.dao.BlackNumberDao;
import com.xr.safe360.db.domain.BlackNumberInfo;
import com.xr.safe360.utils.ToastUtil;

import java.util.List;

public class BlackNumberActivity extends AppCompatActivity {

    private Button bt_add;
    private ListView lv_blacknumber;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberInfoList;
    private int mode = 1;
    private int mCount = 0;
    private boolean mIsload = false;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (adapter == null) {
                adapter = new MyAdapter();

                lv_blacknumber.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                mBlackNumberInfoList = mDao.find(0);
                mCount = mDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add = findViewById(R.id.bt_add);
        lv_blacknumber = findViewById(R.id.lv_blacknumber);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumberInfoList != null) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_blacknumber.getLastVisiblePosition() >= mBlackNumberInfoList.size() - 1
                            && !mIsload) {
                        if (mCount > mBlackNumberInfoList.size()) {
                            new Thread() {
                                @Override
                                public void run() {
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberInfoList.size());
                                    mBlackNumberInfoList.addAll(moreData);
                                    mHandler.sendEmptyMessage(1);
                                }
                            }.start();

                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText et_phone = view.findViewById(R.id.et_phone);
        RadioGroup rg_group = view.findViewById(R.id.rg_group);
        Button bt_submit = view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        dialog.show();
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    mDao.insert(phone, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    mBlackNumberInfoList.add(0, blackNumberInfo);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumberInfoList.size();
        }

        @Override
        public BlackNumberInfo getItem(int position) {
            return mBlackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                viewHolder.tv_phone = convertView.findViewById(R.id.tv_phone);
                viewHolder.tv_mode = convertView.findViewById(R.id.tv_mode);
                viewHolder.tv_delete = convertView.findViewById(R.id.tv_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            TextView tv_phone = convertView.findViewById(R.id.tv_phone);
//            TextView tv_mode = convertView.findViewById(R.id.tv_mode);
//            ImageView tv_delete = convertView.findViewById(R.id.tv_delete);
            viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDao.delete(mBlackNumberInfoList.get(position).phone);
                    mBlackNumberInfoList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            viewHolder.tv_phone.setText(mBlackNumberInfoList.get(position).phone);
            switch (Integer.parseInt(mBlackNumberInfoList.get(position).mode)) {
                case 1:
                    viewHolder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_mode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView tv_delete;
    }
}
