package com.culinars.culinars.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.main.RecommendationsAdapter;
import com.culinars.culinars.adapter.main.RecommendationsSearchAdapter;

import java.util.ArrayList;

/**
 * Created by Oktayşen on 19/6/2016.
 */
public class RecommendationsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private Toolbar toolbar;
    private RecyclerView.Adapter adapter;
    public RecyclerView recyclerView;
    private boolean isSearching;

    public RecommendationsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecommendationsFragment newInstance( Toolbar toolbar) {
        RecommendationsFragment fragment = new RecommendationsFragment();
        fragment.isSearching = false;
        fragment.toolbar = toolbar;
        fragment.adapter = new RecommendationsAdapter();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommendations, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recommendation_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public void setSearching(boolean isSearching) {
        if (this.isSearching != isSearching) {
            //adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
            if (isSearching)
                adapter = new RecommendationsSearchAdapter();
            else
                adapter = new RecommendationsAdapter();
            recyclerView.setAdapter(adapter);
/*            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(adapter);
                    Log.w("SET_ADAPTER", "...");
                }
            }, 100);*/
            this.isSearching = isSearching;
        }
    }

    public void searchWith(String searchQuery, int searchMaxTime, int searchMaxCalories, ArrayList<String> searchIngredients, String searchCuisine, boolean searchOnlyCurrentIngredients) {
        if (adapter instanceof RecommendationsSearchAdapter)
            ((RecommendationsSearchAdapter) adapter).updateSearchParams(searchQuery, searchMaxTime, searchMaxCalories, searchIngredients, searchCuisine, searchOnlyCurrentIngredients);
    }
}