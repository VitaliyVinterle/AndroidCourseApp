package com.project.literarycatalog;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.project.literarycatalog.adapter.TabsFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;
    private Toolbar topToolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initTopToolbar();
        initNavigationView();
        initTabs();
    }


    private void initTopToolbar()
    {
        topToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        topToolbar.setTitle(R.string.app_name);
        topToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return  false;
            }
        });
        //topToolbar.inflateMenu(R.menu.menu);
    }
    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,topToolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch(menuItem.getItemId()){
                    case R.id.add_option:
                        showAddTab();
                        return true;
                    case R.id.search_by_title_option:
                        showSearchByTitleTab();
                        return true;
                    case R.id.search_by_author_option:
                        showSearchByAuthorTab();
                        return true;
                    case R.id.search_by_year_option:
                        showSearchByYearTab();
                        return true;
                    case R.id.all_books_option:
                        showAllBooksTab();
                        return true;
                }
                return true;
            }
        });
    }

    private void showAddTab(){
        viewPager.setCurrentItem(Constants.TAB_ADD);
    }
    private void showSearchByTitleTab() {
        viewPager.setCurrentItem(Constants.TAB_SEARCH_BY_TITLE);
    }
    private void showSearchByAuthorTab() { viewPager.setCurrentItem(Constants.TAB_SEARCH_BY_AUTHOR); }
    private void showSearchByYearTab() {
        viewPager.setCurrentItem(Constants.TAB_SEARCH_BY_YEAR);
    }
    private void showAllBooksTab() {
        viewPager.setCurrentItem(Constants.TAB_ALL_BOOKS);
    }
}
