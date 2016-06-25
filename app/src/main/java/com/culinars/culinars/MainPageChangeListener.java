package com.culinars.culinars;

import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Oktayşen on 23/6/2016.
 */
public class MainPageChangeListener implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    public MainPageChangeListener(Toolbar toolbar, AppBarLayout appBarLayout) {
        this.toolbar = toolbar;
        this.appBarLayout = appBarLayout;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int pos) {
        if (pos != 1) {
            appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        } else {
            appBarLayout.animate().translationY(-(int) (appBarLayout.getHeight())).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
