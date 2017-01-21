package de.knukro.cvjm.konficastle.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.adapter.FreizeitenAdapter;
import de.knukro.cvjm.konficastle.adapter.ProgrammAdapter;
import de.knukro.cvjm.konficastle.fragments.FreizeitenFragment;
import de.knukro.cvjm.konficastle.fragments.GaestebuchFragment;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;
import de.knukro.cvjm.konficastle.structs.RegisterSite;


public class AsyncAdapterSet extends AsyncTask<Void, Void, Object> {

    private static String guestbookPage;

    private final Context context;
    private final int id;
    private final RecyclerView rv;
    private int getPosition, position;
    private boolean init;
    private GaestebuchFragment.GaestebuchAdapter adapter;
    private LinearLayoutManager llm;
    private List<ExpandableTermin> query;
    private ProgressDialog progressDialog;


    public AsyncAdapterSet(Context context, int id, RecyclerView rv, boolean init) {
        this.context = context;
        this.id = id;
        this.rv = rv;
        this.init = init;
        adapter = (GaestebuchFragment.GaestebuchAdapter) rv.getAdapter();
        llm = (LinearLayoutManager) rv.getLayoutManager();
        getPosition = llm.getItemCount();
        if (init) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Lade...");
            progressDialog.show();
        }
    }

    public AsyncAdapterSet(Context context, int id, RecyclerView rv, int position, List<ExpandableTermin> query) {
        this.context = context;
        this.id = id;
        this.rv = rv;
        this.position = position;
        this.query = query;
        if (id == R.id.nav_angebote && FreizeitenFragment.FreizeitenRecycleFragment.categories == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Lade..");
            progressDialog.show();
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {
        switch (id) {
            case R.id.nav_programm:
                return new ProgrammAdapter(query, context, position);
            case R.id.nav_angebote:
                try {
                    synchronized (AsyncAdapterSet.class) {
                        if (FreizeitenFragment.FreizeitenRecycleFragment.categories == null) {
                            FreizeitenFragment.FreizeitenRecycleFragment.categories = WebPagerParser.getFreizeiten();
                        }
                    }
                } catch (Exception e) {
                    Log.d("nav_angebote", "" +e);
                    e.printStackTrace();
                }
                return new FreizeitenAdapter(context, position);
            case R.id.nav_gaestebuch:
                try {
                    RegisterSite landingPage;
                    if (init) {
                        landingPage = WebPagerParser.getEntrys("https://www.cvjm-bayern.de/spenden-kontakt/gaestebuch.html");
                        guestbookPage = landingPage.nextSite;
                        return new GaestebuchFragment.GaestebuchAdapter(context, landingPage);
                    } else {
                        landingPage = WebPagerParser.getEntrys(guestbookPage);
                        guestbookPage = landingPage.nextSite;
                        adapter.currSite.entrys.addAll(landingPage.entrys);
                        adapter.notifyItemInserted(landingPage.entrys.size());
                    }
                } catch (Exception e) {
                    Log.d("nav_gaestebuch", "" +e);
                    e.printStackTrace();
                    return null;
                }
                return adapter;

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object adapter) {
        super.onPostExecute(adapter);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (adapter != null) {
            switch (id) {
                case R.id.nav_programm:
                case R.id.nav_angebote:
                    rv.setAdapter((RecyclerView.Adapter) adapter);
                    break;
                case R.id.nav_gaestebuch:
                    if (init) {
                        rv.setAdapter((RecyclerView.Adapter) adapter);
                    } else {
                        llm.scrollToPosition(getPosition);
                    }
                    break;
            }
        }
    }


}
