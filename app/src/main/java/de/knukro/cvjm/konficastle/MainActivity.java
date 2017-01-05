package de.knukro.cvjm.konficastle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.knukro.cvjm.konficastle.fragments.AbendgebetFragment;
import de.knukro.cvjm.konficastle.fragments.ProgrammFragment;
import de.knukro.cvjm.konficastle.fragments.StartInDenTagRecycleFragment;
import de.knukro.cvjm.konficastle.fragments.WebViewFragment;
import de.knukro.cvjm.konficastle.fragments.WelcomeDialog;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.helper.NotificationHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private NavigationView navigationView;

    private Fragment currFragment;
    private Class fragmentClass;
    private int currView = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbOpenHelper.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        initInteraction();
        //NotificationHelper.testNotification(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currView == -1 || currView == R.id.nav_programm) {
            currView = -1;
            onNavigationItemSelected(navigationView.getMenu().getItem(1));
            ProgrammFragment.setProgrammTitle(this, toolbar);
        }
    }

    private void initInteraction() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String firstboot = getString(R.string.firstboot);
        if (sp.getBoolean(firstboot, true)) {
            sp.edit().putBoolean(firstboot, false).apply();
            WelcomeDialog welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getSupportFragmentManager(), "welcome_dialog");
        }
        NotificationHelper.setupNotifications(this);
    }

    @Override
    public void onBackPressed() {
        if (fragmentClass != null && fragmentClass == WebViewFragment.class &&
                ((WebViewFragment) currFragment).webview.canGoBack()) {
            ((WebViewFragment) currFragment).webview.goBack();
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int viewId = item.getItemId();

        if (viewId == currView) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        Bundle bundle = new Bundle();

        switch (viewId) {
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_abendgebet:
                fragmentClass = AbendgebetFragment.class;
                toolbar.setTitle("Abendgebet");
                break;

            case R.id.nav_angebote:
                fragmentClass = WebViewFragment.class;
                bundle.putString("curl", "https://www.cvjm-bayern.de/urlaub-seminare/freizeiten-und-seminare.html");
                toolbar.setTitle("Freizeit Angebote");
                break;

            /*case R.id.nav_feedback:
                fragmentClass = WebViewFragment.class;
                bundle.putString("curl", "https://www.cvjm-bayern.de/spenden-kontakt/gaestebuch.html");
                toolbar.setTitle("Feedback");
                break;*/

            case R.id.nav_gaestebuch:
                fragmentClass = WebViewFragment.class;
                bundle.putString("curl", "https://www.cvjm-bayern.de/spenden-kontakt/gaestebuch.html");
                toolbar.setTitle("Gästebuch");
                break;

            case R.id.nav_programm:
                fragmentClass = ProgrammFragment.class;
                ProgrammFragment.setProgrammTitle(this, toolbar);
                break;

            case R.id.nav_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                toolbar.setTitle("Einstellungen");
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_start:
                fragmentClass = StartInDenTagRecycleFragment.class;
                toolbar.setTitle("Start in den Tag");
                break;
        }

        if (fragmentClass != null) {
            if (fragmentClass == WebViewFragment.class) {
                tabLayout.setVisibility(View.GONE);
            } else {
                tabLayout.setVisibility(View.VISIBLE);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            try {
                currFragment = (Fragment) fragmentClass.newInstance();
                currFragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ft.replace(R.id.content_main, currFragment).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        currView = viewId;
        return true;
    }

}
