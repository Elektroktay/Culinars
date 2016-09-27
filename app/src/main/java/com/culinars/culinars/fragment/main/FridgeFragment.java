package com.culinars.culinars.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.main.FridgeAdapter;

/**
 * A Fragment that holds the fridge page in a PagerAdapter.
 */
public class FridgeFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private Toolbar toolbar;
    public RecyclerView recyclerView;
    private FridgeAdapter adapter;

    public FridgeFragment() {
    }

    /**
     * Creates a new FridgeFragment. Use this instead of the constructor.
     */
    public static FridgeFragment newInstance(Toolbar toolbar) {
        FridgeFragment fragment = new FridgeFragment();
        fragment.toolbar = toolbar;
        fragment.adapter = new FridgeAdapter();
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
        View rootView = inflater.inflate(R.layout.fragment_fridge, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fridge_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    /**
     * Executes a search with the given query.
     * @param searchQuery Query text.
     */
    public void searchWith(String searchQuery) {
        adapter.updateSearchParams(searchQuery);
    }
}