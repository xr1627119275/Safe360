package com.xr.safe360.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xr.safe360.R;
import com.xr.safe360.db.domain.AppInfo;
import com.xr.safe360.db.domain.ProcessInfo;
import com.xr.safe360.engine.ProcessInfoProvider;
import com.xr.safe360.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_progress_count;
    private TextView tv_memory_info;
    private ListView lv_process_list;
    private Button bt_select_all;
    private Button bt_select_reverse;
    private Button bt_clean;
    private Button bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private myAdapter mAdapter;
    private TextView tv_des;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new myAdapter();
            lv_process_list.setAdapter(mAdapter);
            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户进程(" + mCustomerList.size() + ")");
            }
        }
    };
    private ProcessInfo mProcessInfo;
    private long mAvailSpace;
    private long mTotalSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initUI();
        initTitleData();
        initList();
    }

    private void initList() {
        getData();
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfoList(getApplicationContext());

                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : mProcessInfoList) {
                    if (processInfo.isSystem) {
                        //系统应用
                        mSystemList.add(processInfo);
                    } else {
                        //用户应用
                        mCustomerList.add(processInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }

        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_progress_count.setText("进程总数" + mProcessCount);

        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
        String strTotalSpace = Formatter.formatFileSize(this, mTotalSpace);
        tv_memory_info.setText("剩余/总共:" + strAvailSpace + "/" + strTotalSpace);
    }

    private void initUI() {
        tv_progress_count = findViewById(R.id.tv_progress_count);
        tv_memory_info = findViewById(R.id.tv_memory_info);
        lv_process_list = findViewById(R.id.lv_process_list);
        tv_des = findViewById(R.id.tv_des);
        bt_select_all = findViewById(R.id.bt_select_all);
        bt_select_reverse = findViewById(R.id.bt_select_reverse);
        bt_clean = findViewById(R.id.bt_clean);
        bt_setting = findViewById(R.id.bt_setting);
        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中view就是listView对象
                //firstVisibleItem第一个可见条目索引值
                //visibleItemCount当前一个屏幕的可见条目数
                //总共条目总数
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //滚动到了系统条目
                        tv_des.setText("系统进程(" + mSystemList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tv_des.setText("用户进程(" + mCustomerList.size() + ")");
                    }
                }

            }
        });
        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size() + 1) {
                        mProcessInfo = mCustomerList.get(position - 1);
                    } else {
                        //返回系统应用对应条目的对象
                        mProcessInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            CheckBox cb_box = view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }

                }
            }
        });
    }

    class myAdapter extends BaseAdapter {

        //获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //返回0,代表纯文本条目的状态码
                return 0;
            } else {
                //返回1,代表图片+文本条目状态码
                return 1;
            }
        }

        //listView中添加两个描述条目
        @Override
        public int getCount() {
            return mCustomerList.size() + mSystemList.size() + 2;
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    //返回系统应用对应条目的对象
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);

            if (type == 0) {
                //展示灰色纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户进程(" + mCustomerList.size() + ")");
                } else {
                    holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                //展示图片+文字条目
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String memSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(memSize);
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clean:
                cleanAll();
                break;
            case R.id.bt_setting:

                break;

        }
    }

    private void cleanAll() {
        ArrayList<ProcessInfo> killProcessList = new ArrayList<>();
        for (ProcessInfo processInfo : mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (processInfo.isCheck) {
                killProcessList.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : mSystemList) {
            if (processInfo.isCheck) {
                killProcessList.add(processInfo);
            }
        }
        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo : killProcessList) {
            if (mCustomerList.contains(processInfo)) {
                mCustomerList.remove(processInfo);
            }
            if (mSystemList.contains(processInfo)) {
                mSystemList.remove(processInfo);
            }
            ProcessInfoProvider.killProcess(this, processInfo);
            totalReleaseSpace += processInfo.memSize;

        }
        mProcessCount -= killProcessList.size();
        mAvailSpace += totalReleaseSpace;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        tv_progress_count.setText("进程总数" + mProcessCount);
        tv_memory_info.setText("剩余/总共:" + Formatter.formatFileSize(this,mAvailSpace) + "/" + Formatter.formatFileSize(this, mTotalSpace));

//        ToastUtil.show(this,"杀了"+killProcessList.size()+"个进程,释放了"+Formatter.formatFileSize(this,totalReleaseSpace)+"空间");
        String format = String.format("杀死了%d进程,释放了%s空间", killProcessList.size(), Formatter.formatFileSize(this, totalReleaseSpace));
        ToastUtil.show(this,format);
    }


    private void selectReverse() {
        for (ProcessInfo processInfo : mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }
        for (ProcessInfo processInfo : mSystemList) {
            processInfo.isCheck = !processInfo.isCheck;
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        for (ProcessInfo processInfo : mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }
        for (ProcessInfo processInfo : mSystemList) {
            processInfo.isCheck = true;
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
