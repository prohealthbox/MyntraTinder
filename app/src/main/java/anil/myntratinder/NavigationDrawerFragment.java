package anil.myntratinder;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import anil.myntratinder.adapters.MyntraCategoryExpandableListAdapter;
import anil.myntratinder.models.MyntraCategory;

import static anil.myntratinder.models.MyntraCategory.generateMyntraKidsProductGroups;
import static anil.myntratinder.models.MyntraCategory.generateMyntraMenProductGroups;
import static anil.myntratinder.models.MyntraCategory.generateMyntraWomenProductGroups;
import static anil.myntratinder.models.MyntraCategory.generateSampleProductHeadGroups;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private ExpandableListView mDrawerMenExpandableListView;
    private List<MyntraCategory.ProductHeadGroup> mDrawerMenProductHeadGroups;
    private ExpandableListAdapter mDrawerMenExpandableListAdapter;

    private ExpandableListView mDrawerWomenExpandableListView;
    private List<MyntraCategory.ProductHeadGroup> mDrawerWomenProductHeadGroups;
    private ExpandableListAdapter mDrawerWomenExpandableListAdapter;

    private ExpandableListView mDrawerKidsExpandableListView;
    private List<MyntraCategory.ProductHeadGroup> mDrawerKidsProductHeadGroups;
    private ExpandableListAdapter mDrawerKidsExpandableListAdapter;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragmentView =  inflater.inflate(
                R.layout.fragment_navigation_drawer_myntra_tinder_activity, container, false);
        mDrawerListView = (ListView) fragmentView.findViewById(R.id.listView);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        "Home",
                        "Settings"
                }
        ));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        mDrawerMenProductHeadGroups = generateMyntraMenProductGroups(getActivity());
        mDrawerMenExpandableListView = (ExpandableListView) fragmentView.findViewById(R.id.menExpandableListView);
        mDrawerMenExpandableListAdapter = new MyntraCategoryExpandableListAdapter(getActivity(), mDrawerMenProductHeadGroups);
        mDrawerMenExpandableListView.setAdapter(mDrawerMenExpandableListAdapter);
        mDrawerMenExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = mDrawerMenExpandableListAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition){
                        mDrawerMenExpandableListView.collapseGroup(i);
                    }
                }
                collapseGroupsOfExpandableListView(mDrawerWomenExpandableListView, mDrawerWomenExpandableListAdapter);
                collapseGroupsOfExpandableListView(mDrawerKidsExpandableListView, mDrawerKidsExpandableListAdapter);
            }
        });
        mDrawerMenExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                MyntraCategory.ProductGroup productGroup = (MyntraCategory.ProductGroup) mDrawerMenExpandableListAdapter.getChild(groupPosition, childPosition);
                selectProductGroup(productGroup);
                return true;
            }
        });

        mDrawerWomenProductHeadGroups = generateMyntraWomenProductGroups(getActivity());
        mDrawerWomenExpandableListView = (ExpandableListView) fragmentView.findViewById(R.id.womenExpandableListView);
        mDrawerWomenExpandableListAdapter = new MyntraCategoryExpandableListAdapter(getActivity(), mDrawerWomenProductHeadGroups);
        mDrawerWomenExpandableListView.setAdapter(mDrawerWomenExpandableListAdapter);
        mDrawerWomenExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = mDrawerWomenExpandableListAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition){
                        mDrawerWomenExpandableListView.collapseGroup(i);
                    }
                }
                collapseGroupsOfExpandableListView(mDrawerMenExpandableListView, mDrawerMenExpandableListAdapter);
                collapseGroupsOfExpandableListView(mDrawerKidsExpandableListView, mDrawerKidsExpandableListAdapter);
            }
        });
        mDrawerWomenExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                MyntraCategory.ProductGroup productGroup = (MyntraCategory.ProductGroup) mDrawerWomenExpandableListAdapter.getChild(groupPosition, childPosition);
                selectProductGroup(productGroup);
                return true;
            }
        });

        mDrawerKidsProductHeadGroups = generateMyntraKidsProductGroups(getActivity());
        mDrawerKidsExpandableListView = (ExpandableListView) fragmentView.findViewById(R.id.kidsExpandableListView);
        mDrawerKidsExpandableListAdapter = new MyntraCategoryExpandableListAdapter(getActivity(), mDrawerKidsProductHeadGroups);
        mDrawerKidsExpandableListView.setAdapter(mDrawerKidsExpandableListAdapter);
        mDrawerKidsExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = mDrawerKidsExpandableListAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition){
                        mDrawerKidsExpandableListView.collapseGroup(i);
                    }
                }
                collapseGroupsOfExpandableListView(mDrawerMenExpandableListView, mDrawerMenExpandableListAdapter);
                collapseGroupsOfExpandableListView(mDrawerWomenExpandableListView, mDrawerWomenExpandableListAdapter);
            }
        });
        mDrawerKidsExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                MyntraCategory.ProductGroup productGroup = (MyntraCategory.ProductGroup) mDrawerKidsExpandableListAdapter.getChild(groupPosition, childPosition);
                selectProductGroup(productGroup);
                return true;
            }
        });

        return fragmentView;
    }

    private void collapseGroupsOfExpandableListView(ExpandableListView expandableListView, ExpandableListAdapter expandableListAdapter) {
        int len = expandableListAdapter.getGroupCount();
        for (int i = 0; i < len; i++) {
            expandableListView.collapseGroup(i);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private void selectProductGroup(MyntraCategory.ProductGroup productGroup){
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerProductGroupSelected(productGroup);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // todo: code to handle menu clicks
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void onNavigationDrawerProductGroupSelected(MyntraCategory.ProductGroup productGroup);
    }
}
