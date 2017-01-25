package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.adapter.ProgrammViewPagerAdapter;
import de.knukro.cvjm.konficastle.adapter.ZoomOutPageTransformer;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.helper.InitTabLayout;

/*This Fragments sets up the ViewPager for the Programm*/
public class ProgrammFragment extends Fragment {

    private ViewPager viewPager;

    public static void setProgrammTitle(Context context, Toolbar toolbar) {
        String instance = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.instanz_key), "1");
        if (instance.equals("13")) {
            toolbar.setTitle("ÖC");
        } else {
            toolbar.setTitle("Konfi Castle " + instance);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedValues.setCurrProgrammViewPagerPosition(viewPager.getCurrentItem());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        final View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        final Context context = getContext();
        int currPage = SharedValues.getAndResetCurrProgrammViewPagerPosition();

        viewPager = (ViewPager) rootView.findViewById(R.id.inflater_viewpager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(new ProgrammViewPagerAdapter(getActivity(), getFragmentManager()));
        SharedValues.killRunningAsyncTasks();
        if (currPage != -1) { //Go back to the old page
            viewPager.setCurrentItem(currPage);
        } else if (SharedValues.getCurrProgrammDay(context) <
                DbOpenHelper.getInstance().getDate(context).length) { //ViewPager gets initial set to the current day
            viewPager.setCurrentItem((int) SharedValues.getCurrProgrammDay(context));
        }

        SharedValues.init(getActivity(), viewPager);

        return rootView;
    }

}
