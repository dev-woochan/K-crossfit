package com.example.k_crossfit.Calendar;

import static com.google.android.gms.tasks.Tasks.await;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.BOX_MAP.BoxActivity;
import com.example.k_crossfit.myPage.MyPageActivity;
import com.example.k_crossfit.R;
import com.example.k_crossfit.TimerActivity;
import com.example.k_crossfit.etc.UriTypeAdapter;
import com.example.k_crossfit.Data.WodData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class CalendarActivity extends AppCompatActivity {
    private Button timerButton;
    private Button addWodButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WodData> wodDataArrayList = new ArrayList<>();
    public CalendarAdapter adapter;
    private ImageView enLarge;
    private Button myPageButton;
    private Button boxButton;
    private Gson gson;
    SharedPreferences userData;
    private SharedPreferences wodShared;
    private CalendarView calendarView;
    private String loginId;
    private boolean calendarON;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriTypeAdapter()).create();
        //마이페이지
        userData = getSharedPreferences("userShared", MODE_PRIVATE);
        wodShared = getSharedPreferences("wodShared", MODE_PRIVATE);
        myPageButton = findViewById(R.id.button_calendar_myPage);
        boxButton = findViewById(R.id.button_timer_box);
        calendarView = findViewById(R.id.calendarView_calendar_calendar);
        calendarON = false;
        enLarge = findViewById(R.id.imageview_calendar_enlarge);
        SharedPreferences.Editor editor = userData.edit();
        Intent intent;
        intent = getIntent();
        if (intent != null && intent.hasExtra("kakaoLogin")){
            UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                @Override
                public Unit invoke(User user, Throwable throwable) {
                    String userId = String.valueOf(user.getId());
                    String userName = String.valueOf(user.getKakaoAccount().getName());
                    editor.putString(userId,userId);
                    editor.putString("loginId",userId);
                    editor.putString(userId+"name",userName);
                    editor.commit();
                    return null;
                }
            });
        }
        //카카오 로그인시 인텐트 수신하여 유저 정보 적용


        //캘린더 창 크게줄이기 버튼
        enLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarON == false) {
                    calendarON = true;
                    ViewGroup.LayoutParams params = calendarView.getLayoutParams();
                    params.height = params.height * 5;
                    calendarView.setLayoutParams(params);
                } else {
                    calendarON = false;
                    ViewGroup.LayoutParams params = calendarView.getLayoutParams();
                    params.height = params.height / 5;
                    calendarView.setLayoutParams(params);
                }
            }
        });

        //액티비티 이동용
        boxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoxActivity.class);
                startActivity(intent);
                finish();
            }
        });
        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 타이머 버튼
        timerButton = findViewById(R.id.button_myPage_timer);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTimer = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(moveTimer);
                finish();
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

    //와드 추가
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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