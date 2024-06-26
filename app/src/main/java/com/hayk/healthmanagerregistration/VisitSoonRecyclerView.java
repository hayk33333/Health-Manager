package com.hayk.healthmanagerregistration;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class VisitSoonRecyclerView extends RecyclerView.Adapter<VisitSoonRecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private VisitSoonRecyclerView.ItemClickListener mClickListener;

    private Context context;
    private List<String> visitNames;
    private List<String> visitDates;
    private List<String> visitIds;
    private List<String> visitTimes;
    private FirebaseFirestore db;

    public VisitSoonRecyclerView(Context context, List<String> visitNames, List<String> visitDates, List<String> visitIds, List<String> visitTimes) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.visitDates = visitDates;
        this.visitNames = visitNames;
        this.visitIds = visitIds;
        this.visitTimes = visitTimes;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public VisitSoonRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.visit_soon_list, parent, false);
        return new VisitSoonRecyclerView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.visitName.setText(visitNames.get(position));
        int day = Integer.parseInt(visitDates.get(position));
        if (day == 0) {
            holder.visitDate.setText(context.getString(R.string.today) + "\n" + visitTimes.get(position));
        } else if (day == 1) {
            holder.visitDate.setText(context.getString(R.string.tomorrow) + "\n" + visitTimes.get(position));
        } else if (day < 0){
            holder.visitDate.setText(Math.abs(day) + " " + context.getString(R.string.days_ago) + "\n" + visitTimes.get(position));
        } else {
            holder.visitDate.setText(context.getString(R.string.in) + " " + day + " " + context.getString(R.string.day) + "\n" + visitTimes.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return visitNames.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView visitName;
        private TextView visitDate;
        private Button done;


        ViewHolder(View itemView) {
            super(itemView);
            visitDate = itemView.findViewById(R.id.visit_date);
            visitName = itemView.findViewById(R.id.visit_name);
            done = itemView.findViewById(R.id.done);
            done.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (view.getId() == R.id.done) {
                    mClickListener.onDoneClick(view, getAdapterPosition(),visitIds.get(getAdapterPosition()));
                }
            } else {

            }
        }
    }



    public void setClickListener(VisitSoonRecyclerView.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }



    public interface ItemClickListener {
        void onDoneClick(View view, int position, String id);

    }
}

