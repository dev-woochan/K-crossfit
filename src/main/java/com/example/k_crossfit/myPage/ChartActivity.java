package com.example.k_crossfit.myPage;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_crossfit.Data.WodData;
import com.example.k_crossfit.R;
import com.example.k_crossfit.etc.UriTypeAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartActivity extends AppCompatActivity {
    private TextView userName;
    private TextView maxMonth;
    private BarChart workoutChart;
    private TextView back;
    private Handler handler;
    private TextView totalCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);
        //init
        workoutChart = findViewById(R.id.chart_workoutChart);
        userName = findViewById(R.id.textview_chart_userName);
        back = findViewById(R.id.textview_chart_back);
        maxMonth = findViewById(R.id.textview_chart_maxMonth);
        totalCount = findViewById(R.id.textview_chart_totalCount);

        //쉐어드 설정
        SharedPreferences userShared = getSharedPreferences("userShared", MODE_PRIVATE);
        String loginId = userShared.getString("loginId", "id");
        String name = userShared.getString(loginId + "name", "홍길동");
        userName.setText(name);
        SharedPreferences wodShared = getSharedPreferences("wodShared", MODE_PRIVATE);
        String json = wodShared.getString(loginId + "wodList", "deafaultWodList");
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriTypeAdapter()).create();
        Type type = new TypeToken<ArrayList<WodData>>() {
        }.getType();
        ArrayList<WodData> loadWodList = gson.fromJson(json, type);
        ArrayList<BarEntry> entries = new ArrayList<>();
        //달별로 사용할 데이터 초기화 값 생성
        int mon[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        //데이터 추가
        for (int i = 0; i < mon.length; i++) {
            entries.add(new BarEntry(i, mon[i]));
        }
        // 차트 코드 시작
        //x축 라벨 설정
        String[] labels = new String[]{"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
        XAxis xAxis = workoutChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        // 12월간 횟수표ㅗ시를 위해 12개로 강제지정
        xAxis.setLabelCount(12, false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7);
        //격자 삭제
        xAxis.setDrawGridLines(false);
        //y축 라벨 설정
        YAxis leftAxis = workoutChart.getAxisLeft();
        leftAxis.setAxisMaximum(10);
        leftAxis.setAxisMinimum(0);
        leftAxis.setEnabled(false);
        YAxis rightAxis = workoutChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(10);
        //데이터, 차트 연결
        BarDataSet dataSet = new BarDataSet(entries, "운동횟수");
        //바 모양 설정
        dataSet.setColor(Color.parseColor("#009630"));
        BarData data = new BarData(dataSet);
        workoutChart.getDescription().setEnabled(false);
        workoutChart.animateY(2000);
        workoutChart.setData(data);
        workoutChart.invalidate();
        //핸들러 생성
        handler = new MainHandler();
        //쓰래드 생성 및 시작
        LoadMonthlyWod loadMonthlyWod = new LoadMonthlyWod(loadWodList);
        loadMonthlyWod.start();
        //뒤로가기 버튼
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    class LoadMonthlyWod extends Thread {
        ArrayList<WodData> loadWodList;
        LoadMonthlyWod(ArrayList<WodData> list) {
            this.loadWodList = list;
        }
        public void run() {
            int[] num = new int[12];
            for (int i = 0; i < loadWodList.size(); i++) {
                String date = loadWodList.get(i).wodDate;
                String month = date.substring(5, 7);
                switch (month) {
                    case "01":
                        num[0]++;
                        break;
                    case "02":
                        num[1]++;
                        break;
                    case "03":
                        num[2]++;
                        break;
                    case "04":
                        num[3]++;
                        break;
                    case "05":
                        num[4]++;
                        break;
                    case "06":
                        num[5]++;
                        break;
                    case "07":
                        num[6]++;
                        break;
                    case "08":
                        num[7]++;
                        break;
                    case "09":
                        num[8]++;
                        break;
                    case "10":
                        num[9]++;
                        break;
                    case "11":
                        num[10]++;
                        break;
                    case "12":
                        num[11]++;
                        break;
                }
            }
            for (int i = 0; i < 12; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                bundle.putInt("num", num[i]);
                if (i == 11) {
                    int maxNum = Arrays.stream(num).max().getAsInt();
                    String maxMonth = "";
                    for (int j = 0; j < 12; j++) {
                        if (num[j] == maxNum) {
                            maxMonth += (j + 1) + "월 ";
                        }
                    }
                    bundle.putString("max", maxMonth);
                    bundle.putInt("total", loadWodList.size());
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }

    class MainHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int value = bundle.getInt("num");
            int index = bundle.getInt("index");
            //인덱스 업데이트 !!
            updateEntry(workoutChart, index, value);
            if (index == 11) {
                String maxNum = bundle.getString("max");
                maxMonth.setText(maxNum + " \uD83D\uDC51");
                int total = bundle.getInt("total");
                totalCount.setText(total + "회");
            } else if (index == 10) {
                maxMonth.setText("계산 끝!!");
                totalCount.setText("계산 끝!!");
            } else if (index % 2 == 0) {
                maxMonth.setText("계산중입니다.....");
                totalCount.setText("계산중입니다.....");
            } else {
                maxMonth.setText("계산중입니다...");
                totalCount.setText("계산중입니다...");
            }
        }
    }

    private void updateEntry(BarChart chart, int index, int newValue) {
        // 바의 인덱스와 값을 받아와서 갱신해주는 메서드
        BarData data = chart.getData();
        if (data != null) {
            IBarDataSet set = data.getDataSetByIndex(0);
            if (set != null && index < set.getEntryCount()) {
                if (newValue == 0) {
                } else {
                    BarEntry entry = (BarEntry) set.getEntryForIndex(index);
                    entry.setY(newValue);
                    data.notifyDataChanged();
                    chart.notifyDataSetChanged();
                    chart.animateY(1000);
                    chart.invalidate();
                }
            }
        }
    }
}



