package com.culinars.culinars.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.culinars.culinars.R;

import java.util.HashMap;
import java.util.Map;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    Context context;
    Map<Integer, Boolean> cardFlipStates;

    public RecommendationsAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recommendations_card, parent, false);
        context = parent.getContext();
        cardFlipStates = new HashMap<>();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position > 0) {
            cardFlipStates.put(position, false);
            holder.logoView.setVisibility(View.GONE);
            holder.titleView.setText("Very Long Recipe Title of DOOM " + position);
            AssetManager am = context.getApplicationContext().getAssets();
            holder.titleView.setTypeface(Typeface.createFromAsset(am, "fonts/segoe_ui_light.ttf"));
            holder.imageView.setImageResource(R.drawable.pizza);
            int points = 7;
            for (ImageView img : holder.stars) {
                if (points > 1) {
                    img.setImageResource(R.drawable.ic_star_white_48dp);
                    points -= 2;
                } else if (points == 1) {
                    img.setImageResource(R.drawable.ic_star_half_white_48dp);
                    points -= 1;
                } else {
                    img.setImageResource(R.drawable.ic_star_border_white_48dp);
                }
            }

            holder.cardFrontContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipCard(holder, holder.getAdapterPosition());
                }
            });

            //Back
            holder.cardShortDescription.setText(R.string.large_text);
            holder.cardFactsView.setHasFixedSize(true);
            holder.cardFactsView.setAdapter(new StatsAdapter());
            holder.cardFactsView.setLayoutManager(new LinearLayoutManager(context));
            holder.cardFactsView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    holder.cardFactsView.smoothScrollBy(0, 1);
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        } else {
            holder.container.setVisibility(View.GONE);
        }
    }

    private void flipCard(ViewHolder holder, final int position) {
        final Integer pos = position;
        if (cardFlipStates.get(pos) == null || cardFlipStates.get(pos).equals(false)) {
            openCard(holder, position);
        } else if (cardFlipStates.get(pos).equals(true)) {
            closeCard(holder, position);
        }
    }

    //Front goes down.
    public void closeCard(ViewHolder holder, int position) {
        holder.titleView.setSingleLine(false);
        holder.cardFrontContainer.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

        for (ImageView img : holder.stars) {
            img.animate()
                    .alpha(1)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }
        cardFlipStates.put(position, false);
    }

    //Front goes up.
    public void openCard(ViewHolder holder, int position) {
        holder.titleView.setSingleLine(true);
        holder.cardFrontContainer.animate()
                .translationY(-(int) (holder.cardFrontContainer.getHeight() * 0.7))
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
/*        holder.cardImageContainer.animate()
                .scaleY(0.3f)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        holder.imageViewGradient.animate()
                .scaleY(0.3f)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        holder.imageView.animate()
                .translationY((int) (holder.cardFrontContainer.getHeight() * 0.7 + holder.imageView.getHeight()/2))
                .setInterpolator(new DecelerateInterpolator(2))
                .start();*/
        for (ImageView img : holder.stars) {
            img.animate()
                    .alpha(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }
        cardFlipStates.put(position, true);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, logoView;
        public ImageView[] stars;
        public TextView titleView;
        public FrameLayout cardImageContainer, cardFrontContainer;
        public RelativeLayout cardStarContainer;
        public View imageViewGradient;
        public CardView container;

        public FrameLayout cardBackContainer;
        public RecyclerView cardFactsView;
        public TextView cardShortDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.card_title);
            logoView = (ImageView) itemView.findViewById(R.id.card_logo);
            cardImageContainer = (FrameLayout) itemView.findViewById(R.id.card_image_container);
            imageView = (ImageView) itemView.findViewById(R.id.card_image);
            imageViewGradient = itemView.findViewById(R.id.card_image_gradient);
            container = (CardView) itemView.findViewById(R.id.card_container);
            cardFrontContainer = (FrameLayout) itemView.findViewById(R.id.card_front_container);
            cardStarContainer = (RelativeLayout) itemView.findViewById(R.id.star_container);

            cardBackContainer = (FrameLayout) itemView.findViewById(R.id.card_back_container);
            cardFactsView = (RecyclerView) itemView.findViewById(R.id.card_facts_view);
            cardShortDescription = (TextView) itemView.findViewById(R.id.card_short_description);

            stars = new ImageView[] {
                    (ImageView) itemView.findViewById(R.id.card_rate_1),
                    (ImageView) itemView.findViewById(R.id.card_rate_2),
                    (ImageView) itemView.findViewById(R.id.card_rate_3),
                    (ImageView) itemView.findViewById(R.id.card_rate_4),
                    (ImageView) itemView.findViewById(R.id.card_rate_5)
            };
        }
    }
}
