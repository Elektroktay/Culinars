package com.culinars.culinars.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.culinars.culinars.HidingScrollListener;
import com.culinars.culinars.R;
import com.culinars.culinars.adapter.RecommendationsAdapter;

/**
 * Created by Oktayşen on 19/6/2016.
 */
public class RecommendationsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Toolbar toolbar;

    public RecommendationsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecommendationsFragment newInstance(int sectionNumber, Toolbar toolbar) {
        RecommendationsFragment fragment = new RecommendationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.toolbar = toolbar;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommendations, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recommendation_recycler_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(new RecommendationsAdapter());
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        return rootView;
    }

    //Src: https://mzgreen.github.io/2015/02/15/How-to-hideshow-Toolbar-when-list-is-scroling(part1)/
    private void hideViews() {
        if (toolbar != null) {
            toolbar.animate().translationY(-(int) (toolbar.getHeight() * 1.2)).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    private void showViews() {
        if (toolbar != null) {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        }
    }
}