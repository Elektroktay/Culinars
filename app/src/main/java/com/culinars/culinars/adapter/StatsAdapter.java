package com.culinars.culinars.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.culinars.culinars.R;


public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {

    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_stat_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.stat_title.setText("Calories");
        holder.stat_value.setText("" + (position * 50));
        holder.stat_unit.setText("kcal");
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stat_title, stat_value, stat_unit;
        CardView stat_container;

        public ViewHolder(View itemView) {
            super(itemView);
            stat_title = (TextView) itemView.findViewById(R.id.stat_title);
            stat_value = (TextView) itemView.findViewById(R.id.stat_value);
            stat_unit = (TextView) itemView.findViewById(R.id.stat_unit);
            stat_container = (CardView) itemView.findViewById(R.id.stat_container);
        }
    }
}
