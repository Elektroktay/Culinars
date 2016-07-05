package com.culinars.culinars;

import android.animation.Animator;
import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.culinars.culinars.activity.MainActivity;

/**
 * Created by Oktayşen on 23/6/2016.
 */
public class MainPageChangeListener implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private FrameLayout searchExtra;

    public MainPageChangeListener(Toolbar toolbar, AppBarLayout appBarLayout, FrameLayout searchExtra) {
        this.toolbar = toolbar;
        this.appBarLayout = appBarLayout;
        this.searchExtra = searchExtra;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int pos) {
        final SearchView searchView = (SearchView) toolbar.findViewById(R.id.search_view);

        if (pos != 1) {
            appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            searchExtra.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            if (pos == 0) {
                toolbar.getMenu().findItem(R.id.action_user).setVisible(true);
                toolbar.getMenu().findItem(R.id.action_camera).setVisible(false);
            } else {
                toolbar.getMenu().findItem(R.id.action_user).setVisible(false);
                toolbar.getMenu().findItem(R.id.action_camera).setVisible(true);
            }
        } else {
            appBarLayout.animate().translationY(-(int) (appBarLayout.getHeight()*1.2)).setInterpolator(new AccelerateInterpolator(2)).start();
            searchExtra.animate().translationY(-(int) (appBarLayout.getHeight()*1.2)).setInterpolator(new AccelerateInterpolator(2))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            searchView.setIconified(true);
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {}
                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    }).start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
