package com.hayk.healthmanagerregistration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // Конструктор, который принимает список данных для отображения в RecyclerView
    CustomAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // Создаем новый элемент списка и его ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Заменяем содержимое элемента списка данными из списка
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.timeButton.setText(item);

        }

    // Возвращает общее количество элементов списка
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Хранит и отображает каждый элемент списка
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button timeButton;
        Button pillsButton;

        ViewHolder(View itemView) {
            super(itemView);
            timeButton = itemView.findViewById(R.id.time_button);
            pillsButton = itemView.findViewById(R.id.pills_button);

            timeButton.setOnClickListener(this);
            pillsButton.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if(view.getId() == R.id.time_button ){
                    mClickListener.onTimeButtonClick(view, getAdapterPosition());
                }
                else if(view.getId() == R.id.pills_button ){
                    mClickListener.onPillsButtonClick(view, getAdapterPosition());

                }
            }
        }




    }
    // Позволяет клиентскому коду устанавливать обработчик нажатия элемента списка
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public void updateData(List<String> newData) {
        this.mData = newData;
        notifyDataSetChanged(); // Этот метод уведомляет адаптер о том, что данные изменились, и список нужно обновить
    }


    // Интерфейс для обработки нажатий элементов списка
    public interface ItemClickListener {
        void onTimeButtonClick(View view, int position);
        void onPillsButtonClick(View view, int position);
    }
}

