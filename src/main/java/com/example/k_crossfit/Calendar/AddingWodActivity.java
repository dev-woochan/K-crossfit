package com.example.k_crossfit.Calendar;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.Data.WodData;
import com.example.k_crossfit.Data.WorkoutData;
import com.example.k_crossfit.R;
import com.example.k_crossfit.etc.UriTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddingWodActivity extends AppCompatActivity {
    private ImageView addPhoto; //사진추가
    private Button backButton;//뒤로가기
    private Button saveButton;//저장
    private Button deleteButton;//삭제
    private TextView EditTextwodDate;
    private EditText EditTextwodName;
    private Calendar calendar;
    private EditText EditTextmemo;
    private Button addWorkout;
    private Button deleteWorkout;
    private Uri imageUri;
    private WodData wodData;
    private ArrayList<WodData> wodDataArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WorkoutData> workoutDataArrayList;
    private WorkoutAdapter adapter;
    private WorkoutData workoutData;
    private Intent intent;
    private int pos;
    private Gson gson;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wod);

        //editTExt 매핑
        EditTextwodDate = findViewById(R.id.editText_wod_date);
        EditTextwodName = findViewById(R.id.editText_wod_wodName);
        EditTextmemo = findViewById(R.id.memo_wod);
        addPhoto = findViewById(R.id.imageView_wod_addPhoto);
        deleteButton = findViewById(R.id.button_wod_delete);
        backButton = findViewById(R.id.button_wod_back);
        saveButton = findViewById(R.id.button_wod_save);
        intent = getIntent();
        calendar = Calendar.getInstance();

        gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriTypeAdapter()).create();
        //쉐어드 지정
        SharedPreferences userShared = getSharedPreferences("userShared", MODE_PRIVATE);
        SharedPreferences wodShared = getSharedPreferences("wodShared", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userShared.edit();
        SharedPreferences.Editor wodEditor = wodShared.edit();
        //현재 로그인된 유저 아이디값 가져오기
        String loginId = userShared.getString("loginId", "id없음");
        //유저와 매칭된 와드 불러오기
        String wodList = wodShared.getString(loginId + "wodList", "리스트 없음");
        // json ArrayList<wodData>변환시키기
        Type type = new TypeToken<ArrayList<WodData>>() {
        }.getType();
        // 리스트 불러오기
        if (wodList.equals("리스트 없음")) {
            //없으면 그냥 새로 생성하기
            wodDataArrayList = new ArrayList<>();
        } else {
            //데이터가 있을때 json변환해서 받아오기
            wodDataArrayList = gson.fromJson(wodList, type);
        }

        if (intent.hasExtra("key")) {// 불러온 값이 있는경우
            key = intent.getStringExtra("key");
            pos = intent.getIntExtra("wodNumber", 0);
            String wodSharedString = wodShared.getString(key, "와드없음");
            WodData loadWod = gson.fromJson(wodSharedString, WodData.class);
            saveButton.setText("수정");
            //객체에 담긴 것들로 설정하기
            String loadWodName = loadWod.wodName;
            Uri loadWodPhoto = loadWod.wodPhoto;
            String loadWodDate = loadWod.wodDate;
            String loadWodMemo = loadWod.memo;
            ArrayList<WorkoutData> loadWorkoutData = loadWod.workoutDataArrayList;
            //리사이클러뷰 시작
            recyclerView = findViewById(R.id.recyclerView_wod_workoutView);
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new WorkoutAdapter(loadWorkoutData);
            recyclerView.setAdapter(adapter);
            // 종료
            EditTextmemo.setText(loadWodMemo);
            EditTextwodDate.setText(loadWodDate);
            addPhoto.setImageURI(loadWodPhoto);
            imageUri = loadWodPhoto;
            EditTextwodName.setText(loadWodName);
        } else {
            //불러온 값이 없는경우 workoutArray리스트 1개 생성
            if (workoutDataArrayList == null) {
                workoutDataArrayList = new ArrayList<>();
                workoutData = new WorkoutData("", "", "", "");
                workoutDataArrayList.add(workoutData);
            }

            //리사이클러뷰 시작
            recyclerView = findViewById(R.id.recyclerView_wod_workoutView);
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new WorkoutAdapter(workoutDataArrayList);
            recyclerView.setAdapter(adapter);
            // 종료
        }

        // 날짜 형식 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        //기존 데이터를 불러오는 화면의 경우!

        //화면출력
        //사진 추가 이미지 연결 을 위한 런쳐
        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Uri selectedImage = getImageUri(uri);
                        addPhoto.setImageURI(uri);
                        imageUri = selectedImage;
                    }
                });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        //뒤로가기버튼 시작
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //저장하기 버튼 시작
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //연결하기
                String wodDate = EditTextwodDate.getText().toString();
                String wodName = EditTextwodName.getText().toString();
                String memo = EditTextmemo.getText().toString();
                Uri wodPhoto = getImageUri(imageUri);
                //workout 객체 생성
                ArrayList<WorkoutData> workoutDataArrayList = adapter.saveWorkoutData();
                //객체가 여러개면 계속 add해줄생각임 if문 활용해서 position getcount로 반복문으로 add해주자 !
                wodData = new WodData(wodDate, wodName, workoutDataArrayList, memo, wodPhoto);  //최종 와드 객체 생성

                if (intent.hasExtra("key")) { //인텐트로 생성된 화면의 경우!
                    //수정하는 코드임 수정코드
                    Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                    wodData.setKey(key);
                    wodDataArrayList.set(pos, wodData);
                    //와드 리스트 날자별로 정렬 하기 공부예정임
                    Collections.sort(wodDataArrayList, (w1, w2) -> {
                        LocalDate date1 = LocalDate.parse(w1.wodDate, formatter);
                        LocalDate date2 = LocalDate.parse(w2.wodDate, formatter);
                        return date1.compareTo(date2);
                    });
                    //와드데이터 제이슨화
                    String wodDataJson = gson.toJson(wodData);
                    String wodList = gson.toJson(wodDataArrayList);
                    wodEditor.putString(wodData.getKey(), wodDataJson);
                    wodEditor.putString(loginId + "wodList", wodList);
                    wodEditor.commit();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //와드 추가버튼으로 동작하는경우!
                    Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                    wodDataArrayList.add(wodData);
                    //와드데이터 이름 중복방지 객체에 키값넣기
                    for (int i = 1; i < 100; i++) {
                        if (wodShared.getString(loginId + "wod" + wodData.wodDate + "-" + i, "와드없음").equals("와드없음")) {
                            //같은 이름의 와드데이터가 없을시
                            wodData.setKey(loginId + "wod" + wodData.wodDate + "-1");
                            break;
                        } else {
                            // 있으면 i++
                        }
                    }
                    //와드 리스트 날자별로 정렬 하기 공부예정임
                    Collections.sort(wodDataArrayList, (w1, w2) -> {
                        LocalDate date1 = LocalDate.parse(w1.wodDate, formatter);
                        LocalDate date2 = LocalDate.parse(w2.wodDate, formatter);
                        return date1.compareTo(date2);
                    });
                    int wodIndex = 0;

                    for (int i = 0; i < wodDataArrayList.size(); i++) {
                        if (wodDataArrayList.get(i).getKey().equals(wodData.getKey())) {
                            wodIndex = i;
                        }
                    }

                    //와드데이터 제이슨화
                    String wodDataJson = gson.toJson(wodData);
                    String wodList = gson.toJson(wodDataArrayList);

                    wodEditor.putString(wodData.getKey(), wodDataJson);
                    wodEditor.putString(loginId + "wodList", wodList);

                    intent.putExtra("wodIndex", wodIndex);
                    intent.putExtra("wodKey", wodData.getKey());
                    wodEditor.commit();
                    int addWod = 13;
                    setResult(addWod, intent);
                    finish();
                }
            }
        });

        //삭제버튼
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                wodDataArrayList.remove(pos);
                //와드 리스트 날자별로 정렬 하기 공부예정임
                Collections.sort(wodDataArrayList, (w1, w2) -> {
                    LocalDate date1 = LocalDate.parse(w1.wodDate, formatter);
                    LocalDate date2 = LocalDate.parse(w2.wodDate, formatter);
                    return date1.compareTo(date2);
                });
                String wodList = gson.toJson(wodDataArrayList);
                wodEditor.putString(loginId + "wodList", wodList);
                wodEditor.remove(key);
                wodEditor.commit();
                intent.putExtra("pos", pos);
                setResult(444, intent);
                finish();
            }
        });

        //workout 추가버튼
        addWorkout = findViewById(R.id.button_wod_addWorkout);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //임의의 데이터 생성
                WorkoutData workoutData = new WorkoutData("", "", "", "");
                // 어레이리스트에 추가
                adapter.workoutDataArrayList.add(workoutData);
                //어뎁터에 추가 알림
                adapter.notifyItemInserted(adapter.workoutDataArrayList.size() - 1);
            }
        });

        //운동 삭제버튼
        deleteWorkout = findViewById(R.id.button_wod_deleteWorkout);
        deleteWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.deleteSelectedItems();
            }
        });

        //캘린더 출력
        EditTextwodDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddingWodActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = "";
                        if (month < 10) {
                            date = year + "-0" + month + "-" + dayOfMonth;
                            if (dayOfMonth < 10) {
                                date = year + "-0" + month + "-0" + dayOfMonth;
                            }
                        } else if (dayOfMonth < 10) {
                            date = year + "-" + month + "-0" + dayOfMonth;
                        } else {
                            date = year + "-" + month + "-" + dayOfMonth;
                        }
                        EditTextwodDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


    }

    private Uri getImageUri(Uri imageUri) {
        //이미지를 앱내 파일프로바이더 저장소에 저장하고 그 이미지의 URi를 반환해주는 메소드
        //구글 이미지같은경우에는 권한이 첫 업로드 이후 다른액티비티에서는 사라지기때문에 이렇게 사용해준다.
        // Uri를 Bitmap으로 변환
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
// Bitmap을 파일로 저장
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileName = "image_" + timeStamp + ".png";

        File file = new File(getExternalFilesDir(null), fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

// 파일의 Uri 생성
        Uri fileUri = FileProvider.getUriForFile(this, "com.example.k_crossfit.file-provider", file);

// Uri를 인텐트에 첨부
        return fileUri;
    }

    //포커스 밖의 터치를 했을때 키보드 내려주는 코드 그냥 복붙해서 넣었다 잘동작함 ㅇㅇ
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


}
