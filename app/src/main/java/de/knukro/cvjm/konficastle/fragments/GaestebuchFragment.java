package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.helper.AsyncAdapterSet;
import de.knukro.cvjm.konficastle.structs.Entry;
import de.knukro.cvjm.konficastle.structs.RegisterSite;


public class GaestebuchFragment extends Fragment {

    private LinearLayoutManager llm;
    private FloatingActionButton button;

    @Override
    public void onPause() {
        super.onPause();
        button.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        button.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        SharedValues.killRunningAsyncTasks();
        View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        final Context context = getContext();
        final RecyclerView rv1 = (RecyclerView) rootView.findViewById(R.id.inflater_rec);
        button = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.cvjm-bayern.de/spenden-kontakt/gaestebuch/eintrag-ins-gaestebuch.html"));
                context.startActivity(i);
            }
        });
        rv1.setHasFixedSize(true);
        llm = new LinearLayoutManager(context);
        rv1.setLayoutManager(llm);
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (llm.findLastCompletelyVisibleItemPosition() == llm.getItemCount() - 1)
                    new AsyncAdapterSet(context, R.id.nav_gaestebuch, rv1, false).execute();
            }
        });
        new AsyncAdapterSet(context, R.id.nav_gaestebuch, rv1, true).execute();
        return rootView;
    }


    public static class GaestebuchViewHolder extends RecyclerView.ViewHolder {
        final TextView author, text;

        GaestebuchViewHolder(View view) {
            super(view);
            this.author = (TextView) view.findViewById(R.id.element_guestbook_author);
            this.text = (TextView) view.findViewById(R.id.element_guestbook_text);
        }
    }

    public static class GaestebuchAdapter extends RecyclerView.Adapter<GaestebuchViewHolder> {
        private final LayoutInflater inflater;
        public final RegisterSite currSite;

        public GaestebuchAdapter(Context context, RegisterSite list) {
            inflater = LayoutInflater.from(context);
            if (list != null) {
                this.currSite = list;
            } else {
                this.currSite = new RegisterSite("");
            }
        }

        @Override
        public GaestebuchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GaestebuchViewHolder(inflater.inflate(R.layout.element_guestbook, parent, false));
        }

        @Override
        public void onBindViewHolder(GaestebuchViewHolder holder, int position) {
            Entry currEntry = currSite.entrys.get(position);
            holder.author.setText(currEntry.author);
            holder.text.setText(currEntry.text);
        }


        @Override
        public int getItemCount() {
            return currSite.entrys.size();
        }
    }


}
