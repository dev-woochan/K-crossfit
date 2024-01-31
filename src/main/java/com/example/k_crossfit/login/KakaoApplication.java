package com.example.k_crossfit.login;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(getApplicationContext(),"e044b26be68c7e643e493306d4b62576");
    }
}
