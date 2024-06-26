package com.hayk.healthmanagerregistration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MedRecyclerViewAdapter extends RecyclerView.Adapter<MedRecyclerViewAdapter.MyViewHolder> {

    private List<String> medNames;
    private List<String> times;
    private List<String> dates;
    private List<String> forms;
    private List<String> medIds;
    private Activity context;


    public MedRecyclerViewAdapter(Activity context, List<String> medNames, List<String> times, List<String> dates, List<String> forms, List<String> medIds) {
        this.context = context;
        this.dates = dates;
        this.medNames = medNames;
        this.times = times;
        this.forms = forms;
        this.medIds = medIds;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_list, parent, false);
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String medName = medNames.get(position);
        String medTime = times.get(position);
        String medDate = dates.get(position);
        String medForm = forms.get(position);
        String medId = medIds.get(position);
        holder.bind(medName, medTime, medDate, medForm, medId);
    }

    @Override
    public int getItemCount() {
        return medNames.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView medName;
        private TextView medTime;
        private TextView medDate;
        private Context context;
        private ImageView img;


        public MyViewHolder(@NonNull View itemView, Activity context) {
            super(itemView);
            this.context = context;
            medName = itemView.findViewById(R.id.medName);
            medTime = itemView.findViewById(R.id.med_time_text);
            medDate = itemView.findViewById(R.id.med_date_text);
            img = itemView.findViewById(R.id.medication_icon);


        }

        public void bind(String medNameText, String medTimeText, String medDateText, String medForm, String medId) {
            medName.setText(medNameText);
            String reminderLabel = context.getString(R.string.next_reminder);
            String nextReminder = reminderLabel + medTimeText;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MedInfoActivity.class);
                    intent.putExtra("medId", medId);
                    context.startActivity(intent);
                }
            });
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Date date = sdf.parse(medDateText);

                Calendar calendarToday = Calendar.getInstance();
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.setTime(date);

                if (isSameDay(calendarToday, calendarDate)) {
                    medDateText = context.getResources().getString(R.string.today);
                } else if (isNextDay(calendarToday, calendarDate)) {
                    medDateText = context.getResources().getString(R.string.tomorrow);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            medDate.setText(medDateText);
            medTime.setText(nextReminder);
            if (medDateText == null) {
                medDate.setVisibility(View.GONE);
            }
            if (medTimeText == null){
                medTime.setVisibility(View.GONE);
            }
            Drawable pill_img = context.getResources().getDrawable(R.drawable.pill_list_img);
            Drawable injection_img = context.getResources().getDrawable(R.drawable.injection_list_img);
            Drawable solution_img = context.getResources().getDrawable(R.drawable.solution_list_img);
            Drawable drops_img = context.getResources().getDrawable(R.drawable.drops_list_img);
            Drawable powder_img = context.getResources().getDrawable(R.drawable.solution_list_img);
            Drawable other_img = context.getResources().getDrawable(R.drawable.other_list_img);

            switch (medForm) {
                case "pill":
                    img.setImageDrawable(pill_img);
                    break;
                case "injection":
                    img.setImageDrawable(injection_img);
                    break;
                case "solution":
                    img.setImageDrawable(solution_img);
                    break;
                case "drops":
                    img.setImageDrawable(drops_img);
                    break;
                case "powder":
                    img.setImageDrawable(powder_img);
                    break;
                default:
                    img.setImageDrawable(other_img);
            }

        }
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isNextDay(Calendar cal1, Calendar cal2) {
        cal1.add(Calendar.DAY_OF_MONTH, 1);
        return isSameDay(cal1, cal2);
    }
}
