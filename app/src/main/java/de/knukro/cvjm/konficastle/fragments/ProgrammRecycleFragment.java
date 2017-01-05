package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.Date;
import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;


public class ProgrammRecycleFragment extends Fragment {

    private List<ExpandableTermin> query;
    private Context context;
    private int position;
    private final LinearLayoutManager llm = new LinearLayoutManager(getActivity());

    public static int currDay = -1;


    public static ProgrammRecycleFragment newInstance(List<ExpandableTermin> query, Context context,
                                                      int position) {
        ProgrammRecycleFragment program = new ProgrammRecycleFragment();
        program.query = query;
        program.context = context;
        program.position = position;
        return program;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_programm, container, false);

        final RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recylce_programm);
        rv.setHasFixedSize(true);
        RecycleTerminViewAdapter adapter = new RecycleTerminViewAdapter(query, context);
        rv.setAdapter(adapter);
        rv.setLayoutManager(llm);
        if (currDay == position) {
            llm.scrollToPosition(scrollTo());
        }


        return rootView;
    }


    private int scrollTo() {
        int currHour, terminTime, approx;
        currHour = new Date().getHours();
        approx = 24;
        int i = 0;
        for (ExpandableTermin termin : query) {
            terminTime = Integer.valueOf(termin.time.substring(0, 2)); //WTF
            if (terminTime - currHour < approx) {
                approx = i;
                if (terminTime == currHour)
                    break;
                i++;
            }
        }
        return approx;
    }


    static private class RecycleProgrammViewHolder extends ParentViewHolder {

        final TextView indicator;
        final TextView time;
        final TextView name;

        private RecycleProgrammViewHolder(View viewItem) {
            super(viewItem);
            indicator = (TextView) viewItem.findViewById(R.id.rec_indicator);
            time = (TextView) viewItem.findViewById(R.id.rec_time);
            name = (TextView) viewItem.findViewById(R.id.rec_name);
        }

    }


    static private class RecycleDescriptionViewHolder extends ChildViewHolder {

        final TextView desrc;

        private RecycleDescriptionViewHolder(View viewItem) {
            super(viewItem);
            desrc = (TextView) viewItem.findViewById(R.id.exp_desc);
        }
    }


    static public class RecycleTerminViewAdapter extends
            ExpandableRecyclerAdapter<RecycleProgrammViewHolder, RecycleDescriptionViewHolder> {

        final LayoutInflater inflater;

        private RecycleTerminViewAdapter(@NonNull List<? extends ParentListItem> parentItemList,
                                         Context context) {
            super(parentItemList);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RecycleProgrammViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.expandable_termin, viewGroup, false);
            return new RecycleProgrammViewHolder(view);
        }

        @Override
        public RecycleDescriptionViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.expandable_description, viewGroup, false);
            return new RecycleDescriptionViewHolder(view);
        }

        @Override
        public void onBindParentViewHolder(RecycleProgrammViewHolder recycleProgrammViewHolder,
                                           int i, ParentListItem parentListItem) {
            recycleProgrammViewHolder.time.setText(((ExpandableTermin) parentListItem).time);
            recycleProgrammViewHolder.name.setText(((ExpandableTermin) parentListItem).name);
            if (((ExpandableTermin) parentListItem).details.isEmpty()) {
                recycleProgrammViewHolder.indicator.setVisibility(View.GONE);
            } else {
                recycleProgrammViewHolder.indicator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onBindChildViewHolder(RecycleDescriptionViewHolder recycleDescriptionViewHolder,
                                          int i, Object o) {
            recycleDescriptionViewHolder.desrc.setText(((ExpandableDescription) o).beschreibung);
        }
    }

}
