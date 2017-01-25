package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.adapter.ProgrammAdapter;
import de.knukro.cvjm.konficastle.helper.AsyncAdapterSet;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;


public class ProgrammRecycleFragment extends Fragment {

    private List<ExpandableTermin> query;
    private Context context;
    private int position;


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
        rv.setItemViewCacheSize(35);

        rv.setAdapter(new ProgrammAdapter(query, context, position));

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        int currPosition = SharedValues.getAndResetProgrammScrollPosition();
        if (currPosition != -1) {
            /*This will just happen if the User interacts with the NotizActivity*/
            llm.scrollToPosition(currPosition);
        } else if (SharedValues.getCurrProgrammDay(context) == position) {
            /*Scroll down until event time fits System time*/
            llm.scrollToPosition((scrollToTime(0, query.size() - 1, new Date().getHours()) - 1));
        }
        return rootView;
    }

    private int scrollToTime(int left, int right, int currHour) {
        int median = (left + right) / 2;
        int medianTime = Integer.valueOf(query.get(median).time.substring(0, 2));
        if (left >= right) {
            return right;
        } else if (medianTime == currHour) {
            return median;
        } else if (medianTime < currHour) {
            return scrollToTime(median + 1, right, currHour);
        }
        return scrollToTime(left, median - 1, currHour);
    }

}
