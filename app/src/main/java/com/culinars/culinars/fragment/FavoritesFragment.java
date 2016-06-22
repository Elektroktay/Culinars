package com.culinars.culinars.fragment;

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

import com.culinars.culinars.HidingScrollListener;
import com.culinars.culinars.R;
import com.culinars.culinars.adapter.FavoritesAdapter;
import com.culinars.culinars.adapter.RecommendationsAdapter;

/**
 * Created by Oktayşen on 21/6/2016.
 */
public class FavoritesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private Toolbar toolbar;

    public FavoritesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoritesFragment newInstance(Toolbar toolbar) {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.toolbar = toolbar;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.favorite_recycler_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(new FavoritesAdapter());
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