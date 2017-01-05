package de.knukro.cvjm.konficastle.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.adapter.DynamicViewPagerArrayAdapter;
import de.knukro.cvjm.konficastle.adapter.ZoomOutPageTransformer;


public class StartInDenTagRecycleFragment extends Fragment {

    private static final List<Integer> titleIds = new ArrayList<>();
    private static final List<Integer> valueIds = new ArrayList<>();
    private static final List<String> viewPagerTitles = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        if (titleIds.isEmpty()) {
            titleIds.add(R.array.startInDenTag_saTitel);
            titleIds.add(R.array.startInDenTag_soTitel);

            valueIds.add(R.array.startInDenTag_saText);
            valueIds.add(R.array.startInDenTag_soText);

            viewPagerTitles.add("Samstag");
            viewPagerTitles.add("Sonntag");
        }

        final View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.inflater_viewpager);

        DynamicViewPagerArrayAdapter adapter =
                new DynamicViewPagerArrayAdapter(getFragmentManager(), titleIds, valueIds, viewPagerTitles);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        final TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        return rootView;
    }

}
