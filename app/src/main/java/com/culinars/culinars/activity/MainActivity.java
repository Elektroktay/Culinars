package com.culinars.culinars.activity;

import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.culinars.culinars.MainPageChangeListener;
import com.culinars.culinars.R;
import com.culinars.culinars.adapter.main.RecommendationsSearchAdapter;
import com.culinars.culinars.data.structure.Ingredient;
import com.culinars.culinars.fragment.main.FavoritesFragment;
import com.culinars.culinars.fragment.main.FridgeFragment;
import com.culinars.culinars.fragment.main.RecommendationsFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * The activity that includes recommendations, favorites and the fridge.
 */
public class MainActivity extends AppCompatActivity {

    public ImageView appLogo;
    public Toolbar toolbar;
    public AppBarLayout appBarLayout, searchExtra;
    public FrameLayout searchExtraContainer;
    public ImageButton searchExtraButton;
    public SearchView searchView;

    private RecommendationsFragment recommendationsFragment;

    private String searchQuery = "";
    private int searchMaxTime = -1;
    private int searchMaxCalories = -1;
    private ArrayList<String> searchIngredients = new ArrayList<>();
    private String searchCuisine = "";
    private boolean searchOnlyCurrentIngredients = false;

    private boolean isSearching;

    Runnable backButtonAction;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FridgeFragment fridgeFragment;

    /**
     * This method runs before contents of layout xml are loaded to the screen.
     * Loading the xml onto the screen should be done here.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isSearching = false;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.toolbar_container);
        searchExtra = (AppBarLayout) findViewById(R.id.search_extra_container);
        searchExtraContainer = (FrameLayout) findViewById(R.id.search_extra_major_container);
        searchExtraButton = (ImageButton) findViewById(R.id.search_extra_button);
        searchView = (SearchView) findViewById(R.id.search_view);
        appLogo = (ImageView) findViewById(R.id.app_logo);

        //Toolbar setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Pager adapter setup
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new MainPageChangeListener(toolbar, appBarLayout,
                (FrameLayout) findViewById(R.id.search_extra_major_container)));

        /**
         * This OnClickListener finds the current fragment using black magic
         * and scrolls its RecyclerView up.
         */
        class AppIconClickListener implements View.OnClickListener{
            ViewPager viewPager;
            FragmentPagerAdapter adapter;
            MainActivity activity;
            public AppIconClickListener(ViewPager viewPager, MainActivity activity) {
                this.viewPager = viewPager;
                this.adapter = (FragmentPagerAdapter) viewPager.getAdapter();
                this.activity = activity;
            }
            @Override
            public void onClick(View v) {
                Fragment fragment = activity.getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + viewPager.getCurrentItem());

                if (fragment instanceof RecommendationsFragment
                        && ((RecommendationsFragment) fragment).recyclerView != null)
                    ((RecommendationsFragment) fragment).recyclerView.smoothScrollToPosition(0);
                if (fragment instanceof FridgeFragment
                        && ((FridgeFragment) fragment).recyclerView != null)
                    ((FridgeFragment) fragment).recyclerView.smoothScrollToPosition(0);
                if (fragment instanceof FavoritesFragment
                        && ((FavoritesFragment) fragment).recyclerView != null)
                    ((FavoritesFragment) fragment).recyclerView.smoothScrollToPosition(0);
            }
        }
        appLogo.setOnClickListener(new AppIconClickListener(mViewPager, this));

        //Tabs setup
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.heart);
        tabLayout.getTabAt(2).setIcon(R.drawable.refrigerator_512);



        //SearchSetup
        setUpSearchExtra();
    }

    /**
     * Sets up the interaction between the search bar and the extra parameters, the animations and search functionality.
     * A lot of shit happens here. I can't bother commenting them all.
     */
    private void setUpSearchExtra() {
        searchView.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recommendationsFragment != null)
                    recommendationsFragment.setSearching(true);
                else
                    Log.d("recommendationsFragment", "onClick: fragment is null.");
                MainActivity.this.setBackButtonAction(new Runnable() {
                    @Override
                    public void run() {
                        searchView.setIconified(true);
                    }
                });
                MainActivity.this.appLogo.setVisibility(View.GONE);
                searchView.setLayoutParams(new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT,
                        Toolbar.LayoutParams.MATCH_PARENT
                ));
                AppBarLayout.LayoutParams toolbarParams = (AppBarLayout.LayoutParams) MainActivity.this.toolbar.getLayoutParams();
                toolbarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    appBarLayout.setElevation(0);
                }
                if (mViewPager.getCurrentItem() < 2) {
                    searchExtra.setVisibility(View.VISIBLE);
                    searchView.setQueryHint("search recipes");
                } else {
                    searchExtra.setVisibility(View.GONE);
                    searchView.setQueryHint("search ingredients");
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (recommendationsFragment != null)
                    recommendationsFragment.setSearching(false);
                else
                    Log.d("recommendationsFragment", "onClick: fragment is null.");
                MainActivity.this.setBackButtonAction(null);
                MainActivity.this.appLogo.setVisibility(View.VISIBLE);
                searchView.setLayoutParams(new android.support.v7.widget.Toolbar.LayoutParams(
                        50,
                        android.support.v7.widget.Toolbar.LayoutParams.MATCH_PARENT
                ));
                AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) MainActivity.this.toolbar.getLayoutParams();
                layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    appBarLayout.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                }
                searchExtra.setVisibility(View.GONE);
                if (searchExtraIsExpanded())
                    searchExtraContract(0);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                updateSearchParams();
                return false;
            }
        });

        searchExtraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchExtraIsExpanded()) {
                    searchExtraContract(250);
                } else {
                    searchExtraExpand(250);
                }
            }
        });

        final TextView timeText = (TextView) findViewById(R.id.search_extra_time_text);
        AppCompatSeekBar timeBar = (AppCompatSeekBar) findViewById(R.id.search_extra_time);
        timeBar.setMax(100);
        timeBar.setProgress(100);
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 100) {
                    timeText.setText("Time");
                    searchMaxTime = -1;
                } else if (progress > 90) {
                    timeText.setText("<1 day");
                    searchMaxTime = 1440;
                } else if (progress > 80) {
                    timeText.setText("<12 hrs");
                    searchMaxTime = 720;
                } else if (progress > 70) {
                    timeText.setText("<8 hrs");
                    searchMaxTime = 480;
                } else if (progress > 60) {
                    timeText.setText("<4 hrs");
                    searchMaxTime = 240;
                } else if (progress > 50) {
                    timeText.setText("<2 hrs");
                    searchMaxTime = 120;
                } else if (progress > 40) {
                    timeText.setText("<60 mins");
                    searchMaxTime = 60;
                } else if (progress > 30) {
                    timeText.setText("<30 mins");
                    searchMaxTime = 30;
                } else if (progress > 20) {
                    timeText.setText("<15 mins");
                    searchMaxTime = 15;
                } else if (progress > 10) {
                    timeText.setText("<10 mins");
                    searchMaxTime = 10;
                } else {
                    timeText.setText("<5 mins");
                    searchMaxTime = 5;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {updateSearchParams();}
        });

        final TextView calText = (TextView) findViewById(R.id.search_extra_calories_text);
        AppCompatSeekBar calBar = (AppCompatSeekBar) findViewById(R.id.search_extra_calories);
        calBar.setMax(100);
        calBar.setProgress(100);
        calBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 100) {
                    calText.setText("Calories");
                    searchMaxCalories = -1;
                } else if (progress > 90) {
                    calText.setText("<5000 cal");
                    searchMaxCalories = 5000;
                } else if (progress > 80) {
                    calText.setText("<2500 cal");
                    searchMaxCalories = 2500;
                } else if (progress > 70) {
                    calText.setText("<1000 cal");
                    searchMaxCalories = 1000;
                } else if (progress > 60) {
                    calText.setText("<750 cal");
                    searchMaxCalories = 750;
                } else if (progress > 50) {
                    calText.setText("<500 cal");
                    searchMaxCalories = 500;
                } else if (progress > 40) {
                    calText.setText("<300 cal");
                    searchMaxCalories = 300;
                } else if (progress > 30) {
                    calText.setText("<150 cal");
                    searchMaxCalories = 150;
                } else if (progress > 20) {
                    calText.setText("<75 cal");
                    searchMaxCalories = 75;
                } else if (progress > 10) {
                    calText.setText("<50 cal");
                    searchMaxCalories = 50;
                } else {
                    calText.setText("<25 cal");
                    searchMaxCalories = 25;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {updateSearchParams();}
        });

        final String[] cuisines = new String[]{"", "Arabic", "Italian", "Turkish", "Jewish", "Christian", "Chinese", "Japanese"};

        AppCompatSpinner cuisine = (AppCompatSpinner) findViewById(R.id.search_extra_cuisine);
        cuisine.setAdapter(new ArrayAdapter<>(this, R.layout.fragment_cuisine_spinner_text, cuisines));
        cuisine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchCuisine = cuisines[position];
                updateSearchParams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchCuisine = "";
                updateSearchParams();
            }
        });

        MultiAutoCompleteTextView ingredientsText = (MultiAutoCompleteTextView) findViewById(R.id.search_extra_ingredients);
        ingredientsText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        String[] ingredients = new String[] {"apples", "oranges", "bananas", "tomatoes", "veal", "lamb"};
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(this, R.layout.fragment_cuisine_spinner_text, ingredients);
        ingredientsText.setAdapter(ingredientAdapter);
    }

    /**
     * Begins a search with appropriate search params.
     */
    private void updateSearchParams() {
        //On the recommendations page this will set up a recipe search,
        //and on the fridge page an ingredient search.
        if (mViewPager.getCurrentItem() < 2) {
            if (recommendationsFragment != null)
                recommendationsFragment.searchWith(
                        searchQuery,
                        searchMaxTime,
                        searchMaxCalories,
                        searchIngredients,
                        searchCuisine,
                        searchOnlyCurrentIngredients);
        } else
            fridgeFragment.searchWith(searchQuery);
    }

    /**
     * Expands extra search params with animation.
     * @param durationInMilis How long the animation should last, in miliseconds.
     */
    public void searchExtraExpand(int durationInMilis) {
        searchExtra.animate()
                .translationY(-7)
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(durationInMilis)
                .start();
        searchExtraButton.animate()
                .rotation(180)
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(durationInMilis)
                .start();
        TransitionDrawable transitionExtra = (TransitionDrawable) searchExtra.getBackground();
        transitionExtra.startTransition(durationInMilis);
        TransitionDrawable transitionAppBar = (TransitionDrawable) appBarLayout.getBackground();
        transitionAppBar.startTransition(durationInMilis);
    }

    /**
     * Contracts extra search params with animation.
     * @param durationInMilis How long the animation should last, in miliseconds.
     */
    public void searchExtraContract(int durationInMilis) {
        searchExtra.animate()
                .translationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -240, getResources().getDisplayMetrics()))
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(durationInMilis)
                .start();
        searchExtraButton.animate()
                .rotation(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(durationInMilis)
                .start();
        TransitionDrawable transitionExtra = (TransitionDrawable) searchExtra.getBackground();
        transitionExtra.reverseTransition(durationInMilis);
        TransitionDrawable transitionAppBar = (TransitionDrawable) appBarLayout.getBackground();
        transitionAppBar.reverseTransition(durationInMilis);
    }

    /**
     * Tells whether extra search parameters are expanded or not.
     * @return true if expanded.
     */
    public boolean searchExtraIsExpanded() {
        return searchExtra.getTranslationY() >= -7;
    }

    /**
     * Sets up the options menu.
     */
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

    public void setBackButtonAction(Runnable runnable) {
        this.backButtonAction = runnable;
    }

    /**
     * Is called when the back button is pressed. We're overriding this to provide functionality
     * other than the default in some cases.
     */
    @Override
    public void onBackPressed() {
        if (backButtonAction == null) {
            if (mViewPager.getCurrentItem() > 0)
                mViewPager.setCurrentItem(0);
            else
                super.onBackPressed();
        }
        else
            backButtonAction.run();
    }

    /**
     * Adapter that controls the Pager containing recommendations, favorites and the fridge.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns the appropriate fragment for the given page number.
         * @param position Page number
         * @return Fragment
         */
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0: recommendationsFragment = RecommendationsFragment.newInstance((Toolbar) findViewById(R.id.toolbar));
                    return recommendationsFragment;
                case 1: return FavoritesFragment.newInstance((Toolbar) findViewById(R.id.toolbar), MainActivity.this);
                default: fridgeFragment = FridgeFragment.newInstance((Toolbar) findViewById(R.id.toolbar));
                    return fridgeFragment;
            }
        }

        /**
         * Tells the number of pages in this pager
         * @return number of pages
         */
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
