package com.example.greenbetterymaster;

import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.example.service.mainService;

import java.util.Timer;
import java.util.TimerTask;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Bnt_check;
    private Button Bnt_maintain;
    private Button Bnt_charge;
    private Button Bnt_out;
    private Button Bnt_return;
    private TextView text_bet_v;
    private TextView text_bet_ah;
    private TextView text_bet_type;
    private TextView text_charge_v;
    private TextView text_charge_a;
    private TextView text_out_c;
    private TextView text_out_a;
    private TextView text_temp;
    private TextView text_stop;
    boolean showChinese = true;
    String ac_ok_flag = "0";
    String message;
    String value_set;
    static float battery_res = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        showChinese = MainActivity.showChinese;
        InitMenuShow();
        mainService.page_msg_now = "03";
        Intent intent = getIntent(); //Activity2
        ac_ok_flag = intent.getStringExtra("ac_ok_flag");

        value_set = MainActivity.ac_ok_flag;
        message = intent.getStringExtra("message");

//        if(value_set.equals("0")) { //按键消失术
//            this.Bnt_maintain.setVisibility(View.GONE);
//            this.Bnt_charge.setVisibility(View.GONE);
//            this.Bnt_out.setVisibility(View.GONE);
//        }

        sendMsg2Ble("0bfe87");
        sleep(500);
        sendMsg2Ble("0bfe87");
        if(mainService.set_msg == null){
            sleep(500);
            sendMsg2Ble("0bfe87");
        }
        try {
            Thread.sleep(300);//单位：毫秒
        } catch (Exception e) {
        }
        String preview_set = mainService.set_msg;
        Log.i("BetterySet", "mylog preview_set:"+ preview_set);

        if(preview_set != null){
            String bet_type_ori = preview_set.substring(2,4);
            String s1 = "01";
            String s2 = "02";
            String s3 = "03";
            String s4 = "04";
            if(bet_type_ori.equals(s1)){
                if(showChinese){
                    text_bet_type.setText("锂离子");
                }else{
                    text_bet_type.setText("LI");
                }
            }else if(bet_type_ori.equals(s2)){
                if(showChinese){
                    text_bet_type.setText("铁锂");
                }else{
                    text_bet_type.setText("FE");
                }
            }else if(bet_type_ori.equals(s3)){
                if(showChinese){
                    text_bet_type.setText("铅酸");
                }else{
                    text_bet_type.setText("PB");
                }
            }else if(bet_type_ori.equals(s4)){
                if(showChinese){
                    text_bet_type.setText("镍氢");
                }else{
                    text_bet_type.setText("NI");
                }
            }
            //电池电压
            String bettery_v_ori = preview_set.substring(4,8);
            float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
            text_bet_v.setText(String.valueOf(bettery_v));
            //电池容量
            String bettery_ah_ori = preview_set.substring(8,12);
            float bettery_ah = (float) (IntByteStringHexUtil.covert(bettery_ah_ori) * 0.1);
            text_bet_ah.setText(String.valueOf(bettery_ah));
            //充电电压
            String charge_v_ori = preview_set.substring(12,16);
            float charge_v = (float) (IntByteStringHexUtil.covert(charge_v_ori) * 0.01);
            text_charge_v.setText(String.valueOf(charge_v));
            //充电电流
            String charge_a_ori = preview_set.substring(16,20);
            float charge_a = (float) (IntByteStringHexUtil.covert(charge_a_ori) * 0.1);
            text_charge_a.setText(String.valueOf(charge_a));
            //电池内阻
            String out_c_ori = preview_set.substring(20,24);
//            int out_c = (int) (IntByteStringHexUtil.covert(out_c_ori));
            float out_c = (float) (IntByteStringHexUtil.covert(out_c_ori) * 0.1);
            text_out_c.setText(String.valueOf(out_c));
            battery_res = out_c;
            //放电电流
            String out_a_ori = preview_set.substring(24,28);
            float out_a = (float) (IntByteStringHexUtil.covert(out_a_ori) * 0.1);
            text_out_a.setText(String.valueOf(out_a));
            //停止电压
            String stop_ori = preview_set.substring(28,32);
            float stop = (float) (IntByteStringHexUtil.covert(stop_ori) * 0.1);
            text_stop.setText(String.valueOf(stop));
            //保护温度
            String temp_ori = preview_set.substring(32,34);
            float temp = (float) (IntByteStringHexUtil.covert(temp_ori));
            text_temp.setText(String.valueOf(temp));
        }

        if(mainService.page_msg != null) {
            String workingFlag = mainService.page_msg.substring(4, 6);
            if (!workingFlag.equals("02") && !workingFlag.equals("03") && !workingFlag.equals("04")) {
                sendMsg2Ble("010301e130");
                sleep(50);
                sendMsg2Ble("010301e130");
                sleep(50);
                sendMsg2Ble("010301e130");
            }else {
                if(showChinese){
                    showTip("设备正在工作");
                }else{
                    showTip("The equipment is working");
                }
            }
        }
    }

    private void InitMenuShow() {
        Bnt_return = (Button) findViewById(R.id.Bnt_return);
        Bnt_check = (Button) findViewById(R.id.Bnt_check);
        Bnt_maintain = (Button) findViewById(R.id.Bnt_maintain);
        Bnt_charge = (Button) findViewById(R.id.Bnt_charge);
        Bnt_out = (Button) findViewById(R.id.Bnt_out);
        Bnt_return.setOnClickListener(this);
        Bnt_check.setOnClickListener(this);
        Bnt_maintain.setOnClickListener(this);
        Bnt_charge.setOnClickListener(this);
        Bnt_out.setOnClickListener(this);
        text_stop = (TextView) findViewById(R.id.text_stop);
        text_bet_v = (TextView) findViewById(R.id.text_bet_v);
        text_bet_ah = (TextView) findViewById(R.id.text_bet_ah);
        text_bet_type = (TextView) findViewById(R.id.text_bet_type);
        text_charge_v = (TextView) findViewById(R.id.text_charge_v);
        text_charge_a = (TextView) findViewById(R.id.text_charge_a);
        text_out_c = (TextView) findViewById(R.id.text_out_c);
        text_out_a = (TextView) findViewById(R.id.text_out_a);
        text_temp = (TextView) findViewById(R.id.text_temp);
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PreviewActivity.this, tip, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bnt_check:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("open_flag", "a");
                        mIntent.setClass(PreviewActivity.this, InfoActivity.class);
                        startActivity(mIntent);
                    }
                }).start();
                break;
            case R.id.Bnt_maintain:     //维护
                if(mainService.page_msg != null) {
                    String workingFlag = mainService.page_msg.substring(2, 4);
                    if (workingFlag.equals("04") || workingFlag.equals("05")) {
                        String workingFlag_1 = mainService.page_msg.substring(4, 6);
                        if (!workingFlag_1.equals("04")) {
                            if(workingFlag_1.equals("02")){
                                if(showChinese){
                                    showTip("设备正在充电");
                                }else{
                                    showTip("The device is charging");
                                }
                            }else if(workingFlag_1.equals("03")){
                                if(showChinese){
                                    showTip("设备正在放电");
                                }else{
                                    showTip("The device is discharging");
                                }
                            }
                            return;
                        }
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mIntent_1 = new Intent();
                        mIntent_1.putExtra("open_flag", "d");
                        mIntent_1.setClass(PreviewActivity.this, InfoActivity.class);
                        startActivity(mIntent_1);
                    }
                }).start();
                break;
            case R.id.Bnt_charge:       //充电
                if(mainService.page_msg != null) {
                    String workingFlag = mainService.page_msg.substring(2, 4);
                    if (workingFlag.equals("04") || workingFlag.equals("05")) {
                        String workingFlag_1 = mainService.page_msg.substring(4, 6);
                        if (!workingFlag_1.equals("02")) {
                            if(workingFlag_1.equals("03")){
                                if(showChinese){
                                    showTip("设备正在放电");
                                }else{
                                    showTip("The device is discharging");
                                }
                            }else if(workingFlag_1.equals("04")){
                                if(showChinese){
                                    showTip("设备正在维护");
                                }else{
                                    showTip("The battery is being maintained");
                                }
                            }
                            return;
                        }
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mIntent_2 = new Intent();
                        mIntent_2.putExtra("open_flag", "b");
                        mIntent_2.setClass(PreviewActivity.this, InfoActivity.class);
                        startActivity(mIntent_2);
                    }
                }).start();
                break;
            case R.id.Bnt_out:      //放电
                if(mainService.page_msg != null) {
                    String workingFlag = mainService.page_msg.substring(2, 4);
                    if (workingFlag.equals("04") || workingFlag.equals("05")) {
                        String workingFlag_1 = mainService.page_msg.substring(4, 6);
                        if (!workingFlag_1.equals("03")) {
                            if(workingFlag_1.equals("02")){
                                if(showChinese){
                                    showTip("设备正在充电");
                                }else{
                                    showTip("The device is charging");
                                }
                            }else if(workingFlag_1.equals("04")){
                                if(showChinese){
                                    showTip("设备正在维护");
                                }else{
                                    showTip("The device is Under maintenance");
                                }
                            }
                            return;
                        }
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mIntent_3 = new Intent();
                        mIntent_3.putExtra("open_flag", "c");
                        mIntent_3.setClass(PreviewActivity.this, InfoActivity.class);
                        startActivity(mIntent_3);
                    }
                }).start();
                break;
            case R.id.Bnt_return:
                finish();
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

    @Override
    protected void onDestroy() {

        if(mainService.page_msg != null) {
            String workingFlag = mainService.page_msg.substring(4, 6);
            if (!workingFlag.equals("02") && !workingFlag.equals("03") && !workingFlag.equals("04")) {
                if(message != null){
                    sendMsg2Ble("010201E0A0");
                    sleep(50);
                    sendMsg2Ble("010201E0A0");
                    sleep(50);
                    sendMsg2Ble("010201E0A0");
                    BetterySetActivity.firstSelect = 1;
                }else{
                    sendMsg2Ble("010101E050");
                    sleep(50);
                    sendMsg2Ble("010101E050");
                    sleep(50);
                    sendMsg2Ble("010101E050");
                }
            }else {
                if(showChinese){
                    showTip("设备正在工作");
                }else{
                    showTip("The equipment is working");
                }
            }
        }

        sleep(100);
        super.onDestroy();
    }
}
