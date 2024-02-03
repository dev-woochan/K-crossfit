package com.example.k_crossfit.BOX_MAP;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.widget.Toast;

import com.example.k_crossfit.Calendar.CalendarActivity;
import com.example.k_crossfit.R;
import com.example.k_crossfit.TimerActivity;
import com.example.k_crossfit.myPage.MyPageActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BoxActivity extends AppCompatActivity {
    private TextView loadAddress;
    private Button searchBox;
    private Button timerBtn;
    private Button calendarBtn;
    private Button myPageBtn;
    private ImageView gpsBtn;
    private ImageView mapBtn;
    private RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences.Editor editor;
    private SharedPreferences setting;
    private SharedPreferences boxDataShared;
    private SharedPreferences.Editor boxEditor;
    private SharedPreferences boxTitleShared;
    private SharedPreferences.Editor titleEditor;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private Animation blink;
    private double latitude; //위도
    private double longitude; //경도
    private ApiSearch apiSearch;
    private String searchResult;
    private ArrayList<BoxData> boxList;
    private Context context;
    private String adminArea;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        mapBtn = findViewById(R.id.imageview_box_mapBtn);
        gpsBtn = findViewById(R.id.imageview_box_gpsBtn);
        loadAddress = findViewById(R.id.textview_box_roadAddress);
        searchBox = findViewById(R.id.button_box_searchBox);
        timerBtn = findViewById(R.id.button_box_timer);
        calendarBtn = findViewById(R.id.button_box_calendar);
        myPageBtn = findViewById(R.id.button_timer_myPage);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        boxDataShared =getSharedPreferences("boxDataShared",MODE_PRIVATE);
        boxTitleShared = getSharedPreferences("boxTitleShared",MODE_PRIVATE);
        titleEditor = boxTitleShared.edit();
        boxEditor = boxDataShared.edit();

        editor = setting.edit();
        // 위치서비스 클라이언트
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //애니메이션 불러오기
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        //리사이클러 뷰 지정
        recyclerView = findViewById(R.id.recyclerView_box_boxList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        context = getApplicationContext();


        //지도버튼 클릭시 (다이얼로그 추가예정)
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                //위도 경도값 인텐트에 넣어줌
                if (latitude != 0 && longitude !=0){
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                }
                startActivity(intent);
            }
        });
        //gps버튼 클릭시
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //애니메이션 on 블링크
                gpsBtn.startAnimation(blink);
                //위치정보 불러오는 메서드 실행
                getCurrentLocation();
            }
        });
        // 런처임 위치정보 허가를 해준다.
        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts //멀티플 permission으로 여러 STRING 리스트를 넣어주면 여러가지 허가 실행을함
                                .RequestMultiplePermissions(), result -> {
                            //결과에따라 허가권 설정함
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // fineLocation허가시
                                editor.putBoolean("fineLocationGranted", true); //쉐어드에 넣어주긴했는데 필요없는듯하다. 어차피 승인은 따로 얻어야하기때문 필요없으면 삭제하기!
                                editor.commit();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                editor.putBoolean("coarseLocationGranted", true);
                                editor.commit();
                            } else {
                                //허가를 안눌렀을때 표시할 택스트
                                Toast.makeText(this, "위치권한을 허가해야 내위치 불러오기 기능이 활성화 됩니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );


        if (setting.getBoolean("fineLocationGranted", false)) {
            //위치정보 허가 있는경우 그냥진행
        } else { //허가 없는경우 권한요청
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        //BOX검색 버튼 클릭
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색하기 시작 파라미터값에 지역 값을 넣어줄것이다 ex)강원도 1번 ~~시 한번
                if (adminArea != "" && adminArea != null){
                    apiSearch = new ApiSearch(adminArea+"크로스핏");
                    try {
                        apiSearch.start();
                        //비동기 실행을 막기위해 join으로 끝나면 getJson()
                        apiSearch.join();
                    }catch (Exception e){
                        Log.d("searchThread", "에러 발생 ");
                    }
                    searchResult =  apiSearch.getJson();
                    Log.d("searchThread", "수정한 json: "+searchResult);
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    try {
                        jsonObject = new JSONObject(searchResult);
                        jsonArray = jsonObject.getJSONArray("items");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Type type = new TypeToken<ArrayList<BoxData>>() {
                    }.getType();
                    boxList = new ArrayList<>();
                    Gson gson = new Gson();
                    boxList = gson.fromJson(String.valueOf(jsonArray), type);
                    Log.d("searchThread", "리스트1 번 반환: "+boxList.get(0).title);
                    RecyclerView.Adapter adapter = new BoxAdapter(boxList,getBaseContext(),latitude,longitude);
                    recyclerView.setAdapter(adapter);
                    Geocoder g = new Geocoder(context, Locale.KOREA);
                    for (int i=0; i<boxList.size(); i++){
                        try {
                            // 주소를 위도와 경도로 변환
                            List<Address> addresses = g.getFromLocationName(boxList.get(i).address, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                // 위도와 경도 얻기
                                double latitude = address.getLatitude();
                                double longitude = address.getLongitude();
                                titleEditor.putString(String.valueOf(i),boxList.get(i).title);
                                boxEditor.putString(boxList.get(i).title+"latitude",String.valueOf(latitude));
                                boxEditor.putString(boxList.get(i).title+"logitude",String.valueOf(longitude));
                                boxEditor.putString(boxList.get(i).title+"link",String.valueOf(boxList.get(i).link));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    boxEditor.commit();
                    titleEditor.commit();
                }else {
                 Toast.makeText(getApplicationContext(),"위치정보가 없습니다 내 위치정보를 불러와주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //메뉴 이동
        timerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
            finish();
        });

        calendarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
            startActivity(intent);
            finish();
        });

        myPageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // 현재 위치를 받아오고 그위치를 주소로 반환해 텍스트 넣어주는 메소드
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // getCurrentLocation이 실제적으로 위도 경도를 받아오는 메서드이다
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //위치받기 성공시
                    if (location != null) {
                        //성공 표시를하기위해 초록색으로 변경해줌
                        gpsBtn.setImageResource(R.drawable.gpsgreen);
                        //애니메이션 멈추기
                        gpsBtn.clearAnimation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("gps", "위도 " + latitude + "경도 :" + longitude);
                        //한국 으로 설정 한 Geocoder
                        Geocoder g = new Geocoder(getApplicationContext(), Locale.KOREA);
                        try {
                            // 위도경도값으로 주소 데이터 생성
                            List<Address> address = g.getFromLocation(latitude, longitude, 10);
                            adminArea = address.get(0).getAdminArea();
                            // 주소 받아와서 텍스트 변경해주기 대한민국은 잘라주려고 substring으로 처리해주었다. 너무 길면 표시에 제약이 있어서
                            loadAddress.setText(address.get(0).getAddressLine(0).toString().substring(5));
                            Log.d("gps", address.get(0).getAddressLine(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("gps", "geocode 에러 발생  " + e);
                        }

                    }
                }
            });
        } else {
            //위치권한 없을시 실행못하고 권한 허가 launch 다시 실행해줌
            Toast.makeText(getApplicationContext(), "위치 권한이 없습니다. 위치권한을 허가해주세요 ", Toast.LENGTH_SHORT).show();
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }


}



