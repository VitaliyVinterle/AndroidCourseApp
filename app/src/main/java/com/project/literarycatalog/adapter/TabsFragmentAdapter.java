package com.project.literarycatalog.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.literarycatalog.tab.AbstractTabFragment;
import com.project.literarycatalog.tab.AddTab;
import com.project.literarycatalog.tab.AllBooksTab;
import com.project.literarycatalog.tab.SearchByAuthorTab;
import com.project.literarycatalog.tab.SearchByTitleTab;
import com.project.literarycatalog.tab.SearchByYearTab;

import java.util.HashMap;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter{

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, AddTab.getInstance(context));
        tabs.put(1, SearchByTitleTab.getInstance(context));
        tabs.put(2, SearchByAuthorTab.getInstance(context));
        tabs.put(3, SearchByYearTab.getInstance(context));
        tabs.put(4, AllBooksTab.getInstance(context));
    }
}