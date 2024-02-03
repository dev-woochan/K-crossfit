package com.example.k_crossfit.myPage;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.k_crossfit.BOX_MAP.BoxActivity;
import com.example.k_crossfit.Calendar.CalendarActivity;
import com.example.k_crossfit.Data.WodData;
import com.example.k_crossfit.R;
import com.example.k_crossfit.TimerActivity;
import com.example.k_crossfit.login.LoginActivity;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyPageActivity extends AppCompatActivity {


    private TextView btnModifyProfile;
    private TextView photoText;
    private Button btnLogout;
    private Button btnTimer;
    private Button btnCalendar;
    private Button btnBox;
    private TextView deleteUser;
    private TextView workoutCount;
    private ImageView profilePhoto;
    private TextView moveChart;
    private EditText etUserName;
    private TextView etUserId;
    private EditText etUserTall;
    private EditText etUserWeight;
    private SharedPreferences userShared;
    private SharedPreferences.Editor editor;
    private String loginId;
    private Uri selectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        workoutCount = findViewById(R.id.textView_myPage_workoutCount);
        btnLogout = findViewById(R.id.button_myPage_logout);
        btnModifyProfile = findViewById(R.id.button_myPage_modifyProfile);
        etUserName = findViewById(R.id.edittext_myPage_name);
        etUserId = findViewById(R.id.edittext_myPage_id);
        etUserTall = findViewById(R.id.edittext_myPage_tall);
        etUserWeight = findViewById(R.id.edittext_myPage_weight);
        profilePhoto = findViewById(R.id.imageView_myPage_profilePhoto);
        btnTimer = findViewById(R.id.button_myPage_timer);
        btnCalendar = findViewById(R.id.button_myPage_calendar);
        deleteUser = findViewById(R.id.textView_myPage_deleteUser);
        photoText = findViewById(R.id.textView_myPage_addPhotoText);
        moveChart = findViewById(R.id.textView_myPage_moveChart);
        btnBox = findViewById(R.id.button_timer_box);
        // 리소스 연결 끝
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = setting.edit();
        userShared = getSharedPreferences("userShared", MODE_PRIVATE);
        SharedPreferences wodShared = getSharedPreferences("wodShared", MODE_PRIVATE);
        editor = userShared.edit();
        // 쉐어드 호출 및 에디터 지정
        loginId = userShared.getString("loginId", "id");
        etUserName.setText(userShared.getString(loginId + "name", "이름"));
        etUserId.setText(userShared.getString(loginId, "아이디"));
        etUserTall.setText(userShared.getString(loginId + "tall", ""));
        etUserWeight.setText(userShared.getString(loginId + "weight", ""));
        //와드리스트 불러와서 기록한 횟수 size로 넣어주기
        String wodList = wodShared.getString(loginId + "wodList", "없음");
        workoutCount.setText((WodData.getSize(wodList)).toString());
        //프로필 이미지 있으면 불러오기
        String photoUri = userShared.getString(loginId + "photo", "없음");
        if (!(photoUri.equals("없음"))) {
            Uri photo = Uri.parse(photoUri);
            photoText.setVisibility(View.GONE);
            profilePhoto.setImageURI(photo);
        }

        //      이미지 뷰 클릭 실행용 런쳐 설정
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    public void onActivityResult(Uri uri) {
                        selectedImage = getImageUri(uri);
                        profilePhoto.setImageURI(selectedImage);
                    }
                }
        );

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ACITON_PICK을 실행하면 갤러리 이미지호출,
//                startActivityForResult(intent,20);
                mGetContent.launch("image/*");
            }
        });
        // 캘린더 이동
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //타이머 이동
        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //박스이동
        btnBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoxActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //회원탈퇴 버튼 클릭시 정보 삭제
        //회원탈퇴 터치시 밑줄 가게 만들기
        deleteUser.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //터치시
                        SpannableString content = new SpannableString(deleteUser.getText());
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        deleteUser.setText(content);
                        break;
                    case MotionEvent.ACTION_UP:
                        //땔때
                        deleteUser.setText(deleteUser.getText().toString());
                        //AlertDialog로 동의 혹은 취소 구현
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                        builder.setMessage("데이터가 모두 삭제 됩니다. 정말로 탈퇴 하시겠습니까?");
                        //수락버튼
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //데이터 모두 삭제 후 로그인 화면으로 이동
                                editor.remove(loginId);
                                editor.remove(loginId + "name");
                                editor.remove(loginId + "pass");
                                editor.remove(loginId + "tall");
                                editor.remove(loginId + "weight");
                                editor.remove(loginId + "photo");
                                editor.remove("loginId");
                                editor.remove("autoLoginId");
                                editor.remove("autoLoginPassword");
                                settingEditor.putBoolean("autoLogin", false);
                                editor.commit();
                                settingEditor.commit();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                Toast.makeText(getApplicationContext(), "탈퇴 되었습니다. 회원정보가 모두 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                                    @Override
                                    public Unit invoke(Throwable throwable) {
                                        return null;
                                    }
                                });
                                startActivity(intent);
                                finish();
                            }
                        });
                        //취소 버튼
                        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return true;
            }
        });

        //로그아웃 버튼 클릭시 수행
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove(String.valueOf(R.string.loginId));
                editor.remove("autoLoginId");
                editor.remove("autoLoginPassword");
                settingEditor.putBoolean("autoLogin", false);
                editor.commit();
                settingEditor.commit();
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                });
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
        //프로필 수정버튼 터치시
        btnModifyProfile.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //터치시
                        SpannableString content = new SpannableString(btnModifyProfile.getText());
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        btnModifyProfile.setText(content);
                        break;
                    case MotionEvent.ACTION_UP:
                        //땔때
                        btnModifyProfile.setText(btnModifyProfile.getText().toString());
                        //AlertDialog로 동의 혹은 취소 구현
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                        builder.setMessage("프로필을 수정 하시겠습니까?");
                        //수락버튼
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //수정하기 예 눌렀을때
                                btnModifyProfile.setText(btnModifyProfile.getText().toString());
                                String modifyName = etUserName.getText().toString();
                                String modifyTall = etUserTall.getText().toString();
                                String modifyWeight = etUserWeight.getText().toString();
                                String userId = etUserId.getText().toString();
                                editor.putString(userId + "name", modifyName);
                                editor.putString(userId + "tall", modifyTall);
                                editor.putString(userId + "weight", modifyWeight);
                                if (selectedImage != null) {
                                    editor.putString(userId + "photo", selectedImage.toString());
                                }
                                editor.apply();
                                Toast.makeText(getApplicationContext(), "프로필 수정 완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //취소 버튼
                        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return true;
            }
        });

        moveChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //터치시
                        SpannableString content = new SpannableString(moveChart.getText());
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        moveChart.setText(content);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        moveChart.setText(moveChart.getText().toString());
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                        builder.setMessage("통계를 보시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                return true;
            }
        });


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

    //Uri 변환
    private Uri getImageUri(Uri imageUri) {
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

}
