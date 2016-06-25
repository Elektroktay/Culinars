package com.culinars.culinars.fragment.recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.culinars.culinars.R;

/**
 * Created by Oktayşen on 23/6/2016.
 */
public class SlideshowFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slideshow, container, false);
        ImageView slideshowImage = (ImageView) rootView.findViewById(R.id.slideshow_image);
        return rootView;
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new SlideshowFragment();
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
