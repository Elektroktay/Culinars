package com.culinars.culinars.adapter;

import android.animation.Animator;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.culinars.culinars.CustomSpeedLinearLayoutManager;
import com.culinars.culinars.R;

import java.util.HashMap;
import java.util.Map;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    Context context;
    ViewGroup parent;
    Map<Integer, Boolean> cardFlipStates;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_recommendations_logo, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_recommendations_card, parent, false);
        }
        context = parent.getContext();
        this.parent = parent;
        cardFlipStates = new HashMap<>();
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            cardFlipStates.put(position, false);
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
//            holder.cardShortDescription.setText(R.string.large_text);
//            setUpFactsViev(holder);
//            setUpIngredientsView(holder);
            setUpCalories(holder, position*50, "calories");
            setUpTime(holder, position*5, "hrs");
            setUpIngredients(holder, 20, 20);
            setUpDifficulty(holder, "medium");


            holder.cookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Open Ingredient Screen", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setUpDifficulty(ViewHolder holder, String difficulty) {
        holder.cardDifficultyText.setText(difficulty);
    }

    private void setUpIngredients(ViewHolder holder, int value, int maxIngredients) {
        holder.cardIngredientValue1.setText(""+value);
        holder.cardIngredientValue2.setText(""+maxIngredients);
        holder.cardIngredientUnit.setText("ingredients");
    }

    private void setUpTime(ViewHolder holder, int value, String unit) {
        holder.cardTimeValue.setText("2:30");
        holder.cardTimeUnit.setText(unit);
    }

    private void setUpCalories(ViewHolder holder, int value, String unit) {
        holder.cardCaloriesValue.setText("" + value);
        holder.cardCaloriesUnit.setText(unit);
    }

    /*
    private void setUpIngredientsView(ViewHolder holder) {
        holder.cardIngredientsView.setHasFixedSize(true);
        holder.cardIngredientsView.setAdapter(new IngredientsAdapter());
        holder.cardIngredientsView.setLayoutManager(new CustomSpeedLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, 1000));
        holder.cardIngredientsView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    } */

    /*
    private void setUpFactsViev(ViewHolder holder) {
        holder.cardFactsView.setHasFixedSize(true);
        holder.cardFactsView.setAdapter(new StatsAdapter());
        holder.cardFactsView.setLayoutManager(new CustomSpeedLinearLayoutManager(context, 1000));
        holder.cardFactsView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //holder.cardFactsView.smoothScrollBy(0, 1);
                return rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    } */

    private void flipCard(ViewHolder holder, final int position) {
        final Integer pos = position;
        if (cardFlipStates.get(pos) == null || cardFlipStates.get(pos).equals(false)) {
            openCard(holder, position);
        } else if (cardFlipStates.get(pos).equals(true)) {
            closeCard(holder, position);
        }
    }

    //Front goes down.
    public void closeCard(final ViewHolder holder, int position) {
        holder.titleView.setSingleLine(false);
        holder.cardFrontContainer.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.cardBackContainer.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                })
                .start();
        holder.cardStarContainer.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        holder.cardFavorite.animate()
                .translationY((float) (holder.cardFrontContainer.getHeight() * 0.7))
                .alpha(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

        holder.cookButton.animate()
                .scaleY(0)
                .scaleX(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        cardFlipStates.put(position, false);
    }

    //Front goes up.
    public void openCard(final ViewHolder holder, int position) {
        holder.titleView.setSingleLine(true);
        holder.cardBackContainer.setVisibility(View.VISIBLE);
        holder.cardFrontContainer.animate()
                .translationY(-(float) (holder.cardFrontContainer.getHeight() * 0.7))
                .setInterpolator(new DecelerateInterpolator(2))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.cardBackContainer.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {}
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                })
                .start();
        holder.cardFavorite.animate()
                .translationY(0)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        holder.cardStarContainer.animate()
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();

        holder.cookButton.animate()
                .setStartDelay(1)
                .scaleY(1)
                .scaleX(1)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

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
        public TextView cardCaloriesValue, cardCaloriesUnit;
        public TextView cardTimeValue, cardTimeUnit;
        public TextView cardIngredientValue1, cardIngredientValue2, cardIngredientUnit;
        public TextView cardDifficultyText;
        public ImageView cardFavorite;

        public CardView cookButton;
        public TextView cookButtonText;

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
            cardCaloriesValue = (TextView) itemView.findViewById(R.id.card_calories_value);
            cardCaloriesUnit = (TextView) itemView.findViewById(R.id.card_calories_unit);
            cardTimeValue = (TextView) itemView.findViewById(R.id.card_time_value);
            cardTimeUnit = (TextView) itemView.findViewById(R.id.card_time_unit);
            cardIngredientValue1 = (TextView) itemView.findViewById(R.id.card_ingredient_value1);
            cardIngredientValue2 = (TextView) itemView.findViewById(R.id.card_ingredient_value2);
            cardIngredientUnit = (TextView) itemView.findViewById(R.id.card_ingredient_unit);
            cardDifficultyText = (TextView) itemView.findViewById(R.id.card_difficulty_text);

            cardFavorite = (ImageView) itemView.findViewById(R.id.card_favorite);
            cookButton = (CardView) itemView.findViewById(R.id.cook_button);
            cookButtonText = (TextView) itemView.findViewById(R.id.cook_button_text);

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
