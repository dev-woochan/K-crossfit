package com.example.k_crossfit.Calendar;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_crossfit.Data.WorkoutData;
import com.example.k_crossfit.R;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    WorkoutAdapter(ArrayList<WorkoutData> list) {
        workoutDataArrayList = list;
    }

    public ArrayList<WorkoutData> workoutDataArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected EditText workoutName;
        protected EditText workoutWeight;
        protected EditText workoutCount;
        protected EditText workoutUnit;
        protected CheckBox workoutSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            this.workoutCount = itemView.findViewById(R.id.editText_workoutItem_count);
            this.workoutName = itemView.findViewById(R.id.editText_workoutItem_workoutName);
            this.workoutWeight = itemView.findViewById(R.id.editText_workoutItem_weight);
            this.workoutUnit = itemView.findViewById(R.id.editText_workoutItem_unit);
            this.workoutSelect = itemView.findViewById(R.id.checkbox_workoutItem_select);

            workoutCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    String enteredText = s.toString();
                    int position = getAdapterPosition();
                    WorkoutData workoutData = workoutDataArrayList.get(position);

                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            String count = enteredText;
                            workoutData.workoutCount = count;
                        } catch (Exception e) {
                            Log.d("workoutCount", "afterTextChanged: " + e);
                        }
                    }
                }
            });

            workoutName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    String enteredText = s.toString();
                    int position = getAdapterPosition();
                    WorkoutData workoutData = workoutDataArrayList.get(position);
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            workoutData.workoutName = enteredText;
                        } catch (Exception e) {
                            Log.d("workoutName", "afterTextChanged: " + e);
                        }
                    }
                }
            });


            workoutWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    String enteredText = s.toString();
                    int position = getAdapterPosition();
                    WorkoutData workoutData = workoutDataArrayList.get(position);
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            String weight = enteredText;
                            workoutData.workoutWeight = weight;
                        } catch (Exception e) {
                            Log.d("workoutWeight", "afterTextChanged: " + e);
                        }
                    }
                }
            });

            workoutUnit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    String enteredText = s.toString();
                    int position = getAdapterPosition();
                    WorkoutData workoutData = workoutDataArrayList.get(position);
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            workoutData.workoutUnit = enteredText;
                        } catch (Exception e) {
                            Log.d("workoutUnit", "afterTextChanged: " + e);
                        }
                    }
                }
            });

            workoutSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    WorkoutData workoutData = workoutDataArrayList.get(position);
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            workoutData.setSelected(isChecked);
                        } catch (Exception e) {
                            Log.d("workoutWeight", "afterTextChanged: " + e);
                        }
                    }
                }
            });
        }
    }

    public void deleteSelectedItems() {
        for (int i = workoutDataArrayList.size() - 1; i >= 0; i--) {
            if (workoutDataArrayList.get(i).isSelected()) {
                workoutDataArrayList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public ArrayList<WorkoutData> saveWorkoutData() {
        return workoutDataArrayList;
    }

    public WorkoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_workout, parent, false);
        WorkoutAdapter.ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public void onBindViewHolder(@NonNull WorkoutAdapter.ViewHolder holder, int position) {
        WorkoutData data = workoutDataArrayList.get(position);
        if (data != null) {
            try {
                holder.workoutName.setText(data.workoutName);
                holder.workoutCount.setText(data.workoutCount);
                holder.workoutWeight.setText(data.workoutWeight);
                holder.workoutUnit.setText(data.workoutUnit);
                holder.workoutSelect.setChecked(data.isSelected());
            } catch (Exception e) {
                Log.d("workoutAdapter", "onBindViewHolder: " + e);
            }
        }


    }

    public void updateList(ArrayList<WorkoutData> list) {
        workoutDataArrayList.clear();
        workoutDataArrayList = list;
        notifyDataSetChanged();
    }


    public int getItemCount() {
        return workoutDataArrayList.size();
    }


}
