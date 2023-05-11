package com.example.greenbetterymaster;
import static com.example.service.mainService.isLocationOpen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

import com.example.greenbetterymaster.util.LanguageSettings;
import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.example.service.mainService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Runnable runnable;
    private Handler handler;
    private Button Bnt_set;
    private Button Bnt_run_last;
    private Button Bnt_measure;
    private Button Bnt_open_bluetooth;
    private Button Bnt_send_msg;
    private TextView Text_ac_in;
    private TextView Text_ac_in_1;
    private TextView Text_bettery_in;
    private TextView Text_bettery_in_1;
    private TextView Text_blue;
    private TextView Text_blue_1;
    private TextView Text_title;
    private TextView Text_warning;
    private TextView Text_warning_1;
    private TextView Text_connect;
    private TextView Text_connect_1;
    boolean ac_ok = false;
    boolean dc_ok = false;
    static String ac_ok_flag = "0";
    private long exitTime = 0;
    public static boolean showChinese = true;
    boolean isWorking = false;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLangguage(1);
        startService(new Intent(this, mainService.class));
        InitMenuShow();
//        showChinese = LanguageSettings.showChinese;

        //授权权限
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WAKE_LOCK},1);

        setShow();

        if(mainService.page_msg != null) {
            String workingFlag = mainService.page_msg.substring(4, 6);
            if (!workingFlag.equals("02") && !workingFlag.equals("03") && !workingFlag.equals("04")) {
                sendMsg2Ble("010101E050");
                sleep(50);
                sendMsg2Ble("010101E050");
                sleep(50);
                sendMsg2Ble("010101E050");
            }else {
                if(showChinese){
                    showTip("设备正在工作");
                }else{
                    showTip("The equipment is working");
                }
            }
        }

        mainService.page_msg_now = "01";
    }

    //循环判断蓝牙状态
    public void setShow() {
        /**
         * 方式一：采用Handler的postDelayed(Runnable, long)方法
         */
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // handler自带方法实现定时器
                handler.postDelayed(this, 1000 * 2);//每隔2s执行
                boolean blueIsConnect = BleClientActivity.isConnected;
                BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
                String Page_msg = mainService.page_msg;
                if(blueIsConnect && service != null){
                    if(showChinese){
                        Text_blue_1.setText("已连接");
                    }else{
                        Text_blue_1.setText("OK");
                    }
                    Text_blue.setTextColor(Color.BLUE);
                    Text_blue_1.setTextColor(Color.BLUE);
                    Log.d("blue","蓝牙连接正常");
                }else{
                    if(showChinese){
                        Text_blue_1.setText("未接入");
                    }else{
                        Text_blue_1.setText("None");
                    }
                    Text_blue.setTextColor(Color.RED);
                    Text_blue_1.setTextColor(Color.RED);

                    Text_ac_in_1.setText("NG");
                    Text_bettery_in_1.setText("NG");
                    Text_ac_in.setTextColor(Color.GRAY);
                    Text_ac_in_1.setTextColor(Color.GRAY);
                    Text_bettery_in.setTextColor(Color.GRAY);
                    Text_bettery_in_1.setTextColor(Color.GRAY);
                    Text_connect.setVisibility(View.GONE);
                    Text_connect_1.setVisibility(View.GONE);
                    Log.e("blue","蓝牙连接断开");
                }
                if(Page_msg != null && service != null) {
                    ac_ok = mainService.ac_ok;
                    dc_ok = mainService.dc_ok;
                    if (ac_ok) {
                        if (showChinese) {
                            Text_ac_in_1.setText("已连接");
                        } else {
                            Text_ac_in_1.setText("OK");
                        }
                        Text_ac_in.setTextColor(Color.BLUE);
                        Text_ac_in_1.setTextColor(Color.BLUE);
                    } else {
                        if (showChinese) {
                            Text_ac_in_1.setText("未接入");
                        } else {
                            Text_ac_in_1.setText("None");
                        }
                        Text_ac_in.setTextColor(Color.RED);
                        Text_ac_in_1.setTextColor(Color.RED);
                    }
                    if (dc_ok) {
                        if (showChinese) {
                            Text_bettery_in_1.setText("已连接");
                        } else {
                            Text_bettery_in_1.setText("OK");
                        }
                        Text_bettery_in.setTextColor(Color.BLUE);
                        Text_bettery_in_1.setTextColor(Color.BLUE);
                    } else {
                        if (showChinese) {
                            Text_bettery_in_1.setText("未接入");
                        } else {
                            Text_bettery_in_1.setText("None");
                        }
                        Text_bettery_in.setTextColor(Color.RED);
                        Text_bettery_in_1.setTextColor(Color.RED);
                    }
                    String title_0 = "00";
                    String title_1 = "01";
                    String title_2 = "02";
                    String title_3 = "03";
                    String title_4 = "04";
                    String page_msg = mainService.page_msg;
                    String title = page_msg.substring(6, 8);
                    if (title.equals(title_1)) {
                        Text_title.setText("BM70-5");
                    } else if (title.equals(title_2)) {
                        Text_title.setText("BM70-18");
                    } else if (title.equals(title_3)) {
                        Text_title.setText("BM200-5");
                    } else if (title.equals(title_4)) {
                        Text_title.setText("BM200-32");
                    }

                    //根据标志位显示隐藏按键或提示
                    String show_buttom = page_msg.substring(14, 16);
                    if (show_buttom.equals(title_1)) {
                        Bnt_set.setVisibility(View.GONE);
                        Bnt_run_last.setVisibility(View.GONE);
                        Text_warning.setVisibility(View.VISIBLE);
                        Text_warning_1.setVisibility(View.GONE);
                    } else if (show_buttom.equals(title_0)) {
                        if (dc_ok) {
                            Bnt_set.setVisibility(View.VISIBLE);
                            Bnt_run_last.setVisibility(View.VISIBLE);
                            Text_warning.setVisibility(View.GONE);
                            Text_warning_1.setVisibility(View.GONE);
                        } else {
                            Bnt_set.setVisibility(View.GONE);
                            Bnt_run_last.setVisibility(View.GONE);
                            Text_warning.setVisibility(View.GONE);
                            Text_warning_1.setVisibility(View.VISIBLE);
                        }
                    }
                    if (ac_ok) {
                        ac_ok_flag = "1";
                    }
                    String arg_msg = mainService.arg_msg;
                    if (arg_msg != null) {
                        //有多少级联
                        String jilian_num_ori = arg_msg.substring(2, 4);
                        int jilian_num = (int) (IntByteStringHexUtil.covert(jilian_num_ori));
                        String jilian_tips = String.valueOf(jilian_num) + "S";
                        if (jilian_num > 1) {
                            Text_connect.setVisibility(View.VISIBLE);
                            Text_connect_1.setVisibility(View.VISIBLE);
                            Text_connect_1.setText(jilian_tips);
                            Text_connect.setTextColor(Color.BLUE);
                            Text_connect_1.setTextColor(Color.BLUE);
                        } else {
                            Text_connect.setVisibility(View.GONE);
                            Text_connect_1.setVisibility(View.GONE);
                        }
                    }
                }
            }
        };
        handler.postDelayed(runnable, 1000);//延时多长时间启动定时器
    }

    private void InitMenuShow() {
        Text_blue =  findViewById(R.id.Text_blue);
        Text_blue_1 =  findViewById(R.id.Text_blue_1);
        Text_title =  findViewById(R.id.Text_title);
        Text_ac_in =  findViewById(R.id.Text_ac_in);
        Text_ac_in_1 =  findViewById(R.id.Text_ac_in_1);
        Text_bettery_in =  findViewById(R.id.Text_bettery_in);
        Text_bettery_in_1 =  findViewById(R.id.Text_bettery_in_1);
        Text_warning =  findViewById(R.id.Text_warning);
        Text_warning_1 =  findViewById(R.id.Text_warning_1);
        Text_connect =  findViewById(R.id.Text_connect);
        Text_connect_1 =  findViewById(R.id.Text_connect_1);
        Bnt_set = (Button) findViewById(R.id.Bnt_set);
        Bnt_run_last = (Button) findViewById(R.id.Bnt_run_last);
        Bnt_measure = (Button) findViewById(R.id.Bnt_measure);
        Bnt_open_bluetooth = (Button) findViewById(R.id.Bnt_open_bluetooth);
        Bnt_send_msg = (Button) findViewById(R.id.Bnt_send_msg);
        Bnt_set.setOnClickListener(this);
        Bnt_run_last.setOnClickListener(this);
        Bnt_measure.setOnClickListener(this);
        Bnt_open_bluetooth.setOnClickListener(this);
        Bnt_send_msg.setOnClickListener(this);
    }

    private void setLangguage(int first) {
        String lan = Locale.getDefault().getLanguage();
        Log.i("Login","mylog language is ?"+lan);
        // 切换成英文
        if (Locale.getDefault().getLanguage()
                .equals("zh")) {
            Log.i("Login","mylog language is zh");
            showChinese = true;
            updateActivity("zh",first);
            LanguageSettings.getInstance().switchCurrentLanguage();
        } else {
            Log.i("Login","mylog language is en");
            // 切换成中文
            showChinese = false;
            updateActivity("en",first);
            LanguageSettings.getInstance().switchCurrentLanguage();
        }
    }

    public void sleep(int time) {
        try
        {
            Thread.sleep(time);//单位：毫秒
        } catch (Exception e) {
        }
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMsg2Ble(String text) {
        BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(text);
            BleClientActivity.bleNotifyDeviceByQueue(false,write_cmd);
        }
    }

    private void checkPermission() {
        boolean isLocat = isLocationOpen(getApplicationContext());
        if (!isLocat) {
            Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast.makeText(MainActivity.this, "无法获取位置信息", Toast.LENGTH_SHORT).show();
                startActivityForResult(enableLocate, 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bnt_open_bluetooth:
//                checkPermission();
                // 检查蓝牙开关
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter == null) {
                    if(showChinese){
                        showTip("本机没有找到蓝牙硬件或驱动！");
                    }else{
                        showTip("No Bluetooth hardware or driver was found on this phone！");
                    }
                    return;
                } else {
                    if (!adapter.isEnabled()) {
                        adapter.enable();       //直接开启蓝牙
                        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 112);  //跳转到设置界面
                    }
                }
                // 检查是否支持BLE蓝牙
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    if(showChinese){
                        showTip("本机不支持低功耗蓝牙！");
                    }else{
                        showTip("BLE Bluetooth is not supported on this phone！");
                    }
                    return;
                }
                BleClientActivity.closeConn();
                BleClientActivity.isConnected = false;
                startActivity(new Intent(MainActivity.this, BleClientActivity.class));
//                finish();
                break;
            case R.id.Bnt_set:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, BetterySetActivity.class));
                    }
                }).start();
                break;
            case R.id.Bnt_run_last:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("ac_ok_flag", ac_ok_flag);
                        mIntent.setClass(MainActivity.this, PreviewActivity.class);
                        startActivity(mIntent);
                    }
                }).start();
                break;
            case R.id.Bnt_measure:
                showTip("模糊测量");
                break;
            case R.id.Bnt_send_msg:
                showTip("蓝牙发送");
                sendMsg2Ble("010301e130");
                break;
            default:
                break;
        }
    }

    //系统方法再按一次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void exit(){
        if((System.currentTimeMillis()-exitTime)>2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            finish();
            System.exit(0);
        }
    }

    /**
     * 刷新语言
     */
    public void updateActivity(String sta,int first) {
        // 本地语言设置
        Locale myLocale = new Locale(sta);
        Resources res = getResources();// 获得res资源对象
        DisplayMetrics dm = res.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        Configuration conf = res.getConfiguration();// 获得设置对象
        conf.locale = myLocale;// 简体中文
        res.updateConfiguration(conf, dm);

        if(first <= 1)
        {
            first = 2;
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(LaunchIntent);
            }
        }, 100);
    }


    @Override
    public void onResume() {    //用于返回时刷新蓝牙状态
        super.onResume();
        //重新获取数据的逻辑，此处根据自己的要求回去
        //显示信息的界面
//        showTip("onResume");
        setContentView(R.layout.activity_main);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InitMenuShow();
            }
        });
        setShow();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","onDestroy");
        super.onDestroy();
        BleClientActivity.closeConn();
        Log.e("MainActivity", "closeConn" );
    }
}
/**
 *
 *
 * 　　　　　　　　 ┏┓　　　┏┓
 * 　　　　　　　 ┏┛  ┻━━━┛ ┻┓
 * 　　　　　　　 ┃　　　　　　 ┃
 * 　　　　　　　 ┃　　　━　　　┃
 * 　　　　　　　 ┃　＞　　　＜ ┃
 * 　　　　　　　 ┃　　　　　　 ┃
 * 　　　　　　　 ┃... ⌒　...┃
 * 　　　　　　　 ┃　　　 　　 ┃
 * 　　　　　　　 ┗━┓　　　 ┏━┛
 * 　　　　　　　　　┃　　 　┃　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　 ┃   神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　 ┃
 * 　　　　　　　　　┃　　　 ┃
 * 　　　　　　　　　┃　　　 ┃
 * 　　　　　　　　　┃　　　 ┃
 * 　　　　　　　　　┃　　　 ┗━━━┓
 * 　　　　　　　　　┃　　　　　　 ┣┓
 * 　　　　　　　　　┃　　　　　　┏┛
 * 　　　　　　　　　┗┓ ┓┏━┳┓┏┛
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛
 */