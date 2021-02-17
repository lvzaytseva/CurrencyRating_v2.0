package com.example.currencyratingv20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.currencyratingv20.data.Currency;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CurrAdapter extends RecyclerView.Adapter<CurrAdapter.CurrViewHolder> {
    private final List<Currency> currList;

    public CurrAdapter(List<Currency> currList) {
        this.currList = currList;
    }

    @NonNull
    @Override
    public CurrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.curr_item, parent, false);
        return new CurrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrViewHolder holder, int position) {
        Currency currency = currList.get(position);
        holder.textViewName.setText(currency.getName());
        holder.textViewPrice.setText(String.format(Locale.US, "%.2f", currency.getPrice()));
        holder.textViewDynamics.setText(String.format(Locale.US, "%.2f", currency.getDynamics()));
    }

    @Override
    public int getItemCount() {
        return currList.size();
    }

    static class CurrViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewName;
        private final TextView textViewPrice;
        private final TextView textViewDynamics;

        public CurrViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDynamics = itemView.findViewById(R.id.textViewDynamics);
        }
    }
}
