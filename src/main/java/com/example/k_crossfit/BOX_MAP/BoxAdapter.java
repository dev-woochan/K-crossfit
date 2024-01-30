package com.example.k_crossfit.BOX_MAP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.NaverMap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.CustomViewHolder> {
    public ArrayList<BoxData> boxDataArrayList;
    private Context context;
    public class CustomViewHolder extends RecyclerView.ViewHolder{
        private TextView boxName;
        private TextView distance;
        private TextView link;
        private TextView address;
        private ImageView showMapBtn;
        double latitude;
        double longitude;

        public CustomViewHolder(View itemView){
            super(itemView);
            this.boxName = itemView.findViewById(R.id.textview_boxItem_boxName);
//            this.distance = itemView.findViewById(R.id.textview_boxItem_km);
            this.link = itemView.findViewById(R.id.textview_boxItem_link);
            this.address = itemView.findViewById(R.id.textview_boxItem_address);
            this.showMapBtn = itemView.findViewById(R.id.imageview_boxItem_showMapBtn);

            showMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos  = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        BoxData boxData = boxDataArrayList.get(pos);
                        Intent intent;
                        intent = new Intent(context, MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //인텐트로 mapxy
                        intent.putExtra("title", boxData.title);
                        (context).startActivity(intent);
                    }
                }
            });
        }
    }

    BoxAdapter(ArrayList<BoxData> list,Context context,double latitude, double longitude){
        this.boxDataArrayList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BoxAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //onCreate에서는 레이아웃이랑 뷰를 연동하고 뷰홀더를 만들기만함
        Context context = parent.getContext();
        // 현재 실행중인 콘텍스트를  가져옴
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 레이아웃 인플레이터 : xm파일을 뷰에 연동시킴
        View view = inflater.inflate(R.layout.item_box,parent,false);
        BoxAdapter.CustomViewHolder vh = new BoxAdapter.CustomViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BoxAdapter.CustomViewHolder holder, int position) {
        BoxData boxData = boxDataArrayList.get(position);




        holder.boxName.setText(boxData.title.replaceAll("<b>","").replaceAll("</b>",""));
        holder.link.setText(boxData.link);
        holder.address.setText(boxData.address);
//        holder.distance.setText(String.valueOf(distance));
    }

    @Override
    public int getItemCount() {
        return boxDataArrayList.size();
    }


}
