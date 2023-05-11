package com.example.greenbetterymaster.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.greenbetterymaster.Myapplication;
import com.example.greenbetterymaster.MainActivity;
import com.example.greenbetterymaster.PreviewActivity;
import com.example.greenbetterymaster.R;
import com.example.greenbetterymaster.util.CRC8;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.example.service.mainService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * BLE客户端
 */
public class BleClientActivity extends Activity implements View.OnClickListener{

    private Button btn_return;
    private static final String TAG = BleClientActivity.class.getSimpleName();
    private EditText mWriteET;
    public static TextView mTips;
    private BleDevAdapter mBleDevAdapter;
    private static BluetoothGatt mBluetoothGatt;
    public static boolean isConnected = false;
    private static final int bleSize = 20;
    private static Queue<String> queue = new LinkedList<String>();
    BluetoothDevice myDev;
    boolean showChinese = true;

    public  BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice dev = gatt.getDevice();
            Log.i(TAG, String.format("onConnectionStateChange:%s,%s,%s,%s", dev.getName(), dev.getAddress(), status, newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true;
//                gatt.discoverServices(); //启动服务发现
                gatt.requestMtu(203);
            } else {
                isConnected = false;
                closeConn();
            }
            String message = String.format(status == 0 ? (newState == 2 ? "与[%s]连接成功" : "与[%s]连接断开") : ("与[%s]连接出错,错误码:" + status), dev);
            String message_en = String.format(status == 0 ? (newState == 2 ? "Succeeded in connecting to [%s]" : "Connection is disconnected with [%s]") : ("Error connecting with [%s], error code:" + status), dev);
            Log.d(TAG, "onConnectionStateChange:"+message );

            if(showChinese){
                logTv(message);
            }else{
                logTv(message_en);
            }
            ScrollView mScrollView = findViewById(R.id.sv);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, String.format("onServicesDiscovered:%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), status));
            if (status == BluetoothGatt.GATT_SUCCESS) { //BLE服务发现成功
                // 遍历获取BLE服务Services/Characteristics/Descriptors的全部UUID
                for (BluetoothGattService service : gatt.getServices()) {
                    StringBuilder allUUIDs = new StringBuilder("UUIDs={\nS=" + service.getUuid().toString());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        allUUIDs.append(",\nC=").append(characteristic.getUuid());
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors())
                            allUUIDs.append(",\nD=").append(descriptor.getUuid());
                    }
                    allUUIDs.append("}");
                    Log.i(TAG, "onServicesDiscovered:" + allUUIDs.toString());
//                    logTv("发现服务" + allUUIDs);
                }
//                logTv("连接成功！！！\n Successful connection!!!");
                logTv("⭐ ⭐ ⭐ ⭐ ⭐ ⭐ ⭐ ⭐ ⭐ ⭐");
                ScrollView mScrollView = findViewById(R.id.sv);
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                finish();   //连接成功自动返回主界面
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            String valueStr_1 = IntByteStringHexUtil.byteArrayToHexStr(characteristic.getValue());
            Log.d(TAG, String.format("onCharacteristicRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr_1, status));
            logTv("读取Characteristic[" + uuid + "]:\n" + valueStr_1);
//            logTv(String.format("onCharacteristicRead读取成功:[%s]",characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            mainService.Sending = true;
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            String valueStr_1 = IntByteStringHexUtil.byteArrayToHexStr(characteristic.getValue());
            Log.d(TAG, String.format("onCharacteristicWrite:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr_1, status));
//            logTv("写入Characteristic[" + uuid + "]:\n" + valueStr_1);
            if(queue.size()>0){
                bleNotifyDevice(Base64.decode(queue.poll(), Base64.NO_WRAP));
            }
//            logTv(String.format("onCharacteristicWrite读取成功:"+ valueStr_1));
//            mainService.Sending = false;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            byte[] value = characteristic.getValue();
            String valueStr = new String(characteristic.getValue());
            Log.d(TAG, String.format("onCharacteristicChanged:%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr));;
            String valueStr_1 = IntByteStringHexUtil.byteArrayToHexStr(characteristic.getValue());
            String valueStr_2 = IntByteStringHexUtil.bytesToHex(characteristic.getValue());
            Log.d(TAG, "通知：\n"+ valueStr_1);
            mainService.callbackManager(valueStr_1);
//            logTv("通知Characteristic[" + uuid + "]:\n" + valueStr_1);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            String valueStr_1 = IntByteStringHexUtil.byteArrayToHexStr(descriptor.getValue());
            Log.i(TAG, String.format("onDescriptorRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr_1, status));
            logTv("读取Descriptor[" + uuid + "]:\n" + valueStr_1);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            String valueStr_1 = IntByteStringHexUtil.byteArrayToHexStr(descriptor.getValue());
            Log.d(TAG, String.format("onDescriptorWrite:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr_1, status));
            logTv("写入Descriptor[" + uuid + "]:\n" + valueStr_1);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.i(TAG, String.format("onMtuChanged:%s", mtu));
//            logTv(String.format("onMtuChanged:%s", mtu));
            gatt.discoverServices(); //启动服务发现
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blesearch);
        RecyclerView rv = findViewById(R.id.rv_ble);
        mTips = findViewById(R.id.tv_tips);
        rv.setLayoutManager(new LinearLayoutManager(this));
        showChinese = MainActivity.showChinese;

        mBleDevAdapter = new BleDevAdapter(new BleDevAdapter.Listener() {

            @Override
            public void onItemClick(BluetoothDevice dev) {
//                mainService.closeConn();
                closeConn();
                myDev = dev;
                mBluetoothGatt = dev.connectGatt(BleClientActivity.this, false, mBluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
//                mainService.mBluetoothGatt = dev.connectGatt(BleClientActivity.this, false, mainService.mBluetoothGattCallback);

                if(showChinese){
                    logTv(String.format("与[%s]开始连接............",dev));
                }else{
                    logTv(String.format("Start connecting with [%s]...",dev));
                }
                ScrollView mScrollView = findViewById(R.id.sv);
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        rv.setAdapter(mBleDevAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write_cmd:
//                mainService.setNotify();
                setNotify_1();
                Toast.makeText(BleClientActivity.this, "设置通知", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
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
                Toast.makeText(BleClientActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        setNotify_1();
//        sleep(300);
//        startActivity(new Intent(BleClientActivity.this, MainActivity.class));
//        startService(new Intent(this, mainService.class));
        super.onDestroy();
    }

    /**
     * 清理本地的BluetoothGatt 的缓存，以保证在蓝牙连接设备的时候，设备的服务、特征是最新的
     * @param gatt
     * @return
     */
    public static boolean refreshDeviceCache(BluetoothGatt gatt) {
        if(null != gatt){
            try {
                BluetoothGatt localBluetoothGatt = gatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod( "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(
                            localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return false;
    }

    // BLE中心设备连接外围设备的数量有限(大概2~7个)，在建立新连接之前必须释放旧连接资源，否则容易出现连接错误133
//    void closeConn() {
    public static void closeConn() {
        if (mBluetoothGatt != null) {
            refreshDeviceCache(mBluetoothGatt);
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            isConnected = false;
            Log.e("BleClientActivity", "closeConn" );
        }
    }

    // 扫描BLE
    public void reScan(View view) {
        if (mBleDevAdapter.isScanning){
            if(showChinese){
                logTv("正在扫描...");
            }else{
                logTv("Is scanning...");
            }
            ScrollView mScrollView = findViewById(R.id.sv);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            if(showChinese){
                logTv("扫描完成");
            }else{
                logTv("Scan completion");
            }
        }
        else{
            mBleDevAdapter.reScan();
            if(showChinese){
                logTv("刷新");
            }else{
                logTv("Refresh");
            }
            ScrollView mScrollView = findViewById(R.id.sv);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            if(showChinese){
                logTv("刷新完成");
            }else{
                logTv("Refresh complete");
            }
        }
    }

    public void return_home(View view) {
        finish();
    }

    // 清除Log
    public void cleanLog(View view) {
        mTips.setText("");
    }


    public void write(View view) {
        BluetoothGattService service = getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            String text = mWriteET.getText().toString();
            byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(text);
            bleNotifyDeviceByQueue(false,write_cmd);
        }
    }

    public static void sendMsg2Ble(String text) {
        String Text = text;
        byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(Text);
        bleNotifyDeviceByQueue(false,write_cmd);
    }

    // 设置通知Characteristic变化会回调->onCharacteristicChanged()
    public void setNotify(View view) {
        BluetoothGattService service = getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            mTips.append("setNotify" + "\n");
            // 设置Characteristic通知
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(BleServerActivity.UUID_CHAR_READ_NOTIFY);//通过UUID获取可通知的Characteristic
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);

            // 向Characteristic的Descriptor属性写入通知开关，使蓝牙设备主动向手机发送数据
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleServerActivity.UUID_DESC_NOTITY);
            // descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//和通知类似,但服务端不主动发数据,只指示客户端读取数据
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void setNotify_1() {
        BluetoothGattService service = getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            mTips.append("setNotify" + "\n");
            // 设置Characteristic通知
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(BleServerActivity.UUID_CHAR_READ_NOTIFY);//通过UUID获取可通知的Characteristic
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);

            // 向Characteristic的Descriptor属性写入通知开关，使蓝牙设备主动向手机发送数据
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleServerActivity.UUID_DESC_NOTITY);
            // descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//和通知类似,但服务端不主动发数据,只指示客户端读取数据
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    // 注意：连续频繁读写数据容易失败，读写操作间隔最好200ms以上，或等待上次回调完成后再进行下次读写操作！
    // 读取数据成功会回调->onCharacteristicChanged()
    public void read(View view) {
        BluetoothGattService service = getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            mTips.append("read" + "\n");
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(BleServerActivity.UUID_CHAR_READ_NOTIFY);//通过UUID获取可读的Characteristic
            mBluetoothGatt.readCharacteristic(characteristic);
//            Log.e(TAG,"读取成功" + characteristic.getValue());
//            logTv(String.format("读取成功:[%s]",characteristic.getValue()));
        }
    }

//    @Override
    public final void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        Log.d(TAG, "onCharacteristicChanged: " + value);
        String s0 = Integer.toHexString(value[0] & 0xFF);
        String s = Integer.toHexString(value[1] & 0xFF);
        Log.d(TAG, "onCharacteristicChanged: " + s0 + "、" + s);
        for (byte b : value) {
            Log.d(TAG, "onCharacteristicChanged: " + b);
        }
    }

    // 获取Gatt服务
    public static BluetoothGattService getGattService(UUID uuid) {
        if (!isConnected) {
//            BaseApplication.toast("没有连接", 0);
            Log.e(TAG, "getGattService: 没有连接");
            return null;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuid);
        if (service == null) {
//            BaseApplication.toast("没有找到服务UUID=" + uuid, 0);
            Log.e(TAG, "getGattService: 没有找到服务UUID=" + uuid);
        }
        return service;
    }

    // 输出日志
    public void logTv(final String msg) {
        if (isDestroyed())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTips.append(msg + "\n\n");
//                mTips.setTextColor(Color.BLUE);
            }
        });
    }

    public void writeCmd(View view){
        String text = "010301e130";
        byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(text);
        bleNotifyDeviceByQueue(false,write_cmd);
    }

    public void writeLongCmd(View view){
        String text = "010302a131";
//        String text = "AA01010101000F0102030405060708090A0B0C0D0E0F";
        String crc = CRC8.calcCrc8Str(IntByteStringHexUtil.hexStrToByteArray(text));
//        String message = text + crc;
        String message = text;

        byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(message);
        //byte[] write_cmd = new byte[] {(byte)0xaa,0x01,0x01,0x01,0x01,0x00,0x00,(byte)0x2D};

        // 1、定义通讯协议，如下(这里只是个举例，可以项目扩展)
        // 协议号(2个字节)    消息号(1个字节)  功能(1个字节) 子功能(1个字节) 数据长度(2个字节) Data(N个字节) CRC校验(1个字节)
        //  AA01                 01            01          01            0000                        2D
        // 消息号(1个字节) 功能(1个字节) 子功能(1个字节) 数据长度(2个字节) 数据内容(N个字节) CRC校验(1个字节)
        //   01            01           01            0000            --             2D
        //2、封装通用发送数据接口(拆包)
        //该接口根据会发送数据内容按最大字节数拆分(一般20字节)放入队列，拆分完后，依次从队列里取出发送
        //3、封装通用接收数据接口(组包)
        //该接口根据从接收的数据按协议里的定义解析数据长度判读是否完整包，不是的话把每条消息累加起来
        //4、解析完整的数据包，进行业务逻辑处理
        //5、协议还可以加密解密，需要注意的选算法参数的时候，加密后的长度最好跟原数据长度一致，这样不会影响拆包组包
        logTv("写入长指令(大于20字节):\n" + message);
        bleNotifyDeviceByQueue(false,write_cmd);
    }

    public static void bleNotifyDeviceByQueue(boolean isNeedEncrypt, byte[] data){
        Log.e(TAG, "bleNotifyAppByQueue isNeedEncrypt=" + isNeedEncrypt);
        boolean isNeedSend = true;
        if (queue.size() > 0) {
            isNeedSend = false;
        }
        int section = data.length / bleSize;
        int remain = data.length % bleSize;
        //Log.e(TAG, "queue.size=" + queue.size() + ",data.length=" + data.length + ",section=" + section + ",remain=" + remain);
        if (section > 1 || (section == 1 && remain > 0)) {
            for (int i = 0; i < section; i++) {
                byte b_data[] = new byte[bleSize];
                System.arraycopy(data, i * bleSize, b_data, 0, bleSize);
                queue.add(Base64.encodeToString(b_data, Base64.NO_WRAP));
            }
            if (remain > 0) {
                byte b_data[] = new byte[remain];
                System.arraycopy(data, section * bleSize, b_data, 0, remain);
                queue.add(Base64.encodeToString(b_data, Base64.NO_WRAP));
            }
        } else {
            byte b_data[] = new byte[data.length];
            System.arraycopy(data, 0, b_data, 0, data.length);
            queue.add(Base64.encodeToString(b_data, Base64.NO_WRAP));
        }
        if (isNeedSend) {
            bleNotifyDevice(Base64.decode(queue.poll(), Base64.NO_WRAP));
        }
    }

    private static void bleNotifyDevice(byte[] data) {
        BluetoothGattService service = getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(BleServerActivity.UUID_CHAR_WRITE);//通过UUID获取可写的Characteristic
            characteristic.setValue(data); //单次最多20个字节
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }
}