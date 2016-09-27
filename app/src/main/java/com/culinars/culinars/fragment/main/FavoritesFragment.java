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
 * A Fragment that holds the favorites page in a PagerAdapter.
 */
public class FavoritesFragment extends Fragment {
    private Toolbar toolbar;
    public RecyclerView recyclerView;
    private MainActivity activity;

    public FavoritesFragment() {
    }

    /**
     * Creates a new FavoritesFragment. Use this instead of the constructor.
     */
    public static FavoritesFragment newInstance(Toolbar toolbar, MainActivity activity) {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.toolbar = toolbar;
        fragment.activity = activity;
        return fragment;
    }

    /**
     * This method is called before xml is loaded onto the screen (inflating).
     * Inflation must be done here.
     * @return The view that was created as a result.
     */
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

    /**
     * Changes the adapter of the recyclerView with the new one, and makes the back button return to the original adapter.
     * @param adapter New adapter to replace the old one.
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        if (activity != null) {
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
}