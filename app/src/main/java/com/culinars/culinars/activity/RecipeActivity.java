package com.culinars.culinars.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.culinars.culinars.R;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.fragment.recipe.RecipeFactsFragment;
import com.culinars.culinars.fragment.recipe.RecipeIngredientsFragment;
import com.culinars.culinars.fragment.recipe.RecipeOthersFragment;
import com.culinars.culinars.fragment.recipe.SlideshowFragment;

public class RecipeActivity extends AppCompatActivity {

    Recipe currentRecipe;


    /**
     *     /**
     * This method runs before contents of layout xml are loaded to the screen.
     * Loading the xml onto the screen should be done here.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if (currentRecipe == null)
            currentRecipe = (Recipe) getIntent().getSerializableExtra("recipe");

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(currentRecipe.title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ViewPager setup
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.recipe_view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        //viewPager.addOnPageChangeListener(new MainPageChangeListener(toolbar));

        SlideshowFragment.PagerAdapter slideshowAdapter = new SlideshowFragment.PagerAdapter(getSupportFragmentManager(), currentRecipe);
        ViewPager slideshowPager = (ViewPager) findViewById(R.id.recipe_image_container);
        slideshowPager.setAdapter(slideshowAdapter);

        //Tabs setup
        TabLayout tabLayout = (TabLayout) findViewById(R.id.recipe_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.getTabAt(0).setIcon(R.drawable.home);
        //tabLayout.getTabAt(1).setIcon(R.drawable.heart);
        //tabLayout.getTabAt(2).setIcon(R.drawable.refrigerator_512);

        final FrameLayout buttonSlideContainer = (FrameLayout) findViewById(R.id.button_slide_container);
        final NestedScrollView bottomSheet = (NestedScrollView) findViewById(R.id.recipe_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.recipe_bottom_sheet_container));
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300+70, getResources().getDisplayMetrics()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        CardView cookButton = (CardView) findViewById(R.id.recipe_cook_button);
        cookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeActivity.this, InstructionsActivity.class);
                intent.putExtra("recipe", currentRecipe);
                RecipeActivity.this.startActivity(intent);
            }
        });
    }


    /**
     * An adapter that fills a viewPager with Ingredients, Facts and Similar Recipes pages.
     */
    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns the appropriate page for the given page number
         * @param position The page number
         * @return The page, or null if page number is invalid.
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return RecipeIngredientsFragment.newInstance(currentRecipe) ;
                case 1: return RecipeFactsFragment.newInstance(currentRecipe);
                case 2: return RecipeOthersFragment.newInstance(currentRecipe);
            }
            return null;
        }

        /**
         * Gives the number of pages in this adapter.
         * @return Number of pages.
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Returns the page title of the given position
         * @param position The position of the page.
         * @return The title.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Ingredients";
                case 1: return "Facts";
                case 2: return "Other Recipes";
            }
            return super.getPageTitle(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
