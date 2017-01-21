package de.knukro.cvjm.konficastle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.knukro.cvjm.konficastle.fragments.FreizeitenFragment;


public class FreizeitViewPagerAdapter extends FragmentStatePagerAdapter {

    public FreizeitViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return FreizeitenFragment.FreizeitenRecycleFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Kinder";
            case 1:
                return "Teens";
            case 2:
                return "Junge Erwachsene";
            case 3:
                return "Erwachsene";
            case 4:
                return "Familien/Alleinerziehende";
            case 5:
                return "Frauen";
            case 6:
                return "Mitarbeiterin(innen)";
        }
        return "Fehler" + (position + 1);
    }

}
