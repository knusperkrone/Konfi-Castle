package de.knukro.cvjm.konficastle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import de.knukro.cvjm.konficastle.fragments.DynamicTextRecycleFragment;


public class DynamicMenuViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> tabs;

    public DynamicMenuViewPagerAdapter(FragmentManager fm, List<String> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return DynamicTextRecycleFragment.newInstance(tabs.get(position));
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
