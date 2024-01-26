package com.example.k_crossfit.BOX_MAP;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.k_crossfit.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        //프레그먼트 인스턴스를 생성해주는 코드 (프레그먼트에 관한 공부 필요(적당히만))
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.fragment_map_view);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_map_view, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        //네이버 지도 FusedLocationSource권한 허가를 위한것
        locationSource =
                new FusedLocationSource(this, 1000);
    }

    //위치 받아오기 클릭시 실행될 퍼미션 코드 자동으로 호출된다.
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


    //onMapReady는 지도 불러오는게 준비되었을때 실행된다 여기서 onCREATE 처럼 지도에 추가할 기능들, 포커스(카메라) 좌표등을 설정해줄수있다.
    // 좌표데이터가있는 객체를선택해서 지도를 불러올때 이곳에서 좌표값을 지정해서 카메라를 고정해줄 예정임
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        //UI세팅 인스턴스 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setLogoClickEnabled(true);
        //UI세팅 끝 컴파스, 확대버튼, 내위치 활성화 버튼 추가한것임
        naverMap.setLocationSource(locationSource);
        //대중교통 삭제 (느려지는것같아서 필요없는것 삭제한것임)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false);
        //위치정보 인텐트로 받아서 인텐트로 위치시키기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("latitude")){
            //인텐트에서 좌표값 받아옴 LatLng는 네이버 지도의 좌표값 클래스임
            LatLng latLng = new LatLng(intent.getDoubleExtra("latitude",0.0),intent.getDoubleExtra("longitude",0.0));
            //밑에 두개는 카메라가 이동하는것같아서 일단뺌
//            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng);
//            naverMap.moveCamera(cameraUpdate);
            //세팅은 밑에가 맞는듯 ㅇㅇ
            CameraPosition cameraPosition = new CameraPosition(latLng,10);
            naverMap.setCameraPosition(cameraPosition);


        }

    }

}
