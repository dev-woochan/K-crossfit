package com.example.k_crossfit.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.R;
import com.example.k_crossfit.Data.WodData;

import java.util.ArrayList;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CustomViewHolder> {

    public ArrayList<WodData> wodDataArrayList;
    private Context context;
    //뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView wodPhoto;
        protected TextView wodName;
        protected TextView wodDate;

        //뷰홀더 생성자 View를 파라미터로 받아 연동한다.
        public CustomViewHolder(View itemView) {
            super(itemView);
            this.wodName = itemView.findViewById(R.id.textview_item_wodName);
            this.wodPhoto = itemView.findViewById(R.id.imageView_item_wodPhoto);
            this.wodDate = itemView.findViewById(R.id.textView_item_wodDate);
            //아이템 뷰 클릭시 동작 ( 데이터 바탕으로 addingWodActivity생성 시켜주기)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        WodData clickWodData = wodDataArrayList.get(pos);
                        Intent loadWod = new Intent(context, AddingWodActivity.class);
                        //액티비티가 아닌 컨텍스트에서는 플래그를 사용해야한다
                        loadWod.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        //인텐트로 와드 넘버, 키를 보내줌
                        loadWod.putExtra("key", clickWodData.getKey());
                        loadWod.putExtra("wodNumber", pos);
                        int loadWodCode = 120;
                        ((Activity) context).startActivityForResult(loadWod, loadWodCode);
                    }
                }
            });
        }
    }

    //생성자
    CalendarAdapter(ArrayList<WodData> list, Context context) {
        this.wodDataArrayList = list;
        this.context = context;
    }

    public CalendarAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //onCreate에서는 레이아웃이랑 뷰를 연동하고 뷰홀더를 만들기만함
        Context context = parent.getContext();
        // 현재 실행중인 콘텍스트를  가져옴
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 레이아웃 인플레이터 : xm파일을 뷰에 연동시킴
        View view = inflater.inflate(R.layout.item_wod, parent, false);
        // view에 인플레이터로 item_wod(아이템뷰) 연동시키기
        CalendarAdapter.CustomViewHolder vh = new CalendarAdapter.CustomViewHolder(view);
        //vh 뷰홀더 만들기
        return vh;
    }

    public void onBindViewHolder(@NonNull CalendarAdapter.CustomViewHolder holder, int position) {
        //data 는 각 리스트의 와드들
        WodData data = wodDataArrayList.get(position);
        //wod Title = 와드이름
        holder.wodName.setText(data.wodName);
        //wodPhoto에 Uri 지정
        holder.wodPhoto.setImageURI(data.wodPhoto);
        //memo에 memo넣기 !
        holder.wodDate.setText(data.wodDate);
        //
    }

    @Override
    public int getItemCount() {
        //와드의 숫자만큼 표시
        return wodDataArrayList.size();
    }

    public void updateData(ArrayList<WodData> list) {
        this.wodDataArrayList = list;
        notifyDataSetChanged();
    }
}


