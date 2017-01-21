package de.knukro.cvjm.konficastle.helper;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import de.knukro.cvjm.konficastle.R;


public class InitTabLayout {

    public static void init(Activity activity, ViewPager viewPager) {
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() < 6) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        tabLayout.setVisibility(View.VISIBLE);
    }

}
