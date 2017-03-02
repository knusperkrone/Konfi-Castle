package de.knukro.cvjm.konficastle.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import de.knukro.cvjm.konficastle.structs.DynamicTextElement;


public class DynamicMenuAdapter extends ExpandableRecyclerAdapter<DynamicTextElement, DynamicTextElement, DynamicTextElement.DynamicTextParentHolder, DynamicTextElement.DynamicTextChildHolder> {

    public DynamicMenuAdapter(@NonNull List<DynamicTextElement> parentList) {
        super(parentList);
    }

    @NonNull
    @Override
    public DynamicTextElement.DynamicTextParentHolder onCreateParentViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @NonNull
    @Override
    public DynamicTextElement.DynamicTextChildHolder onCreateChildViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindParentViewHolder(@NonNull DynamicTextElement.DynamicTextParentHolder dynamicTextParentHolder, int i, @NonNull DynamicTextElement dynamicTextElement) {

    }

    @Override
    public void onBindChildViewHolder(@NonNull DynamicTextElement.DynamicTextChildHolder dynamicTextChildHolder, int i, int i1, @NonNull DynamicTextElement dynamicTextElement) {

    }
}
