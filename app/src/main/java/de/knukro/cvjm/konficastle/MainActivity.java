package de.knukro.cvjm.konficastle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.knukro.cvjm.konficastle.dialogs.WelcomeDialog;
import de.knukro.cvjm.konficastle.fragments.AbendgebetFragment;
import de.knukro.cvjm.konficastle.fragments.FreizeitenFragment;
import de.knukro.cvjm.konficastle.fragments.GaestebuchFragment;
import de.knukro.cvjm.konficastle.fragments.ProgrammFragment;
import de.knukro.cvjm.konficastle.fragments.StartInDenTagRecycleFragment;
import de.knukro.cvjm.konficastle.helper.BootReceiver;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.helper.DbUpdater;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private NavigationView navigationView;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currFragment;
    private Class fragmentClass;
    private int currView = -1;
    private static boolean update = false;


    private void showFragment() {
        if (fragmentClass != null) {
            try {
                currFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fm.beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.content_main, currFragment)
                    .commit();
            currFragment = null;
        }
    }

    public static void refreshView() {
        update = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SharedValues.toExpand = extras.getString(SharedValues.TO_EXPAND, "");
        }

        DbOpenHelper.initInstance(this); //Init DbOpenHelper

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerClosed(view);
                showFragment();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        ((FloatingActionButton) findViewById(R.id.floatingActionButton)).hide();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String firstboot = getString(R.string.firstboot);
        ChangeLog cl = new ChangeLog(this);
        if (sp.getBoolean(firstboot, true)) {
            WelcomeDialog welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(fm, "welcome_dialog");
            BootReceiver.resetNotifications(this);
            cl.updateVersionInPreferences(); //Don't show changelog
            sp.edit().putBoolean(firstboot, false).apply();
        } else if (cl.isFirstRun()) {
            cl.getLogDialog().show();
            BootReceiver.resetNotifications(this);
        }
        new DbUpdater(this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currView == -1 || (update && currView == R.id.nav_programm)) {
            ProgrammFragment.setProgrammTitle(this, toolbar);
            currView = R.id.nav_programm;
            navigationView.setCheckedItem(R.id.nav_programm);
            fragmentClass = ProgrammFragment.class;
            showFragment();
            update = false;
        }
    }


    @Override
    public void onBackPressed() {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            finish();
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
        currView = -1;
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


        switch (viewId) {
            case R.id.nav_about:
                startActivity(new Intent(getApplication(), AboutActivity.class));
                break;

            case R.id.nav_abendgebet:
                fragmentClass = AbendgebetFragment.class;
                toolbar.setTitle("Abendgebet");
                break;

            case R.id.nav_angebote:
                fragmentClass = FreizeitenFragment.class;
                toolbar.setTitle("Freizeit Angebote");
                break;

            /*case R.id.nav_feedback:
                fragmentClass = WebViewFragment.class;
                bundle.putString("curl", "https://www.cvjm-bayern.de/spenden-kontakt/gaestebuch.html");
                toolbar.setTitle("Feedback");
                break;*/

            case R.id.nav_gaestebuch:
                fragmentClass = GaestebuchFragment.class;
                toolbar.setTitle("Gästebuch");
                break;

            case R.id.nav_programm:
                fragmentClass = ProgrammFragment.class;
                ProgrammFragment.setProgrammTitle(this, toolbar);
                break;

            case R.id.nav_setting:
                startActivity(new Intent(getApplication(), SettingsActivity.class));
                toolbar.setTitle("Einstellungen");
                break;

            case R.id.nav_start:
                fragmentClass = StartInDenTagRecycleFragment.class;
                toolbar.setTitle("Start in den Tag");
                break;
        }

        if (fragmentClass != null && fragmentClass == GaestebuchFragment.class) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }

        if (fragmentClass == null) {
            viewId = -1;
        }

        currView = viewId;

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
