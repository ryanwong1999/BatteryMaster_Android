package com.example.greenbetterymaster;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenbetterymaster.ble.BleClientActivity;
import com.example.greenbetterymaster.ble.BleServerActivity;
import com.example.greenbetterymaster.util.IntByteStringHexUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;


public class CurveActivity extends AppCompatActivity implements View.OnClickListener{
    int Function = 0;
    private Button Bnt_return;
    private LineChart lineChart;
    private TextView device_id;
    private LinearLayout color_1;
    private LinearLayout color_2;
    private LinearLayout color_3;
    private LinearLayout color_4;
    private LinearLayout color_5;
    private LinearLayout color_6;
    private LinearLayout color_7;
    private LinearLayout color_8;
    private TextView color_1_1;
    private TextView color_2_1;
    private TextView color_3_1;
    private TextView color_4_1;
    private TextView color_5_1;
    private TextView color_6_1;
    private TextView color_7_1;
    private TextView color_8_1;
    //时间数组
    int[] strArray_t_1;
    int[] strArray_t_2;
    int[] strArray_t_3;
    int[] strArray_t_4;
    int[] strArray_t_5;
    int[] strArray_t_6;
    int[] strArray_t_7;
    int[] strArray_t_8;
    //电压数组
    Float [] strArray_v_1;
    Float [] strArray_v_2;
    Float [] strArray_v_3;
    Float [] strArray_v_4;
    Float [] strArray_v_5;
    Float [] strArray_v_6;
    Float [] strArray_v_7;
    Float [] strArray_v_8;
    //电流数组
    Float [] strArray_a_1;
    Float [] strArray_a_2;
    Float [] strArray_a_3;
    Float [] strArray_a_4;
    Float [] strArray_a_5;
    Float [] strArray_a_6;
    Float [] strArray_a_7;
    Float [] strArray_a_8;
    int i_jilian_num;
    private float max_v;
    private float min_v;
    private float max_a;
    private float min_a;

//    private int max_t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve);

        InitMenuShow();

        Intent intent = getIntent();
        Function = intent.getIntExtra("Function", Function);

        strArray_t_1 = InfoActivity.strArray_t_1;
        strArray_t_2 = InfoActivity.strArray_t_2;
        strArray_t_3 = InfoActivity.strArray_t_3;
        strArray_t_4 = InfoActivity.strArray_t_4;
        strArray_t_5 = InfoActivity.strArray_t_5;
        strArray_t_6 = InfoActivity.strArray_t_6;
        strArray_t_7 = InfoActivity.strArray_t_7;
        strArray_t_8 = InfoActivity.strArray_t_8;
        strArray_v_1 = InfoActivity.strArray_v_1;
        strArray_v_2 = InfoActivity.strArray_v_2;
        strArray_v_3 = InfoActivity.strArray_v_3;
        strArray_v_4 = InfoActivity.strArray_v_4;
        strArray_v_5 = InfoActivity.strArray_v_5;
        strArray_v_6 = InfoActivity.strArray_v_6;
        strArray_v_7 = InfoActivity.strArray_v_7;
        strArray_v_8 = InfoActivity.strArray_v_8;
        strArray_a_1 = InfoActivity.strArray_a_1;
        strArray_a_2 = InfoActivity.strArray_a_2;
        strArray_a_3 = InfoActivity.strArray_a_3;
        strArray_a_4 = InfoActivity.strArray_a_4;
        strArray_a_5 = InfoActivity.strArray_a_5;
        strArray_a_6 = InfoActivity.strArray_a_6;
        strArray_a_7 = InfoActivity.strArray_a_7;
        strArray_a_8 = InfoActivity.strArray_a_8;
        i_jilian_num = InfoActivity.jilian_num;

        setDataShowColor();
        setAxisRange();
        setLenged();
    }

    private void InitMenuShow() {
        lineChart = findViewById(R.id.curveView_mp);
        Bnt_return = (Button) findViewById(R.id.Bnt_return);
        Bnt_return.setOnClickListener(this);
        color_1 = (LinearLayout) findViewById(R.id.color_1);
        color_2 = (LinearLayout) findViewById(R.id.color_2);
        color_3 = (LinearLayout) findViewById(R.id.color_3);
        color_4 = (LinearLayout) findViewById(R.id.color_4);
        color_5 = (LinearLayout) findViewById(R.id.color_5);
        color_6 = (LinearLayout) findViewById(R.id.color_6);
        color_7 = (LinearLayout) findViewById(R.id.color_7);
        color_8 = (LinearLayout) findViewById(R.id.color_8);
        color_1_1 = (TextView) findViewById(R.id.color_1_1);
        color_2_1 = (TextView) findViewById(R.id.color_2_1);
        color_3_1 = (TextView) findViewById(R.id.color_3_1);
        color_4_1 = (TextView) findViewById(R.id.color_4_1);
        color_5_1 = (TextView) findViewById(R.id.color_5_1);
        color_6_1 = (TextView) findViewById(R.id.color_6_1);
        color_7_1 = (TextView) findViewById(R.id.color_7_1);
        color_8_1 = (TextView) findViewById(R.id.color_8_1);
    }

//    int max_arr[][] = new int[][]{strArray_t_1, strArray_t_2, strArray_t_3, strArray_t_4,
//            strArray_t_5, strArray_t_6, strArray_t_7, strArray_t_8};
    private void setDataShowColor() {
        String name_1 = "vol/v", name_2 = "cur/a";
        if(i_jilian_num == 1){
            setData(name_1, name_2,1);
            int max_t = getMax_t(1);
            setxAxis(max_t);
            color_1_1.setText(InfoActivity.id_1);
        }else if(i_jilian_num == 2){
            int max_t = getMax_t(2);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,2);
            color_2.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
        }else if(i_jilian_num == 3){
            int max_t = getMax_t(3);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,3);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
        }else if(i_jilian_num == 4){
            int max_t = getMax_t(4);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3 || InfoActivity.tip2_4 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,4);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_4.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
            color_4_1.setText(InfoActivity.id_4);
        }else if(i_jilian_num == 5){
            int max_t = getMax_t(5);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3 || InfoActivity.tip2_4 <= 0.3
                    || InfoActivity.tip2_5 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,5);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_4.setVisibility(View.VISIBLE);
            color_5.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
            color_4_1.setText(InfoActivity.id_4);
            color_5_1.setText(InfoActivity.id_5);
        }else if(i_jilian_num == 6){
            int max_t = getMax_t(6);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3 || InfoActivity.tip2_4 <= 0.3
                    || InfoActivity.tip2_5 <= 0.3 || InfoActivity.tip2_6 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,6);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_4.setVisibility(View.VISIBLE);
            color_5.setVisibility(View.VISIBLE);
            color_6.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
            color_4_1.setText(InfoActivity.id_4);
            color_5_1.setText(InfoActivity.id_5);
            color_6_1.setText(InfoActivity.id_6);
        }else if(i_jilian_num == 7){
            int max_t = getMax_t(7);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3 || InfoActivity.tip2_4 <= 0.3
                    || InfoActivity.tip2_5 <= 0.3 || InfoActivity.tip2_6 <= 0.3 || InfoActivity.tip2_7 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,7);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_4.setVisibility(View.VISIBLE);
            color_5.setVisibility(View.VISIBLE);
            color_6.setVisibility(View.VISIBLE);
            color_7.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
            color_4_1.setText(InfoActivity.id_4);
            color_5_1.setText(InfoActivity.id_5);
            color_6_1.setText(InfoActivity.id_6);
            color_7_1.setText(InfoActivity.id_7);
        }else if(i_jilian_num == 8){
            int max_t = getMax_t(8);
            setxAxis(max_t);
            if(InfoActivity.tip2 <= 0.3 || InfoActivity.tip2_2 <= 0.3 || InfoActivity.tip2_3 <= 0.3 || InfoActivity.tip2_4 <= 0.3
                    || InfoActivity.tip2_5 <= 0.3 || InfoActivity.tip2_6 <= 0.3 || InfoActivity.tip2_7 <= 0.3 || InfoActivity.tip2_8 <= 0.3) {
                reset_arr(i_jilian_num, max_t);     //0.3A判断
            }
            setData(name_1, name_2,8);
            color_2.setVisibility(View.VISIBLE);
            color_3.setVisibility(View.VISIBLE);
            color_4.setVisibility(View.VISIBLE);
            color_5.setVisibility(View.VISIBLE);
            color_6.setVisibility(View.VISIBLE);
            color_7.setVisibility(View.VISIBLE);
            color_8.setVisibility(View.VISIBLE);
            color_1_1.setText(InfoActivity.id_1);
            color_2_1.setText(InfoActivity.id_2);
            color_3_1.setText(InfoActivity.id_3);
            color_4_1.setText(InfoActivity.id_4);
            color_5_1.setText(InfoActivity.id_5);
            color_6_1.setText(InfoActivity.id_6);
            color_7_1.setText(InfoActivity.id_7);
            color_8_1.setText(InfoActivity.id_8);
        }
    }

    private void reset_arr(int num, int max_t) {
        int[][] max_arr_t = new int[][]{strArray_t_1, strArray_t_2, strArray_t_3, strArray_t_4, strArray_t_5, strArray_t_6, strArray_t_7, strArray_t_8};
        Float[][] max_arr_v = new Float[][]{strArray_v_1, strArray_v_2, strArray_v_3, strArray_v_4, strArray_v_5, strArray_v_6, strArray_v_7, strArray_v_8};
        Float[][] max_arr_a = new Float[][]{strArray_a_1, strArray_a_2, strArray_a_3, strArray_a_4, strArray_a_5, strArray_a_6, strArray_a_7, strArray_a_8};
        for(int i = 0; i < num - 1; i++){   //
            float bili =  (float) (max_arr_t[max_t - 1][99]) / (float)(max_arr_t[i][99]);   //大比小
            Log.w("curve"+i,max_arr_t[i][99] + " " + max_arr_t[max_t - 1][99]
                    + " " + max_arr_v[i][99] + " " + max_arr_a[i][99]);
            if(bili != 1){
                Log.w("curve"+i,"时间轴不一样"+ bili);
                Log.w("curve"+i, Arrays.toString(max_arr_v[i]));
                Log.w("curve"+i, Arrays.toString(max_arr_a[i]));
                int bili_100 = (int) (100 / bili);
                for(int j = 0; j < 100; j++){
                    if(j <= bili_100){
                        max_arr_v[i][j] = max_arr_v[i][(int) (j * bili)];
                        max_arr_a[i][j] = max_arr_a[i][(int) (j * bili)];
                    }else{
                        max_arr_a[i][j] = (float) 0;
                        if(i == 0){
                            max_arr_v[i][j] = InfoActivity.tip2;
                        }if(i == 1){
                            max_arr_v[i][j] = InfoActivity.tip2_2;
                        }if(i == 2){
                            max_arr_v[i][j] = InfoActivity.tip2_3;
                        }if(i == 3){
                            max_arr_v[i][j] = InfoActivity.tip2_4;
                        }if(i == 4){
                            max_arr_v[i][j] = InfoActivity.tip2_5;
                        }if(i == 5){
                            max_arr_v[i][j] = InfoActivity.tip2_6;
                        }if(i == 6){
                            max_arr_v[i][j] = InfoActivity.tip2_7;
                        }if(i == 7){
                            max_arr_v[i][j] = InfoActivity.tip2_8;
                        }else {
                            max_arr_v[i][j] = max_arr_v[i][99];
                        }
                    }
                }
                Log.w("curve"+i, Arrays.toString(max_arr_v[i]));
                Log.w("curve"+i, Arrays.toString(max_arr_a[i]));
            }
        }
    }

    private int getMax_t(int num) {
//        int max_t[] = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99],
//                strArray_t_5[99], strArray_t_6[99], strArray_t_7[99], strArray_t_8[99]};
        int max_t_num = 1;
        if (num == 2) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i+1;
                }
            }
        }else if (num == 3) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }else if (num == 4) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }else if (num == 5) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99],
                    strArray_t_5[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }else if (num == 6) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99],
                    strArray_t_5[99], strArray_t_6[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }else if (num == 7) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99],
                    strArray_t_5[99], strArray_t_6[99], strArray_t_7[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }else if (num == 8) {
            int[] max_t = new int[]{strArray_t_1[99], strArray_t_2[99], strArray_t_3[99], strArray_t_4[99],
                    strArray_t_5[99], strArray_t_6[99], strArray_t_7[99], strArray_t_8[99]};
            for (int i = 0; i < max_t.length; i++) {
                if (max_t[i] > max_a) {
                    max_a = max_t[i];
                    max_t_num = i + 1;
                }
            }
        }
        return max_t_num;
    }

    private void setxAxis(int num) {
        ArrayList<String> xvalue=new ArrayList<>();//x轴时间
        if(num == 1){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_1[i]));
            }
        }else if(num == 2){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_2[i]));
            }
        }else if(num == 3){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_3[i]));
            }
        }else if(num == 4){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_4[i]));
            }
        }else if(num == 5){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_5[i]));
            }
        }else if(num == 6){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_6[i]));
            }
        }else if(num == 7){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_7[i]));
            }
        }else if(num == 8){
            for (int i = 0; i < 100; i++) {
                xvalue.add(String.valueOf(strArray_t_8[i]));
            }
        }
        //获取此图表的x轴
        XAxis xAxis = lineChart.getXAxis();
        //设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setEnabled(true);
        //是否绘制轴线
        xAxis.setDrawAxisLine(true);
        //设置x轴上每个点对应的线
        xAxis.setDrawGridLines(true);
        //绘制标签  指x轴上的对应数值
        xAxis.setDrawLabels(true);
        //设置x轴的显示位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //让x轴上自定义的值和折线上相对应
        xAxis.setGranularity(1);
        //设置横轴线的颜色
        xAxis.setAxisLineColor(Color.WHITE);
        //设置横轴字体颜色
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(13f);
        //设置x轴标签数，默认为6个
        xAxis.setLabelCount(8);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xvalue.get((int) value);
            }
        });
    }

    private void setData(String name1,String name2,int num) {
        if(num == 1){
            //这里随机数了两个集合
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
            }
            //这里两条线
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            //创建一个数据集
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            LineData data = new LineData(dataSets);
            //设置数据
            lineChart.setData(data);
        }else if(num == 2){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 3){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,Color.BLUE);
            setLine_l(set1_3,Color.BLUE);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 4){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            ArrayList <Entry> list_a_4=new ArrayList<>();
            ArrayList <Entry> list_v_4=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
                list_a_4.add(new Entry(i, strArray_v_4[i]));
                list_v_4.add(new Entry(i, strArray_a_4[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            LineDataSet set1_4 = new LineDataSet(list_a_4, name1);
            LineDataSet set2_4 = new LineDataSet(list_v_4, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,Color.BLUE);
            setLine_l(set1_3,Color.BLUE);
            setLine_r(set2_4,Color.GREEN);
            setLine_l(set1_4,Color.GREEN);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            dataSets.add(set1_4);
            dataSets.add(set2_4);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 5){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            ArrayList <Entry> list_a_4=new ArrayList<>();
            ArrayList <Entry> list_v_4=new ArrayList<>();
            ArrayList <Entry> list_a_5=new ArrayList<>();
            ArrayList <Entry> list_v_5=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
                list_a_4.add(new Entry(i, strArray_v_4[i]));
                list_v_4.add(new Entry(i, strArray_a_4[i]));
                list_a_5.add(new Entry(i, strArray_v_5[i]));
                list_v_5.add(new Entry(i, strArray_a_5[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            LineDataSet set1_4 = new LineDataSet(list_a_4, name1);
            LineDataSet set2_4 = new LineDataSet(list_v_4, name2);
            LineDataSet set1_5 = new LineDataSet(list_a_5, name1);
            LineDataSet set2_5 = new LineDataSet(list_v_5, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,Color.BLUE);
            setLine_l(set1_3,Color.BLUE);
            setLine_r(set2_4,Color.GREEN);
            setLine_l(set1_4,Color.GREEN);
            setLine_r(set2_5,Color.CYAN);
            setLine_l(set1_5,Color.CYAN);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            dataSets.add(set1_4);
            dataSets.add(set2_4);
            dataSets.add(set1_5);
            dataSets.add(set2_5);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 6){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            ArrayList <Entry> list_a_4=new ArrayList<>();
            ArrayList <Entry> list_v_4=new ArrayList<>();
            ArrayList <Entry> list_a_5=new ArrayList<>();
            ArrayList <Entry> list_v_5=new ArrayList<>();
            ArrayList <Entry> list_a_6=new ArrayList<>();
            ArrayList <Entry> list_v_6=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
                list_a_4.add(new Entry(i, strArray_v_4[i]));
                list_v_4.add(new Entry(i, strArray_a_4[i]));
                list_a_5.add(new Entry(i, strArray_v_5[i]));
                list_v_5.add(new Entry(i, strArray_a_5[i]));
                list_a_6.add(new Entry(i, strArray_v_6[i]));
                list_v_6.add(new Entry(i, strArray_a_6[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            LineDataSet set1_4 = new LineDataSet(list_a_4, name1);
            LineDataSet set2_4 = new LineDataSet(list_v_4, name2);
            LineDataSet set1_5 = new LineDataSet(list_a_5, name1);
            LineDataSet set2_5 = new LineDataSet(list_v_5, name2);
            LineDataSet set1_6 = new LineDataSet(list_a_6, name1);
            LineDataSet set2_6 = new LineDataSet(list_v_6, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,Color.BLUE);
            setLine_l(set1_3,Color.BLUE);
            setLine_r(set2_4,Color.GREEN);
            setLine_l(set1_4,Color.GREEN);
            setLine_r(set2_5,Color.CYAN);
            setLine_l(set1_5,Color.CYAN);
            setLine_r(set2_6,0xffff00ff);
            setLine_l(set1_6,0xffff00ff);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            dataSets.add(set1_4);
            dataSets.add(set2_4);
            dataSets.add(set1_5);
            dataSets.add(set2_5);
            dataSets.add(set1_6);
            dataSets.add(set2_6);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 7){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            ArrayList <Entry> list_a_4=new ArrayList<>();
            ArrayList <Entry> list_v_4=new ArrayList<>();
            ArrayList <Entry> list_a_5=new ArrayList<>();
            ArrayList <Entry> list_v_5=new ArrayList<>();
            ArrayList <Entry> list_a_6=new ArrayList<>();
            ArrayList <Entry> list_v_6=new ArrayList<>();
            ArrayList <Entry> list_a_7=new ArrayList<>();
            ArrayList <Entry> list_v_7=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
                list_a_4.add(new Entry(i, strArray_v_4[i]));
                list_v_4.add(new Entry(i, strArray_a_4[i]));
                list_a_5.add(new Entry(i, strArray_v_5[i]));
                list_v_5.add(new Entry(i, strArray_a_5[i]));
                list_a_6.add(new Entry(i, strArray_v_6[i]));
                list_v_6.add(new Entry(i, strArray_a_6[i]));
                list_a_7.add(new Entry(i, strArray_v_7[i]));
                list_v_7.add(new Entry(i, strArray_a_7[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            LineDataSet set1_4 = new LineDataSet(list_a_4, name1);
            LineDataSet set2_4 = new LineDataSet(list_v_4, name2);
            LineDataSet set1_5 = new LineDataSet(list_a_5, name1);
            LineDataSet set2_5 = new LineDataSet(list_v_5, name2);
            LineDataSet set1_6 = new LineDataSet(list_a_6, name1);
            LineDataSet set2_6 = new LineDataSet(list_v_6, name2);
            LineDataSet set1_7 = new LineDataSet(list_a_7, name1);
            LineDataSet set2_7 = new LineDataSet(list_v_7, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,Color.BLUE);
            setLine_l(set1_3,Color.BLUE);
            setLine_r(set2_4,Color.GREEN);
            setLine_l(set1_4,Color.GREEN);
            setLine_r(set2_5,Color.CYAN);
            setLine_l(set1_5,Color.CYAN);
            setLine_r(set2_6,0xffff00ff);
            setLine_l(set1_6,0xffff00ff);
            setLine_r(set2_7,0xff3700b3);
            setLine_l(set1_7,0xff3700b3);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            dataSets.add(set1_4);
            dataSets.add(set2_4);
            dataSets.add(set1_5);
            dataSets.add(set2_5);
            dataSets.add(set1_6);
            dataSets.add(set2_6);
            dataSets.add(set1_7);
            dataSets.add(set2_7);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }else if(num == 8){
            ArrayList <Entry> list_a=new ArrayList<>();
            ArrayList <Entry> list_v=new ArrayList<>();
            ArrayList <Entry> list_a_2=new ArrayList<>();
            ArrayList <Entry> list_v_2=new ArrayList<>();
            ArrayList <Entry> list_a_3=new ArrayList<>();
            ArrayList <Entry> list_v_3=new ArrayList<>();
            ArrayList <Entry> list_a_4=new ArrayList<>();
            ArrayList <Entry> list_v_4=new ArrayList<>();
            ArrayList <Entry> list_a_5=new ArrayList<>();
            ArrayList <Entry> list_v_5=new ArrayList<>();
            ArrayList <Entry> list_a_6=new ArrayList<>();
            ArrayList <Entry> list_v_6=new ArrayList<>();
            ArrayList <Entry> list_a_7=new ArrayList<>();
            ArrayList <Entry> list_v_7=new ArrayList<>();
            ArrayList <Entry> list_a_8=new ArrayList<>();
            ArrayList <Entry> list_v_8=new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list_a.add(new Entry(i, strArray_v_1[i]));
                list_v.add(new Entry(i, strArray_a_1[i]));
                list_a_2.add(new Entry(i, strArray_v_2[i]));
                list_v_2.add(new Entry(i, strArray_a_2[i]));
                list_a_3.add(new Entry(i, strArray_v_3[i]));
                list_v_3.add(new Entry(i, strArray_a_3[i]));
                list_a_4.add(new Entry(i, strArray_v_4[i]));
                list_v_4.add(new Entry(i, strArray_a_4[i]));
                list_a_5.add(new Entry(i, strArray_v_5[i]));
                list_v_5.add(new Entry(i, strArray_a_5[i]));
                list_a_6.add(new Entry(i, strArray_v_6[i]));
                list_v_6.add(new Entry(i, strArray_a_6[i]));
                list_a_7.add(new Entry(i, strArray_v_7[i]));
                list_v_7.add(new Entry(i, strArray_a_7[i]));
                list_a_8.add(new Entry(i, strArray_v_8[i]));
                list_v_8.add(new Entry(i, strArray_a_8[i]));
            }
            LineDataSet set1 = new LineDataSet(list_a, name1);
            LineDataSet set2 = new LineDataSet(list_v, name2);
            LineDataSet set1_2 = new LineDataSet(list_a_2, name1);
            LineDataSet set2_2 = new LineDataSet(list_v_2, name2);
            LineDataSet set1_3 = new LineDataSet(list_a_3, name1);
            LineDataSet set2_3 = new LineDataSet(list_v_3, name2);
            LineDataSet set1_4 = new LineDataSet(list_a_4, name1);
            LineDataSet set2_4 = new LineDataSet(list_v_4, name2);
            LineDataSet set1_5 = new LineDataSet(list_a_5, name1);
            LineDataSet set2_5 = new LineDataSet(list_v_5, name2);
            LineDataSet set1_6 = new LineDataSet(list_a_6, name1);
            LineDataSet set2_6 = new LineDataSet(list_v_6, name2);
            LineDataSet set1_7 = new LineDataSet(list_a_7, name1);
            LineDataSet set2_7 = new LineDataSet(list_v_7, name2);
            LineDataSet set1_8 = new LineDataSet(list_a_8, name1);
            LineDataSet set2_8 = new LineDataSet(list_v_8, name2);
            setLine_r(set2,Color.YELLOW);
            setLine_l(set1,Color.YELLOW);
            setLine_r(set2_2,Color.RED);
            setLine_l(set1_2,Color.RED);
            setLine_r(set2_3,0x0000FF);
            setLine_l(set1_3,0x0000FF);
            setLine_r(set2_4,0x66CC00);
            setLine_l(set1_4,0x66CC00);
            setLine_r(set2_5,0x6699FF);
            setLine_l(set1_5,0x6699FF);
            setLine_r(set2_6,0xFF00FF);
            setLine_l(set1_6,0xFF00FF);
            setLine_r(set2_7,0xFF6600);
            setLine_l(set1_7,0xFF6600);
            setLine_r(set2_8,0x99FFCC);
            setLine_l(set1_8,0x99FFCC);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set1_2);
            dataSets.add(set2_2);
            dataSets.add(set1_3);
            dataSets.add(set2_3);
            dataSets.add(set1_4);
            dataSets.add(set2_4);
            dataSets.add(set1_5);
            dataSets.add(set2_5);
            dataSets.add(set1_6);
            dataSets.add(set2_6);
            dataSets.add(set1_7);
            dataSets.add(set2_7);
            dataSets.add(set1_8);
            dataSets.add(set2_8);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }

    }

    private void setLine_l(LineDataSet set, int color) {
        //设置线条的颜色
        set.setColor(color);
        //虚线模式下绘制直线
//        set.enableDashedLine(20f, 5f, 0f);
        //点击后高亮线的显示颜色
        set.enableDashedHighlightLine(50f, 15f, 0f);
        //平滑
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //设置数据小圆点的颜色
        set.setCircleColor(color);
        //设置圆点是否有空心
        set.setDrawCircles(false);
        //设置线条的宽度，最大10f,最小0.2f
        set.setLineWidth(5f);
        //设置小圆点的半径，最小1f，默认4f
        set.setCircleRadius(1f);
        //设置是否显示小圆点
        set.setDrawCircles(false);
        //设置字体颜色
        set.setValueTextColor(Color.WHITE);
        //设置字体大小
        set.setValueTextSize(15f);
        //设置是否填充
        set.setDrawFilled(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
    }

    private void setLine_r(LineDataSet set, int color) {
        set.setColor(color);
//        set.enableDashedLine(20f, 5f, 0f);
        set.enableDashedHighlightLine(50f, 15f, 0f);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setCircleColor(color);
        set.setDrawCircles(false);
        set.setLineWidth(1.5f);
        set.setCircleRadius(1f);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(15f);
        set.setDrawFilled(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
    }

    private void setAxisRange(){
        //获取电压数组最大最小值
        max_v = strArray_v_1[0];
        min_v = strArray_v_1[0];
        for (int i_1 = 0; i_1 < strArray_v_1.length; i_1++){
            if (strArray_v_1[i_1] > max_v){
                max_v = strArray_v_1[i_1];

            }if (strArray_v_1[i_1] < min_v){
                min_v = strArray_v_1[i_1];
            }
        }
        //获取电流数组最大最小值
        max_a = strArray_a_1[0];
        min_a = strArray_a_1[0];
        for ( int i_2 = 0; i_2 < strArray_a_1.length; i_2++){
            if (strArray_a_1[i_2] > max_a){
                max_a = strArray_a_1[i_2];

            }if (strArray_a_1[i_2] < min_a){
                min_a = strArray_a_1[i_2];
            }
        }

        //设置右边y轴
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);//设置颜
        rightAxis.setTextSize(15f);
        rightAxis.setAxisMaximum(max_a * 11/10);//设置最大值
        rightAxis.setAxisMinimum(0);//设置最小值
        rightAxis.setDrawGridLines(false);//是否绘制网格
        rightAxis.setDrawZeroLine(true);//是否绘制0刻度线
        rightAxis.setGranularityEnabled(false);//等间距
        rightAxis.setGridColor(Color.GRAY);
        rightAxis.setEnabled(true);
        //设置左边y轴
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(15f);
        leftAxis.setAxisMaximum(max_v * 21/20); //11/10
        leftAxis.setAxisMinimum(min_v * 19/20); //9/10
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(false);//是否绘制0刻度线
        leftAxis.setGridColor(Color.GRAY);
        leftAxis.setEnabled(true);
    }

    private void setLenged(){
        Legend legend=lineChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(20f);
        //设置图例垂直对齐
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //设置图例居中
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //设置图例方向
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //设置图例是在图内还是图外绘制
        legend.setDrawInside(false);
        //图例条目之间距离
        legend.setXEntrySpace(10f);
        //设置图例可否换行
        legend.setWordWrapEnabled(true);
        //设置图例现状为线.默认为方形
         legend.setForm(Legend.LegendForm.LINE);
        //是否隐藏图例/true_否，false_是
        legend.setEnabled(false);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bnt_return:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(Function == 2){
            sendMsg2Ble("010402a301");
            sleep(50);
            sendMsg2Ble("010402a301");
            sleep(50);
            sendMsg2Ble("010402a301");
        }else if(Function == 3){
            sendMsg2Ble("01040362c1");
            sleep(50);
            sendMsg2Ble("01040362c1");
            sleep(50);
            sendMsg2Ble("01040362c1");
        }else if(Function == 4){
            sendMsg2Ble("0104042303");
            sleep(50);
            sendMsg2Ble("0104042303");
            sleep(50);
            sendMsg2Ble("0104042303");
        }
        sleep(100);
    }
}