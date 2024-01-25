package com.example.k_crossfit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.fragment_map_view);
        if (mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_map_view,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(@NonNull NaverMap naverMap){

    }
}
