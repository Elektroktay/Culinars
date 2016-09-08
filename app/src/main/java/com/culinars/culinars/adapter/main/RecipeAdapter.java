package com.culinars.culinars.adapter.main;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.activity.RecipeActivity;
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.structure.User;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public abstract class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    Context context;
    ViewGroup parent;
    ArrayList<Content> contents;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_recommendations_card, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_recommendations_loading, parent, false);
        }
        context = parent.getContext();
        this.parent = parent;
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1)
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemViewType(position) == 0 && getDataAtPos(position) != null) {
            if (getDataAtPos(position).title != null)
                setUpTitle(holder, context, getDataAtPos(position).title);
            else
                setUpTitle(holder, context, "Empty Title");
            if (getImageAtPos(position) != null) {
                setUpImageView(holder, getImageAtPos(position));
                holder.cardLoading.setVisibility(View.INVISIBLE);
            } else {
                setUpImageView(holder, BitmapFactory.decodeResource(context.getResources(), R.drawable.gradient_black_horizontal));
                holder.cardLoading.setVisibility(View.INVISIBLE);
            }
            setUpStars(holder, getDataAtPos(position).getStarsAverage());
            setUpCalories(holder, getDataAtPos(position).calories, "calories");
            setUpTime(holder, getDataAtPos(position).time);
            User.loadCurrent().onGet(new FB.GetListener() {
                @Override
                public void onDataChange(DataSnapshot s) {
                    User res = User.from(s);
                    if (res != null) {
                        if (getDataAtPos(holder.getAdapterPosition()).ingredients != null)
                            setUpIngredients(holder, res.getExistingIngredientCount(getDataAtPos(holder.getAdapterPosition())), getDataAtPos(holder.getAdapterPosition()).ingredients.size());
                        else
                            setUpIngredients(holder, res.getExistingIngredientCount(getDataAtPos(holder.getAdapterPosition())), -1);
                    }
                }
            });
            setUpDifficulty(holder, getDataAtPos(position).difficulty_scale);

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipCard(holder);
                }
            });
            holder.cookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("recipe", getDataAtPos(holder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
            User.loadCurrent().onGet(new FB.GetListener() {
                @Override
                public void onDataChange(DataSnapshot s) {
                    User res = User.from(s);
                    if (res != null) {
                        Recipe cur = getDataAtPos(holder.getAdapterPosition());
                        holder.cardFavorite.setImageResource(res.isFavorite(cur.uid)?R.drawable.heart:R.drawable.heart_outline);
                    }
                }
            });
            holder.cardFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick(holder.getAdapterPosition(), holder.cardFavorite);
                }
            });
        }
    }

    private void setUpImageView(ViewHolder holder, Bitmap bitmap) {
        holder.imageView.setImageBitmap(bitmap);
    }

    private void setUpTitle(ViewHolder holder, Context context, String title) {
        holder.titleView.setText(title);
        AssetManager am = context.getApplicationContext().getAssets();
        holder.titleView.setTypeface(Typeface.createFromAsset(am, "fonts/segoe_ui_light.ttf"));
    }

    private void setUpStars(ViewHolder holder, int points) {
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
    }

    private void setUpDifficulty(ViewHolder holder, int difficultyScale) {
        String difficulty = "";
        switch (difficultyScale) {
            case 0: difficulty = "easy"; break;
            case 1: difficulty = "medium"; break;
            case 2: difficulty = "hard"; break;
        };
        holder.cardDifficultyText.setText(difficulty);
    }

    private void setUpIngredients(ViewHolder holder, int value, int maxIngredients) {
        holder.cardIngredientValue1.setText(""+value);
        holder.cardIngredientValue2.setText(""+maxIngredients);
        holder.cardIngredientUnit.setText("ingredients");
    }

    private void setUpTime(ViewHolder holder, int value) {
        if (value > 59) {
            if (value%60 < 10)
                holder.cardTimeValue.setText(((int) Math.floor(value / 60)) + ":0" + (value % 60));
            else
                holder.cardTimeValue.setText(((int) Math.floor(value / 60)) + ":" + (value % 60));
            holder.cardTimeUnit.setText("hrs");
        } else {
            holder.cardTimeValue.setText("" + value);
            holder.cardTimeUnit.setText("mins");
        }
    }

    private void setUpCalories(ViewHolder holder, int value, String unit) {
        holder.cardCaloriesValue.setText("" + value);
        holder.cardCaloriesUnit.setText(unit);
    }

    private void flipCard(ViewHolder holder) {
        if (holder.cardFrontContainer.getTranslationY() > holder.cardFrontContainer.getHeight() * -0.5) {
            openCard(holder);
        } else {
            closeCard(holder);
        }
    }

    //Front goes down.
    public void closeCard(final ViewHolder holder) {
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
    }

    //Front goes up.
    public void openCard(final ViewHolder holder) {
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
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

        holder.cookButton.animate()
                .setStartDelay(1)
                .scaleY(1)
                .scaleX(1)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
    }

    public abstract Recipe getDataAtPos(int position);
    public abstract Bitmap getImageAtPos(int position);

    public abstract void onFavoriteClick(int adapterPosition, ImageView cardFavorite);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, logoView;
        public ImageView[] stars;
        public TextView titleView;
        public FrameLayout cardImageContainer, cardFrontContainer;
        public RelativeLayout cardStarContainer;
        public View imageViewGradient;
        public CardView container;
        public ProgressBar cardLoading;

        public FrameLayout cardBackContainer;
        public TextView cardCaloriesValue, cardCaloriesUnit;
        public TextView cardTimeValue, cardTimeUnit;
        public TextView cardIngredientValue1, cardIngredientValue2, cardIngredientUnit;
        public TextView cardDifficultyText;
        public ImageView cardFavorite;

        public CardView cookButton;
        public TextView cookButtonText;

        public ProgressBar loadingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.card_title);
            logoView = (ImageView) itemView.findViewById(R.id.app_logo);
            cardImageContainer = (FrameLayout) itemView.findViewById(R.id.card_image_container);
            imageView = (ImageView) itemView.findViewById(R.id.card_image);
            imageViewGradient = itemView.findViewById(R.id.card_image_gradient);
            container = (CardView) itemView.findViewById(R.id.card_container);
            cardFrontContainer = (FrameLayout) itemView.findViewById(R.id.card_front_container);
            cardStarContainer = (RelativeLayout) itemView.findViewById(R.id.star_container);
            cardLoading = (ProgressBar) itemView.findViewById(R.id.recommendations_loading);

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

            loadingBar = (ProgressBar) itemView.findViewById(R.id.recommendations_loading);
        }
    }
}
