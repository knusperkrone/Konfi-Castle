package de.knukro.cvjm.konficastle.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.adapter.FreizeitViewPagerAdapter;
import de.knukro.cvjm.konficastle.adapter.FreizeitenAdapter;
import de.knukro.cvjm.konficastle.adapter.ZoomOutPageTransformer;
import de.knukro.cvjm.konficastle.helper.AsyncAdapterSet;
import de.knukro.cvjm.konficastle.structs.Category;


public class FreizeitenFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        final View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        SharedValues.killRunningAsyncTasks(FreizeitenFragment.class);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.inflater_viewpager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(new FreizeitViewPagerAdapter(getFragmentManager()));
        SharedValues.initTablayout(getActivity(), viewPager);

        return rootView;
    }


    public static class FreizeitenRecycleFragment extends Fragment {

        public static List<Category> categories;
        public int position;

        public static FreizeitenRecycleFragment newInstance(int position) {
            FreizeitenRecycleFragment fragment = new FreizeitenRecycleFragment();
            fragment.position = position;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
            final Activity context = getActivity();

            //Write Permission makes Performance way better!
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
            final RecyclerView rv1 = (RecyclerView) rootView.findViewById(R.id.inflater_rec);
            rv1.setHasFixedSize(true);
            rv1.setLayoutManager(new LinearLayoutManager(context));
            rv1.setItemViewCacheSize(35);
            rv1.setDrawingCacheEnabled(true);
            rv1.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            if (categories == null) {
                new AsyncAdapterSet(context, R.id.nav_angebote, rv1, position).execute();
            } else {
                rv1.setAdapter(new FreizeitenAdapter(context, position));
            }
            return rootView;
        }
    }

}
