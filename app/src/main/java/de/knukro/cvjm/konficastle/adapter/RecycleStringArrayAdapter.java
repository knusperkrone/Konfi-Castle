package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ArrayParentStruct;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;

/*This is the Adapter for DynamicViewPagerArrayAdapter and more ore less just a viewHolder*/
class RecycleStringArrayAdapter extends
        ExpandableRecyclerAdapter<ArrayParentStruct, ExpandableDescription, RecycleStringArrayAdapter.AbendgebetTitleViewHolder,
                RecycleStringArrayAdapter.AbendgebetTextViewHolder> {

    private final LayoutInflater inflater;


    RecycleStringArrayAdapter(@NonNull List<ArrayParentStruct> parentItemList,
                              Context context) {
        super(parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AbendgebetTitleViewHolder onCreateParentViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AbendgebetTitleViewHolder(inflater.inflate(R.layout.element_abendgebet_title, viewGroup, false));
    }

    @NonNull
    @Override
    public AbendgebetTextViewHolder onCreateChildViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AbendgebetTextViewHolder(inflater.inflate(R.layout.element_abendgebet_text, viewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(@NonNull AbendgebetTitleViewHolder abendgebetTitleViewHolder, int i, @NonNull ArrayParentStruct parentListItem) {
        abendgebetTitleViewHolder.title.setText(parentListItem.title);
    }

    @Override
    public void onBindChildViewHolder(@NonNull AbendgebetTextViewHolder abendgebetTextViewHolder, int i, int i1, @NonNull ExpandableDescription o) {
        abendgebetTextViewHolder.text.setText(o.description);
    }

    static class AbendgebetTitleViewHolder extends ParentViewHolder {

        final TextView title;

        private AbendgebetTitleViewHolder(View viewItem) {
            super(viewItem);
            title = (TextView) viewItem.findViewById(R.id.abendgebet_title);
        }
    }

    static class AbendgebetTextViewHolder extends ChildViewHolder {

        final TextView text;

        private AbendgebetTextViewHolder(View viewItem) {
            super(viewItem);
            text = (TextView) viewItem.findViewById(R.id.abendgebet_text);
        }
    }

}


