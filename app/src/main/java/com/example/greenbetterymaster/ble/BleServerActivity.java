package com.example.greenbetterymaster.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import java.util.Arrays;
import java.util.UUID;

import com.example.greenbetterymaster.BaseApplication;
import com.example.greenbetterymaster.R;
import com.example.greenbetterymaster.util.CRC8;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;

/**
 * BLE服务端
 */
public class BleServerActivity extends Activity {

    private static final String TAG = BleServerActivity.class.getSimpleName();

//    public static final UUID UUID_SERVICE = UUID.fromString("10000000-0000-0000-0000-000000000000"); //自定义UUID
//    public static final UUID UUID_CHAR_READ_NOTIFY = UUID.fromString("11000000-0000-0000-0000-000000000000");
//    public static final UUID UUID_DESC_NOTITY = UUID.fromString("11100000-0000-0000-0000-000000000000");
//    public static final UUID UUID_CHAR_WRITE = UUID.fromString("12000000-0000-0000-0000-000000000000");
    public static final UUID UUID_SERVICE = UUID.fromString("0003cdd0-0000-1000-8000-00805f9b0131"); //自定义UUID
    public static final UUID UUID_CHAR_READ_NOTIFY = UUID.fromString("0003cdd1-0000-1000-8000-00805f9b0131");
    public static final UUID UUID_DESC_NOTITY = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_WRITE = UUID.fromString("0003cdd2-0000-1000-8000-00805f9b0131");

    private TextView mTips;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser; // BLE广播
    private BluetoothGattServer mBluetoothGattServer; // BLE服务端

    // BLE广播Callback
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            logTv("BLE广播开启成功");
        }

        @Override
        public void onStartFailure(int errorCode) {
            logTv("BLE广播开启失败,错误码:" + errorCode);
        }
    };

    // BLE服务端Callback
    private BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.i(TAG, String.format("onConnectionStateChange:%s,%s,%s,%s", device.getName(), device.getAddress(), status, newState));
            logTv(String.format(status == 0 ? (newState == 2 ? "与[%s]连接成功" : "与[%s]连接断开") : ("与[%s]连接出错,错误码:" + status), device));

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {// 连接成功

                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {// 断开连接

                }
            } else {
                Log.i(TAG, "connect fail");
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i(TAG, String.format("onServiceAdded:%s,%s", status, service.getUuid()));
            logTv(String.format(status == 0 ? "添加服务[%s]成功" : "添加服务[%s]失败,错误码:" + status, service.getUuid()));
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, String.format("onCharacteristicReadRequest:%s,%s,%s,%s,%s", device.getName(), device.getAddress(), requestId, offset, characteristic.getUuid()));
            String response = "CHAR_" + (int) (Math.random() * 100); //模拟数据
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response.getBytes());// 响应客户端
            logTv("客户端读取Characteristic[" + characteristic.getUuid() + "]:\n" + response);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {
            // 获取客户端发过来的数据
            String requestStr = new String(requestBytes);
            Log.i(TAG, String.format("onCharacteristicWriteRequest:%s,%s,%s,%s,%s,%s,%s,%s", device.getName(), device.getAddress(), requestId, characteristic.getUuid(),
                    preparedWrite, responseNeeded, offset, requestStr));

            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, requestBytes);// 响应客户端
            logTv("客户端写入Characteristic[" + characteristic.getUuid() + "]:\n" + requestStr);


            receiveAppCmd(requestBytes);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.i(TAG, String.format("onDescriptorReadRequest:%s,%s,%s,%s,%s", device.getName(), device.getAddress(), requestId, offset, descriptor.getUuid()));
            String response = "DESC_" + (int) (Math.random() * 100); //模拟数据
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response.getBytes()); // 响应客户端
            logTv("客户端读取Descriptor[" + descriptor.getUuid() + "]:\n" + response);
        }

        @Override
        public void onDescriptorWriteRequest(final BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            // 获取客户端发过来的数据
            String valueStr = Arrays.toString(value);
            Log.i(TAG, String.format("onDescriptorWriteRequest:%s,%s,%s,%s,%s,%s,%s,%s", device.getName(), device.getAddress(), requestId, descriptor.getUuid(),
                    preparedWrite, responseNeeded, offset, valueStr));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);// 响应客户端
            logTv("客户端写入Descriptor[" + descriptor.getUuid() + "]:\n" + valueStr);

            // 简单模拟通知客户端Characteristic变化
            if (Arrays.toString(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE).equals(valueStr)) { //是否开启通知

                deviceNotify = device;
                descriptorNotify = descriptor;

                final BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            SystemClock.sleep(3000);
                            String response = "CHAR_" + (int) (Math.random() * 100); //模拟数据
                            characteristic.setValue(response);
                            mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
                            logTv("通知客户端改变Characteristic[" + characteristic.getUuid() + "]:\n" + response);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            Log.i(TAG, String.format("onExecuteWrite:%s,%s,%s,%s", device.getName(), device.getAddress(), requestId, execute));
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.i(TAG, String.format("onNotificationSent:%s,%s,%s", device.getName(), device.getAddress(), status));
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.i(TAG, String.format("onMtuChanged:%s,%s,%s", device.getName(), device.getAddress(), mtu));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleserver);

        initBleBlueTooth();

        initView();
    }

    private void initBleBlueTooth(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // ============启动BLE蓝牙广播(广告) =================================================================================
        //广播设置(必须)
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY) //广播模式: 低功耗,平衡,低延迟
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH) //发射功率级别: 极低,低,中,高
                .setTimeout(0)
                .setConnectable(true) //能否连接,广播分为可连接广播和不可连接广播
                .build();

        //广播数据(必须，广播启动就会发送)
        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true) //包含蓝牙名称
                .setIncludeTxPowerLevel(true) //包含发射功率级别
                .addManufacturerData(1, new byte[]{23, 33}) //设备厂商数据，自定义
                .build();

        //扫描响应数据(可选，当客户端扫描时才发送)
        AdvertiseData scanResponse = new AdvertiseData.Builder()
                .addManufacturerData(2, new byte[]{66, 66}) //设备厂商数据，自定义
                .addServiceUuid(new ParcelUuid(UUID_SERVICE)) //服务UUID
//                .addServiceData(new ParcelUuid(UUID_SERVICE), new byte[]{2}) //服务数据，自定义
                .build();

        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponse, mAdvertiseCallback);

        // 注意：必须要开启可连接的BLE广播，其它设备才能发现并连接BLE服务端!
        // =============启动BLE蓝牙服务端=====================================================================================
        BluetoothGattService service = new BluetoothGattService(UUID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //添加可读+通知characteristic
        BluetoothGattCharacteristic characteristicRead = new BluetoothGattCharacteristic(UUID_CHAR_READ_NOTIFY,BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        characteristicRead.addDescriptor(new BluetoothGattDescriptor(UUID_DESC_NOTITY, BluetoothGattCharacteristic.PERMISSION_WRITE));
        service.addCharacteristic(characteristicRead);

        //添加可写characteristic
        BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHAR_WRITE, BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristicWrite);

        if (bluetoothManager != null){
            mBluetoothGattServer = bluetoothManager.openGattServer(this, mBluetoothGattServerCallback);
        }

        mBluetoothGattServer.addService(service);
    }

    private void initView(){
        mTips = findViewById(R.id.tv_tips);

    	Button btn_write_cmd = findViewById(R.id.btn_write_cmd);

        btn_write_cmd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            	writeCmd();
            }
        });


        Button btn_clean_log = findViewById(R.id.btn_clean_log);

        btn_clean_log.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mTips.setText("");
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothLeAdvertiser != null){
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }

        if (mBluetoothGattServer != null){
            mBluetoothGattServer.close();
        }
    }

    private void logTv(final String msg) {
        if (isDestroyed())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseApplication.toast(msg, 0);
                mTips.append(msg + "\n\n");
            }
        });
    }


    private BluetoothDevice deviceNotify;
    private BluetoothGattDescriptor descriptorNotify;
    private void writeCmd(){
        if(deviceNotify == null && descriptorNotify == null){
            return;
        }

        String response = "device write:Hello";

        BluetoothGattCharacteristic characteristic = descriptorNotify.getCharacteristic();
        characteristic.setValue(response);

        mBluetoothGattServer.notifyCharacteristicChanged(deviceNotify, characteristic, false);
    }


    /*************************************************** ble指令解析 ***********************************************************************/

    boolean isFullMessage = true;
    int totalMessageLength = 0;
    int curMessageLength = 0;
    String strFullMessage = "";
    boolean  isEncrypted = false;
    private void receiveAppCmd(byte[] requestBytes){

        String appCmd = IntByteStringHexUtil.byteArrayToHexStr(requestBytes);

        Log.e(TAG,"receiveAppCmd 客户端写入Characteristic HexStr :\n" + appCmd);

        if(isFullMessage){

            String version_number = appCmd.substring(0,4);
            String message_id = appCmd.substring(4,6);
            String function = appCmd.substring(6,8);
            String sub_function = appCmd.substring(8,10);

            String data_length = appCmd.substring(10,14);
            totalMessageLength = 14 + Integer.valueOf(data_length, 16) * 2 + 2;//头+data+crc

            if (appCmd.length() < totalMessageLength) {
                Log.e(TAG,"receiveAppCmd isFullMessage=false");

                isFullMessage = false;
                strFullMessage = appCmd;
                curMessageLength = appCmd.length() ;
            }else {
                isFullMessage = true;
                strFullMessage = appCmd;
            }
        }else {
            curMessageLength += appCmd.length();

            strFullMessage += appCmd;

            if (curMessageLength >= totalMessageLength) {
                isFullMessage = true;
            }
        }

        if(isFullMessage){
            Log.e(TAG, "strFullMessage =" + strFullMessage);
            logTv("客户端写入长指令(大于20字节) strFullMessage:\n" + strFullMessage);

            //crc校验
            String crc = strFullMessage.substring(strFullMessage.length()-2);
            String message = strFullMessage.substring(0,strFullMessage.length()-2);
            String verifyCrc = CRC8.calcCrc8Str(IntByteStringHexUtil.hexStrToByteArray(message));

            boolean ret = crc.toLowerCase().equals(verifyCrc.toLowerCase());

            Log.e(TAG, "verifyCrc ret=" + ret);

            if(ret){
                parseAppCmd(strFullMessage);
            }
        }
    }

    private void parseAppCmd(String appCmd){
        Log.e(TAG, "parseAppCmd appCmd=" + appCmd);

        if(appCmd.length()<16){
            return;
        }

        String version_number = appCmd.substring(0,4);
        String message_id = appCmd.substring(4,6);
        String function = appCmd.substring(6,8);
        String sub_function = appCmd.substring(8,10);

        Log.e(TAG, "parseAppCmd function=" + function+",sub_function="+sub_function);

        if (function.equals("01") && sub_function.equals("01")) {

        }
    }
}