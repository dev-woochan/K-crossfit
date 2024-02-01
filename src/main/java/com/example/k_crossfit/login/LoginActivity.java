package com.example.k_crossfit.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ipsec.ike.IkeSessionCallback;
import android.net.ipsec.ike.IkeSessionConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_crossfit.Calendar.CalendarActivity;
import com.example.k_crossfit.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApi;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;


public class LoginActivity extends AppCompatActivity {
    private Button signInBtn;
    //회원가입 버튼
    private Button login;
    private CheckBox autoLoginCheckBox;
    private EditText idInput;
    //이메일 입력칸
    private EditText passwordInput;
    private Button kakaoLogin;
    //패스워드 입력칸

    //각 버튼 및 텍스트 변수 선언

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //화면표시를 위한 setContentView
        idInput = findViewById(R.id.editText_login_id);// 이메일입력한것
        passwordInput = findViewById(R.id.editText_login_PasswordInput);//패스워드 입력한것
        signInBtn = findViewById(R.id.button_login_signIn); //회원가입 버튼
        autoLoginCheckBox = findViewById(R.id.checkBox_login_autoLogin);
        kakaoLogin = findViewById(R.id.button_login_kakaoLogin);
        SharedPreferences userData = getSharedPreferences("userShared", MODE_PRIVATE);
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        SharedPreferences.Editor settingEditor = setting.edit();
        //카카오

        String keyHash = Utility.INSTANCE.getKeyHash(getApplicationContext());
        Log.d("KeyHash", "onCreate: " + keyHash);
        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //카카오 계정을 이용한 로그인
                UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {

                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        if (oAuthToken != null) {
                            //로그인 성공 시
                            Toast.makeText(getApplicationContext(), "카카오 로그인 성공", Toast.LENGTH_SHORT);
                            //정보 쉐어드에 저장해서 로그인시키기
                            Intent loginSuccess = new Intent(getApplicationContext(), CalendarActivity.class);
                            loginSuccess.putExtra("kakaoLogin","kakaoLogin");
                            //loginId는 지금 어떤 아이디가 로그인되었는지 저장하는 값임
                            startActivity(loginSuccess);
                        } else {
                            //로그인 실패시
                            Toast.makeText(getApplicationContext(), "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
                            Log.d("kakaoLogin", "invoke: " + throwable);
                        }
                        return null;
                    }
                });
                //위에가 안끝나도 밑에 코드가 진행됨 수정필요 아예 로그인 이후에 저장을하던가 ㅇㅇ
                // 카카오 로그인 데이터가 있는지 조회 => 저장 이런식으로 구현하면될듯



            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });// 클릭시 회원가입 화면으로 이동

        //로그인 버튼 수행 시작
        login = findViewById(R.id.button_login_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idInput.getText().toString();
                String password = passwordInput.getText().toString();
                //입력값 받아오기
                if (id.equals(userData.getString(id, "id없음"))) { //id 가 있는경우
                    if (userData.getString(id + "pass", "id없음").equals(password)) {
                        //pass 맞은경우
                        if (autoLoginCheckBox.isChecked()) {
                            //autologin체크되어있을시
                            editor.putString("autoLoginId", idInput.getText().toString());
                            editor.putString("autoLoginPassword", passwordInput.getText().toString());
                            settingEditor.putBoolean("autoLogin", true);
                            settingEditor.commit();
                            editor.commit();
                        } else {
                            settingEditor.putBoolean("autoLogin", false);
                            settingEditor.commit();
                        }
                        Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                        Intent loginSuccess = new Intent(getApplicationContext(), CalendarActivity.class);
                        //loginId는 지금 어떤 아이디가 로그인되었는지 저장하는 값임
                        editor.putString("loginId", id);
                        editor.commit();
                        startActivity(loginSuccess);
                    } else { //pass 틀린경우
                        Toast.makeText(getApplicationContext(), "id 및 패스워드를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "id 및 패스워드를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        boolean boo = setting.getBoolean("autoLogin", false); //로그인 정보 기억하기 체크 유무 확인
        if (boo) { // 체크가 되어있다면 아래 코드를 수행
            //저장된 아이디와 암호를 가져와 셋팅한다.
            idInput.setText(userData.getString("autoLoginId", ""));
            passwordInput.setText(userData.getString("autoLoginPassword", ""));
            autoLoginCheckBox.setChecked(true); //체크박스는 여전히 체크 표시 하도록 셋팅
            login.callOnClick();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 회원가입 후 인텐트에서 이름,패스워드,이메일을받아옴 start때 받아와야하나 ??
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


