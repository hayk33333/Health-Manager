package AddMedFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hayk.healthmanagerregistration.R;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String> mData;
    private List<String> mDoseData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String doseType;

    public CustomAdapter(Context context, List<String> data, String doseType, List<String> doseData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.doseType = doseType;
        this.mDoseData = doseData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.timeButton.setText(item);
        String cString = String.valueOf(position + 1) + ".";
        holder.count.setText(cString);
        holder.doseType.setText(doseType);
        holder.doseCount.setText(mDoseData.get(position));


    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button timeButton;
        Button doseCount;
        TextView count;
        TextView doseType;

        ViewHolder(View itemView) {
            super(itemView);
            timeButton = itemView.findViewById(R.id.time_button);
            doseCount = itemView.findViewById(R.id.dose_count);
            doseType = itemView.findViewById(R.id.dose_types);
            count = itemView.findViewById(R.id.count);
            timeButton.setOnClickListener(this);
            doseCount.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if(view.getId() == R.id.time_button ){
                    mClickListener.onTimeButtonClick(view, getAdapterPosition());
                }
                else if(view.getId() == R.id.dose_count ){
                    mClickListener.onDoseCountButtonClick(view, getAdapterPosition());

                }
            }
        }




    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }



    public interface ItemClickListener {
        void onTimeButtonClick(View view, int position);
        void onDoseCountButtonClick(View view, int position);
    }
}

