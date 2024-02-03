package com.example.k_crossfit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_crossfit.BOX_MAP.BoxActivity;
import com.example.k_crossfit.Calendar.CalendarActivity;
import com.example.k_crossfit.etc.MySoundPlayer;
import com.example.k_crossfit.myPage.MyPageActivity;

import java.util.Random;

public class TimerActivity extends AppCompatActivity {
    private EditText hourText; //시간
    private EditText minuteText; //분
    private EditText secondText; //초
    private ImageView adImage;
    private Button startButton; //시작버튼
    private Button stopButton;//정지버튼
    private Button resetButton;//리셋버튼 = 00으로만듬
    private Button calendarButton; //캘린더 이동버튼
    private Button boxButton;

    private ProgressBar progressBar;// 진행 도를 표시해주고픈 프로그래스바
    private CountDownTimer countDownTimer;
    private Button myPageButton;
    private boolean timerRunning = false; //타이머 상태
    private boolean firstState; // 000000인지 아닌지
    private long time = 0; //시간
    private long tempTime = 0; //현재 흐르고 있는시간


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        startButton = findViewById(R.id.button_timer_start);
        stopButton = findViewById(R.id.button_timer_stop);
        resetButton = findViewById(R.id.button_timer_reset);
        calendarButton = findViewById(R.id.button_box_calendar);
        myPageButton = findViewById(R.id.button_timer_myPage);
        boxButton = findViewById(R.id.button_timer_box);
        hourText = findViewById(R.id.editText_timer_hour);
        minuteText = findViewById(R.id.editText_timer_minute);
        secondText = findViewById(R.id.editText_timer_second);
        progressBar = findViewById(R.id.progressBar_timer_bar);
        adImage = findViewById(R.id.imageView_timer_ad);
        //메인 스레드의 루퍼를 받아옴
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // 백그라운드에서 작업할것들
            for (int i =0; i < 5000; i++){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.d("쓰레드 장애", "run: " + e);
                }
                handler.post(() -> {
                    Random random = new Random();
                    int rand = random.nextInt(4);
                    if (rand == 0) {
                        adImage.setImageResource(R.drawable.ad1);
                    } else if (rand == 1) {
                        adImage.setImageResource(R.drawable.ad2);
                    } else if (rand == 2) {
                        adImage.setImageResource(R.drawable.ad3);
                    } else if (rand == 3) {
                        adImage.setImageResource(R.drawable.ad4);
                    }
                });
            }
        }).start();

    boxButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BoxActivity.class);
            startActivity(intent);
            finish();
        }
    });
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
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

        //시작버튼
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstState = true;
                startTimer();
            }
        });
        //정지버튼
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstState = true;
                stopTimer();
            }
        });
        //리셋버튼
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    tempTime = 0;
                    updateTimer();
                } else {
                    Toast.makeText(getApplicationContext(), "타이머 진행중에는 초기화할수 없습니다", Toast.LENGTH_SHORT);
                }
            }
        });
        //프로그래스 바 불확실이아닌 확실한 바로 설정 (수치에따라 변화하는바 )
        progressBar.setIndeterminate(false);

        updateTimer();
        //소리재생을위한 soundplayer 선언
        MySoundPlayer.initSounds(getApplicationContext());
    }

    public void startTimer() {
        if (firstState) {
            String hourInput = hourText.getText().toString(); //시간입력값
            String minuteInput = minuteText.getText().toString(); //분입력값
            String secondInput = secondText.getText().toString(); //초입력값
            time = (Long.parseLong(hourInput) * 3600000) + (Long.parseLong(minuteInput) * 60000) + (Long.parseLong(secondInput) * 1000);
            // 시간은 3600x1000 ms라 1000곱해줌 분은 60*1000  초는 *1000
            progressBar.setMax((int) time);
            progressBar.setMin(0);
        } else {
            time = tempTime;
        }
        countDownTimer = new CountDownTimer(time, 1000) {
            //time, 을 1000 = 1초 씩 감소시킴
            @Override
            public void onTick(long millisUntilFinished) { //1초가 지날때마다 millsUntil~가 줄어듬
                tempTime = millisUntilFinished;
                updateTimer();
                if (millisUntilFinished <= 4000) {
                    MySoundPlayer.play(MySoundPlayer.BEEP);
                    //소리재생 삑
                }
            }
            @Override
            public void onFinish() {
                progressBar.setProgress(((int) time));
            }
        }.start();
        timerRunning = true;
        firstState = false;
    }

    //타이머 정지
    private void stopTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    //시간 업데이트
    private void updateTimer() {
        int hour = (int) tempTime / 3600000; // 전체에서 1시간으로 나누면 시간이 나옴
        int minute = (int) tempTime % 3600000 / 60000; //전체에서 1시간으로 나누면 나머지 에서 분으로 나눔 = 분
        int seconds = (int) tempTime % 3600000 % 60000 / 1000;// 초로 나누면 몫이 초
        if (timerRunning) {
            progressBar.setProgress((int) (time - tempTime));
        }
        if (hour < 10) {
            hourText.setText("0" + hour);
        } else {
            hourText.setText("" + hour);
        }
        if (minute < 10) {
            minuteText.setText("0" + minute);
        } else {
            minuteText.setText("" + minute);
        }
        if (seconds < 10) {
            secondText.setText("0" + seconds);
        } else {
            secondText.setText("" + seconds);
        }
    }

    //정지 시작
    private void startstop() {
        if (timerRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        startstop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "타이머를 일시 중지 했습니다", Toast.LENGTH_SHORT).show();
    }
}
