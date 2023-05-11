package com.example.greenbetterymaster.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenbetterymaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BleDevAdapter extends RecyclerView.Adapter<BleDevAdapter.VH> {

    private static final String TAG = BleDevAdapter.class.getSimpleName();

    private static final String DeviceName = "WH-BEL 105";//要搜索的设备蓝牙名称高亮显示

    private final Listener mListener;
    private final Handler mHandler = new Handler();
    private final List<BleDev> mDevices = new ArrayList<>();
    public boolean isScanning;

    private final ScanCallback mScanCallback = new ScanCallback() {// 扫描Callback
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            BleDev dev = new BleDev(device, result);

            if (!mDevices.contains(dev)) {
                String deviceName = device.getName();// 获取蓝牙设备名字
                String deviceAddress = device.getAddress();// 获取蓝牙设备mac地址
                int deviceBondState = device.getBondState();// 蓝牙绑定状态

                if(deviceName != null){
                    mDevices.add(dev);
                }
//                mDevices.add(dev);

                /*if(deviceName!=null && !deviceName.toLowerCase().contains("mi")){
                  mDevices.add(dev);
                }*/

                BluetoothClass bluetoothClass = device.getBluetoothClass();
                final int deviceClass = bluetoothClass.getDeviceClass(); //设备类型（音频、手机、电脑、音箱等等）
                final int majorDeviceClass = bluetoothClass.getMajorDeviceClass();//具体的设备类型（例如音频设备又分为音箱、耳机、麦克风等等）

                Log.e(TAG,"onScanResult ======= deviceName:"+deviceName+",deviceAddress="+deviceAddress+",deviceBondState="+deviceBondState
                +",deviceClass="+Integer.toHexString(deviceClass)+",majorDeviceClass="+Integer.toHexString(majorDeviceClass));
                
                notifyDataSetChanged();

                Log.i(TAG, "onScanResult: " + result); // result.getScanRecord() 获取BLE广播数据
            }
        }
    };

    BleDevAdapter(Listener listener) {
        mListener = listener;
        scanBle();
    }

    // 重新扫描
    public void reScan() {
        mDevices.clear();
        notifyDataSetChanged();
        scanBle();
    }

    // 扫描BLE蓝牙(不会扫描经典蓝牙)
    private void scanBle() {
        isScanning = true;

        Log.e(TAG,"scanBle");

//        BluetoothAdapter bluetoothAdapter = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE).getDefaultAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        // Android5.0新增的扫描API，扫描返回的结果更友好，比如BLE广播数据以前是byte[] scanRecord，而新API帮我们解析成ScanRecord类

        bluetoothLeScanner.startScan(mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(mScanCallback); //停止扫描
                isScanning = false;
            }
        }, 3000);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dev, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        BleDev dev = mDevices.get(position);
//        if(dev.dev.getName() != null){
//            String name = dev.dev.getName();
//            String address = dev.dev.getAddress();
//            holder.name.setText(String.format("%s, %s", name, address));
//        }
        String name = dev.dev.getName();
        String address = dev.dev.getAddress();
        holder.name.setText(String.format("%s, %s", name, address));
//        holder.name.setText(String.format("%s, %s, Rssi=%s", name, address, dev.scanResult.getRssi()));
//        holder.address.setText(String.format("广播数据{%s}", dev.scanResult.getScanRecord()));

        if(holder.name.getText().toString().toLowerCase().contains(DeviceName)){
            Log.e(TAG,"name="+name);
            holder.name.setBackgroundColor(Color.RED);
        }else {
            holder.name.setBackgroundColor(0);
        }

    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name;
        final TextView address;

        VH(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            name.setTextColor(Color.BLACK);
            address.setTextColor(Color.BLACK);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d(TAG, "onClick, getAdapterPosition=" + pos);
            if (pos >= 0 && pos < mDevices.size())
                mListener.onItemClick(mDevices.get(pos).dev);
        }
    }

    public interface Listener {
        void onItemClick(BluetoothDevice dev);
    }

    public static class BleDev {
        public BluetoothDevice dev;
        ScanResult scanResult;

        BleDev(BluetoothDevice device, ScanResult result) {
            dev = device;
            scanResult = result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BleDev bleDev = (BleDev) o;
            return Objects.equals(dev, bleDev.dev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dev);
        }
    }
}
