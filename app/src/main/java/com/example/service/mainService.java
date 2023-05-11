package com.example.service;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.bluetooth.BluetoothDevice;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.greenbetterymaster.BetterySetActivity;
import com.example.greenbetterymaster.InfoActivity;
import com.example.greenbetterymaster.MainActivity;
import com.example.greenbetterymaster.PreviewActivity;
import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.CRC16;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class mainService extends Service {

    public static boolean Sending = false;
    private static String TAG = "mainService";
    private static Queue<String> queue = new LinkedList<String>();
    private static Runnable runnable;
    private static Handler handler;
    public static String page_msg_now = "00";
    public static String page_msg;
    public static String set_msg;
    public static String arg_msg;
    public static String arg_msg_1;
    public static String temp_string;
    public static String set_callback_msg;
    public static String curve_msg_t;
    public static String curve_msg_v;
    public static String curve_msg_a;
    public static boolean ac_ok = false;
    public static boolean dc_ok = false;

    public static void callbackManager(String callback) {
        String s1 = "0A";
        String s2 = "0B";
        String s3 = "0C";
        String s4 = "0D";
        String s5 = "02";
        String s6 = "0E";
        String s7 = "0F";
        String s8 = "10";

        int callback_length = callback.length();
        String temp_string_1 = callback.substring(callback_length - 4, callback_length);
        String temp_string_2 = callback.substring(0, callback_length - 4);
        String temp_string_crc =  CRC16.getCRC16(IntByteStringHexUtil.hexStrToByteArray(temp_string_2));
        if(!temp_string_1.equals(temp_string_crc)){
            Log.e(TAG, "通知crc校验错误：\n"+ callback + "收到的" + temp_string_1 + "算出来的" + temp_string_2);
            return;
        }

        temp_string = callback.substring(0, 2);
//        Log.e(TAG, "callbackManager" + temp_string);
        if (temp_string.equals(s1)) {
            page_msg = callback;
            String off = "00";
            String on = "01";
            String ac = callback.substring(8,10);
            String dc = callback.substring(10,12);
            if (ac.equals(off)) {
                ac_ok = false;
            } else if (ac.equals(on)) {
                ac_ok = true;
            }
            if (dc.equals(off)) {
                dc_ok = false;
            } else if (dc.equals(on)) {
                dc_ok = true;
            }
        } else if (temp_string.equals(s2)) {
            set_msg = callback;
        } else if (temp_string.equals(s3)) {
            arg_msg = callback;
        } else if (temp_string.equals(s4)) {
            arg_msg_1 = callback;
            Log.i("mainservice",arg_msg_1);
        } else if (temp_string.equals(s5)) {
            set_callback_msg = callback;
        } else if (temp_string.equals(s6)) {
            curve_msg_v = callback;
        } else if (temp_string.equals(s7)) {
            curve_msg_t = callback;
        } else if (temp_string.equals(s8)) {
            curve_msg_a = callback;
        }
    }

    @SuppressLint("NewApi")
    public void onCreate() {
//        startForeground(1, new Notification());
        super.onCreate();
//        Toast.makeText(mainService.this, "mainService", Toast.LENGTH_SHORT).show();
//        checkPermission();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //你要做的事
                BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
                if (service != null) {
                    Sending = true;
                    try{
                        sendMsg2Ble("0A3F47");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("Info","0A3F47发送");
                    Sending = false;

                    Log.i("Info", "mainService.page_msg_now == \"04\"");
                    sleep(600);
                    Sending = true;
                    try{
                        sendMsg2Ble("0CBF45");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("Info", "0CBF45发送");
                    Sending = false;
                    sleep(600);
                }
            }
        }, 0, 500);//0秒后执行，每1秒执行一次
    }

    public mainService() {
    }

    public void sendMsg2Ble(String text) {
        BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(text);
            BleClientActivity.bleNotifyDeviceByQueue(false,write_cmd);
        }
    }

    public void sleep(int time) {
        try
        {
            Thread.sleep(time);//单位：毫秒
        } catch (Exception e) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void checkPermission() {
        boolean isLocat = isLocationOpen(getApplicationContext());
        if (!isLocat) {
            Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast.makeText(mainService.this, "无法获取位置信息", Toast.LENGTH_SHORT).show();
//                startActivityForResult(enableLocate, 1);
        }
    }

    /**
     * 判断位置信息是否开启
     *
     * @param context
     * @return
     */
    public static boolean isLocationOpen(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider || isNetWorkProvider;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        BleClientActivity.closeConn();
        Log.d(TAG, "mainService onDestroy" );
    }
}