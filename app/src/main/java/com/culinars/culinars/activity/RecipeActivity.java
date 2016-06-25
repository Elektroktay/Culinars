package com.culinars.culinars.activity;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.culinars.culinars.R;
import com.culinars.culinars.fragment.recipe.RecipeFactsFragment;
import com.culinars.culinars.fragment.recipe.RecipeIngredientsFragment;
import com.culinars.culinars.fragment.recipe.RecipeOthersFragment;
import com.culinars.culinars.fragment.recipe.SlideshowFragment;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Cool Recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ViewPager setup
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.recipe_view_pager);
        viewPager.setAdapter(pagerAdapter);
        //viewPager.addOnPageChangeListener(new MainPageChangeListener(toolbar));

        SlideshowFragment.PagerAdapter slideshowAdapter = new SlideshowFragment.PagerAdapter(getSupportFragmentManager());
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
        bottomSheet.post(new Runnable() {
            @Override
            public void run() {
                bottomSheet.scrollTo(0,0);
            }
        });
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300+70, getResources().getDisplayMetrics()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheet.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                buttonSlideContainer.setTranslationY(scrollY);
            }
        });
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new RecipeIngredientsFragment();
                case 1: return new RecipeFactsFragment();
                case 2: return new RecipeOthersFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

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
}
