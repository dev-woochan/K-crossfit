package com.example.k_crossfit.login;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.k_crossfit.R;

public class SignInActivity extends AppCompatActivity {

    Button backButton;
    Button duplicateCheckButton;
    Button signInButton;
    EditText nameInput;
    EditText idInput;
    EditText passwordInput;
    EditText passwordConfirmInput;
    CheckBox serviceCheckBox;
    CheckBox personalCheckBox;
    //각 버튼 , EditText 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //쉐어드 구현부
        Context context = getApplicationContext();
        SharedPreferences loginShared = context.getSharedPreferences("userShared",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginShared.edit();
        //쉐어드 설정 끝



        backButton = findViewById(R.id.button_signin_back); //view와 변수 연결
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); //뒤로가기 버튼 클릭시 loginactivity로 돌아가게됨

        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    backButton.setTextColor(Color.GRAY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    backButton.setTextColor(Color.WHITE);
                } else {
                    backButton.setTextColor(Color.WHITE);
                }
                return false;
            }
        });
        //뒤로가기 터치했을때 색깔변화 눌렀을때만 변확하고 때면 원래 색상으로 돌아오게함

        duplicateCheckButton = findViewById(R.id.button_signin_duplicateCheck);
        duplicateCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = idInput.getText().toString();
                if (loginShared.getString(userid,"중복없음").equals(userid)){
                    Toast.makeText(getApplicationContext(), " 중복된 아이디가 있습니다 다른아이디를 사용해주세요  ", Toast.LENGTH_SHORT).show();
                    idInput.setTextColor(Color.parseColor("#e90039"));
                }else {
                    Toast.makeText(getApplicationContext(), " 중복된 아이디가 없습니다   ", Toast.LENGTH_SHORT).show();
                    idInput.setTextColor(Color.GREEN);
                    idInput.setTextColor(Color.parseColor("#006400"));
                }
            }
        });

        //이메일 중복체크 => 나중에 구현예정 뭐랑 비교할지 모르겠음

        nameInput = findViewById(R.id.editText_signIn_name);
        //이름 입력칸
        nameInput.setPrivateImeOptions("defaultInputmode=korean;");
        idInput = findViewById(R.id.editText_signIn_id);

        // 이메일입력칸
        passwordInput = findViewById(R.id.editText_signIn_password);
        // 패스워드 입력칸
        passwordConfirmInput = findViewById(R.id.editText_signIn_passwordConfirm);
        // 패스워드 재확인 칸
        serviceCheckBox = findViewById(R.id.checkBox_signin_TermsOfService);
        // 서비스 이용동의 체크박스
        personalCheckBox = findViewById(R.id.checkBox_signin_personalInfo);
        // 개인정보 이용동의 체크박스

        signInButton = findViewById(R.id.button_signin_signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nameInput.getText().toString();
                String userid = idInput.getText().toString();
                String userPassword = passwordInput.getText().toString();
                String userPasswordConfirm = passwordConfirmInput.getText().toString();
                Boolean serviceCheck = serviceCheckBox.isChecked();
                Boolean personalCheck = personalCheckBox.isChecked();

                if (userName.isEmpty() || userid.isEmpty() || userPassword.isEmpty() || userPasswordConfirm.isEmpty()) {
                    //빈칸이 존재하는경우
                    Toast.makeText(getApplicationContext(), "빈칸이 존재합니다 ", Toast.LENGTH_SHORT).show();
                } else if (userPassword.length() < 8) {
                    //비밀번호 짧은경우
                    Toast.makeText(getApplicationContext(), "패스워드는 8자리 이상으로 설정해주세요 ", Toast.LENGTH_SHORT).show();
                } else if (userPassword.length() > 16) {
                    Toast.makeText(getApplicationContext(), "패스워드는 16자리 이하로 설정해주세요 ", Toast.LENGTH_SHORT).show();
                } else if (!userPassword.equals(userPasswordConfirm)) {
                    //패스워드가 다른경우
                    Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다 ", Toast.LENGTH_SHORT).show();
                } else if (serviceCheck == false) {
                    //서비스 체크안한경우
                    Toast.makeText(getApplicationContext(), "서비스 이용동의에 체크해주세요", Toast.LENGTH_SHORT).show();
                    serviceCheckBox.setTextColor(Color.RED);
                } else if (personalCheck == false) {
                    //개인정보 체크 안한경우
                    Toast.makeText(getApplicationContext(), "서비스 이용동의에 체크해주세요", Toast.LENGTH_SHORT).show();
                    personalCheckBox.setTextColor(Color.RED);
                } else {
                    //모든게 충족하는경우
                    Toast.makeText(getApplicationContext(), userName + "님 회원가입이 완료되었습니다", Toast.LENGTH_LONG).show();
                    Intent successSignIn = new Intent(getApplicationContext(), LoginActivity.class);
                    

                    // 정보로 쉐어드 생성
                    editor.putString(userid,userid);
                    editor.putString(userid+"pass",userPassword);
                    editor.putString(userid+"name",userName);
                    editor.commit();


                    setResult(RESULT_OK,successSignIn);

                    finish();
                }
            }
        });
        //회원가입 버튼
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("signin", "onPause: 실행");
    }

    protected void onResume() {
        super.onResume();
        Log.d("signin", "onResume: 실행");
    }

    protected void onStop() {
        super.onStop();
        Log.d("signin", "onStop: 실행");
        finish();
    }
}