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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import AddMedFragments.CustomAdapter;

public class VisitRecyclerViewAdapter extends RecyclerView.Adapter<VisitRecyclerViewAdapter.MyViewHolder> {

    private List<String> visitNames;

    private Activity context;
    private List<String> dates;
    private List<String> doctorNames;
    private List<String> hospitalNames;
    private static List<String> visitIds;


    public VisitRecyclerViewAdapter(Activity context, List<String> visitNames, List<String> dates, List<String> doctorNames, List<String> hospitalNames, List<String> visitIds) {
        this.context = context;
        this.visitNames = visitNames;
        this.dates = dates;
        this.doctorNames = doctorNames;
        this.hospitalNames = hospitalNames;
        this.visitIds = visitIds;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_list, parent, false);
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String visitName = visitNames.get(position);
        String visitDate = dates.get(position);
        String hospitalName = hospitalNames.get(position);
        String doctorName = doctorNames.get(position);
        String visitId = visitIds.get(position);
        System.out.println(visitId);

        holder.bind(visitName, visitDate, doctorName,hospitalName, visitId);
    }

    @Override
    public int getItemCount() {
        return visitNames.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView visitName;
        private TextView hospitalName;
        private TextView doctorName;

        private Context context;
        private TextView date;


        public MyViewHolder(@NonNull View itemView, Activity context) {
            super(itemView);
            this.context = context;
            hospitalName = itemView.findViewById(R.id.hospital_name);
            doctorName = itemView.findViewById(R.id.doctor_name);
            visitName = itemView.findViewById(R.id.visit_name);
            date = itemView.findViewById(R.id.visit_date);




        }

        public void bind(String visitNameText, String dateText, String doctorNameText, String hospitalNameText, String visitId) {
            visitName.setText(visitNameText);
            if (hospitalNameText.isEmpty()){
                hospitalName.setVisibility(View.GONE);
            }
            if (doctorNameText.isEmpty()){
                doctorName.setVisibility(View.GONE);
            }
            hospitalName.setText("Hospital:" + hospitalNameText);
            doctorName.setText("Doctor:" + doctorNameText);
            date.setText(dateText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VisitInfoActivity.class);

                    intent.putExtra("visitId", visitId);
                    context.startActivity(intent);
                }
            });
//            try {
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                Date date = sdf.parse();
//
//                Calendar calendarToday = Calendar.getInstance();
//                Calendar calendarDate = Calendar.getInstance();
//                calendarDate.setTime(date);
//
//                if (isSameDay(calendarToday, calendarDate)) {
//                    medDateText = "Today";
//                } else if (isNextDay(calendarToday, calendarDate)) {
//                    medDateText = "Tomorrow";
//                }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }




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
