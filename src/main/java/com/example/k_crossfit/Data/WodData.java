package com.example.k_crossfit.Data;


//와드 클래스 그날의 와드를 생성할시에 와드정보들을 객체로 만들어줄 예정


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.k_crossfit.etc.UriTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WodData implements Parcelable {
    public String wodDate;
    public String wodName;
    public Uri wodPhoto;
    public ArrayList<WorkoutData> workoutDataArrayList;
    public String memo;
    private String key;

    public WodData(String wodDate,String wodName, ArrayList<WorkoutData> workoutDataArrayList, String memo, Uri wodPhoto){
        this.memo = memo;
        this.wodName = wodName;
        this.wodPhoto = wodPhoto;
        this.workoutDataArrayList = workoutDataArrayList;
        this.wodDate = wodDate;
    }

    public void setKey (String key){
        //와드 데이터 불러오는 키로 사용할 키값
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }

    public WodData(Parcel in){
       this.wodDate = in.readString();
        //날짜
        this.wodName = in.readString();
        //이름
        this.workoutDataArrayList = in.createTypedArrayList(WorkoutData.CREATOR);
        //운동 모은 어레이리스트
        this.wodPhoto = in.readParcelable(Uri.class.getClassLoader());
        //사진 Uri
        this.memo = in.readString();
    }

    public static final Creator<WodData> CREATOR = new Creator<WodData>() {
        @Override
        public WodData createFromParcel(Parcel in) {
            return new WodData(in);
        }

        @Override
        public WodData[] newArray(int size) {
            return new WodData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(wodDate);
        dest.writeString(wodName);
        dest.writeTypedList(workoutDataArrayList);
        dest.writeParcelable(wodPhoto, flags);
        dest.writeString(memo);
    }

    public static Integer getSize(String json){
        //와드 기록 수량을 불러오는 메소드
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriTypeAdapter()).create();
        if (json.equals("없음")){
            return 0;
        }else {
        Type type = new TypeToken<ArrayList<WodData>>() {
        }.getType();
        ArrayList<WodData> list = gson.fromJson(json,type);
        return list.size();
    }}


}

