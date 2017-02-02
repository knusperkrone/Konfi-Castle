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

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.List;

import de.knukro.cvjm.konficastle.NotizenActivity;
import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;


public class ProgrammAdapter extends
        ExpandableRecyclerAdapter<ExpandableTermin, ExpandableDescription, ProgrammAdapter.RecycleProgrammViewHolder, ProgrammAdapter.RecycleDescriptionViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final int currDay;

    public ProgrammAdapter(@NonNull List<ExpandableTermin> parentItemList,
                           Context context, int day) {
        super(parentItemList);
        this.context = context;
        this.currDay = day;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecycleProgrammViewHolder onCreateParentViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.expandable_termin, viewGroup, false);
        return new RecycleProgrammViewHolder(view);
    }

    @Override
    public RecycleDescriptionViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.expandable_description, viewGroup, false);
        return new RecycleDescriptionViewHolder(view, context);
    }

    @Override
    public void onBindParentViewHolder(@NonNull final RecycleProgrammViewHolder recycleProgrammViewHolder, final int i, @NonNull final ExpandableTermin parentListItem) {
        final String name = parentListItem.name;
        final String time = parentListItem.time;
        recycleProgrammViewHolder.time.setText(time);
        recycleProgrammViewHolder.name.setText(name);
        final ExpandableRecyclerAdapter adapter = this;

        recycleProgrammViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Intent myIntent = new Intent(context, NotizenActivity.class);
                myIntent.putExtra("title", "Tag " + currDay + ": " + name);
                myIntent.putExtra("day", String.valueOf(currDay + 1));
                myIntent.putExtra("time", time);
                myIntent.putExtra("parent", i);

                SharedValues.setAdapter(adapter);
                context.startActivity(myIntent);
                return true;
            }
        });

        if (parentListItem.details.isEmpty()) {
            recycleProgrammViewHolder.indicator.setVisibility(View.GONE);
        } else {
            recycleProgrammViewHolder.indicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull final RecycleDescriptionViewHolder recycleDescriptionViewHolder, final int i, final int i1, @NonNull final ExpandableDescription o) {
        final String beschreibung = o.description.toString();
            /* Notiz implementation may seem buggy, but works pretty good and feels somehow faster
             * than an extra join on the database*/
        if (beschreibung.startsWith("00NOTIZ::")) {
            final ExpandableRecyclerAdapter adapter = this;
            recycleDescriptionViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent myIntent = new Intent(context, NotizenActivity.class);
                    myIntent.putExtra("title", context.getString(R.string.adapter_programm_defaultnotiz));
                    myIntent.putExtra("day", String.valueOf(currDay + 1));
                    myIntent.putExtra("time", o.time);
                    myIntent.putExtra("content", beschreibung);
                    myIntent.putExtra("parent", i);

                    SharedValues.setAdapter(adapter);
                    context.startActivity(myIntent);
                    return true;
                }
            });

            recycleDescriptionViewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            recycleDescriptionViewHolder.desrc.setText(beschreibung.substring(9));
            recycleDescriptionViewHolder.cv.setForeground(recycleDescriptionViewHolder.animation);
        } else {
            recycleDescriptionViewHolder.cv.setForeground(null);
            recycleDescriptionViewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            recycleDescriptionViewHolder.desrc.setText(beschreibung);
        }
    }

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
        final Drawable animation;

        private RecycleDescriptionViewHolder(View viewItem, Context context) {
            super(viewItem);
            desrc = (TextView) viewItem.findViewById(R.id.exp_desc);
            cv = (CardView) viewItem.findViewById(R.id.cardview_description);
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            animation = typedArray.getDrawable(0);
        }
    }

}