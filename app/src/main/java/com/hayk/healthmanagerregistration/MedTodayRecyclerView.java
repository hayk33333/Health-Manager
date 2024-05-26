package com.hayk.healthmanagerregistration;


import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class MedTodayRecyclerView extends RecyclerView.Adapter<MedTodayRecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private MedTodayRecyclerView.ItemClickListener mClickListener;
    private List<String> medNames;
    private List<String> medIds;
    private List<String> doseCounts;
    private List<String> doseTypes;
    private List<Long> differenceInMills;
    private Context context;
    private FirebaseFirestore db;

    public MedTodayRecyclerView(Context context, List<String> medIds, List<String> medNames, List<Long> differenceInMills, List<String> doseCounts, List<String> doseTypes) {
        this.mInflater = LayoutInflater.from(context);
        this.medNames = medNames;
        this.medIds = medIds;
        this.differenceInMills = differenceInMills;
        this.doseCounts = doseCounts;
        this.doseTypes = doseTypes;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public MedTodayRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.med_today_list, parent, false);
        return new MedTodayRecyclerView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.medNameTV.setText(medNames.get(position));
        long timeInMills = differenceInMills.get(position);
        String doseType = doseTypes.get(position);
        switch (doseType) {
            case "pill(s)":
                doseType = context.getResources().getString(R.string.pill_s);
                break;
            case "mL":
                doseType = context.getResources().getString(R.string.ml);
                break;
            case "Syringe(s)":
                doseType = context.getResources().getString(R.string.syringe_s);
                break;

            case "Unit":
                doseType = context.getResources().getString(R.string.unit);
                break;
            case "Ampules(s)":
                doseType = context.getResources().getString(R.string.ampules_s);
                break;
            case "Vial(s)":
                doseType = context.getResources().getString(R.string.vial_s);
                break;
            case "Cup(s)":
                doseType = context.getResources().getString(R.string.cup_s);
                break;
            case "Drop(s)":
                doseType = context.getResources().getString(R.string.drop_s);
                break;
            case "Packet(s)":
                doseType = context.getResources().getString(R.string.packet_s);
                break;
            case "Gram(s)":
                doseType = context.getResources().getString(R.string.gram_s);
                break;
            case "Tablespoon(s)":
                doseType = context.getResources().getString(R.string.tablespoon_s);
                break;
            case "Teaspoon(s)":
                doseType = context.getResources().getString(R.string.teaspoon_s);
                break;
        }
        if (timeInMills <= 0) {
            holder.time.setText("00:00");
        } else {
            startTimer(timeInMills, holder, doseType, doseCounts.get(position));
        }

    }
    private void startTimer(long timeInMillis, ViewHolder holder, String doseType, String doseCount) {
        CountDownTimer countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished / (1000 * 60 * 60));
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int seconds = (int) ((millisUntilFinished / 1000) % 60);
                holder.time.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds) + "\n" + doseCount + " " + doseType);
            }

            public void onFinish() {
                holder.time.setText("00:00:00");
            }
        }.start();
    }


    @Override
    public int getItemCount() {
        return medNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView medNameTV;
        private TextView time;
        private TextView take;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.med_time_text);
            medNameTV = itemView.findViewById(R.id.med_name);
            take = itemView.findViewById(R.id.take);
            take.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (view.getId() == R.id.take) {
                    mClickListener.onTakeClick(view, getAdapterPosition());
                    onTake(medIds.get(getAdapterPosition()), doseCounts.get(getAdapterPosition()));
                }
            } else {

            }
        }
    }

    private void onTake(String medId, String doseCount) {

        setTextAlarm(medId);
        updateMedCount(context, medId, Integer.parseInt(doseCount));
        setIsTake(medId, true);

    }

    private void setTextAlarm(String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            AlarmReceiver alarmReceiver = new AlarmReceiver();
                            alarmReceiver.setNextAlarm(context, medId, documentSnapshot.getString("medFrequency"));
                        } else {
                            System.out.println("med does not exists");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("error to read from db");
                    }
                });

    }

    private void setIsTake(String medId, boolean isTake) {
        CollectionReference medsCollection = db.collection("meds");
        HashMap<String, Object> data = new HashMap<>();
        data.put("isTake", isTake);
        medsCollection.document(medId).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void updateMedCount(Context context, String medId, int doseCount) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            try {
                                long count = documentSnapshot.getLong("medCount");
                                long remindCount = documentSnapshot.getLong("medRemindCount");


                                count -= doseCount;
                                if (count < 0) {
                                    count = 0;
                                }
                                if (count <= remindCount) {

                                   getNotificationText(context, medId, (int) (remindCount));
                                }
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("medCount", count);

                                medsCollection.document(medId).update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Error updating document: " + e);
                                            }
                                        });


                            } catch (Exception e) {
                                System.out.println("catch" + e.getMessage());
                            }


                        } else {
                            System.out.println("chka");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error reading document: " + e);
                    }
                });
    }
    private void getNotificationText(Context context, String medId, int count) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String doseType = documentSnapshot.getString("doseType");
                            String medName = documentSnapshot.getString("medName");
                            String text = "Only " + count + " " + doseType + " left of " + medName + ". It's time to stock up.";
                            AlarmReceiverNotification alarm = new AlarmReceiverNotification();
                            alarm.setAlarm(context, 10, text, "MED_RUN_OUT", medId, count);


                        } else {
                            System.out.println("chka");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error reading document: " + e);
                    }
                });

    }
    public void setClickListener(MedTodayRecyclerView.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }



    public interface ItemClickListener {
        void onTakeClick(View view, int position);
    }
}

