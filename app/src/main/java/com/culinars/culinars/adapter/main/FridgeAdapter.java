package com.culinars.culinars.adapter.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.ReferenceMultiple;
import com.culinars.culinars.data.structure.Ingredient;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {

    Context context;
    int dataLimit;
    private ReferenceMultiple<Ingredient> data;


    public FridgeAdapter() {
        dataLimit = 100;
        refreshData("");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_fridge_cards, parent, false);
        return new ViewHolder(v);
    }

    public void refreshData(String completionText) {
        data = DataManager.getInstance().findIngredient(completionText, dataLimit);
        data.addOnDataChangeListener(new ReferenceMultiple.OnDataChangeListener<Ingredient>() {
            @Override
            public void onDataChange(Ingredient newValue, int event) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (DataManager.getInstance().hasIngredient(data.getValueAt(position).name))
            holder.fridge_check.setImageResource(R.drawable.check_green);
        else
            holder.fridge_check.setImageResource(R.drawable.check_white);

        holder.fridge_text.setText(data.getValueAt(position).name);
        holder.fridge_image.setImageResource(R.drawable.cooking_prep3);
        holder.fridge_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataManager.getInstance().hasIngredient(data.getValueAt(position).name)) {
                    DataManager.getInstance().setIngredient(data.getValueAt(position).name, false);
                    holder.fridge_check.setImageResource(R.drawable.check_white);
                } else {
                    DataManager.getInstance().setIngredient(data.getValueAt(position).name, true);
                    holder.fridge_check.setImageResource(R.drawable.check_green);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.getValues().size();
        else
            return 5;
    }

    public void updateSearchParams(String searchQuery) {
        if (searchQuery.length() > 1) {
            String firstLetter = searchQuery.substring(0, 1);
            String rest = searchQuery.substring(1);
            refreshData(firstLetter.toUpperCase() + rest);
        } else {
            refreshData(searchQuery);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView fridge_container;
        ImageView fridge_image, fridge_check;
        TextView fridge_text;

        public ViewHolder(View itemView) {
            super(itemView);
            fridge_container = (CardView) itemView.findViewById(R.id.card_fridge_container);
            fridge_image = (ImageView) itemView.findViewById(R.id.card_fridge_image);
            fridge_text = (TextView) itemView.findViewById(R.id.card_fridge_text);
            fridge_check = (ImageView) itemView.findViewById(R.id.card_fridge_check);
        }
    }
}
