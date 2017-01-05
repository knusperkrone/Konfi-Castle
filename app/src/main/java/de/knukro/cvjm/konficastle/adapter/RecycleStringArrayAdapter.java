package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ArrayParentStruct;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;

public class RecycleStringArrayAdapter extends
        ExpandableRecyclerAdapter<RecycleStringArrayAdapter.AbendgebetTitleViewHolder,
                RecycleStringArrayAdapter.AbendgebetTextViewHolder> {

    private final LayoutInflater inflater;

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

    public RecycleStringArrayAdapter(@NonNull List<? extends ParentListItem> parentItemList,
                                     Context context) {
        super(parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AbendgebetTitleViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.abendgebet_title, viewGroup, false);
        return new AbendgebetTitleViewHolder(view);

    }

    @Override
    public AbendgebetTextViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.abendgebet_text, viewGroup, false);
        return new AbendgebetTextViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(AbendgebetTitleViewHolder abendgebetTitleViewHolder, int i,
                                       ParentListItem parentListItem) {
        abendgebetTitleViewHolder.title.setText(((ArrayParentStruct) parentListItem).title);
    }

    @Override
    public void onBindChildViewHolder(AbendgebetTextViewHolder textViewHolder, int i, Object o) {
        textViewHolder.text.setText(((ExpandableDescription) o).beschreibung);
    }

}


