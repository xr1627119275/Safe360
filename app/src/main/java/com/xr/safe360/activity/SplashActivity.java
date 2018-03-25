package com.xr.safe360.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xr.safe360.R;
import com.xr.safe360.utils.ConstantValue;
import com.xr.safe360.utils.SpUtils;
import com.xr.safe360.utils.StreamUtil;
import com.xr.safe360.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mVersionDes;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
            }
        }
    };
    private String mDownloadUrl;
    private RelativeLayout rl_root;


    /**
     * 显示更新Dialog
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("升级提醒");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();

            }
        });
        builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();


    }

    /**
     * 下载Apk
     */
    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }

    /**
     * @param file 安装文件
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setData(Uri.fromFile(file));
//        intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面关掉导航界面
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initUI();
        initData();
        initAnimation();
        initDB();
    }

    private void initDB() {

        initAddressDB("address.db");

    }

    private void initAddressDB(String dbNname) {
        File files = getFilesDir();
        File file = new File(files, dbNname);
        if (file.exists()) {
            return;
        }
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            in = getAssets().open("address.db");
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int temp = -1;
            while ((temp = in.read(bytes)) != -1) {
                fos.write(bytes, 0, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null && fos != null) {
                try {
                    fos.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 添加淡入的效果
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tv_version_name.setText("版本名称:" + getVersionName());
        //检查是否有更新
        mLocalVersionCode = getVersionCode();
        if (SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            public void run() {
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL("http://144.202.4.47:8080/update");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String json = StreamUtil.streamTostring(inputStream);
                        Log.d(TAG, json);
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            message.what = UPDATE_VERSION;
                        } else {
                            message.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    try {
                        Thread.sleep(endTime - startTime > 4000 ? 0 : 4000 - (endTime - startTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(message);
                }
            }

            ;
        }.start();


    }

    /**
     * 返回版本号
     *
     * @return 返回0获取失败
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 初始化UI alt-shift-j
     */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
        rl_root = findViewById(R.id.rl_root);
    }


    /**
     * 获取版本名称:清单文件
     *
     * @return 应用版本名称 返回null代表异常
     */
    public String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
