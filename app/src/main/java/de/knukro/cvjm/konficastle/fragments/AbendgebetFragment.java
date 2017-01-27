package de.knukro.cvjm.konficastle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.adapter.DynamicViewPagerArrayAdapter;
import de.knukro.cvjm.konficastle.adapter.ZoomOutPageTransformer;


public class AbendgebetFragment extends Fragment {

    private static final List<Integer> titleIds = new ArrayList<>();
    private static final List<Integer> valueIds = new ArrayList<>();
    private static final List<String> viewPagerTitles = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        if (titleIds.isEmpty()) {
            titleIds.add(R.array.abendgebet_titles1);
            titleIds.add(R.array.abendgebet_liederTitel);

            valueIds.add(R.array.abendgebet_values1);
            valueIds.add(R.array.abendgebet_liederText);

            viewPagerTitles.add("Texte");
            viewPagerTitles.add("Lieder");
        }

        final View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.inflater_viewpager);

        viewPager.setAdapter(new DynamicViewPagerArrayAdapter(getFragmentManager(), titleIds, valueIds, viewPagerTitles));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        SharedValues.initTablayout(getActivity(), viewPager);


        return rootView;
    }

}
