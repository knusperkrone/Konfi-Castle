package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.TextContent;


public class DynamicTextRecycleFragment extends Fragment {

    private String tabName;

    public static Fragment newInstance(String tabName) {
        DynamicTextRecycleFragment fragment = new DynamicTextRecycleFragment();
        fragment.tabName = tabName;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inflate_array, container, false);
        final RecyclerView rv1 = (RecyclerView) rootView.findViewById(R.id.inflater_rec);
        Context context = getActivity();
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new LinearLayoutManager(context));
        rv1.setItemViewCacheSize(25);
        rv1.setAdapter(new DynamicTextContentAdapter(DbOpenHelper.getInstance().getContentForTab(tabName)));
        return rootView;
    }

    static class TextContentParentViewHolder extends ParentViewHolder {

        private TextContentParentViewHolder(View viewItem) {
            super(viewItem);
        }
    }

    static class TextContentChildViewHolder extends ChildViewHolder {

        private TextContentChildViewHolder(View viewItem) {
            super(viewItem);
        }
    }

    public static class DynamicTextContentAdapter extends ExpandableRecyclerAdapter<TextContent, TextContent, TextContentParentViewHolder, TextContentChildViewHolder> {

        public DynamicTextContentAdapter(@NonNull List<TextContent> parentList) {
            super(parentList);
        }

        @NonNull
        @Override
        public TextContentParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @NonNull
        @Override
        public TextContentChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindParentViewHolder(@NonNull TextContentParentViewHolder textContentParentViewHolder, int i, @NonNull TextContent textContent) {

        }

        @Override
        public void onBindChildViewHolder(@NonNull TextContentChildViewHolder textContentChildViewHolder, int i, int i1, @NonNull TextContent textContent) {

        }
    }

}
