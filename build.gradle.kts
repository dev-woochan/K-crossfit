import org.apache.tools.ant.util.JavaEnvUtils.VERSION_1_8
import java.util.regex.Pattern.compile

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.k_crossfit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.k_crossfit"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.4.32"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true;
    }
    buildToolsVersion = "33.0.1"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs/gradle-wrapper.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //Gson (json 변환기)
    implementation("com.google.code.gson:gson:2.10.1")
    //mpChart sdk
    implementation("com.github.Philjay:MPAndroidChart:v3.0.2")
    //네이버 지도 sdk
    implementation("com.naver.maps:map-sdk:3.17.0")
    //fragment
    val fragment_version = "1.6.2"
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
}





