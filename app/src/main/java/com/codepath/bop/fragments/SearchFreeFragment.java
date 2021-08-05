package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

import okhttp3.HttpUrl;

public class SearchFreeFragment extends Fragment {

    //class constants
    public static final String TAG = "Search Free Fragment";

    //instance variables
    private BottomNavigationView tabsFree;
    private FragmentManager fragmentManager;
    private static String url;
    private String currentURL;

    public SearchFreeFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //update the presence of a menu
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_free, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //reference to views
        tabsFree = view.findViewById(R.id.tabsFree);

        //initialize variables
        currentURL = "";
        url = "";
    }

    public void tabs(){
        tabsFree.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentManager = getChildFragmentManager();
                //show fragment based on the tab that was clicked:
                switch (item.getItemId()){
                    case R.id.allResultsFree:
                        if(fragmentManager.findFragmentByTag("allResults") != null) {
                            if (currentURL.equals(url)){
                                //if the fragment exists, show it w/ animation
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                        .show(fragmentManager.findFragmentByTag("allResults"))
                                        .commit();
                            }else{
                                //create a new fragment for a new query
                                fragmentManager.beginTransaction()
                                        .replace(R.id.flContainerFSF, new ResultsFragment(true, true, false), "allResults")
                                        .commit();
                                //update current URL
                                currentURL = url;
                            }
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainerFSF, new ResultsFragment(true, true, false), "allResults")
                                    .commit();
                            //update current URL
                            currentURL = url;
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("artistResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("artistResults")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("albumResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("albumResults")).commit();
                        }
                        break;

                    case R.id.artistResultsFree:
                        if(fragmentManager.findFragmentByTag("artistResults") != null) {
                            if (currentURL.equals(url)){
                                //if the fragment exists, show it w/ animation
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                        .show(fragmentManager.findFragmentByTag("artistResults"))
                                        .commit();
                            }else{
                                //create a new fragment for a new query
                                fragmentManager.beginTransaction()
                                        .replace(R.id.flContainerFSF, new ResultsFragment(true, false, false), "artistResults")
                                        .commit();
                                //update current URL
                                currentURL = url;
                            }
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainerFSF, new ResultsFragment(true, false, false), "artistResults")
                                    .commit();
                            //update current URL
                            currentURL = url;
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("allResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("allResults")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("albumResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("albumResults")).commit();
                        }
                        break;

                    case R.id.albumResultsFree:
                    default:
                        if(fragmentManager.findFragmentByTag("albumResults") != null) {
                            if (currentURL.equals(url)){
                                //if the fragment exists, show it w/ animation
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                        .show(fragmentManager.findFragmentByTag("albumResults"))
                                        .commit();
                            }else{
                                //create a new fragment for a new query
                                fragmentManager.beginTransaction()
                                        .replace(R.id.flContainerFSF, new ResultsFragment(false, true, false), "albumResults")
                                        .commit();
                                //update current URL
                                currentURL = url;
                            }
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainerFSF, new ResultsFragment(false, true, false), "albumResults")
                                    .commit();
                            //update current URL
                            currentURL = url;
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("allResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("allResults")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("artistResults") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("artistResults")).commit();
                        }
                        break;
                }
                return true;
            }
        });

        // Set default selection
        tabsFree.setSelectedItemId(R.id.allResultsFree);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search_fragment, menu);
        //find view
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //open search bar when fragment opens
        searchView.onActionViewExpanded();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // hide search fragment
                getActivity().getSupportFragmentManager().beginTransaction().hide(SearchFreeFragment.this).commit();
                //set title back to Browse
                getActivity().setTitle("Browse");
                //show Browse fragment
                getActivity().getSupportFragmentManager().beginTransaction().show(getActivity().getSupportFragmentManager().findFragmentByTag("browse")).commit();
                return false;
            }
        });
        //call a query on the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //create url for search query
                HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.searchURL)).newBuilder();
                urlBuilder.addQueryParameter("q", query);
                urlBuilder.addQueryParameter("type", "track,artist,album");
                urlBuilder.addQueryParameter("limit", String.valueOf(50));
                url = urlBuilder.build().toString();

                //close search bar
                searchView.onActionViewCollapsed();
                //dismiss keyboard
                searchView.clearFocus();

                //show correct tabs and fragments
                tabs();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout button
        if (item.getItemId() == R.id.Slogout) {
            onStop();
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            //go back to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public static String getURL(){
        return url;
    }

}
