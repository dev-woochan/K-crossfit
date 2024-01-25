package com.example.k_crossfit.Data;//package com.example.k_crossfit;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkoutData implements Parcelable {
    public String workoutName;
    public String workoutCount;
    public String workoutWeight;
    public String workoutUnit;
    private boolean isSelected;
    // 생성자, getter, setter 등 필요한 메서드들을 구현

    // 일반적인 생성자
    public WorkoutData(String workoutName, String workoutCount, String workoutWeight, String workoutUnit) {
        this.workoutName = workoutName;
        this.workoutCount = workoutCount;
        this.workoutWeight = workoutWeight;
        this.workoutUnit = workoutUnit;
        this.isSelected = false;
    }

    protected WorkoutData(Parcel in) {
        workoutName = in.readString();
        workoutCount = in.readString();
        workoutWeight = in.readString();
        workoutUnit = in.readString();
        isSelected = in.readByte() != 0; // isSelected == true if byte != 0
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Parcelable을 구현하는 부분
    public static final Parcelable.Creator<WorkoutData> CREATOR = new Parcelable.Creator<WorkoutData>() {
        @Override
        public WorkoutData createFromParcel(Parcel in) {
            return new WorkoutData(in);
        }

        @Override
        public WorkoutData[] newArray(int size) {
            return new WorkoutData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(workoutName);
        dest.writeString(workoutCount);
        dest.writeString(workoutWeight);
        dest.writeString(workoutUnit);
        dest.writeByte((byte) (isSelected ? 1 : 0)); // isSelected is true if byte == 1
    }

}
