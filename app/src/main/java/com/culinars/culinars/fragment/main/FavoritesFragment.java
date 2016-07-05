package com.culinars.culinars.fragment.main;

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

import com.culinars.culinars.R;
import com.culinars.culinars.activity.MainActivity;
import com.culinars.culinars.adapter.main.FavoritesAdapter;
import com.culinars.culinars.adapter.main.FavoritesAdapter2;

/**
 * Created by Oktayşen on 21/6/2016.
 */
public class FavoritesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private Toolbar toolbar;
    public RecyclerView recyclerView;
    private MainActivity activity;

    public FavoritesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoritesFragment newInstance(Toolbar toolbar, MainActivity activity) {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.toolbar = toolbar;
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.favorite_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        setAdapter(new FavoritesAdapter(this));
        return rootView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        if (adapter instanceof FavoritesAdapter2)
            activity.setBackButtonAction(new Runnable() {
                @Override
                public void run() {
                    setAdapter(new FavoritesAdapter(FavoritesFragment.this));
                }
            });
        else
            activity.setBackButtonAction(null);
    }
}