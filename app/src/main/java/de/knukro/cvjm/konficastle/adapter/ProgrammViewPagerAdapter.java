package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import de.knukro.cvjm.konficastle.fragments.ProgrammRecycleFragment;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;


public class ProgrammViewPagerAdapter extends FragmentStatePagerAdapter {


    private final Context context;

    private final ArrayList<ArrayList<ExpandableTermin>> queryRes;

    public ProgrammViewPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        queryRes = DbOpenHelper.getInstance(context).getProgramm(context);
    }


    @Override
    public Fragment getItem(int position) {
        return ProgrammRecycleFragment.newInstance(queryRes.get(position), context, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return queryRes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tag " + (position + 1);
    }
}
