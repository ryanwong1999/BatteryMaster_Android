package com.example.greenbetterymaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.CRC16;
import com.example.greenbetterymaster.util.FileUtils;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.example.greenbetterymaster.util.LanguageSettings;
import com.example.service.mainService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Runnable runnable;
    private Handler handler;
    private Button Bnt_show_curve;
    private Button Bnt_onoff;
    private Button Bnt_return;
    private String open_flag = "a";
    private TextView text_tip1;
//    private TextView text_info;
//    private TextView text_info_out;
    private TextView text_run_time_h;
    private TextView text_run_time_m;
    private TextView text_battery_v;
    private TextView text_battery_ah;
    private TextView text_battery_type;

    private TextView text_num;
    private TextView text_tip2;
    private TextView text_tip3;
    private TextView text_battery_omega;
    private TextView text_soc_1;
    private TextView text_soh;
    private TextView text_out;
    private TextView text_temp;
    private TextView text_num_2;
    private TextView text_tip2_2;
    private TextView text_tip3_2;
    private TextView text_battery_omega_2;
    private TextView text_soc_1_2;
    private TextView text_soh_2;
    private TextView text_out_2;
    private TextView text_temp_2;
    private TextView text_num_3;
    private TextView text_tip2_3;
    private TextView text_tip3_3;
    private TextView text_battery_omega_3;
    private TextView text_soc_1_3;
    private TextView text_soh_3;
    private TextView text_out_3;
    private TextView text_temp_3;
    private TextView text_num_4;
    private TextView text_tip2_4;
    private TextView text_tip3_4;
    private TextView text_battery_omega_4;
    private TextView text_soc_1_4;
    private TextView text_soh_4;
    private TextView text_out_4;
    private TextView text_temp_4;
    private TextView text_num_5;
    private TextView text_tip2_5;
    private TextView text_tip3_5;
    private TextView text_battery_omega_5;
    private TextView text_soc_1_5;
    private TextView text_soh_5;
    private TextView text_out_5;
    private TextView text_temp_5;
    private TextView text_num_6;
    private TextView text_tip2_6;
    private TextView text_tip3_6;
    private TextView text_battery_omega_6;
    private TextView text_soc_1_6;
    private TextView text_soh_6;
    private TextView text_out_6;
    private TextView text_temp_6;
    private TextView text_num_7;
    private TextView text_tip2_7;
    private TextView text_tip3_7;
    private TextView text_battery_omega_7;
    private TextView text_soc_1_7;
    private TextView text_soh_7;
    private TextView text_out_7;
    private TextView text_temp_7;
    private TextView text_num_8;
    private TextView text_tip2_8;
    private TextView text_tip3_8;
    private TextView text_battery_omega_8;
    private TextView text_soc_1_8;
    private TextView text_soh_8;
    private TextView text_out_8;
    private TextView text_temp_8;
    private TextView text_soh_flag;

    static boolean run_flag = false;
    boolean showChinese = true;
    boolean isShowing = false;
    boolean working = false;
    boolean showCurveFlag = false;
    boolean oneHourFlag = false;
    boolean timeOutFlag = false;
    boolean stopRequestFlag = false;
    static String curve_id = "0";
    String curve_msg_t;
    String curve_msg_t_i;
    String curve_msg_v;
    String curve_msg_v_i;
    String curve_msg_a;
    String curve_msg_a_i;
    //曲线数组
    static int [] strArray_t_1;
    static int [] strArray_t_2;
    static int [] strArray_t_3;
    static int [] strArray_t_4;
    static int [] strArray_t_5;
    static int [] strArray_t_6;
    static int [] strArray_t_7;
    static int [] strArray_t_8;
    //电压
    static Float [] strArray_v_1;
    static Float [] strArray_v_2;
    static Float [] strArray_v_3;
    static Float [] strArray_v_4;
    static Float [] strArray_v_5;
    static Float [] strArray_v_6;
    static Float [] strArray_v_7;
    static Float [] strArray_v_8;
    //电流
    static Float [] strArray_a_1;
    static Float [] strArray_a_2;
    static Float [] strArray_a_3;
    static Float [] strArray_a_4;
    static Float [] strArray_a_5;
    static Float [] strArray_a_6;
    static Float [] strArray_a_7;
    static Float [] strArray_a_8;
    int Function = 0;
    static int jilian_num = 1;
    //输出log
    String tempLog = "0";
    String battLog = "0";
    String vLog = "0";
    String ahLog = "0";
    //设备id
    static String id_1 = "01";
    static String id_2 = "02";
    static String id_3 = "03";
    static String id_4 = "04";
    static String id_5 = "05";
    static String id_6 = "06";
    static String id_7 = "07";
    static String id_8 = "08";

    static float tip2 = 0;
    static float tip2_2 = 0;
    static float tip2_3 = 0;
    static float tip2_4 = 0;
    static float tip2_5 = 0;
    static float tip2_6 = 0;
    static float tip2_7 = 0;
    static float tip2_8 = 0;

    static float tip3 = 0;
    static float tip3_2 = 0;
    static float tip3_3 = 0;
    static float tip3_4 = 0;
    static float tip3_5 = 0;
    static float tip3_6 = 0;
    static float tip3_7 = 0;
    static float tip3_8 = 0;

    String temp_1 = "00";
    String temp_2 = "01";
    String temp_3 = "02";
    String temp_4 = "03";
    String temp_5 = "04";
    String temp_6 = "05";
    String temp_7 = "06";
    String temp_8 = "07";
    String temp_9 = "08";
    String temp_10 = "09";
    String temp_11 = "0A";
    String temp_12 = "0B";
    String temp_13 = "0C";
    String temp_14 = "0D";
    String temp_15 = "0E";
    String temp_16 = "0F";
    String temp_17 = "10";
    String temp_18 = "11";
    String temp_19 = "12";
    String temp_20 = "13";
    String temp_21 = "14";
    String temp_22 = "15";
    String temp_23 = "16";
    String temp_24 = "17";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        InitMenuShow();

        showChinese = MainActivity.showChinese;
        mainService.page_msg_now = "04";
        Intent intent = getIntent();
        open_flag = intent.getStringExtra("open_flag");
        String aa = "a";
        String bb = "b";
        String cc = "c";
        String dd = "d";

        if(Objects.equals(open_flag, aa)){
//            Bnt_show_curve.setVisibility(View.INVISIBLE);
            sendMsg2Ble("010401E300");
            sleep(50);
            sendMsg2Ble("010401E300");
            sleep(50);
            sendMsg2Ble("010401E300");
            Function = 1;
        }else if(Objects.equals(open_flag, bb)){
//            Bnt_show_curve.setVisibility(View.VISIBLE);
            sendMsg2Ble("010402A301");
            sleep(50);
            sendMsg2Ble("010402A301");
            sleep(50);
            sendMsg2Ble("010402A301");
            Function = 2;
        }else if(Objects.equals(open_flag, cc)){
//            Bnt_show_curve.setVisibility(View.VISIBLE);
            sendMsg2Ble("01040362C1");
            sleep(50);
            sendMsg2Ble("01040362C1");
            sleep(50);
            sendMsg2Ble("01040362C1");
            Function = 3;
        }else if(Objects.equals(open_flag, dd)){
//            Bnt_show_curve.setVisibility(View.VISIBLE);
            sendMsg2Ble("0104042303");
            sleep(50);
            sendMsg2Ble("0104042303");
            sleep(50);
            sendMsg2Ble("0104042303");
            Function = 4;
        }

        String preview_set = mainService.set_msg;
        if(preview_set != null){
            String bet_type_ori = preview_set.substring(2,4);
            String s1 = "01";
            String s2 = "02";
            String s3 = "03";
            String s4 = "04";
            if(bet_type_ori.equals(s1)){
                if(showChinese){
                    text_battery_type.setText("锂电池");
                }else{
                    text_battery_type.setText("LI");
                }
                battLog = "锂电池";
            }else if(bet_type_ori.equals(s2)){
                if(showChinese){
                    text_battery_type.setText("铁锂电池");
                }else{
                    text_battery_type.setText("FE");
                }
                battLog = "铁锂电池";
            }else if(bet_type_ori.equals(s3)){
                if(showChinese){
                    text_battery_type.setText("铅酸电池");
                }else{
                    text_battery_type.setText("PB");
                }
                battLog = "铅酸电池";
            }else if(bet_type_ori.equals(s4)){
                if(showChinese){
                    text_battery_type.setText("镍氢电池");
                }else{
                    text_battery_type.setText("NI");
                }
                battLog = "镍氢电池";
            }

            String bettery_v_ori = preview_set.substring(4,8);
            float bettery_v = (float) (IntByteStringHexUtil.covert(bettery_v_ori) * 0.1);
            text_battery_v.setText(String.valueOf(bettery_v) + "V");
            vLog = String.valueOf(bettery_v) + "V";

            String bettery_ah_ori = preview_set.substring(8,12);
            float bettery_ah = (float) (IntByteStringHexUtil.covert(bettery_ah_ori) * 0.1);
            text_battery_ah.setText(String.valueOf(bettery_ah) + "Ah");
            ahLog = String.valueOf(bettery_ah) + "Ah";
        }
        showinfo();
    }

    private void InitMenuShow() {
        Bnt_show_curve = (Button) findViewById(R.id.Bnt_show_curve);
        Bnt_show_curve.setOnClickListener(this);
        Bnt_onoff = (Button) findViewById(R.id.Bnt_onoff);
        Bnt_onoff.setOnClickListener(this);
        Bnt_return = (Button) findViewById(R.id.Bnt_return);
        Bnt_return.setOnClickListener(this);
        text_tip1 = findViewById(R.id.text_tip1);
        text_run_time_h = findViewById(R.id.text_run_time_h);
        text_run_time_m = findViewById(R.id.text_run_time_m);
        text_battery_v = findViewById(R.id.text_battery_v);
        text_battery_ah = findViewById(R.id.text_battery_ah);
        text_battery_type = findViewById(R.id.text_battery_type);
//        text_info = findViewById(R.id.text_info);
//        text_info_out = findViewById(R.id.text_info_out);
        text_soh_flag = findViewById(R.id.text_soh_flag);
        text_num = findViewById(R.id.text_num);
        text_tip2 = findViewById(R.id.text_tip2);
        text_tip3 = findViewById(R.id.text_tip3);
        text_battery_omega = findViewById(R.id.text_battery_omega);
        text_soc_1 = findViewById(R.id.text_soc_1);
        text_soh = findViewById(R.id.text_soh);
        text_out = findViewById(R.id.text_out);
        text_temp = findViewById(R.id.text_temp);
        text_num_2 = findViewById(R.id.text_num_2);
        text_tip2_2 = findViewById(R.id.text_tip2_2);
        text_tip3_2 = findViewById(R.id.text_tip3_2);
        text_battery_omega_2 = findViewById(R.id.text_battery_omega_2);
        text_soc_1_2 = findViewById(R.id.text_soc_1_2);
        text_soh_2 = findViewById(R.id.text_soh_2);
        text_out_2 = findViewById(R.id.text_out_2);
        text_temp_2 = findViewById(R.id.text_temp_2);
        text_num_3 = findViewById(R.id.text_num_3);
        text_tip2_3 = findViewById(R.id.text_tip2_3);
        text_tip3_3 = findViewById(R.id.text_tip3_3);
        text_battery_omega_3 = findViewById(R.id.text_battery_omega_3);
        text_soc_1_3 = findViewById(R.id.text_soc_1_3);
        text_soh_3 = findViewById(R.id.text_soh_3);
        text_out_3 = findViewById(R.id.text_out_3);
        text_temp_3 = findViewById(R.id.text_temp_3);
        text_num_4 = findViewById(R.id.text_num_4);
        text_tip2_4 = findViewById(R.id.text_tip2_4);
        text_tip3_4 = findViewById(R.id.text_tip3_4);
        text_battery_omega_4 = findViewById(R.id.text_battery_omega_4);
        text_soc_1_4 = findViewById(R.id.text_soc_1_4);
        text_soh_4 = findViewById(R.id.text_soh_4);
        text_out_4 = findViewById(R.id.text_out_4);
        text_temp_4 = findViewById(R.id.text_temp_4);
        text_num_5 = findViewById(R.id.text_num_5);
        text_tip2_5 = findViewById(R.id.text_tip2_5);
        text_tip3_5 = findViewById(R.id.text_tip3_5);
        text_battery_omega_5 = findViewById(R.id.text_battery_omega_5);
        text_soc_1_5 = findViewById(R.id.text_soc_1_5);
        text_soh_5 = findViewById(R.id.text_soh_5);
        text_out_5 = findViewById(R.id.text_out_5);
        text_temp_5 = findViewById(R.id.text_temp_5);
        text_num_6 = findViewById(R.id.text_num_6);
        text_tip2_6 = findViewById(R.id.text_tip2_6);
        text_tip3_6 = findViewById(R.id.text_tip3_6);
        text_battery_omega_6 = findViewById(R.id.text_battery_omega_6);
        text_soc_1_6 = findViewById(R.id.text_soc_1_6);
        text_soh_6 = findViewById(R.id.text_soh_6);
        text_out_6 = findViewById(R.id.text_out_6);
        text_temp_6 = findViewById(R.id.text_temp_6);
        text_num_7 = findViewById(R.id.text_num_7);
        text_tip2_7 = findViewById(R.id.text_tip2_7);
        text_tip3_7 = findViewById(R.id.text_tip3_7);
        text_battery_omega_7 = findViewById(R.id.text_battery_omega_7);
        text_soc_1_7 = findViewById(R.id.text_soc_1_7);
        text_soh_7 = findViewById(R.id.text_soh_7);
        text_out_7 = findViewById(R.id.text_out_7);
        text_temp_7 = findViewById(R.id.text_temp_7);
        text_num_8 = findViewById(R.id.text_num_8);
        text_tip2_8 = findViewById(R.id.text_tip2_8);
        text_tip3_8 = findViewById(R.id.text_tip3_8);
        text_battery_omega_8 = findViewById(R.id.text_battery_omega_8);
        text_soc_1_8 = findViewById(R.id.text_soc_1_8);
        text_soh_8 = findViewById(R.id.text_soh_8);
        text_out_8 = findViewById(R.id.text_out_8);
        text_temp_8 = findViewById(R.id.text_temp_8);
    }

    public void showinfo(){
        /**
         * 方式一：采用Handler的postDelayed(Runnable, long)方法
         */
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // handler自带方法实现定时器
                handler.postDelayed(this, 500);//每隔500ms执行
                Log.d("info","info更新");
                String Arg_msg = mainService.arg_msg;
                if(Arg_msg != null) {
                    //有多少级联
                    String jilian_num_ori = Arg_msg.substring(2, 4);
                    jilian_num = (int) (IntByteStringHexUtil.covert(jilian_num_ori));

                    //设备序号
                    String num_ori = Arg_msg.substring(4, 6);
                    int num = (int) (IntByteStringHexUtil.covert(num_ori));
                    text_num.setText(String.valueOf(num));
                    String num_s = Integer.toHexString(num);
                    id_1 = addZeroForNum(num_s, 2);
                    //soc
                    String soc_1_ori = Arg_msg.substring(8, 10);
                    int soc_1 = (int) (IntByteStringHexUtil.covert(soc_1_ori));
                    text_soc_1.setText(String.valueOf(soc_1));
                    //soh flag
                    String soh_flag = Arg_msg.substring(10, 12);
                    //soh
                    String soh_ori = Arg_msg.substring(12, 14);
                    int soh = (int) (IntByteStringHexUtil.covert(soh_ori));
                    if(soh > 100) soh = 100;
                    //内阻
                    String battery_omega_ori = Arg_msg.substring(14, 18);
                    float battery_omega = (float) (IntByteStringHexUtil.covert(battery_omega_ori) * 0.1);
                    DecimalFormat df_battery_omega = new DecimalFormat("0.0");//保留两位小数
                    text_battery_omega.setText(df_battery_omega.format(battery_omega));
                    if(soh_flag.equals("02")){
                        float soh1 = (PreviewActivity.battery_res / battery_omega) * 100;
                        text_soh.setText(String.valueOf((int)soh1) + "% R");
                    }else if(soh_flag.equals("03")){
                        text_soh.setText(String.valueOf(soh) + "%");
                    }else{
                        text_soh.setText("----");
                    }
                    if(showChinese){
                        text_soh_flag.setText("状态");
                    }else{
                        text_soh_flag.setText("state");
                    }
                    //时间h
                    String run_time_h_ori = Arg_msg.substring(18, 20);
                    int run_time_h = (int) (IntByteStringHexUtil.covert(run_time_h_ori));
                    text_run_time_h.setText(String.valueOf(run_time_h) + "h");
                    //时间m
                    String run_time_m_ori = Arg_msg.substring(20, 22);
                    int run_time_m = (int) (IntByteStringHexUtil.covert(run_time_m_ori));
                    text_run_time_m.setText(String.valueOf(run_time_m) + "m");
                    if(run_time_m >= 0 || run_time_h >= 0){
                        oneHourFlag = true;
                    }else {
                        oneHourFlag = false;
                    }
                    //暂停开始
                    String working_flag = Arg_msg.substring(22, 24);
                    if (working_flag.equals(temp_1)) {
                        working = false;
                        if(showChinese){
                            Bnt_onoff.setText("开始");
                        }else{
                            Bnt_onoff.setText("ON");
                        }
//                        Bnt_onoff.setTextColor(Color.RED);
                        Bnt_onoff.setBackground(getResources().getDrawable(R.drawable.btn_shap_r));
                    } else if (working_flag.equals(temp_2)) {
                        working = true;
                        if(showChinese){
                            Bnt_onoff.setText("暂停");
                        }else{
                            Bnt_onoff.setText("OFF");
                        }
//                        Bnt_onoff.setTextColor(Color.WHITE);
                        Bnt_onoff.setBackground(getResources().getDrawable(R.drawable.btn_shap_p));
                    }
                    //运行状态
                    String tip1_ori = Arg_msg.substring(24, 26);
//                    Log.d("info","后台服务循环执行 哈哈哈哈"+tip1_ori);
                    if(tip1_ori.equals(temp_1)) {
                        if(showChinese){
                            text_tip1.setText("待机");
                        }else{
                            text_tip1.setText("Standby");
                        }
                    }else if(tip1_ori.equals(temp_2)){
                        if(showChinese){
                            text_tip1.setText("充电中");
                        }else{
                            text_tip1.setText("Charging");
                        }
                    }else if(tip1_ori.equals(temp_3)){
                        if(showChinese){
                            text_tip1.setText("暂停充电");
                        }else{
                            text_tip1.setText("Pause charging");
                        }
                    }else if(tip1_ori.equals(temp_4)){
                        if(showChinese){
                            text_tip1.setText("充电完成");
                        }else{
                            text_tip1.setText("Charge complete");
                        }
                    }else if(tip1_ori.equals(temp_5)){
                        if(showChinese){
                            text_tip1.setText("空电充满");
                        }else{
                            text_tip1.setText("From empty to full");
                        }
                    }else if(tip1_ori.equals(temp_6)){
                        if(showChinese){
                            text_tip1.setText("非空起充");
                        }else{
                            text_tip1.setText("From Non-empty charge");
                        }
                    }else if(tip1_ori.equals(temp_7)){
                        if(showChinese){
                            text_tip1.setText("放电中");
                        }else{
                            text_tip1.setText("Discharging");
                        }
                    }else if(tip1_ori.equals(temp_8)){
                        if(showChinese){
                            text_tip1.setText("暂停放电");
                        }else{
                            text_tip1.setText("Pause discharge");
                        }
                    }else if(tip1_ori.equals(temp_9)){
                        if(showChinese){
                            text_tip1.setText("放电完成");
                        }else{
                            text_tip1.setText("Discharge completed");
                        }
                    }else if(tip1_ori.equals(temp_10)){
                        if(showChinese){
                            text_tip1.setText("满电放空");
                        }else{
                            text_tip1.setText("From full to empty");
                        }
                    }else if(tip1_ori.equals(temp_11)){
                        if(showChinese){
                            text_tip1.setText("半电放空");
                        }else{
                            text_tip1.setText("From Non-full to empty");
                        }
                    }else if(tip1_ori.equals(temp_12)){
                        if(showChinese){
                            text_tip1.setText("检测中");
                        }else{
                            text_tip1.setText("Detecting");
                        }
                    }else if(tip1_ori.equals(temp_13)){
                        if(showChinese){
                            text_tip1.setText("检测完成");
                        }else{
                            text_tip1.setText("Detection completed");
                        }
                    }else if(tip1_ori.equals(temp_14)){
                        if(showChinese){
                            text_tip1.setText("维护充电");
                        }else{
                            text_tip1.setText("Maintenance charge");
                        }
                    }else if(tip1_ori.equals(temp_15)){
                        if(showChinese){
                            text_tip1.setText("维护放电");
                        }else{
                            text_tip1.setText("Maintenance discharge");
                        }
                    }else if(tip1_ori.equals(temp_16)){
                        if(showChinese){
                            text_tip1.setText("暂停维护");
                        }else{
                            text_tip1.setText("Suspending maintenance");
                        }
                    }else if(tip1_ori.equals(temp_17)){
                        if(showChinese){
                            text_tip1.setText("维护完成");
                        }else{
                            text_tip1.setText("Maintenance completed");
                        }
                    }else if(tip1_ori.equals(temp_18)){
                        if(showChinese){
                            text_tip1.setText("短路");
                        }else{
                            text_tip1.setText("Short circuit");
                        }
                    }else if(tip1_ori.equals(temp_19)){
                        if(showChinese){
                            text_tip1.setText("过压");
                        }else{
                            text_tip1.setText("Overvoltage");
                        }
                    }else if(tip1_ori.equals(temp_20)){
                        if(showChinese){
                            text_tip1.setText("过流");
                        }else{
                            text_tip1.setText("Overcurrent");
                        }
                    }else if(tip1_ori.equals(temp_21)){
                        if(showChinese){
                            text_tip1.setText("温度过高");
                        }else{
                            text_tip1.setText("High temperature");
                        }
                    }else if(tip1_ori.equals(temp_22)){
                        if(showChinese){
                            text_tip1.setText("风扇异常");
                        }else{
                            text_tip1.setText("Fan err");
                        }
                    }else if(tip1_ori.equals(temp_23)){
                        if(showChinese){
                            text_tip1.setText("请接市电");
                        }else{
                            text_tip1.setText("Please connect to AC");
                        }
                    }else if(tip1_ori.equals(temp_24)){
                        if(showChinese){
                            text_tip1.setText("电池异常");
                        }else{
                            text_tip1.setText("Battery err");
                        }
                    }
                    //容量
                    String out_ori = Arg_msg.substring(26, 30);
                    float out = (float) (IntByteStringHexUtil.covert(out_ori) * 0.1);
                    DecimalFormat df_out = new DecimalFormat("0.00");//保留两位小数
                    text_out.setText(df_out.format(out));
                    //电压
                    String tip2_ori = Arg_msg.substring(30, 34);
                    tip2 = (float) (IntByteStringHexUtil.covert(tip2_ori) * 0.001);
                    if(tip2 >= 10){
                        DecimalFormat df_v = new DecimalFormat("00.00");//保留两位小数
                        text_tip2.setText(df_v.format(tip2));
                    }else {
                        DecimalFormat df_v = new DecimalFormat("0.000");//保留3位小数
                        text_tip2.setText(df_v.format(tip2));
                    }
                    //电流
                    String tip3_ori = Arg_msg.substring(34, 38);
                    tip3 = (float) (IntByteStringHexUtil.covert(tip3_ori) * 0.001);
                    if(tip3 >= 10){
                        DecimalFormat df_a = new DecimalFormat("00.0");//保留1位小数
                        text_tip3.setText(df_a.format(tip3));
                    }else {
                        DecimalFormat df_a = new DecimalFormat("0.00");//保留两位小数
                        text_tip3.setText(df_a.format(tip3));
                    }
                    //触点温度
                    String temp_ori = Arg_msg.substring(38, 40);
                    String temp = String.valueOf(IntByteStringHexUtil.covert(temp_ori));
                    text_temp.setText(temp);
                    tempLog = String.valueOf(temp);
//                    0C02 0101390000070014000100390CD7001E17 020B000501010100360CB03E1C1847C8
                    //2台级联
                    if(jilian_num >= 2){
                        //设备序号
                        String num_ori_2 = Arg_msg.substring(4+36, 6+36);
                        int num_2 = (int) (IntByteStringHexUtil.covert(num_ori_2));
                        text_num_2.setVisibility(View.VISIBLE);
                        text_num_2.setText(String.valueOf(num_2));
                        String num_s_2 = Integer.toHexString(num_2);
                        id_2 = addZeroForNum(num_s_2, 2);
                        //soc
                        String soc_1_ori_2 = Arg_msg.substring(6+36, 8+36);
                        int soc_1_2 = (int) (IntByteStringHexUtil.covert(soc_1_ori_2));
                        text_soc_1_2.setVisibility(View.VISIBLE);
                        text_soc_1_2.setText(String.valueOf(soc_1_2));
                        //内阻
                        String battery_omega_ori_2 = Arg_msg.substring(10+36, 14+36);
                        float battery_omega_2 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_2) * 0.1);
                        DecimalFormat df_battery_omega_2 = new DecimalFormat("0.0");//保留两位小数
                        text_battery_omega_2.setVisibility(View.VISIBLE);
                        text_battery_omega_2.setText(df_battery_omega_2.format(battery_omega_2));
                        //soh flag
                        String soh2_flag = Arg_msg.substring(34+36, 36+36);
                        //soh
                        String soh_ori_2 = Arg_msg.substring(8+36, 10+36);
                        int soh_2 = (int) (IntByteStringHexUtil.covert(soh_ori_2));
                        if(soh_2 > 100) soh_2 = 100;
                        text_soh_2.setVisibility(View.VISIBLE);
                        if(soh2_flag.equals("02")){
                            float soh2 = (PreviewActivity.battery_res / battery_omega_2) * 100;
                            text_soh_2.setText(String.valueOf((int)soh2) + "% R");
                        }else if(soh2_flag.equals("03")){
                            text_soh_2.setText(String.valueOf(soh_2) + "%");
                        }else{
                            text_soh_2.setText("----");
                        }
                        //12,14 ac
                        //14,16 dc
                        //16,18 tip1
                        //容量
                        String out_ori_2 = Arg_msg.substring(20+36, 24+36);
                        float out_2 = (float) (IntByteStringHexUtil.covert(out_ori_2) * 0.1);
                        DecimalFormat df_out_2 = new DecimalFormat("0.00");//保留两位小数
                        text_out_2.setVisibility(View.VISIBLE);
                        text_out_2.setText(df_out_2.format(out_2));
                        //电压
                        String tip2_ori_2 = Arg_msg.substring(24+36, 28+36);
                        tip2_2 = (float) (IntByteStringHexUtil.covert(tip2_ori_2) * 0.001);
                        if(tip2_2 >= 10){
                            DecimalFormat df_v_2 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_2.setVisibility(View.VISIBLE);
                            text_tip2_2.setText(df_v_2.format(tip2_2));
                        }else {
                            DecimalFormat df_v_2 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_2.setVisibility(View.VISIBLE);
                            text_tip2_2.setText(df_v_2.format(tip2_2));
                        }
                        //电流
                        String tip3_ori_2 = Arg_msg.substring(28+36, 32+36);
                        tip3_2 = (float) (IntByteStringHexUtil.covert(tip3_ori_2) * 0.001);
                        if(tip3_2 >= 10){
                            DecimalFormat df_a_2 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_2.setVisibility(View.VISIBLE);
                            text_tip3_2.setText(df_a_2.format(tip3_2));
                        }else {
                            DecimalFormat df_a_2 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_2.setVisibility(View.VISIBLE);
                            text_tip3_2.setText(df_a_2.format(tip3_2));
                        }
                        //触点温度
                        String temp_ori_2 = Arg_msg.substring(32+36, 34+36);
                        String temp_2 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_2));
                        text_temp_2.setVisibility(View.VISIBLE);
                        text_temp_2.setText(temp_2);
                    }

                    //3台级联
                    if(jilian_num >= 3){
                        //设备序号
                        String num_ori_3 = Arg_msg.substring(4+36+32 , 6+36+32);
                        int num_3 = (int) (IntByteStringHexUtil.covert(num_ori_3));
                        text_num_3.setVisibility(View.VISIBLE);
                        text_num_3.setText(String.valueOf(num_3));
                        String num_s_3 = Integer.toHexString(num_3);
                        id_3 = addZeroForNum(num_s_3, 2);
                        //soc
                        String soc_1_ori_3 = Arg_msg.substring(6+36+32, 8+36+32);
                        int soc_1_3 = (int) (IntByteStringHexUtil.covert(soc_1_ori_3));
                        text_soc_1_3.setVisibility(View.VISIBLE);
                        text_soc_1_3.setText(String.valueOf(soc_1_3));
                        //内阻
                        String battery_omega_ori_3 = Arg_msg.substring(10+36+32, 14+36+32);
                        float battery_omega_3 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_3) * 0.1);
                        DecimalFormat df_battery_omega_3 = new DecimalFormat("0.0");
                        text_battery_omega_3.setVisibility(View.VISIBLE);
                        text_battery_omega_3.setText(df_battery_omega_3.format(battery_omega_3));
                        //soh flag
                        String soh3_flag = Arg_msg.substring(32+36+32, 34+36+32);
                        //soh
                        String soh_ori_3 = Arg_msg.substring(8+36+32, 10+36+32);
                        int soh_3 = (int) (IntByteStringHexUtil.covert(soh_ori_3));
                        if(soh_3 > 100) soh_3 = 100;
                        text_soh_3.setVisibility(View.VISIBLE);
                        if(soh3_flag.equals("02")){
                            float soh3 = (PreviewActivity.battery_res / battery_omega_3) * 100;
                            text_soh_3.setText(String.valueOf((int)soh3) + "% R");
                        }else if(soh3_flag.equals("03")){
                            text_soh_3.setText(String.valueOf(soh_3) + "%");
                        }else{
                            text_soh_3.setText("----");
                        }
                        //容量
                        String out_ori_3 = Arg_msg.substring(20+36+32, 24+36+32);
                        float out_3 = (float) (IntByteStringHexUtil.covert(out_ori_3) * 0.1);
                        DecimalFormat df_out_3 = new DecimalFormat("0.00");//保留两位小数
                        text_out_3.setVisibility(View.VISIBLE);
                        text_out_3.setText(df_out_3.format(out_3));
                        //电压
                        String tip2_ori_3 = Arg_msg.substring(24+36+32, 28+36+32);
                        tip2_3 = (float) (IntByteStringHexUtil.covert(tip2_ori_3) * 0.001);
                        if(tip2_3 >= 10){
                            DecimalFormat df_v_3 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_3.setVisibility(View.VISIBLE);
                            text_tip2_3.setText(df_v_3.format(tip2_3));
                        }else {
                            DecimalFormat df_v_3 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_3.setVisibility(View.VISIBLE);
                            text_tip2_3.setText(df_v_3.format(tip2_3));
                        }
                        //电流
                        String tip3_ori_3 = Arg_msg.substring(28+36+32, 32+36+32);
                        tip3_3 = (float) (IntByteStringHexUtil.covert(tip3_ori_3) * 0.001);
                        if(tip3_3 >= 10){
                            DecimalFormat df_a_3 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_3.setVisibility(View.VISIBLE);
                            text_tip3_3.setText(df_a_3.format(tip3_3));
                        }else {
                            DecimalFormat df_a_3 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_3.setVisibility(View.VISIBLE);
                            text_tip3_3.setText(df_a_3.format(tip3_3));
                        }
                        //触点温度
                        String temp_ori_3 = Arg_msg.substring(32+36+32, 34+36+32);
                        String temp_3 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_3));
                        text_temp_3.setVisibility(View.VISIBLE);
                        text_temp_3.setText(temp_3);
                    }

                    //4台级联
                    if(jilian_num >= 4){
                        //设备序号
                        String num_ori_4 = Arg_msg.substring(4+36+(32*2), 6+36+(32*2));
                        int num_4 = (int) (IntByteStringHexUtil.covert(num_ori_4));
                        text_num_4.setVisibility(View.VISIBLE);
                        text_num_4.setText(String.valueOf(num_4));
                        String num_s_4 = Integer.toHexString(num_4);
                        id_4 = addZeroForNum(num_s_4, 2);
                        //soc
                        String soc_1_ori_4 = Arg_msg.substring(6+36+(32*2), 8+36+(32*2));
                        int soc_1_4 = (int) (IntByteStringHexUtil.covert(soc_1_ori_4));
                        text_soc_1_4.setVisibility(View.VISIBLE);
                        text_soc_1_4.setText(String.valueOf(soc_1_4));
                        //内阻
                        String battery_omega_ori_4 = Arg_msg.substring(10+36+(32*2), 14+36+(32*2));
                        float battery_omega_4 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_4) * 0.1);
                        DecimalFormat df_battery_omega_4 = new DecimalFormat("0.0");
                        text_battery_omega_4.setVisibility(View.VISIBLE);
                        text_battery_omega_4.setText(df_battery_omega_4.format(battery_omega_4));
                        //soh flag
                        String soh4_flag = Arg_msg.substring(34+36+(32*2), 36+36+(32*2));
                        //soh
                        String soh_ori_4 = Arg_msg.substring(8+36+(32*2), 10+36+(32*2));
                        int soh_4 = (int) (IntByteStringHexUtil.covert(soh_ori_4));
                        if(soh_4 > 100) soh_4 = 100;
                        text_soh_4.setVisibility(View.VISIBLE);
                        if(soh4_flag.equals("02")){
                            float soh4 = (PreviewActivity.battery_res / battery_omega_4) * 100;
                            text_soh_4.setText(String.valueOf((int)soh4) + "% R");
                        }else if(soh4_flag.equals("03")){
                            text_soh_4.setText(String.valueOf(soh_4) + "%");
                        }else{
                            text_soh_4.setText("----");
                        }
                        //容量
                        String out_ori_4 = Arg_msg.substring(20+36+(32*2), 24+36+(32*2));
                        float out_4 = (float) (IntByteStringHexUtil.covert(out_ori_4) * 0.1);
                        DecimalFormat df_out_4 = new DecimalFormat("0.00");//保留两位小数
                        text_out_4.setVisibility(View.VISIBLE);
                        text_out_4.setText(df_out_4.format(out_4));
                        //电压
                        String tip2_ori_4 = Arg_msg.substring(24+36+(32*2), 28+36+(32*2));
                        tip2_4 = (float) (IntByteStringHexUtil.covert(tip2_ori_4) * 0.001);
                        if(tip2_4 >= 10){
                            DecimalFormat df_v_4 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_4.setVisibility(View.VISIBLE);
                            text_tip2_4.setText(df_v_4.format(tip2_4));
                        }else {
                            DecimalFormat df_v_4 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_4.setVisibility(View.VISIBLE);
                            text_tip2_4.setText(df_v_4.format(tip2_4));
                        }
                        //电流
                        String tip3_ori_4 = Arg_msg.substring(28+36+(32*2), 32+36+(32*2));
                        tip3_4 = (float) (IntByteStringHexUtil.covert(tip3_ori_4) * 0.001);
                        if(tip3_4 >= 10){
                            DecimalFormat df_a_4 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_4.setVisibility(View.VISIBLE);
                            text_tip3_4.setText(df_a_4.format(tip3_4));
                        }else {
                            DecimalFormat df_a_4 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_4.setVisibility(View.VISIBLE);
                            text_tip3_4.setText(df_a_4.format(tip3_4));
                        }
                        //触点温度
                        String temp_ori_4 = Arg_msg.substring(32+36+(32*2), 34+36+(32*2));
                        String temp_4 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_4));
                        text_temp_4.setVisibility(View.VISIBLE);
                        text_temp_4.setText(temp_4);
                    }

                    //5台级联
                    if(jilian_num >= 5){
                        //设备序号
                        String num_ori_5 = Arg_msg.substring(4+36+(32*3), 6+36+(32*3));
                        int num_5 = (int) (IntByteStringHexUtil.covert(num_ori_5));
                        text_num_5.setVisibility(View.VISIBLE);
                        text_num_5.setText(String.valueOf(num_5));
                        String num_s_5 = Integer.toHexString(num_5);
                        id_5 = addZeroForNum(num_s_5, 2);
                        //soc
                        String soc_1_ori_5 = Arg_msg.substring(6+36+(32*3), 8+36+(32*3));
                        int soc_1_5 = (int) (IntByteStringHexUtil.covert(soc_1_ori_5));
                        text_soc_1_5.setVisibility(View.VISIBLE);
                        text_soc_1_5.setText(String.valueOf(soc_1_5));
                        //内阻
                        String battery_omega_ori_5 = Arg_msg.substring(10+36+(32*3), 14+36+(32*3));
                        float battery_omega_5 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_5) * 0.1);
                        DecimalFormat df_battery_omega_5 = new DecimalFormat("0.0");
                        text_battery_omega_5.setVisibility(View.VISIBLE);
                        text_battery_omega_5.setText(df_battery_omega_5.format(battery_omega_5));
                        //soh flag
                        String soh5_flag = Arg_msg.substring(34+36+(32*3), 36+36+(32*3));
                        //soh
                        String soh_ori_5 = Arg_msg.substring(8+36+(32*3), 10+36+(32*3));
                        int soh_5 = (int) (IntByteStringHexUtil.covert(soh_ori_5));
                        if(soh_5 > 100) soh_5 = 100;
                        text_soh_5.setVisibility(View.VISIBLE);
                        if(soh5_flag.equals("02")){
                            float soh5 = (PreviewActivity.battery_res / battery_omega_5) * 100;
                            text_soh_2.setText(String.valueOf((int)soh5) + "% R");
                        }else if(soh5_flag.equals("03")){
                            text_soh_2.setText(String.valueOf(soh_5) + "%");
                        }else{
                            text_soh_2.setText("----");
                        }
                        //容量
                        String out_ori_5 = Arg_msg.substring(20+36+(32*3), 24+36+(32*3));
                        float out_5 = (float) (IntByteStringHexUtil.covert(out_ori_5) * 0.1);
                        DecimalFormat df_out_5 = new DecimalFormat("0.00");//保留两位小数
                        text_out_5.setVisibility(View.VISIBLE);
                        text_out_5.setText(df_out_5.format(out_5));
                        //电压
                        String tip2_ori_5 = Arg_msg.substring(24+36+(32*3), 28+36+(32*3));
                        tip2_5 = (float) (IntByteStringHexUtil.covert(tip2_ori_5) * 0.001);
                        if(tip2_5 >= 10){
                            DecimalFormat df_v_5 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_5.setVisibility(View.VISIBLE);
                            text_tip2_5.setText(df_v_5.format(tip2_5));
                        }else {
                            DecimalFormat df_v_5 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_5.setVisibility(View.VISIBLE);
                            text_tip2_5.setText(df_v_5.format(tip2_5));
                        }
                        //电流
                        String tip3_ori_5 = Arg_msg.substring(28+36+(32*3), 32+36+(32*3));
                        tip3_5 = (float) (IntByteStringHexUtil.covert(tip3_ori_5) * 0.001);
                        if(tip3_5 >= 10){
                            DecimalFormat df_a_5 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_5.setVisibility(View.VISIBLE);
                            text_tip3_5.setText(df_a_5.format(tip3_5));
                        }else {
                            DecimalFormat df_a_5 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_5.setVisibility(View.VISIBLE);
                            text_tip3_5.setText(df_a_5.format(tip3_5));
                        }
                        //触点温度
                        String temp_ori_5 = Arg_msg.substring(32+36+(32*3), 34+36+(32*3));
                        String temp_5 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_5));
                        text_temp_5.setVisibility(View.VISIBLE);
                        text_temp_5.setText(temp_5);
                    }

                    //6台级联
                    if(jilian_num >= 6){
                        //设备序号
                        String num_ori_6 = Arg_msg.substring(4+36+(32*4), 6+36+(32*4));
                        int num_6 = (int) (IntByteStringHexUtil.covert(num_ori_6));
                        text_num_6.setVisibility(View.VISIBLE);
                        text_num_6.setText(String.valueOf(num_6));
                        String num_s_6 = Integer.toHexString(num_6);
                        id_6 = addZeroForNum(num_s_6, 2);
                        //soc
                        String soc_1_ori_6 = Arg_msg.substring(6+36+(32*4), 8+36+(32*4));
                        int soc_1_6 = (int) (IntByteStringHexUtil.covert(soc_1_ori_6));
                        text_soc_1_6.setVisibility(View.VISIBLE);
                        text_soc_1_6.setText(String.valueOf(soc_1_6));
                        //内阻
                        String battery_omega_ori_6 = Arg_msg.substring(10+36+(32*4), 14+36+(32*4));
                        float battery_omega_6 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_6) * 0.1);
                        DecimalFormat df_battery_omega_6 = new DecimalFormat("0.0");
                        text_battery_omega_6.setVisibility(View.VISIBLE);
                        text_battery_omega_6.setText(df_battery_omega_6.format(battery_omega_6));
                        //soh flag
                        String soh6_flag = Arg_msg.substring(34+36+(32*4), 36+36+(32*4));
                        //soh
                        String soh_ori_6 = Arg_msg.substring(8+36+(32*4), 10+36+(32*4));
                        int soh_6 = (int) (IntByteStringHexUtil.covert(soh_ori_6));
                        if(soh_6 > 100) soh_6 = 100;
                        text_soh_6.setVisibility(View.VISIBLE);
                        if(soh6_flag.equals("02")){
                            float soh6 = (PreviewActivity.battery_res / battery_omega_6) * 100;
                            text_soh_6.setText(String.valueOf((int)soh6) + "% R");
                        }else if(soh6_flag.equals("03")){
                            text_soh_6.setText(String.valueOf(soh_6) + "%");
                        }else{
                            text_soh_6.setText("----");
                        }
                        //容量
                        String out_ori_6 = Arg_msg.substring(20+36+(32*4), 24+36+(32*4));
                        float out_6 = (float) (IntByteStringHexUtil.covert(out_ori_6) * 0.1);
                        DecimalFormat df_out_6 = new DecimalFormat("0.00");//保留两位小数
                        text_out_6.setVisibility(View.VISIBLE);
                        text_out_6.setText(df_out_6.format(out_6));
                        //电压
                        String tip2_ori_6 = Arg_msg.substring(24+36+(32*4), 28+36+(32*4));
                        tip2_6 = (float) (IntByteStringHexUtil.covert(tip2_ori_6) * 0.001);
                        if(tip2_6 >= 10){
                            DecimalFormat df_v_6 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_6.setVisibility(View.VISIBLE);
                            text_tip2_6.setText(df_v_6.format(tip2_6));
                        }else {
                            DecimalFormat df_v_6 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_6.setVisibility(View.VISIBLE);
                            text_tip2_6.setText(df_v_6.format(tip2_6));
                        }
                        //电流
                        String tip3_ori_6 = Arg_msg.substring(28+36+(32*4), 32+36+(32*4));
                        tip3_6 = (float) (IntByteStringHexUtil.covert(tip3_ori_6) * 0.001);
                        if(tip3_6 >= 10){
                            DecimalFormat df_a_6 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_6.setVisibility(View.VISIBLE);
                            text_tip3_6.setText(df_a_6.format(tip3_6));
                        }else {
                            DecimalFormat df_a_6 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_6.setVisibility(View.VISIBLE);
                            text_tip3_6.setText(df_a_6.format(tip3_6));
                        }
                        //触点温度
                        String temp_ori_6 = Arg_msg.substring(32+36+(32*4), 34+36+(32*4));
                        String temp_6 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_6));
                        text_temp_6.setVisibility(View.VISIBLE);
                        text_temp_6.setText(temp_6);
                    }

                    //7台级联
                    if(jilian_num >= 7){
                        //设备序号
                        String num_ori_7 = Arg_msg.substring(4+36+(32*5), 6+36+(32*5));
                        int num_7 = (int) (IntByteStringHexUtil.covert(num_ori_7));
                        text_num_7.setVisibility(View.VISIBLE);
                        text_num_7.setText(String.valueOf(num_7));
                        String num_s_7 = Integer.toHexString(num_7);
                        id_7 = addZeroForNum(num_s_7, 2);
                        //soc
                        String soc_1_ori_7 = Arg_msg.substring(6+36+(32*5), 8+36+(32*5));
                        int soc_1_7 = (int) (IntByteStringHexUtil.covert(soc_1_ori_7));
                        text_soc_1_7.setVisibility(View.VISIBLE);
                        text_soc_1_7.setText(String.valueOf(soc_1_7));
                        //内阻
                        String battery_omega_ori_7 = Arg_msg.substring(10+36+(32*5), 14+36+(32*5));
                        float battery_omega_7 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_7) * 0.1);
                        DecimalFormat df_battery_omega_7 = new DecimalFormat("0.0");
                        text_battery_omega_7.setVisibility(View.VISIBLE);
                        text_battery_omega_7.setText(df_battery_omega_7.format(battery_omega_7));
                        //soh flag
                        String soh7_flag = Arg_msg.substring(34+36+(32*5), 36+36+(32*5));
                        //soh
                        String soh_ori_7 = Arg_msg.substring(8+36+(32*5), 10+36+(32*5));
                        int soh_7 = (int) (IntByteStringHexUtil.covert(soh_ori_7));
                        if(soh_7 > 100) soh_7 = 100;
                        text_soh_7.setVisibility(View.VISIBLE);
                        if(soh7_flag.equals("02")){
                            float soh7 = (PreviewActivity.battery_res / battery_omega_7) * 100;
                            text_soh_7.setText(String.valueOf((int)soh7) + "% R");
                        }else if(soh7_flag.equals("03")){
                            text_soh_7.setText(String.valueOf(soh_7) + "%");
                        }else{
                            text_soh_7.setText("----");
                        }
                        //容量
                        String out_ori_7 = Arg_msg.substring(20+36+(32*5), 24+36+(32*5));
                        float out_7 = (float) (IntByteStringHexUtil.covert(out_ori_7) * 0.1);
                        DecimalFormat df_out_7 = new DecimalFormat("0.00");//保留两位小数
                        text_out_7.setVisibility(View.VISIBLE);
                        text_out_7.setText(df_out_7.format(out_7));
                        //电压
                        String tip2_ori_7 = Arg_msg.substring(24+36+(32*5), 28+36+(32*5));
                        tip2_7 = (float) (IntByteStringHexUtil.covert(tip2_ori_7) * 0.001);
                        if(tip2_7 >= 10){
                            DecimalFormat df_v_7 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_7.setVisibility(View.VISIBLE);
                            text_tip2_7.setText(df_v_7.format(tip2_7));
                        }else {
                            DecimalFormat df_v_7 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_7.setVisibility(View.VISIBLE);
                            text_tip2_7.setText(df_v_7.format(tip2_7));
                        }
                        //电流
                        String tip3_ori_7 = Arg_msg.substring(28+36+(32*5), 32+36+(32*5));
                        tip3_7 = (float) (IntByteStringHexUtil.covert(tip3_ori_7) * 0.001);
                        if(tip3_7 >= 10){
                            DecimalFormat df_a_7 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_7.setVisibility(View.VISIBLE);
                            text_tip3_7.setText(df_a_7.format(tip3_7));
                        }else {
                            DecimalFormat df_a_7 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_7.setVisibility(View.VISIBLE);
                            text_tip3_7.setText(df_a_7.format(tip3_7));
                        }
                        //触点温度
                        String temp_ori_7 = Arg_msg.substring(32+36+(32*5), 34+36+(32*5));
                        String temp_7 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_7));
                        text_temp_7.setVisibility(View.VISIBLE);
                        text_temp_7.setText(temp_7);
                    }

                    //8台级联
                    if(jilian_num >= 8){
                        //设备序号
                        String num_ori_8 = Arg_msg.substring(4+36+(32*6), 6+36+(32*6));
                        int num_8 = (int) (IntByteStringHexUtil.covert(num_ori_8));
                        text_num_8.setVisibility(View.VISIBLE);
                        text_num_8.setText(String.valueOf(num_8));
                        String num_s_8 = Integer.toHexString(num_8);
                        id_8 = addZeroForNum(num_s_8, 2);
                        //soc
                        String soc_1_ori_8 = Arg_msg.substring(6+36+(32*6), 8+36+(32*6));
                        int soc_1_8 = (int) (IntByteStringHexUtil.covert(soc_1_ori_8));
                        text_soc_1_8.setVisibility(View.VISIBLE);
                        text_soc_1_8.setText(String.valueOf(soc_1_8));
                        //内阻
                        String battery_omega_ori_8 = Arg_msg.substring(10+36+(32*6), 12+36+(32*6));
                        float battery_omega_8 = (float) (IntByteStringHexUtil.covert(battery_omega_ori_8) * 0.1);
                        DecimalFormat df_battery_omega_8 = new DecimalFormat("0.0");
                        text_battery_omega_8.setVisibility(View.VISIBLE);
                        text_battery_omega_8.setText(df_battery_omega_8.format(battery_omega_8));
                        //soh flag
                        String soh8_flag = Arg_msg.substring(32+36+(32*6), 34+36+(32*6));
                        //soh
                        String soh_ori_8 = Arg_msg.substring(8+36+(32*6), 10+36+(32*6));
                        int soh_8 = (int) (IntByteStringHexUtil.covert(soh_ori_8));
                        if(soh_8 > 100) soh_8 = 100;
                        text_soh_8.setVisibility(View.VISIBLE);
                        if(soh8_flag.equals("02")){
                            float soh8 = (PreviewActivity.battery_res / battery_omega_8) * 100;
                            text_soh_8.setText(String.valueOf((int)soh8) + "% R");
                        }else if(soh8_flag.equals("03")){
                            text_soh_8.setText(String.valueOf(soh_8) + "%");
                        }else{
                            text_soh_8.setText("----");
                        }
                        //容量
                        String out_ori_8 = Arg_msg.substring(18+36+(32*6), 22+36+(32*6));
                        float out_8 = (float) (IntByteStringHexUtil.covert(out_ori_8) * 0.1);
                        DecimalFormat df_out_8 = new DecimalFormat("0.00");//保留两位小数
                        text_out_8.setVisibility(View.VISIBLE);
                        text_out_8.setText(df_out_8.format(out_8));
                        //电压
                        String tip2_ori_8 = Arg_msg.substring(22+36+(32*6), 26+36+(32*6));
                        tip2_8 = (float) (IntByteStringHexUtil.covert(tip2_ori_8) * 0.001);
                        if(tip2_8 >= 10){
                            DecimalFormat df_v_8 = new DecimalFormat("00.00");//保留两位小数
                            text_tip2_8.setVisibility(View.VISIBLE);
                            text_tip2_8.setText(df_v_8.format(tip2_8));
                        }else {
                            DecimalFormat df_v_8 = new DecimalFormat("0.000");//保留3位小数
                            text_tip2_8.setVisibility(View.VISIBLE);
                            text_tip2_8.setText(df_v_8.format(tip2_8));
                        }
                        //电流
                        String tip3_ori_8 = Arg_msg.substring(26+36+(32*6), 30+36+(32*6));
                        tip3_8 = (float) (IntByteStringHexUtil.covert(tip3_ori_8) * 0.001);
                        if(tip3_8 >= 10){
                            DecimalFormat df_a_8 = new DecimalFormat("00.0");//保留1位小数
                            text_tip3_8.setVisibility(View.VISIBLE);
                            text_tip3_8.setText(df_a_8.format(tip3_8));
                        }else {
                            DecimalFormat df_a_8 = new DecimalFormat("0.00");//保留两位小数
                            text_tip3_8.setVisibility(View.VISIBLE);
                            text_tip3_8.setText(df_a_8.format(tip3_8));
                        }
                        //触点温度
                        String temp_ori_8 = Arg_msg.substring(30+36+(32*6), 32+36+(32*6));
                        String temp_8 = String.valueOf(IntByteStringHexUtil.covert(temp_ori_8));
                        text_temp_8.setVisibility(View.VISIBLE);
                        text_temp_8.setText(temp_8);
                    }
                }
            }
        };
        handler.postDelayed(runnable, 500);//延时多长时间启动定时器
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

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InfoActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
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

    //曲线获取时间数据
    private void RequestsCurve_t(String id, int num) {
        if(num == 1){
            strArray_t_1 = new int [100];
        }else if(num == 2){
            strArray_t_2 = new int [100];
        }else if(num == 3){
            strArray_t_3 = new int [100];
        }else if(num == 4){
            strArray_t_4 = new int [100];
        }else if(num == 5){
            strArray_t_5 = new int [100];
        }else if(num == 6){
            strArray_t_6 = new int [100];
        }else if(num == 7){
            strArray_t_7 = new int [100];
        }else if(num == 8){
            strArray_t_8 = new int [100];
        }
        for (int i = 0; i < 2; i++) {
            String msg_t_i = addZeroForNum(String.valueOf(i + 1),2);
            String msg_t_i_hex = IntByteStringHexUtil.oneStrToHexStr(msg_t_i).toUpperCase();
            String msg_t = "0f" + id + msg_t_i_hex;
            String crc16 = CRC16.getCRC16(IntByteStringHexUtil.hexStrToByteArray(msg_t));
            String message_t = msg_t + crc16;
            try {
                sendMsg2Ble(message_t);
                Log.w("infocurve","message_t_send "+message_t);
            }catch (Exception e){
                Log.e("infocurve","message_t "+e.toString());
            }
            sleep(350);
            if(mainService.curve_msg_t != null){
                curve_msg_t = mainService.curve_msg_t;
                curve_msg_t_i = curve_msg_t.substring(4,6);
                Log.i("Info","message_t"+" "+curve_msg_t+" "+msg_t_i_hex+" "+curve_msg_t_i);
                int t_t = 0;
                while(!curve_msg_t_i.equals(msg_t_i_hex)){
                    t_t++;
                    if(t_t >= 60){
                        Log.e("Info","message_t return");
                        timeOutFlag = true;
                        return;
                    }
                    try {
                        sendMsg2Ble(message_t);
                        Log.i("Info","message_t "+message_t);
                    }catch (Exception e){
                        Log.e("Info","message_t "+e.toString());
                    }
                    curve_msg_t = mainService.curve_msg_t;
                    curve_msg_t_i = curve_msg_t.substring(4,6);
                    Log.e("Info","message_t错误！要再发一次"+" "+curve_msg_t+" "+msg_t_i_hex+" "+curve_msg_t_i);
                    sleep(300);
                }
                //把数据放进数组
                for (int i_1 = 0; i_1 < 50; i_1++) {
                    String temp_i = curve_msg_t.substring(4 * (i_1 + 1) + 2, 4 * (i_1 + 1) + 4 + 2);
                    int temp_i_1 = (int) (IntByteStringHexUtil.covert(temp_i));
                    if(num == 1){
                        strArray_t_1[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 2){
                        strArray_t_2[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 3){
                        strArray_t_3[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 4){
                        strArray_t_4[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 5){
                        strArray_t_5[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 6){
                        strArray_t_6[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 7){
                        strArray_t_7[(50 * i) + i_1] = temp_i_1;
                    }else if(num == 8){
                        strArray_t_8[(50 * i) + i_1] = temp_i_1;
                    }
                }
            }
            sleep(10);
        }
        if(num == 1){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_1));
        }else if(num == 2){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_2));
        }else if(num == 3){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_3));
        }else if(num == 4){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_4));
        }else if(num == 5){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_5));
        }else if(num == 6){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_6));
        }else if(num == 7){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_7));
        }else if(num == 8){
            Log.i("Info","message_t finally"+ id + Arrays.toString(strArray_t_8));
        }
    }
    //曲线获取电压数据
    private void RequestsCurve_v(String id, int num) {
        if(num == 1){
            strArray_v_1 = new Float [100];
        }else if(num == 2){
            strArray_v_2 = new Float [100];
        }else if(num == 3){
            strArray_v_3 = new Float [100];
        }else if(num == 4){
            strArray_v_4 = new Float [100];
        }else if(num == 5){
            strArray_v_5 = new Float [100];
        }else if(num == 6){
            strArray_v_6 = new Float [100];
        }else if(num == 7){
            strArray_v_7 = new Float [100];
        }else if(num == 8){
            strArray_v_8 = new Float [100];
        }
        for (int j = 0; j < 2; j++) {
            String msg_v_i = addZeroForNum(String.valueOf(j + 1),2);
            String msg_v_i_hex = IntByteStringHexUtil.oneStrToHexStr(msg_v_i).toUpperCase();
            String msg_v = "0e" + id + msg_v_i_hex;
            String crc16 = CRC16.getCRC16(IntByteStringHexUtil.hexStrToByteArray(msg_v));
            String message_v = msg_v + crc16;
            try {
                sendMsg2Ble(message_v);
                Log.w("infocurve","message_v_send "+message_v);
            }catch (Exception e){
                Log.e("Info","message_v "+e.toString());
            }
            sleep(350);
            if(mainService.curve_msg_v != null){
                curve_msg_v = mainService.curve_msg_v;
                curve_msg_v_i = curve_msg_v.substring(4,6);
                Log.i("Info","message_v"+" "+curve_msg_v+" "+msg_v_i_hex+" "+curve_msg_v_i);
                int t_v = 0;
                while(!curve_msg_v_i.equals(msg_v_i_hex)){
                    t_v++;
                    if(t_v >= 60){
                        Log.e("Info","message_v return");
                        timeOutFlag = true;
                        return;
                    }
                    try {
                        sendMsg2Ble(message_v);
                        Log.i("Info","message_v "+message_v);
                    }catch (Exception e){
                        Log.e("Info","message_v "+e.toString());
                    }
                    curve_msg_v = mainService.curve_msg_v;
                    curve_msg_v_i = curve_msg_v.substring(4,6);
                    Log.e("Info","message_v错误！要再发一次"+" "+curve_msg_v+" "+msg_v_i_hex+" "+curve_msg_v_i);
                    sleep(300);
                }
                //把数据放进数组
                for (int j_1 = 0; j_1 < 50; j_1++) {
                    String temp_j = curve_msg_v.substring(4 * (j_1 + 1) + 2, 4 * (j_1 + 1) + 4 + 2);
                    float temp_j_1 = (float) (IntByteStringHexUtil.covert(temp_j) * 0.001);
                    if(num == 1){
                        strArray_v_1[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 2){
                        strArray_v_2[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 3){
                        strArray_v_3[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 4){
                        strArray_v_4[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 5){
                        strArray_v_5[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 6){
                        strArray_v_6[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 7){
                        strArray_v_7[(50 * j) + j_1] = temp_j_1;
                    }else if(num == 8){
                        strArray_v_8[(50 * j) + j_1] = temp_j_1;
                    }
                }
            }
            sleep(10);
        }
        if(num == 1){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_1));
        }else if(num == 2){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_2));
        }else if(num == 3){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_3));
        }else if(num == 4){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_4));
        }else if(num == 5){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_5));
        }else if(num == 6){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_6));
        }else if(num == 7){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_7));
        }else if(num == 8){
            Log.i("Info","message_v finally"+ id + Arrays.toString(strArray_v_8));
        }
    }
    //曲线获取电流数据
    private void RequestsCurve_a(String id, int num) {
        if(num == 1){
            strArray_a_1 = new Float [100];
        }else if(num == 2){
            strArray_a_2 = new Float [100];
        }else if(num == 3){
            strArray_a_3 = new Float [100];
        }else if(num == 4){
            strArray_a_4 = new Float [100];
        }else if(num == 5){
            strArray_a_5 = new Float [100];
        }else if(num == 6){
            strArray_a_6 = new Float [100];
        }else if(num == 7){
            strArray_a_7 = new Float [100];
        }else if(num == 8){
            strArray_a_8 = new Float [100];
        }
        for (int k = 0; k < 2; k++) {
            String msg_a_i = addZeroForNum(String.valueOf(k + 1),2);
            String msg_a_i_hex = IntByteStringHexUtil.oneStrToHexStr(msg_a_i).toUpperCase();
            String msg_a = "10" + id + msg_a_i_hex;
            String crc16 = CRC16.getCRC16(IntByteStringHexUtil.hexStrToByteArray(msg_a));
            String message_a = msg_a + crc16;
            try {
                sendMsg2Ble(message_a);
                Log.w("infocurve","message_a_send"+message_a);
            }catch (Exception e){
                Log.e("Info","message_a"+e.toString());
            }
            sleep(350);
            if(mainService.curve_msg_a != null){
                curve_msg_a = mainService.curve_msg_a;
                curve_msg_a_i = curve_msg_a.substring(4,6);
                Log.i("Info","message_a "+" "+curve_msg_a+" "+msg_a_i_hex+" "+curve_msg_a_i);
                int t_a = 0;
                while(!curve_msg_a_i.equals(msg_a_i_hex)){
                    t_a++;
                    if(t_a >= 60){
                        Log.e("Info","message_t return");
                        timeOutFlag = true;
                        return;
                    }
                    try {
                        sendMsg2Ble(message_a);
                        Log.i("Info","message_a "+message_a);
                    }catch (Exception e){
                        Log.e("Info","message_a "+e.toString());
                    }
                    curve_msg_a = mainService.curve_msg_a;
                    curve_msg_a_i = curve_msg_a.substring(4,6);
                    Log.e("Info","message_a错误！要再发一次"+" "+curve_msg_a+" "+msg_a_i_hex+" "+curve_msg_a_i);
                    sleep(300);
                }
                //把数据放进数组
                for (int k_1 = 0; k_1 < 50; k_1++) {
                    String temp_k = curve_msg_a.substring(4 * (k_1 + 1) + 2, 4 * (k_1 + 1) + 4 + 2);
                    float temp_k_1 = (float) (IntByteStringHexUtil.covert(temp_k) * 0.001);
                    if(num == 1){
                        strArray_a_1[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 2){
                        strArray_a_2[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 3){
                        strArray_a_3[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 4){
                        strArray_a_4[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 5){
                        strArray_a_5[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 6){
                        strArray_a_6[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 7){
                        strArray_a_7[(50 * k) + k_1] = temp_k_1;
                    }else if(num == 8){
                        strArray_a_8[(50 * k) + k_1] = temp_k_1;
                    }
                }
            }
            sleep(10);
        }
        if(num == 1){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_1));
        }else if(num == 2){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_2));
        }else if(num == 3){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_3));
        }else if(num == 4){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_4));
        }else if(num == 5){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_5));
        }else if(num == 6){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_6));
        }else if(num == 7){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_7));
        }else if(num == 8){
            Log.i("Info","message_a finally"+ id + Arrays.toString(strArray_a_8));
        }
    }

    //测试用曲线
    private void RequestsCurve_t_1() {
        jilian_num = 2;
        strArray_t_1 = new int [100];
        strArray_t_2 = new int [100];
        for(int i = 0; i < 100; i++){
            strArray_t_1[i] = i;
            strArray_t_2[i] = (int) (i * 1.1);
        }
        Log.i("Info","message_t_1 finally"+" "+ Arrays.toString(strArray_t_1));
        Log.i("Info","message_t_2 finally"+" "+ Arrays.toString(strArray_t_2));
    }
    private void RequestsCurve_v_1() {
        strArray_v_1 = new Float [100];
        strArray_v_2 = new Float [100];
        for(int j = 0; j < 100; j++){
            strArray_v_1[j] = Float.valueOf((float) (0.001 * j * j) + 30);
            strArray_v_2[j] = Float.valueOf((float) (0.001 * j * j) + 25);
        }
        Log.i("Info","message_v_1 finally"+" "+ Arrays.toString(strArray_v_1));
        Log.i("Info","message_v_2 finally"+" "+ Arrays.toString(strArray_v_2));
    }
    private void RequestsCurve_a_1() {
        strArray_a_1 = new Float [100];
        strArray_a_2 = new Float [100];
        for(int k = 0; k < 100; k++){
            strArray_a_1[k] = Float.valueOf((float) (0.001 * k * k) + 2);
            strArray_a_2[k] = Float.valueOf((float) (0.001 * k * k) + 5);
        }
        Log.i("Info","message_a_1 finally"+" "+ Arrays.toString(strArray_a_1));
        Log.i("Info","message_a_2 finally"+" "+ Arrays.toString(strArray_a_2));
    }

//    private void ShowCurve_t(String id) {
//        if(!stopRequestFlag) {
//            RequestsCurve_t(id);
//            sleep(100);
//            if (strArray_t[0] == null && !timeOutFlag) {
//                RequestsCurve_t(id);
//            }
//            for (int i = 0; i < 100; i++) {
//                if (strArray_t[i] == null) {
//                    stopRequestFlag = true;
//                }else {
//                    stopRequestFlag = false;
//                }
//            }
//        }
//    }
    private void TestCurve() {
        RequestsCurve_t_1();
        RequestsCurve_v_1();
        RequestsCurve_a_1();
        Intent mIntent = new Intent();
        mIntent.putExtra("Function", Function);
        mIntent.setClass(InfoActivity.this, CurveActivity.class);
        startActivity(mIntent);
    }

    private void ShowCurve(String id, int num) {
        if(!stopRequestFlag) {
            RequestsCurve_t(id, num);
            sleep(100);
            RequestsCurve_v(id, num);
            sleep(100);
            RequestsCurve_a(id, num);
            sleep(100);
            if(num == 1) {
                if (strArray_v_1[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_1[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_1[i] == null || strArray_a_1[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 2) {
                if (strArray_v_2[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_2[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_2[i] == null || strArray_a_2[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 3) {
                if (strArray_v_3[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_3[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_3[i] == null || strArray_a_3[i] == null) {
                        stopRequestFlag = true;
                    }
                }
            }else if(num == 4) {
                if (strArray_v_4[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_4[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_4[i] == null || strArray_a_4[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 5) {
                if (strArray_v_5[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_5[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_5[i] == null || strArray_a_5[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 6) {
                if (strArray_v_6[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_6[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_6[i] == null || strArray_a_6[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 7) {
                if (strArray_v_7[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_7[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_7[i] == null || strArray_a_7[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }else if(num == 8) {
                if (strArray_v_8[0] == null && !timeOutFlag) {
                    RequestsCurve_v(id, num);
                }
                if (strArray_a_8[0] == null && !timeOutFlag) {
                    RequestsCurve_a(id, num);
                }
                for (int i = 0; i < 100; i++) {
                    if (strArray_v_8[i] == null || strArray_a_8[i] == null) {
                        stopRequestFlag = true;
                    }else {
                        stopRequestFlag = false;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bnt_onoff:
                if(working){
                    if(mainService.Sending){
                        sleep(150);
                        sendMsg2Ble("03000140");
                        Log.i("crash", "03000140冲突");
                    }else {
                        sendMsg2Ble("03000140");
                    }
                    working = false;
                    if(showChinese){
                        Bnt_onoff.setText("开始");
                    }else{
                        Bnt_onoff.setText("ON");
                    }
                    Bnt_onoff.setBackground(getResources().getDrawable(R.drawable.btn_shap_r));
                    showTip("ON");
                }else{
                    if(mainService.Sending){
                        sleep(150);
                        sendMsg2Ble("0301c080");
                    }else {
                        sendMsg2Ble("0301c080");
                    }
                    working = true;
                    if(showChinese){
                        Bnt_onoff.setText("暂停");
                    }else{
                        Bnt_onoff.setText("OFF");
                    }
                    Bnt_onoff.setBackground(getResources().getDrawable(R.drawable.btn_shap_p));
                    showTip("OFF");
                }
                break;
            case R.id.Bnt_show_curve:
//                TestCurve();

                if(mainService.page_msg != null){
                    String show_curve_flag = mainService.page_msg.substring(12, 14);
                    if (show_curve_flag.equals(temp_1)) {
                        showCurveFlag = false;
                    } else if (show_curve_flag.equals(temp_2)) {
                        showCurveFlag = true;
                    }
                }
                stopRequestFlag = false;
                timeOutFlag = false;
                BluetoothGattService service = BleClientActivity.getGattService(BleServerActivity.UUID_SERVICE);
                if (service != null) {
                    if (showCurveFlag || oneHourFlag) {
                        if (mainService.page_msg != null) {
                            //先让单片机显示曲线界面获取到数据
                            String page_msg_info = mainService.page_msg.substring(4, 6);
                            if (page_msg_info.equals(temp_3)) {
                                sendMsg2Ble("010502a291");
                                sleep(50);
                                sendMsg2Ble("010502a291");
                                sleep(50);
                                sendMsg2Ble("010502a291");
                            } else if (page_msg_info.equals(temp_4)) {
                                sendMsg2Ble("0105036351");
                                sleep(50);
                                sendMsg2Ble("0105036351");
                                sleep(50);
                                sendMsg2Ble("0105036351");
                            } else if (page_msg_info.equals(temp_5)) {
                                sendMsg2Ble("0105042293");
                                sleep(50);
                                sendMsg2Ble("0105042293");
                                sleep(50);
                                sendMsg2Ble("0105042293");
                            }
                        }
                        if (!isShowing) {
                            ProgressDialog dialog_curve = new ProgressDialog(this); //1.创建一个ProgressDialog的实例
                            if (showChinese) {
                                dialog_curve.setMessage("获取曲线数据中，请稍等...");
                            } else {
                                dialog_curve.setMessage("Obtain curve data, please wait a moment...");
                            }
                            dialog_curve.show();        //5.将ProgessDialog显示出来
                            new Thread(() -> {
                                isShowing = true;
                                if (jilian_num == 1) {
                                    ShowCurve(id_1, 1);
                                } else if (jilian_num == 2) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                } else if (jilian_num == 3) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                } else if (jilian_num == 4) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                    ShowCurve(id_4, 4);
                                } else if (jilian_num == 5) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                    ShowCurve(id_4, 4);
                                    ShowCurve(id_5, 5);
                                } else if (jilian_num == 6) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                    ShowCurve(id_4, 4);
                                    ShowCurve(id_5, 5);
                                    ShowCurve(id_6, 6);
                                } else if (jilian_num == 7) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                    ShowCurve(id_4, 4);
                                    ShowCurve(id_5, 5);
                                    ShowCurve(id_6, 6);
                                    ShowCurve(id_7, 7);
                                } else if (jilian_num == 8) {
                                    ShowCurve(id_1, 1);
                                    ShowCurve(id_2, 2);
                                    ShowCurve(id_3, 3);
                                    ShowCurve(id_4, 4);
                                    ShowCurve(id_5, 5);
                                    ShowCurve(id_6, 6);
                                    ShowCurve(id_7, 7);
                                    ShowCurve(id_8, 8);
                                }
                                isShowing = false;
                                dialog_curve.dismiss();
                                if (!stopRequestFlag) {
                                    sleep(50);
                                    Intent mIntent = new Intent();
                                    mIntent.putExtra("Function", Function);
                                    mIntent.setClass(InfoActivity.this, CurveActivity.class);
                                    startActivity(mIntent);
                                } else {
                                    if (showChinese) {
                                        showTip("曲线数据接收有误，请重试");
                                    } else {
                                        showTip("Error curve data received, please try again");
                                    }
                                    if (Function == 2) {
                                        sendMsg2Ble("010402a301");
                                        sleep(50);
                                        sendMsg2Ble("010402a301");
                                        sleep(50);
                                        sendMsg2Ble("010402a301");
                                    } else if (Function == 3) {
                                        sendMsg2Ble("01040362c1");
                                        sleep(50);
                                        sendMsg2Ble("01040362c1");
                                        sleep(50);
                                        sendMsg2Ble("01040362c1");
                                    } else if (Function == 4) {
                                        sendMsg2Ble("0104042303");
                                        sleep(50);
                                        sendMsg2Ble("0104042303");
                                        sleep(50);
                                        sendMsg2Ble("0104042303");
                                    }
                                }
                            }).start();
                        } else {
                            if (showChinese) {
                                showTip("正在获取曲线，请稍等...");
                            } else {
                                showTip("btaining curves, please wait...");
                            }
                        }
                    } else {
                        String tips_msg = mainService.page_msg;
                        if (tips_msg != null) {
                            String tips_msg_1 = mainService.page_msg.substring(4, 6);
                            if (tips_msg_1.equals("02")) {
                                if (showChinese) {
                                    showTip("非完整充电过程，无法测定充电曲线");
                                } else {
                                    showTip("Incomplete charging process, unable to determine the charging curve");
                                }
                            } else if (tips_msg_1.equals("03")) {
                                if (showChinese) {
                                    showTip("非完整放电过程，无法测定放电曲线");
                                } else {
                                    showTip("Incomplete discharge process, unable to determine the discharge curve");
                                }
                            }
                        }
                    }
                }else{
                    if (showChinese) {
                        showTip("请连接蓝牙");
                    } else {
                        showTip("Please connect Bluetooth");
                    }
                }
                break;
            case R.id.Bnt_return:
                finish();
                break;

            default:
                break;
        }
    }

//    //动态申请权限
//    public static boolean isGrantExternalRW(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            activity.requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            }, 1);
//            return false;
//        }
//        return true;
//    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);

        sendMsg2Ble("010301e130");
        sleep(50);
        sendMsg2Ble("010301e130");
        sleep(50);
        sendMsg2Ble("010301e130");

        sleep(100);
        super.onDestroy();
    }
}



//                    //将曲线数据写进内存log日志中
//                    Date date = new Date();
//                    String time = date.toLocaleString();
//                    Log.i("md", "时间time为： "+time);
//                    String info_log = time + " " +  "\n"
//                            + "电池：" + battLog + " "
//                            + "电压：" + vLog + " "
//                            + "容量：" + ahLog+ " "
//                            + "温度：" + tempLog + "°C" + "\n"
//                            + "曲线时间：";
//                    for(int i = 0; i<100; i++) {
//                        info_log = info_log + " " + strArray_t[i];
//                    }
//                    info_log = info_log  +  "\n" + "曲线电压：";
//                    for(int j = 0; j<100; j++) {
//                        info_log = info_log + " " + strArray_v[j];
//                    }
//                    info_log = info_log  +  "\n" + "曲线电流：";
//                    for(int k = 0; k<100; k++) {
//                        info_log = info_log + " " + strArray_a[k];
//                    }
//                    info_log = info_log + "\n";
//                    FileUtils.writeTxtToFile(info_log, "/storage/emulated/0/GreenLog/", "GreenLog.txt");