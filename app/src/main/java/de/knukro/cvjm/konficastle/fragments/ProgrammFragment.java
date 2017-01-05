package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.adapter.ProgrammViewPagerAdapter;
import de.knukro.cvjm.konficastle.adapter.ZoomOutPageTransformer;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.helper.SchedulerHelper;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

public class ProgrammFragment extends Fragment {

    public static void setProgrammTitle(Context context, Toolbar toolbar) {
        String instance = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.instanz_key), "1");
        if (instance.equals("13")) {
            toolbar.setTitle("ÖC");
        } else {
            toolbar.setTitle("Konfi Castle " + instance);
        }
    }

    private static int getViewPagerPosition(Context context) {
        SchedulerObject toCheck = DbOpenHelper.getInstance(context).getDates(context);
        int i = (int) SchedulerHelper.getDayDiff(toCheck);
        return (i == -1) ? 0 : i;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        final View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);

        ProgrammRecycleFragment.currDay = getViewPagerPosition(getActivity());

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.inflater_viewpager);
        ProgrammViewPagerAdapter adapter = new ProgrammViewPagerAdapter(getActivity(), getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(ProgrammRecycleFragment.currDay);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

}
