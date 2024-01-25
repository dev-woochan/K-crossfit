package com.example.k_crossfit.Calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.BoxActivity;
import com.example.k_crossfit.MapActivity;
import com.example.k_crossfit.myPage.MyPageActivity;
import com.example.k_crossfit.R;
import com.example.k_crossfit.TimerActivity;
import com.example.k_crossfit.etc.UriTypeAdapter;
import com.example.k_crossfit.Data.WodData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {
    private Button timerButton;
    private Button addWodButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WodData> wodDataArrayList = new ArrayList<>();
    public CalendarAdapter adapter;
    private Button myPageButton;
    private Button boxButton;
    private Gson gson;
    SharedPreferences userData;
    private SharedPreferences wodShared;
    private String loginId;
    private SharedPreferences.Editor wodSharedEditor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriTypeAdapter()).create();
        //마이페이지
        userData = getSharedPreferences("userShared", MODE_PRIVATE);
        wodShared = getSharedPreferences("wodShared", MODE_PRIVATE);
        myPageButton = findViewById(R.id.button_calendar_myPage);
        boxButton = findViewById(R.id.button_Calendar_box);

        boxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoxActivity.class);
                startActivity(intent);
            }
        });


        loginId = userData.getString("loginId", "id없음");

        //와드데이터 불러오는 코드
        SharedPreferences.Editor wodSharedEditor = wodShared.edit();
        String wodList = wodShared.getString(loginId + "wodList", "와드없음");
        Type type = new TypeToken<ArrayList<WodData>>() {
        }.getType();


        if (!(wodList.equals("와드없음"))) { //와드가 있을경우
            ArrayList<WodData> savedWodList = gson.fromJson(wodList, type);
            wodDataArrayList = savedWodList;
        }



        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent);
            }
        });

        // 타이머 버튼
        timerButton = findViewById(R.id.button_myPage_timer);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTimer = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(moveTimer);
            }
        });

        // 와드 추가 버튼
        addWodButton = findViewById(R.id.button_calendar_addWod);
        addWodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddingWodActivity.class);
                startActivityForResult(intent, 101);
            }
        });
        //리사이클러뷰 시작
        recyclerView = findViewById(R.id.recyclerView_calendar_wodView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 여기서는 지역 변수로 선언하지 않고 멤버 변수인 adapter를 사용
        adapter = new CalendarAdapter(wodDataArrayList, this);
        recyclerView.setAdapter(adapter);
        // 초기화
        // 인텐트 받아올 인텐트
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ClassLoader classLoader = this.getClass().getClassLoader();

        switch (requestCode) {
            case 101:
                //와드 추가의 경우
                if (resultCode == 13) { //와드추가
                    WodData wod = null;
                    //인텐트에서 추가될 인덱스와 와드 키값을 받아옴
                    int wodIndex = 0;
                    String wodKey = "";
                    if (data != null) {
                        wodIndex = data.getIntExtra("wodIndex", 0);
                        wodKey = data.getStringExtra("wodKey");
                    }
                    //쉐어드에서 와드 받아오고 제이슨에서 와드데이터로 변환
                    String wodJson = wodShared.getString(wodKey, "와드없음");
                    wod = gson.fromJson(wodJson, WodData.class);
                    adapter.wodDataArrayList.add(wodIndex, wod);
                    adapter.notifyItemInserted(wodIndex);
                }
                break;

            case 120: //리사이클러뷰 클릭해서 실행된 activit의 경우임
                //수정의 경우
                if (resultCode == RESULT_OK) {
                    String wodList = wodShared.getString(loginId + "wodList", "wodList");
                    Type type = new TypeToken<ArrayList<WodData>>() {
                    }.getType();
                    ArrayList<WodData> modifyWodList = gson.fromJson(wodList, type);
                    adapter.wodDataArrayList = modifyWodList;
                    adapter.notifyDataSetChanged();
                } else if (resultCode == 444) {
                    //삭제의 경우
                    int pos = data.getIntExtra("pos", 0);
                    adapter.wodDataArrayList.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
        }
    }
}