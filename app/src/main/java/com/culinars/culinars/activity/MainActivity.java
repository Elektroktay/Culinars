package com.culinars.culinars.activity;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.culinars.culinars.MainPageChangeListener;
import com.culinars.culinars.R;
import com.culinars.culinars.fragment.main.FavoritesFragment;
import com.culinars.culinars.fragment.main.FridgeFragment;
import com.culinars.culinars.fragment.main.RecommendationsFragment;

public class MainActivity extends AppCompatActivity {

    public ImageView appLogo;
    public Toolbar toolbar;
    public AppBarLayout appBarLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar setup
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        appBarLayout = (AppBarLayout) findViewById(R.id.toolbar_container);

        //ViewPager setup
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new MainPageChangeListener(toolbar, appBarLayout));

        //Tabs setup
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.heart);
        tabLayout.getTabAt(2).setIcon(R.drawable.refrigerator_512);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //SearchSetup

        appLogo = (ImageView) findViewById(R.id.app_logo);

        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.appLogo.setVisibility(View.GONE);
                searchView.setLayoutParams(new android.support.v7.widget.Toolbar.LayoutParams(
                        android.support.v7.widget.Toolbar.LayoutParams.MATCH_PARENT,
                        android.support.v7.widget.Toolbar.LayoutParams.MATCH_PARENT
                ));
                AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) MainActivity.this.toolbar.getLayoutParams();
                layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MainActivity.this.appLogo.setVisibility(View.VISIBLE);
                searchView.setLayoutParams(new android.support.v7.widget.Toolbar.LayoutParams(
                        50,
                        android.support.v7.widget.Toolbar.LayoutParams.MATCH_PARENT
                ));
                AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) MainActivity.this.toolbar.getLayoutParams();
                layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                return false;
            }
        });

        //Drawer etup
//        final FrameLayout drawer = (FrameLayout) findViewById(R.id.main_drawer);
//        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
/*        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawer);
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Setup user button
        MenuItem userButton = menu.findItem(R.id.action_user);
        if (userButton == null)
            return true;
        userButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                userPopup(item.getActionView());
                return false;
            }
        });
        /*
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MENU_SEARCH", "SearchOnQueryTextSubmit: " + query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        */
        return true;
    }

    /**
     * Pops up the menu when user clicks the portrait.
     * @param v View of the portrait.
     */
    public void userPopup(View v) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_user));
        popup.inflate(R.menu.menu_user);
        popup.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recommendations, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0: return RecommendationsFragment.newInstance((Toolbar) findViewById(R.id.toolbar));
                case 1: return FavoritesFragment.newInstance((Toolbar) findViewById(R.id.toolbar));
                case 2: return FridgeFragment.newInstance((Toolbar) findViewById(R.id.toolbar));
                default: return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
