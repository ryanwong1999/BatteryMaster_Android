package com.example.greenbetterymaster;

import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.CRC16;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.example.service.mainService;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BetterySetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Bnt_save;
    private Button Bnt_quit;
    private EditText edit_bettery_v;
    private EditText edit_bettery_ah;
    private EditText edit_charge_v;
    private EditText edit_charge_a;
    private EditText edit_out_c;
    private EditText edit_out_a;
    private EditText edit_stop_v;
    private EditText edit_protect_t;
    private Spinner spin_bettery;
    private Spinner spin_bettery_v_li;
    private Spinner spin_bettery_v_fe;
    private Spinner spin_bettery_v_pb;
    private Spinner spin_bettery_v_ni;

    private String bettery_v = "1.1";
    private String bettery_ah = "111";
    private String charge_v = "1.1";
    private String charge_a = "1.1";
    private String out_c = "1.1";
    private String out_a = "1.1";
    private String stop_v = "1.1";
    private String protect_t = "11";
    private SharedPreferences settings;
    boolean showChinese = true;
    static int firstSelect = 0;
    String my_battery_type;
    String my_bettery_v;
    String ibattery_type = "0";
    int battery_type = 0;
    double charge_v_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bettery_set);
        settings = getSharedPreferences("data", 0);
        showChinese = MainActivity.showChinese;

        mainService.set_callback_msg = null;
        mainService.page_msg_now = "02";
        InitMenuShow();

        if(mainService.page_msg != null) {
            String workingFlag = mainService.page_msg.substring(4, 6);
            if (!workingFlag.equals("02") && !workingFlag.equals("03") && !workingFlag.equals("04")) {
                sendMsg2Ble("010201E0A0");
                sleep(50);
                sendMsg2Ble("010201E0A0");
                sleep(50);
                sendMsg2Ble("010201E0A0");
            }else {
                if(showChinese){
                    showTip("设备正在工作");
                }else{
                    showTip("The equipment is working");
                }
            }
        }

        sleep(150);
        InitSpinner(0);
        InitSpinner_li(0);
        setShow();
        edit_bettery_ah.addTextChangedListener(textWatcher);
    }
    //监听电池容量EditText
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG","afterTextChanged--------------->");
            mySetText();
            firstSelect++;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            Log.d("TAG","beforeTextChanged--------------->");
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("TAG","onTextChanged--------------->");
        }
    };

    public void mySetText() {
        //根据电池容量修改内阻值
        String bettery_ah = edit_bettery_ah.getText().toString().trim();
        boolean test_2 = isDouble(bettery_ah);
        boolean test_2_1 = isInteger(bettery_ah);
        if(!test_2 && !test_2_1){
            showTip("请输入正确的电池容量");
            return;
        }
        BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            if (bettery_ah.length() >= 1 && firstSelect > 1) {
                Log.i("firstSelect", "firstSelect2:" + firstSelect);
                String page_msg = mainService.page_msg;
                String title = page_msg.substring(6, 8);

                float bettery_ah_f = Float.parseFloat(bettery_ah);
                if (bettery_ah_f > 400) {
                    edit_bettery_ah.setText("400");
                }
                bettery_ah = edit_bettery_ah.getText().toString().trim();
                bettery_ah_f = Float.parseFloat(bettery_ah);
                //充电电流
                double charge_a = 0.2 * bettery_ah_f;
                if (title.equals("03")) {    //BM200-5
                    if (charge_a > 30) {    //5v
                        charge_a = 30;
                    }
                } else if (title.equals("04")) { //BM200-32
                    if (charge_a > 8) {    //32v
                        charge_a = 8;
                    }
                }
                if(charge_a > 200/charge_v_new){
                    charge_a = 200/charge_v_new;
                }
                //5v原来 32v充电最大8a 放电6a
                DecimalFormat df_charge_a = new DecimalFormat("0.0");//保留1位小数
                edit_charge_a.setText(df_charge_a.format(charge_a));
                //放电电流
                double out_a = 0.2 * bettery_ah_f;
                if (title.equals("03")) {    //BM200-5
                    if (out_a > 25) {   //5v
                        out_a = 25;
                    }
                } else if (title.equals("04")) { //BM200-32
                    if (out_a > 6) {   //32v
                        out_a = 6;
                    }
                }
                Log.i("out_a", "out_a:" + out_a);
                Log.i("out_a", "charge_v_new:" + charge_v_new);
                if(out_a > 90/charge_v_new){
                    out_a = 90/charge_v_new;
                }
                DecimalFormat df_out_a = new DecimalFormat("0.0");//保留1位小数
                edit_out_a.setText(df_out_a.format(out_a));
                //电池内阻
                if (my_bettery_v != null) {
                    Log.i("res", "my_bettery_v:" + my_bettery_v);
                    Log.i("res", "battery_type:" + battery_type);
                    if (battery_type == 1) {
                        double li_n = Float.parseFloat(my_bettery_v);
                        li_n /= 3.7;
                        Log.i("res", "li_n:" + li_n);
                        double li_res = li_n * (1 + 400 / bettery_ah_f);
                        if (li_res > 100) {
                            li_res = 100;
                        }
                        DecimalFormat df_li_res = new DecimalFormat("0.0");//保留1位小数
                        edit_out_c.setText(df_li_res.format(li_res));
                        Log.i("res", "edit_out_c:" + li_res);
                    } else if (battery_type == 2) {
                        double fe_n = Float.parseFloat(my_bettery_v);
                        fe_n /= 3.2;
                        Log.i("res", "fe_n:" + fe_n);
                        double fe_res = fe_n * (1 + 400 / bettery_ah_f);
                        if (fe_res > 100) {
                            fe_res = 100;
                        }
                        DecimalFormat df_fe_res = new DecimalFormat("0.0");//保留1位小数
                        edit_out_c.setText(df_fe_res.format(fe_res));
                        Log.i("res", "edit_out_c:" + fe_res);
                    } else if (battery_type == 3) {     //铅酸电池
                        double pb_v = Float.parseFloat(my_bettery_v);
                        double pb_res = (pb_v / 12) * (1 + 300 / bettery_ah_f);
                        if (pb_res > 100) {
                            pb_res = 100;
                        }
                        DecimalFormat df_pb_res = new DecimalFormat("0.0");//保留1位小数
                        edit_out_c.setText(df_pb_res.format(pb_res));
                        Log.i("res", "edit_out_c:" + pb_res);
                    } else if (battery_type == 4) {
                        double ni_n = Float.parseFloat(my_bettery_v);
                        ni_n /= 1.2;
                        Log.i("res", "ni_n:" + ni_n);
                        double ni_res = ni_n * (1 + 200 / bettery_ah_f);
                        if (ni_res > 100) {
                            ni_res = 100;
                        }
                        DecimalFormat df_ni_res = new DecimalFormat("0.0");//保留1位小数
                        edit_out_c.setText(df_ni_res.format(ni_res));
                        Log.i("res", "edit_out_c:" + ni_res);
                    }
                }
            }
        }
    }

    public void setShow() {
        sendMsg2Ble("0bfe87");
        if(mainService.set_msg == null){
            sleep(500);
            sendMsg2Ble("0bfe87");
        }

        sleep(500);
        String value_set = mainService.set_msg;
        Log.i("BetterySet", "mylog value_set:"+ value_set);
        if(value_set != null){
            String spin_bettery_ori = value_set.substring(2,4);
            String temp_spin_1 = "01";
            String temp_spin_2 = "02";
            String temp_spin_3 = "03";
            String temp_spin_4 = "04";
            Spinner spinner = (Spinner) findViewById(R.id.spin_bettery);
            if(spin_bettery_ori.equals(temp_spin_1)) {
                InitSpinner(0);
                String page_msg = mainService.page_msg;
                if(page_msg != null){
                    String title = page_msg.substring(6,8);
                    if (title.equals("01")) {   //          BM70-5
                        InitSpinner_li(0);
                    } else if (title.equals("02")) {    //          BM70-18
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 3.7 || bettery_v_ori.equals("0025")){
                            InitSpinner_li(0);
                        }else if(bettery_v == 7.4 || bettery_v_ori.equals("004A")){
                            InitSpinner_li(1);
                        }else if(bettery_v == 11.1 || bettery_v_ori.equals("006F")){
                            InitSpinner_li(2);
                        }else if(bettery_v == 14.8 || bettery_v_ori.equals("0094")){
                            InitSpinner_li(3);
                        }else{
                            InitSpinner_li(0);
                        }
                    } else if (title.equals("03")) {    //          BM200-5
                        InitSpinner_li(0);
                    } else if (title.equals("04")) {    //          BM200-32
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        Log.i("InitSpinner_li", "bettery_v:" + bettery_v);
                        if(bettery_v == 3.7 || bettery_v_ori.equals("0025")){
                            InitSpinner_li(0);
                        }else if(bettery_v == 7.4 || bettery_v_ori.equals("004A")){
                            InitSpinner_li(1);
                        }else if(bettery_v == 11.1 || bettery_v_ori.equals("006F")){
                            InitSpinner_li(2);
                        }else if(bettery_v == 14.8 || bettery_v_ori.equals("0094")){
                            InitSpinner_li(3);
                        }else if(bettery_v == 18.5 || bettery_v_ori.equals("00B9")){
                            InitSpinner_li(4);
                        }else if(bettery_v == 22.2 || bettery_v_ori.equals("00DE")){
                            InitSpinner_li(5);
                        }else if(bettery_v == 25.9 || bettery_v_ori.equals("0103")){
                            InitSpinner_li(6);
                        }else{
                            InitSpinner_li(0);
                        }
                    }
                }else{
                    InitSpinner_li(0);
                }
            }else if(spin_bettery_ori.equals(temp_spin_2)){
                InitSpinner(1);
                String page_msg = mainService.page_msg;
                if(page_msg != null){
                    String title = page_msg.substring(6, 8);
                    if (title.equals("01")) {   //          BM70-5
                        InitSpinner_fe(0);
                    } else if (title.equals("02")) {    //          BM70-18
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 3.2 || bettery_v_ori.equals("0020")){
                            InitSpinner_fe(0);
                        }else if(bettery_v == 6.4 || bettery_v_ori.equals("0040")){
                            InitSpinner_fe(1);
                        }else if(bettery_v == 9.6 || bettery_v_ori.equals("0060")){
                            InitSpinner_fe(2);
                        }else if(bettery_v == 12.8 || bettery_v_ori.equals("0080")){
                            InitSpinner_fe(3);
                        }else{
                            InitSpinner_fe(0);
                        }
                    } else if (title.equals("03")) {    //          BM200-5
                        InitSpinner_fe(0);
                    } else if (title.equals("04")) {    //          BM200-32
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        Log.i("InitSpinner_li", "bettery_v:" + bettery_v);
                        if(bettery_v == 3.2 || bettery_v_ori.equals("0020")){
                            InitSpinner_fe(0);
                        }else if(bettery_v == 6.4 || bettery_v_ori.equals("0040")){
                            InitSpinner_fe(1);
                        }else if(bettery_v == 9.6 || bettery_v_ori.equals("0060")){
                            InitSpinner_fe(2);
                        }else if(bettery_v == 12.8 || bettery_v_ori.equals("0080")){
                            InitSpinner_fe(3);
                        }else if(bettery_v == 16.0 || bettery_v_ori.equals("00A0")){
                            InitSpinner_fe(4);
                        }else if(bettery_v == 19.2 || bettery_v_ori.equals("00C0")){
                            InitSpinner_fe(5);
                        }else if(bettery_v == 22.4 || bettery_v_ori.equals("00E0")){
                            InitSpinner_fe(6);
                        }else if(bettery_v == 25.6 || bettery_v_ori.equals("0100")){
                            InitSpinner_fe(7);
                        }else{
                            InitSpinner_fe(0);
                        }
                    }
                }else{
                    InitSpinner_fe(0);
                }
            }else if(spin_bettery_ori.equals(temp_spin_3)){
                InitSpinner(2);
                String page_msg = mainService.page_msg;
                if(page_msg != null){
                    String title = page_msg.substring(6, 8);
                    if (title.equals("01")) {   //          BM70-5
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 2.0 || bettery_v_ori.equals("0014")){
                            InitSpinner_pb(0);
                        }else if(bettery_v == 4.0 || bettery_v_ori.equals("0028")){
                            InitSpinner_pb(1);
                        }else{
                            InitSpinner_pb(0);
                        }
                    } else if (title.equals("02")) {    //          BM70-18
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 2.0 || bettery_v_ori.equals("0014")){
                            InitSpinner_pb(0);
                        }else if(bettery_v == 4.0 || bettery_v_ori.equals("0028")){
                            InitSpinner_pb(1);
                        }else if(bettery_v == 6.0 || bettery_v_ori.equals("003C")){
                            InitSpinner_pb(2);
                        }else if(bettery_v == 8.0 || bettery_v_ori.equals("0050")){
                            InitSpinner_pb(3);
                        }else if(bettery_v == 10.0 || bettery_v_ori.equals("0064")){
                            InitSpinner_pb(4);
                        }else if(bettery_v == 12.0 || bettery_v_ori.equals("0078")){
                            InitSpinner_pb(5);
                        }else if(bettery_v == 14.0 || bettery_v_ori.equals("008C")){
                            InitSpinner_pb(6);
                        }else{
                            InitSpinner_pb(0);
                        }
                    } else if (title.equals("03")) {    //          BM200-5
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 2.0 || bettery_v_ori.equals("0014")){
                            InitSpinner_pb(0);
                        }else if(bettery_v == 4.0 || bettery_v_ori.equals("0028")){
                            InitSpinner_pb(1);
                        }else{
                            InitSpinner_pb(0);
                        }
                    } else if (title.equals("04")) {    //          BM200-32
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 2.0 || bettery_v_ori.equals("0014")){
                            InitSpinner_pb(0);
                        }else if(bettery_v == 4.0 || bettery_v_ori.equals("0028")){
                            InitSpinner_pb(1);
                        }else if(bettery_v == 6.0 || bettery_v_ori.equals("003C")){
                            InitSpinner_pb(2);
                        }else if(bettery_v == 8.0 || bettery_v_ori.equals("0050")){
                            InitSpinner_pb(3);
                        }else if(bettery_v == 10.0 || bettery_v_ori.equals("0064")){
                            InitSpinner_pb(4);
                        }else if(bettery_v == 12.0 || bettery_v_ori.equals("0078")){
                            InitSpinner_pb(5);
                        }else if(bettery_v == 14.0 || bettery_v_ori.equals("008C")){
                            InitSpinner_pb(6);
                        }else if(bettery_v == 16.0 || bettery_v_ori.equals("00A0")){
                            InitSpinner_pb(7);
                        }else if(bettery_v == 18.0 || bettery_v_ori.equals("00B4")){
                            InitSpinner_pb(8);
                        }else if(bettery_v == 20.0 || bettery_v_ori.equals("00C8")){
                            InitSpinner_pb(9);
                        }else if(bettery_v == 22.0 || bettery_v_ori.equals("00DC")){
                            InitSpinner_pb(10);
                        }else if(bettery_v == 24.0 || bettery_v_ori.equals("00F0")){
                            InitSpinner_pb(11);
                        }else if(bettery_v == 26.0 || bettery_v_ori.equals("0104")){
                            InitSpinner_pb(12);
                        }else if(bettery_v == 28.0 || bettery_v_ori.equals("0118")){
                            InitSpinner_pb(13);
                        }else{
                            InitSpinner_pb(0);
                        }
                    }
                }else{
                    InitSpinner_pb(0);
                }
            }else if(spin_bettery_ori.equals(temp_spin_4)){
                InitSpinner(3);
                String page_msg = mainService.page_msg;
                if(page_msg != null){
                    String title = page_msg.substring(6, 8);
                    if (title.equals("01")) {   //          BM70-5
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 1.2){
                            InitSpinner_ni(0);
                        }else if(bettery_v == 2.4){
                            InitSpinner_ni(1);
                        }else if(bettery_v == 3.6){
                            InitSpinner_ni(2);
                        }else{
                            InitSpinner_ni(0);
                        }
                    } else if (title.equals("02")) {    //          BM70-18
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 1.2){
                            InitSpinner_ni(0);
                        }else if(bettery_v == 2.4){
                            InitSpinner_ni(1);
                        }else if(bettery_v == 3.6){
                            InitSpinner_ni(2);
                        }else if(bettery_v == 4.8){
                            InitSpinner_ni(3);
                        }else if(bettery_v == 6.0){
                            InitSpinner_ni(4);
                        }else if(bettery_v == 7.2){
                            InitSpinner_ni(5);
                        }else if(bettery_v == 8.4){
                            InitSpinner_ni(6);
                        }else if(bettery_v == 9.6){
                            InitSpinner_ni(7);
                        }else if(bettery_v == 10.8){
                            InitSpinner_ni(8);
                        }else if(bettery_v == 12.0){
                            InitSpinner_ni(9);
                        }else{
                            InitSpinner_ni(0);
                        }
                    } else if (title.equals("03")) {    //          BM200-5
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 1.2){
                            InitSpinner_ni(0);
                        }else if(bettery_v == 2.4){
                            InitSpinner_ni(1);
                        }else if(bettery_v == 3.6){
                            InitSpinner_ni(2);
                        }else{
                            InitSpinner_ni(0);
                        }
                    } else if (title.equals("04")) {    //          BM200-32
                        String bettery_v_ori = value_set.substring(4,8);
                        float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
                        if(bettery_v == 1.2){
                            InitSpinner_ni(0);
                        }else if(bettery_v == 2.4){
                            InitSpinner_ni(1);
                        }else if(bettery_v == 3.6){
                            InitSpinner_ni(2);
                        }else if(bettery_v == 4.8){
                            InitSpinner_ni(3);
                        }else if(bettery_v == 6.0){
                            InitSpinner_ni(4);
                        }else if(bettery_v == 7.2){
                            InitSpinner_ni(5);
                        }else if(bettery_v == 8.4){
                            InitSpinner_ni(6);
                        }else if(bettery_v == 9.6){
                            InitSpinner_ni(7);
                        }else if(bettery_v == 10.8){
                            InitSpinner_ni(8);
                        }else if(bettery_v == 12.0){
                            InitSpinner_ni(9);
                        }else if(bettery_v == 13.2){
                            InitSpinner_ni(10);
                        }else if(bettery_v == 14.4){
                            InitSpinner_ni(11);
                        }else if(bettery_v == 15.6){
                            InitSpinner_ni(12);
                        }else if(bettery_v == 16.8){
                            InitSpinner_ni(13);
                        }else if(bettery_v == 18.0){
                            InitSpinner_ni(14);
                        }else if(bettery_v == 19.2){
                            InitSpinner_ni(15);
                        }else if(bettery_v == 20.4){
                            InitSpinner_ni(16);
                        }else if(bettery_v == 21.6){
                            InitSpinner_ni(17);
                        }else if(bettery_v == 22.8){
                            InitSpinner_ni(18);
                        }else if(bettery_v == 24.0){
                            InitSpinner_ni(19);
                        }else{
                            InitSpinner_ni(0);
                        }
                    }
                }else{
                    InitSpinner_ni(0);
                }
            }

            String bettery_v_ori = value_set.substring(4,8);
            float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
//            edit_bettery_v.setText(String.valueOf(bettery_v));
            //电池容量
            String bettery_ah_ori = value_set.substring(8,12);
            float bettery_ah = (float) (IntByteStringHexUtil.covert(bettery_ah_ori) * 0.1);
            edit_bettery_ah.setText(String.valueOf(bettery_ah));
            //充电电压
            String charge_v_ori = value_set.substring(12,16);
            float charge_v = (float) (IntByteStringHexUtil.covert(charge_v_ori) * 0.01);
            charge_v_new = charge_v;
            edit_charge_v.setText(String.valueOf(charge_v));
            //充电电流
            String charge_a_ori = value_set.substring(16,20);
            float charge_a = (float) (IntByteStringHexUtil.covert(charge_a_ori) * 0.1);
            edit_charge_a.setText(String.valueOf(charge_a));
            //电池内阻
            String out_c_ori = value_set.substring(20,24);
//            int out_c = (int) (IntByteStringHexUtil.covert(out_c_ori));
            float out_c = (float) (IntByteStringHexUtil.covert(out_c_ori) * 0.1);
            edit_out_c.setText(String.valueOf(out_c));
            //放电电流
            String out_a_ori = value_set.substring(24,28);
            float out_a = (float) (IntByteStringHexUtil.covert(out_a_ori) * 0.1);
            edit_out_a.setText(String.valueOf(out_a));
            //停止电压
            String stop_v_ori = value_set.substring(28,32);
            float stop_v = (float) (IntByteStringHexUtil.covert(stop_v_ori) * 0.1);
            edit_stop_v.setText(String.valueOf(stop_v));
            //保护温度
            String protect_t_ori = value_set.substring(32,34);
            int protect_t = (int) (IntByteStringHexUtil.covert(protect_t_ori));
            edit_protect_t.setText(String.valueOf(protect_t));

            Log.i("firstSelect", "firstSelect:" + firstSelect);
            if(firstSelect < 1){
                //充电电流
                String charge_a_ori_1 = value_set.substring(16,20);
                float charge_a_1 = (float) (IntByteStringHexUtil.covert(charge_a_ori_1) * 0.1);
                edit_charge_a.setText(String.valueOf(charge_a_1));
                //放电电流
                String out_a_ori_1 = value_set.substring(24,28);
                float out_a_1 = (float) (IntByteStringHexUtil.covert(out_a_ori_1) * 0.1);
                edit_out_a.setText(String.valueOf(out_a_1));
//                //停止电压
//                String stop_v_ori_1 = value_set.substring(28,32);
//                float stop_v_1 = (float) (IntByteStringHexUtil.covert(stop_v_ori_1) * 0.1);
//                edit_stop_v.setText(String.valueOf(stop_v_1));
                Log.i("firstSelect", "charge_a_1:" + charge_a_1);
                Log.i("firstSelect", "out_a_1:" + out_a_1);
            }
        }
    }

    public void sleep(int time) {
        try
        {
            Thread.sleep(time);//单位：毫秒
        } catch (Exception e) {
        }
    }

    public void sendMsg2Ble(String text) {
        BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
        if (service != null) {
            byte[] write_cmd = IntByteStringHexUtil.hexStrToByteArray(text);
            BleClientActivity.bleNotifyDeviceByQueue(false,write_cmd);
        }
    }

    private void InitMenuShow() {
//        edit_bettery_v = findViewById(R.id.edit_bettery_v);
        edit_bettery_ah = findViewById(R.id.edit_bettery_ah);
        edit_charge_v = findViewById(R.id.edit_charge_v);
        edit_charge_a = findViewById(R.id.edit_charge_a);
        edit_out_c = findViewById(R.id.edit_out_c);
        edit_out_a = findViewById(R.id.edit_out_a);
        edit_stop_v = findViewById(R.id.edit_stop_v);
        edit_protect_t = findViewById(R.id.edit_protect_t);
        Bnt_save = (Button) findViewById(R.id.Bnt_save);
        Bnt_quit = (Button) findViewById(R.id.Bnt_quit);
        Bnt_save.setOnClickListener(this);
        Bnt_quit.setOnClickListener(this);
    }

    //选择电池类型
    public void InitSpinner(int flag) {
        spin_bettery = findViewById(R.id.spin_bettery);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.battery_data, R.layout.my_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_bettery.setAdapter(adapter);
        spin_bettery.setSelection(flag);
        spin_bettery.setVisibility(View.VISIBLE); //设置可见
        spin_bettery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] spinner_content = getResources().getStringArray(R.array.battery_data);
                my_battery_type = String.valueOf(spinner_content[position]);
                firstSelect++;
                if(firstSelect > 1){
//                    Toast.makeText(BetterySetActivity.this, "你点击的是:" + spinner_content[position],
//                            Toast.LENGTH_SHORT).show();
                    if(spinner_content[position].equals("锂离子") || spinner_content[position].equals("LI")){
                        battery_type=1;
                        InitSpinner_li(0);
                    }
                    if(spinner_content[position].equals("铁锂") || spinner_content[position].equals("FE")){
                        battery_type=2;
                        InitSpinner_fe(0);
                    }
                    if(spinner_content[position].equals("铅酸") || spinner_content[position].equals("PB")){
                        battery_type=3;
                        InitSpinner_pb(0);
                    }
                    if(spinner_content[position].equals("镍氢") || spinner_content[position].equals("NI")){
                        battery_type=4;
                        InitSpinner_ni(0);
                    }
                    mySetText();
                }else{
                    if(spinner_content[position].equals("锂离子") || spinner_content[position].equals("LI")){
                        battery_type=1;
                    }
                    if(spinner_content[position].equals("铁锂") || spinner_content[position].equals("FE")){
                        battery_type=2;
                    }
                    if(spinner_content[position].equals("铅酸") || spinner_content[position].equals("PB")){
                        battery_type=3;
                    }
                    if(spinner_content[position].equals("镍氢") || spinner_content[position].equals("NI")){
                        battery_type=4;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    String page_msg = mainService.page_msg;
//    if(page_msg != null){
//        String title = page_msg.substring(6, 8);
//        if (title.equals("01")) {   //          BM70-5
//
//        } else if (title.equals("02")) {    //          BM70-18
//
//        } else if (title.equals("03")) {    //          BM200-5
//
//        } else if (title.equals("04")) {    //          BM200-32
//
//        }
//    }

    public void InitSpinner_li(int flag) {
        spin_bettery_v_li = findViewById(R.id.spin_bettery_v);
        String page_msg = mainService.page_msg;
        if(page_msg != null) {
            String title = page_msg.substring(6, 8);
            if (title.equals("01")) {
//            BM70-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_li_70_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_li.setAdapter(adapter);
                spin_bettery_v_li.setSelection(flag);
                spin_bettery_v_li.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_li.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_li_70_5);
                        if(spinner_content[position].equals("3.6/3.7")){
                            spinner_content[position] = "3.7";
                        }
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.7")){
                                edit_charge_v.setText("4.2");
                                charge_v_new = 4.2;
                                edit_stop_v.setText("3.1");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("02")) {
//            BM70-18
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_li_70_18, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_li.setAdapter(adapter);
                spin_bettery_v_li.setSelection(flag);
                spin_bettery_v_li.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_li.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_li_70_18);
                        if(spinner_content[position].equals("3.6/3.7")){
                            spinner_content[position] = "3.7";
                        }else if(spinner_content[position].equals("7.2/7.4")){
                            spinner_content[position] = "7.4";
                        }else if(spinner_content[position].equals("10.8/11.1")){
                            spinner_content[position] = "11.1";
                        }else if(spinner_content[position].equals("14.4/14.8")){
                            spinner_content[position] = "14.8";
                        }
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.7")){
                                edit_charge_v.setText("4.2");
                                charge_v_new = 4.2;
                                edit_stop_v.setText("3.1");
                            }else if(spinner_content[position].equals("7.4")){
                                edit_charge_v.setText("8.4");
                                charge_v_new = 8.4;
                                edit_stop_v.setText("6.3");
                            }else if(spinner_content[position].equals("11.1")){
                                edit_charge_v.setText("12.6");
                                charge_v_new = 12.6;
                                edit_stop_v.setText("9.4");
                            }else if(spinner_content[position].equals("14.8")){
                                edit_charge_v.setText("16.8");
                                charge_v_new = 16.8;
                                edit_stop_v.setText("12.6");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("03")) {
//            BM200-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_li_200_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_li.setAdapter(adapter);
                spin_bettery_v_li.setSelection(flag);
                spin_bettery_v_li.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_li.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_li_200_5);
                        if(spinner_content[position].equals("3.6/3.7")){
                            spinner_content[position] = "3.7";
                        }
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.7")){
                                edit_charge_v.setText("4.2");
                                charge_v_new = 4.2;
                                edit_stop_v.setText("3.1");
                            }
                            mySetText();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("04")) {
//            BM200-32
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_li_200_32, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_li.setAdapter(adapter);
                spin_bettery_v_li.setSelection(flag);
                spin_bettery_v_li.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_li.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_li_200_32);
                        if(spinner_content[position].equals("3.6/3.7")){
                            spinner_content[position] = "3.7";
                        }else if(spinner_content[position].equals("7.2/7.4")){
                            spinner_content[position] = "7.4";
                        }else if(spinner_content[position].equals("10.8/11.1")){
                            spinner_content[position] = "11.1";
                        }else if(spinner_content[position].equals("14.4/14.8")){
                            spinner_content[position] = "14.8";
                        }else if(spinner_content[position].equals("18.0/18.5")){
                            spinner_content[position] = "18.5";
                        }else if(spinner_content[position].equals("21.6/22.2")){
                            spinner_content[position] = "22.2";
                        }else if(spinner_content[position].equals("25.2/25.9")){
                            spinner_content[position] = "25.9";
                        }
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.7")){
                                edit_charge_v.setText("4.2");
                                charge_v_new = 4.2;
                                edit_stop_v.setText("3.1");
                            }else if(spinner_content[position].equals("7.4")){
                                edit_charge_v.setText("8.4");
                                charge_v_new = 8.4;
                                edit_stop_v.setText("6.3");
                            }else if(spinner_content[position].equals("11.1")){
                                edit_charge_v.setText("12.6");
                                charge_v_new = 12.6;
                                edit_stop_v.setText("9.4");
                            }else if(spinner_content[position].equals("14.8")){
                                edit_charge_v.setText("16.8");
                                charge_v_new = 16.8;
                                edit_stop_v.setText("12.6");
                            }else if(spinner_content[position].equals("18.5")){
                                edit_charge_v.setText("21.0");
                                charge_v_new = 21.0;
                                edit_stop_v.setText("15.7");
                            }else if(spinner_content[position].equals("22.2")){
                                edit_charge_v.setText("25.2");
                                charge_v_new = 25.2;
                                edit_stop_v.setText("18.9");
                            }else if(spinner_content[position].equals("25.9")){
                                edit_charge_v.setText("29.4");
                                charge_v_new = 29.4;
                                edit_stop_v.setText("22.0");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }else {
//            BM200-5
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.battery_v_li_200_5, R.layout.my_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_bettery_v_li.setAdapter(adapter);
            spin_bettery_v_li.setSelection(flag);
            spin_bettery_v_li.setVisibility(View.VISIBLE); //设置可见
            spin_bettery_v_li.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] spinner_content = getResources().getStringArray(R.array.battery_v_li_200_5);
                    my_bettery_v = String.valueOf(spinner_content[position]);
                    mySetText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void InitSpinner_fe(int flag) {
        spin_bettery_v_fe = findViewById(R.id.spin_bettery_v);
        String page_msg = mainService.page_msg;
        if(page_msg != null) {
            String title = page_msg.substring(6, 8);
            if (title.equals("01")) {   //          BM70-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_fe_70_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_fe.setAdapter(adapter);
                spin_bettery_v_fe.setSelection(flag);
                spin_bettery_v_fe.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_fe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_fe_70_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.2")){
                                edit_charge_v.setText("3.65");
                                charge_v_new = 3.65;
                                edit_stop_v.setText("2.7");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("02")) {    //          BM70-18
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_fe_70_18, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_fe.setAdapter(adapter);
                spin_bettery_v_fe.setSelection(flag);
                spin_bettery_v_fe.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_fe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_fe_70_18);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.2")){
                                edit_charge_v.setText("3.65");
                                charge_v_new = 3.65;
                                edit_stop_v.setText("2.7");
                            }else if(spinner_content[position].equals("6.4")){
                                edit_charge_v.setText("7.3");
                                charge_v_new = 7.3;
                                edit_stop_v.setText("5.4");
                            }else if(spinner_content[position].equals("9.6")){
                                edit_charge_v.setText("10.95");
                                charge_v_new = 10.95;
                                edit_stop_v.setText("8.2");
                            }else if(spinner_content[position].equals("12.8")){
                                edit_charge_v.setText("14.6");
                                charge_v_new = 14.6;
                                edit_stop_v.setText("10.9");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("03")) {    //          BM200-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_fe_200_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_fe.setAdapter(adapter);
                spin_bettery_v_fe.setSelection(flag);
                spin_bettery_v_fe.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_fe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_fe_200_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.2")){
                                edit_charge_v.setText("3.65");
                                charge_v_new = 3.65;
                                edit_stop_v.setText("2.7");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("04")) {    //          BM200-32
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_fe_200_32, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_fe.setAdapter(adapter);
                spin_bettery_v_fe.setSelection(flag);
                spin_bettery_v_fe.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_fe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_fe_200_32);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("3.2")){
                                edit_charge_v.setText("3.65");
                                charge_v_new = 3.65;
                                edit_stop_v.setText("2.7");
                            }else if(spinner_content[position].equals("6.4")){
                                edit_charge_v.setText("7.3");
                                charge_v_new = 7.3;
                                edit_stop_v.setText("5.4");
                            }else if(spinner_content[position].equals("9.6")){
                                edit_charge_v.setText("10.95");
                                charge_v_new = 10.95;
                                edit_stop_v.setText("8.2");
                            }else if(spinner_content[position].equals("12.8")){
                                edit_charge_v.setText("14.6");
                                charge_v_new = 14.6;
                                edit_stop_v.setText("10.9");
                            }else if(spinner_content[position].equals("16.0")){
                                edit_charge_v.setText("18.25");
                                charge_v_new = 18.25;
                                edit_stop_v.setText("13.6");
                            }else if(spinner_content[position].equals("19.2")){
                                edit_charge_v.setText("21.9");
                                charge_v_new = 21.9;
                                edit_stop_v.setText("16.3");
                            }else if(spinner_content[position].equals("22.4")){
                                edit_charge_v.setText("25.55");
                                charge_v_new = 25.55;
                                edit_stop_v.setText("19.0");
                            }else if(spinner_content[position].equals("25.6")){
                                edit_charge_v.setText("29.2");
                                charge_v_new = 29.2;
                                edit_stop_v.setText("21.8");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {    //          BM200-5
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.battery_v_fe_200_5, R.layout.my_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_bettery_v_fe.setAdapter(adapter);
            spin_bettery_v_fe.setSelection(flag);
            spin_bettery_v_fe.setVisibility(View.VISIBLE); //设置可见
            spin_bettery_v_fe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] spinner_content = getResources().getStringArray(R.array.battery_v_fe_200_5);
                    my_bettery_v = String.valueOf(spinner_content[position]);
                    mySetText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void InitSpinner_pb(int flag) {
        spin_bettery_v_pb = findViewById(R.id.spin_bettery_v);
        String page_msg = mainService.page_msg;
        if(page_msg != null) {
            String title = page_msg.substring(6, 8);
            if (title.equals("01")) {   //          BM70-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_pb_70_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_pb.setAdapter(adapter);
                spin_bettery_v_pb.setSelection(flag);
                spin_bettery_v_pb.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_pb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_pb_70_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("2.0")){
                                edit_charge_v.setText("2.45");
                                charge_v_new = 2.45;
                                edit_stop_v.setText("1.8");
                            }else if(spinner_content[position].equals("4.0")){
                                edit_charge_v.setText("4.9");
                                charge_v_new = 4.9;
                                edit_stop_v.setText("3.6");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("02")) {    //          BM70-18
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_pb_70_18, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_pb.setAdapter(adapter);
                spin_bettery_v_pb.setSelection(flag);
                spin_bettery_v_pb.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_pb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_pb_70_18);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("2.0")){
                                edit_charge_v.setText("2.45");
                                charge_v_new = 2.45;
                                edit_stop_v.setText("1.8");
                            }else if(spinner_content[position].equals("4.0")){
                                edit_charge_v.setText("4.9");
                                charge_v_new = 4.9;
                                edit_stop_v.setText("3.6");
                            }else if(spinner_content[position].equals("6.0")){
                                edit_charge_v.setText("7.35");
                                charge_v_new = 7.35;
                                edit_stop_v.setText("5.4");
                            }else if(spinner_content[position].equals("8.0")){
                                edit_charge_v.setText("9.8");
                                charge_v_new = 9.8;
                                edit_stop_v.setText("7.2");
                            }else if(spinner_content[position].equals("10.0")){
                                edit_charge_v.setText("12.25");
                                charge_v_new = 12.25;
                                edit_stop_v.setText("9.0");
                            }else if(spinner_content[position].equals("12.0")){
                                edit_charge_v.setText("14.7");
                                charge_v_new = 14.7;
                                edit_stop_v.setText("10.8");
                            }else if(spinner_content[position].equals("14.0")){
                                edit_charge_v.setText("17.15");
                                charge_v_new = 17.15;
                                edit_stop_v.setText("12.6");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("03")) {    //          BM200-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_pb_200_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_pb.setAdapter(adapter);
                spin_bettery_v_pb.setSelection(flag);
                spin_bettery_v_pb.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_pb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_pb_200_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("2.0")){
                                edit_charge_v.setText("2.45");
                                charge_v_new = 2.45;
                                edit_stop_v.setText("1.8");
                            }else if(spinner_content[position].equals("4.0")){
                                edit_charge_v.setText("4.9");
                                charge_v_new = 4.9;
                                edit_stop_v.setText("3.6");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("04")) {    //          BM200-32
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_pb_200_32, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_pb.setAdapter(adapter);
                spin_bettery_v_pb.setSelection(flag);
                spin_bettery_v_pb.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_pb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_pb_200_32);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("2.0")){
                                edit_charge_v.setText("2.45");
                                charge_v_new = 2.45;
                                edit_stop_v.setText("1.8");
                            }else if(spinner_content[position].equals("4.0")){
                                edit_charge_v.setText("4.9");
                                charge_v_new = 4.9;
                                edit_stop_v.setText("3.6");
                            }else if(spinner_content[position].equals("6.0")){
                                edit_charge_v.setText("7.35");
                                charge_v_new = 7.35;
                                edit_stop_v.setText("5.4");
                            }else if(spinner_content[position].equals("8.0")){
                                edit_charge_v.setText("9.8");
                                charge_v_new = 9.8;
                                edit_stop_v.setText("7.2");
                            }else if(spinner_content[position].equals("10.0")){
                                edit_charge_v.setText("12.25");
                                charge_v_new = 12.25;
                                edit_stop_v.setText("9.0");
                            }else if(spinner_content[position].equals("12.0")){
                                edit_charge_v.setText("14.7");
                                charge_v_new = 14.7;
                                edit_stop_v.setText("10.8");
                            }else if(spinner_content[position].equals("14.0")){
                                edit_charge_v.setText("17.15");
                                charge_v_new = 17.15;
                                edit_stop_v.setText("12.6");
                            }else if(spinner_content[position].equals("16.0")){
                                edit_charge_v.setText("19.6");
                                charge_v_new = 19.6;
                                edit_stop_v.setText("14.4");
                            }else if(spinner_content[position].equals("18.0")){
                                edit_charge_v.setText("22.05");
                                charge_v_new = 22.05;
                                edit_stop_v.setText("16.2");
                            }else if(spinner_content[position].equals("20.0")){
                                edit_charge_v.setText("24.5");
                                charge_v_new = 24.5;
                                edit_stop_v.setText("18.0");
                            }else if(spinner_content[position].equals("22.0")){
                                edit_charge_v.setText("26.95");
                                charge_v_new = 26.95;
                                edit_stop_v.setText("19.8");
                            }else if(spinner_content[position].equals("24.0")){
                                edit_charge_v.setText("29.4");
                                charge_v_new = 29.4;
                                edit_stop_v.setText("21.6");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }else { //          BM200-5
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.battery_v_pb_200_5, R.layout.my_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_bettery_v_pb.setAdapter(adapter);
            spin_bettery_v_pb.setSelection(flag);
            spin_bettery_v_pb.setVisibility(View.VISIBLE); //设置可见
            spin_bettery_v_pb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] spinner_content = getResources().getStringArray(R.array.battery_v_pb_200_5);
                    my_bettery_v = String.valueOf(spinner_content[position]);
                    mySetText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void InitSpinner_ni(int flag) {
        spin_bettery_v_ni = findViewById(R.id.spin_bettery_v);
        String page_msg = mainService.page_msg;
        if(page_msg != null) {
            String title = page_msg.substring(6, 8);
            if (title.equals("01")) {   //          BM70-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_ni_70_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_ni.setAdapter(adapter);
                spin_bettery_v_ni.setSelection(flag);
                spin_bettery_v_ni.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_ni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_ni_70_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("1.2")){
                                edit_charge_v.setText("1.6");
                                charge_v_new = 1.6;
                                edit_stop_v.setText("1.0");
                            }else if(spinner_content[position].equals("2.4")){
                                edit_charge_v.setText("3.2");
                                charge_v_new = 3.2;
                                edit_stop_v.setText("2.0");
                            }else if(spinner_content[position].equals("3.6")){
                                edit_charge_v.setText("4.8");
                                charge_v_new = 4.8;
                                edit_stop_v.setText("3.0");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("02")) {    //          BM70-18
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_ni_70_18, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_ni.setAdapter(adapter);
                spin_bettery_v_ni.setSelection(flag);
                spin_bettery_v_ni.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_ni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_ni_70_18);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("1.2")){
                                edit_charge_v.setText("1.6");
                                charge_v_new = 1.6;
                                edit_stop_v.setText("1.0");
                            }else if(spinner_content[position].equals("2.4")){
                                edit_charge_v.setText("3.2");
                                charge_v_new = 3.2;
                                edit_stop_v.setText("2.0");
                            }else if(spinner_content[position].equals("3.6")){
                                edit_charge_v.setText("4.8");
                                charge_v_new = 4.8;
                                edit_stop_v.setText("3.0");
                            }else if(spinner_content[position].equals("4.8")){
                                edit_charge_v.setText("6.4");
                                charge_v_new = 6.4;
                                edit_stop_v.setText("4.0");
                            }else if(spinner_content[position].equals("6.0")){
                                edit_charge_v.setText("8.2");
                                charge_v_new = 8.2;
                                edit_stop_v.setText("5.0");
                            }else if(spinner_content[position].equals("7.2")){
                                edit_charge_v.setText("9.8");
                                charge_v_new = 9.8;
                                edit_stop_v.setText("6.0");
                            }else if(spinner_content[position].equals("8.4")){
                                edit_charge_v.setText("11.4");
                                charge_v_new = 11.4;
                                edit_stop_v.setText("7.0");
                            }else if(spinner_content[position].equals("9.6")){
                                edit_charge_v.setText("13.0");
                                charge_v_new = 13.0;
                                edit_stop_v.setText("8.0");
                            }else if(spinner_content[position].equals("10.8")){
                                edit_charge_v.setText("14.6");
                                charge_v_new = 14.6;
                                edit_stop_v.setText("9.0");
                            }else if(spinner_content[position].equals("12.0")){
                                edit_charge_v.setText("16.2");
                                charge_v_new = 16.2;
                                edit_stop_v.setText("10.0");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("03")) {    //          BM200-5
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_ni_200_5, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_ni.setAdapter(adapter);
                spin_bettery_v_ni.setSelection(flag);
                spin_bettery_v_ni.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_ni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_ni_200_5);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("1.2")){
                                edit_charge_v.setText("1.6");
                                charge_v_new = 1.6;
                                edit_stop_v.setText("1.0");
                            }else if(spinner_content[position].equals("2.4")){
                                edit_charge_v.setText("3.2");
                                charge_v_new = 3.2;
                                edit_stop_v.setText("2.0");
                            }else if(spinner_content[position].equals("3.6")){
                                edit_charge_v.setText("4.8");
                                charge_v_new = 4.8;
                                edit_stop_v.setText("3.0");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (title.equals("04")) {    //          BM200-32
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.battery_v_ni_200_32, R.layout.my_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_bettery_v_ni.setAdapter(adapter);
                spin_bettery_v_ni.setSelection(flag);
                spin_bettery_v_ni.setVisibility(View.VISIBLE); //设置可见
                spin_bettery_v_ni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] spinner_content = getResources().getStringArray(R.array.battery_v_ni_200_32);
                        my_bettery_v = String.valueOf(spinner_content[position]);
                        if(firstSelect >= 1){
                            if(spinner_content[position].equals("1.2")){
                                edit_charge_v.setText("1.6");
                                charge_v_new = 1.6;
                                edit_stop_v.setText("1.0");
                            }else if(spinner_content[position].equals("2.4")){
                                edit_charge_v.setText("3.2");
                                charge_v_new = 3.2;
                                edit_stop_v.setText("2.0");
                            }else if(spinner_content[position].equals("3.6")){
                                edit_charge_v.setText("4.8");
                                charge_v_new = 4.8;
                                edit_stop_v.setText("3.0");
                            }else if(spinner_content[position].equals("4.8")){
                                edit_charge_v.setText("6.4");
                                charge_v_new = 6.4;
                                edit_stop_v.setText("4.0");
                            }else if(spinner_content[position].equals("6.0")){
                                edit_charge_v.setText("8.2");
                                charge_v_new = 8.2;
                                edit_stop_v.setText("5.0");
                            }else if(spinner_content[position].equals("7.2")){
                                edit_charge_v.setText("9.8");
                                charge_v_new = 9.8;
                                edit_stop_v.setText("6.0");
                            }else if(spinner_content[position].equals("8.4")){
                                edit_charge_v.setText("11.4");
                                charge_v_new = 11.4;
                                edit_stop_v.setText("7.0");
                            }else if(spinner_content[position].equals("9.6")){
                                edit_charge_v.setText("13.0");
                                charge_v_new = 13.0;
                                edit_stop_v.setText("8.0");
                            }else if(spinner_content[position].equals("10.8")){
                                edit_charge_v.setText("14.6");
                                charge_v_new = 14.6;
                                edit_stop_v.setText("9.0");
                            }else if(spinner_content[position].equals("12.0")){
                                edit_charge_v.setText("16.2");
                                charge_v_new = 16.2;
                                edit_stop_v.setText("10.0");
                            }else if(spinner_content[position].equals("13.2")){
                                edit_charge_v.setText("17.8");
                                charge_v_new = 17.8;
                                edit_stop_v.setText("11.0");
                            }else if(spinner_content[position].equals("14.4")){
                                edit_charge_v.setText("19.4");
                                charge_v_new = 19.4;
                                edit_stop_v.setText("12.0");
                            }else if(spinner_content[position].equals("15.6")){
                                edit_charge_v.setText("21.0");
                                charge_v_new = 21.0;
                                edit_stop_v.setText("13.0");
                            }else if(spinner_content[position].equals("16.8")){
                                edit_charge_v.setText("22.6");
                                charge_v_new = 22.6;
                                edit_stop_v.setText("14.0");
                            }else if(spinner_content[position].equals("18.0")){
                                edit_charge_v.setText("24.2");
                                charge_v_new = 24.2;
                                edit_stop_v.setText("15.0");
                            }else if(spinner_content[position].equals("19.2")){
                                edit_charge_v.setText("25.8");
                                charge_v_new = 25.8;
                                edit_stop_v.setText("16.0");
                            }else if(spinner_content[position].equals("20.4")){
                                edit_charge_v.setText("27.4");
                                charge_v_new = 27.4;
                                edit_stop_v.setText("17.0");
                            }else if(spinner_content[position].equals("21.6")){
                                edit_charge_v.setText("29.0");
                                charge_v_new = 29.0;
                                edit_stop_v.setText("18.0");
                            }
                            mySetText();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {    //          BM200-5
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.battery_v_ni_200_5, R.layout.my_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_bettery_v_ni.setAdapter(adapter);
            spin_bettery_v_ni.setSelection(flag);
            spin_bettery_v_ni.setVisibility(View.VISIBLE); //设置可见
            spin_bettery_v_ni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] spinner_content = getResources().getStringArray(R.array.battery_v_ni_200_5);
                    my_bettery_v = String.valueOf(spinner_content[position]);
                    mySetText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }



    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BetterySetActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String stringFloat2HexStr(String s, int arg, boolean flag, int strLength) {
        int i_s = Math.round(Float.parseFloat(s) * arg);
        String s_s = Integer.toHexString(i_s);
        if(flag){
            String zero_s_s = addZeroForNum(s_s, strLength);
            return zero_s_s;
        }else {
            return s_s;
        }
    }

    //前面补0方法
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            // sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bnt_save:
                BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
                if (service != null) {
//                bettery_v = edit_bettery_v.getText().toString().trim();
                    bettery_v = my_bettery_v;
                    bettery_ah = edit_bettery_ah.getText().toString().trim();
                    charge_v = edit_charge_v.getText().toString().trim();
                    charge_a = edit_charge_a.getText().toString().trim();
                    out_c = edit_out_c.getText().toString().trim();
                    out_a = edit_out_a.getText().toString().trim();
                    stop_v = edit_stop_v.getText().toString().trim();
                    protect_t = edit_protect_t.getText().toString().trim();

//                boolean test_1 = isDouble(bettery_v);
//                boolean test_1_1 = isInteger(bettery_v);
//                if(!test_1 && !test_1_1){
//                    showTip("请输入正确的电池电压");
//                    return;
//                }
                    boolean test_2 = isDouble(bettery_ah);
                    boolean test_2_1 = isInteger(bettery_ah);
                    if (!test_2 && !test_2_1) {
                        showTip("请输入正确的电池容量");
                        return;
                    }
                    boolean test_3 = isDouble(charge_v);
                    boolean test_3_1 = isInteger(charge_v);
                    if (!test_3 && !test_3_1) {
                        showTip("请输入正确的充电恒压");
                        return;
                    }
                    boolean test_4 = isDouble(charge_a);
                    boolean test_4_1 = isInteger(charge_a);
                    if (!test_4 && !test_4_1) {
                        showTip("请输入正确的充电恒流");
                        return;
                    }
                    boolean test_5 = isDouble(out_c);
                    boolean test_5_1 = isInteger(out_c);
                    if (!test_5 && !test_5_1) {
                        showTip("请输入正确的放电倍率");
                        return;
                    }
                    boolean test_6 = isDouble(out_a);
                    boolean test_6_1 = isInteger(out_a);
                    if (!test_6 && !test_6_1) {
                        showTip("请输入正确的放电恒流");
                        return;
                    }
                    boolean test_7 = isDouble(stop_v);
                    boolean test_7_1 = isInteger(stop_v);
                    if (!test_7 && !test_7_1) {
                        showTip("请输入正确的停止电压");
                        return;
                    }
                    boolean test_8 = isDouble(protect_t);
                    boolean test_8_1 = isInteger(protect_t);
                    if (!test_8 && !test_8_1) {
                        showTip("请输入正确的保护温度");
                        return;
                    }

                    bettery_v = stringFloat2HexStr(bettery_v, 10, true, 4);
                    bettery_ah = stringFloat2HexStr(bettery_ah, 10, true, 4);
                    charge_v = stringFloat2HexStr(charge_v, 100, true, 4);
                    charge_a = stringFloat2HexStr(charge_a, 10, true, 4);
                    out_c = stringFloat2HexStr(out_c, 1, true, 2);
                    out_a = stringFloat2HexStr(out_a, 10, true, 4);
                    stop_v = stringFloat2HexStr(stop_v, 10, true, 4);
                    protect_t = stringFloat2HexStr(protect_t, 1, true, 2);

                    String bat_type_1 = "锂离子";
                    String bat_type_1_1 = "LI";
                    String bat_type_2 = "铁锂";
                    String bat_type_2_1 = "FE";
                    String bat_type_3 = "铅酸";
                    String bat_type_3_1 = "PB";
                    String bat_type_4 = "镍氢";
                    String bat_type_4_1 = "NI";

                    if (my_battery_type.equals(bat_type_1) || my_battery_type.equals(bat_type_1_1)) {
                        ibattery_type = "01";
                    } else if (my_battery_type.equals(bat_type_2) || my_battery_type.equals(bat_type_2_1)) {
                        ibattery_type = "02";
                    } else if (my_battery_type.equals(bat_type_3) || my_battery_type.equals(bat_type_3_1)) {
                        ibattery_type = "03";
                    } else if (my_battery_type.equals(bat_type_4) || my_battery_type.equals(bat_type_4_1)) {
                        ibattery_type = "04";
                    }

                    String text = "02" + ibattery_type + bettery_v + bettery_ah + charge_v + charge_a + out_c + out_a + stop_v + protect_t;
                    String crc16 = CRC16.getCRC16(IntByteStringHexUtil.hexStrToByteArray(text));
                    String message = text + crc16;
                    Log.i("BetterySet", "ibattery_type:" + ibattery_type + " bettery_v:" + bettery_v + " bettery_ah:" + bettery_ah + " charge_v:" + charge_v
                            + " charge_a:" + charge_a + " out_c:" + out_c + " out_a:" + out_a + " stop_v:" + stop_v + " protect_t:" + protect_t);
                    Log.i("BetterySet", "mylog CRC:" + message);
                    sendMsg2Ble(message);
                    sleep(50);
                    sendMsg2Ble(message);
                    sleep(300);

                    String valueStr = mainService.set_callback_msg;
                    if (valueStr != null && valueStr.equals("023E81") && bettery_v != null) {
                        firstSelect = 0;
                        Intent mIntent = new Intent();
                        mIntent.putExtra("message", message);
                        mIntent.setClass(BetterySetActivity.this, PreviewActivity.class);
                        startActivity(mIntent);
                    } else {
                        Log.e("BetterySet", "ibattery_type:" + ibattery_type + " bettery_v:" + bettery_v + " bettery_ah:" + bettery_ah + " charge_v:" + charge_v
                                + " charge_a:" + charge_a + " out_c:" + out_c + " out_a:" + out_a + " stop_v:" + stop_v + " protect_t:" + protect_t);
                        if(showChinese){
                            showTip("发送失败，请重试");
                        }else{
                            showTip("Failed to send, please try again");
                        }
                    }
                } else {
                    if(showChinese){
                        showTip("蓝牙未连接");
                    }else{
                        showTip("Bluetooth disconnected");
                    }
                }
                break;
            case R.id.Bnt_quit:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    @Override
//    public void onResume() {    //用于返回时刷新蓝牙状态
//        super.onResume();
//        //重新获取数据的逻辑，此处根据自己的要求回去
//
//        //显示信息的界面
////        showTip("onResume");
//        setContentView(R.layout.activity_bettery_set);
//        InitMenuShow();
//        if(mainService.Sending){
//            sleep(150);
//            sendMsg2Ble("010201E0A0");
//        }else {
//            sendMsg2Ble("010201E0A0");
//        }
//        InitSpinner(0);
//        mainService.set_callback_msg = null;
//        setShow();
//    }

    @Override
    protected void onDestroy() {
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
        firstSelect = 0;
        sleep(100);
        super.onDestroy();
    }
}
