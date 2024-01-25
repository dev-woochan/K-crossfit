package com.example.k_crossfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.widget.Toast;


public class BoxActivity extends AppCompatActivity {
    private TextView loadAddress;
    private Button searchBox;
    private ImageView gpsBtn;
    private ImageView mapBtn;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        mapBtn = findViewById(R.id.imageview_box_mapBtn);

        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                editor.putBoolean("fineLocationGranted", true);
                                editor.commit();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                editor.putBoolean("coarseLocationGranted", true);
                                editor.commit();
                            } else {
                                Toast.makeText(this, "위치권한을 허가해야 내위치 불러오기 기능이 활성화 됩니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );


        if (setting.getBoolean("fineLocationGranted", false)) {
            //위치정보 허가 있는경우 그냥진행
        } else { //허가 없는경우 권한요청
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }


}
