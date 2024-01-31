package com.example.k_crossfit.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_crossfit.Calendar.CalendarActivity;
import com.example.k_crossfit.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

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
        KakaoSdk.init(this,"e044b26be68c7e643e493306d4b62576");
        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().loginWithKakaoAccount(getBaseContext(), new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        return null;
                    }
                });
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivityForResult(intent, 102);
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


