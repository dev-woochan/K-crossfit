package com.example.k_crossfit.login;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

public class KakaoApplication extends Application {
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(getApplicationContext(),"e044b26be68c7e643e493306d4b62576");
    }
}
