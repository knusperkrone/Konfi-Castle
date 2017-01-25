package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.NotizenActivity;
import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;


public class ProgrammAdapter extends
        ExpandableRecyclerAdapter<ProgrammAdapter.RecycleProgrammViewHolder, ProgrammAdapter.RecycleDescriptionViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final int currDay;
    private final DbOpenHelper dbOpenHelper;
    private final Drawable animationResource;


    static class RecycleProgrammViewHolder extends ParentViewHolder {

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


    static class RecycleDescriptionViewHolder extends ChildViewHolder {

        final TextView desrc;
        final CardView cv;

        private RecycleDescriptionViewHolder(View viewItem) {
            super(viewItem);
            desrc = (TextView) viewItem.findViewById(R.id.exp_desc);
            cv = (CardView) viewItem.findViewById(R.id.cardview_description);
        }
    }


    public ProgrammAdapter(@NonNull List<? extends ParentListItem> parentItemList,
                           Context context, int day) {
        super(parentItemList);
        this.context = context;
        this.currDay = day;
        inflater = LayoutInflater.from(context);
        dbOpenHelper = DbOpenHelper.getInstance();
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        animationResource = typedArray.getDrawable(0 /* index */);
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
    public void onBindParentViewHolder(final RecycleProgrammViewHolder recycleProgrammViewHolder,
                                       final int i, ParentListItem parentListItem) {

        final String name = ((ExpandableTermin) parentListItem).name;
        final String time = ((ExpandableTermin) parentListItem).time;
        recycleProgrammViewHolder.time.setText(time);
        recycleProgrammViewHolder.name.setText(name);


        recycleProgrammViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                
                SharedValues.toExpand = name;
                SharedValues.setProgrammScrollPosition(i - 1);

                Intent myIntent = new Intent(context, NotizenActivity.class);
                myIntent.putExtra("expandend", recycleProgrammViewHolder.isExpanded());
                myIntent.putExtra("title", "Tag " + currDay + ": " + name);
                myIntent.putExtra("time", time);
                myIntent.putExtra("day", String.valueOf(currDay + 1));
                context.startActivity(myIntent);
                return true;
            }
        });

        if (((ExpandableTermin) parentListItem).details.isEmpty()) {
            recycleProgrammViewHolder.indicator.setVisibility(View.GONE);
        } else {
            recycleProgrammViewHolder.indicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindChildViewHolder(final RecycleDescriptionViewHolder recycleDescriptionViewHolder,
                                      final int i, final Object o) {

        final String beschreibung = ((ExpandableDescription) o).beschreibung.toString();
            /* Notiz implementation may seem buggy, but works pretty good and feels somehow faster
             * than an extra join on the database*/
        if (beschreibung.startsWith("00NOTIZ::")) {
            recycleDescriptionViewHolder.cv.setForeground(animationResource);
            recycleDescriptionViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    /*Save the Programm state*/
                    ArrayList<ExpandableTermin> list;
                    do {
                        list = dbOpenHelper.getProgramm(context).get(currDay);
                        if (list == null) {
                            try {
                                wait(2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } while (list == null);
                    SharedValues.setProgrammScrollPosition(recycleDescriptionViewHolder.getAdapterPosition() - 2);
                    SharedValues.toExpand = binarySearch(list, ((ExpandableDescription) o).time, 0, list.size() - 1);

                    Intent myIntent = new Intent(context, NotizenActivity.class);
                    myIntent.putExtra("title", "Notiz bearbeiten");
                    myIntent.putExtra("content", beschreibung);
                    myIntent.putExtra("day", String.valueOf(currDay + 1));
                    myIntent.putExtra("time", ((ExpandableDescription) o).time);
                    context.startActivity(myIntent);
                    return true;
                }
            });

            recycleDescriptionViewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            recycleDescriptionViewHolder.desrc.setText(beschreibung.substring(9));
        } else {
            recycleDescriptionViewHolder.cv.setForeground(null);
            recycleDescriptionViewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            recycleDescriptionViewHolder.desrc.setText(beschreibung);
        }
    }


    private static String binarySearch(List<ExpandableTermin> list, String toSearch, int left, int right) {
        if (left > right) {
            return "";
        }
        int median = (left + right) / 2;
        int cmp = compare(list.get(median).time, toSearch);
        if (cmp == 0) {
            return list.get(median).name;
        }
        if (cmp > 0) {
            return binarySearch(list, toSearch, left, median - 1);
        }
        return binarySearch(list, toSearch, median + 1, right);
    }

    /*Compare to time representing Stings in formation: HH:MM*/
    private static int compare(String date1, String date2) {
        int out = Integer.valueOf(date1.substring(0, 2)) - Integer.valueOf(date2.substring(0, 2));
        if (out == 0) {
            out = Integer.valueOf(date1.substring(3, 5)) - Integer.valueOf(date2.substring(3, 5));
        }
        return out;
    }


}