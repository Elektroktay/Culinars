package com.culinars.culinars.adapter.recipe;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.culinars.culinars.R;

public class RecipeFactsAdapter extends RecyclerView.Adapter<RecipeFactsAdapter.ViewHolder> {

    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        context = parent.getContext();
        v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_recipe_facts_card, parent, false);
/*        else {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_recipe_facts_buttons, parent, false);
        }*/

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            holder.facts_title.setText("Calories");
            holder.facts_amount.setText((position * 100) + " kcal");
            if (position % 2 == 0) {
                holder.facts_card.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.facts_card.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
            }
        }
        //holder.facts_checkbox.setChecked(position%2==0);
    }

    @Override
    public int getItemCount() {
        return 13;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView facts_card;
        //ImageView facts_image;
        TextView facts_title, facts_amount;
        //AppCompatCheckBox facts_checkbox;
        FrameLayout facts_container;

        public ViewHolder(View itemView) {
            super(itemView);
            facts_card = (CardView) itemView.findViewById(R.id.recipe_facts_card);
            //facts_image = (ImageView) itemView.findViewById(R.id.recipe_facts_image);
            facts_title = (TextView) itemView.findViewById(R.id.recipe_facts_title);
            facts_amount = (TextView) itemView.findViewById(R.id.recipe_facts_amount);
            //facts_checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.recipe_facts_checkbox);
            facts_container = (FrameLayout) itemView.findViewById(R.id.recipe_facts_container);
        }
    }
}
