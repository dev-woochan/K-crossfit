package com.example.k_crossfit.BOX_MAP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.R;


import java.util.ArrayList;


public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.CustomViewHolder> {
    public ArrayList<BoxData> boxDataArrayList;
    private Context context;
    private double userLongitude;
    private double userLatitude;
    public class CustomViewHolder extends RecyclerView.ViewHolder{
        private TextView boxName;
        private TextView distance;
        private TextView link;
        private TextView address;
        private ImageView showMapBtn;
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
                        intent.putExtra("mapx", boxData.mapx);
                        intent.putExtra("mapy", boxData.mapy);
                        (context).startActivity(intent);
                    }
                }
            });
        }
    }

    BoxAdapter(ArrayList<BoxData> list,Context context,double latitude, double longitude){
        this.boxDataArrayList = list;
        this.context = context;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
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
//        Tm128 tm128 = new Tm128(Double.parseDouble(boxData.mapx),Double.parseDouble(boxData.mapy));
//        Log.d("gps", "mapx: "+boxData.mapx);
//        Log.d("gps", "mapy: "+boxData.mapy);
//        Log.d("gps", "userlatitude: "+userLatitude);
//        Log.d("gps", "userlongitude: "+userLongitude);
//        LatLng latLng = tm128.toLatLng();
//        Log.d("gps", "latitude: "+position+latLng.latitude);
//        Log.d("gps", "longitude: "+position+latLng.longitude);
//        LatLng userLatLng = new LatLng(userLatitude,userLongitude);
//        double distance = userLatLng.distanceTo(latLng);

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
