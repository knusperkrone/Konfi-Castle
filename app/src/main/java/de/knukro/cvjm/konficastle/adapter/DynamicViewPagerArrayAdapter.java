package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ArrayParentStruct;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;

/*Set's up a Viewholder that iflates itself from values of a XML-String[]*/
public class DynamicViewPagerArrayAdapter extends FragmentStatePagerAdapter {

    private final List<Integer> titleIds;
    private final List<Integer> valueIds;
    private final List<String> viewPagerTitles;


    public DynamicViewPagerArrayAdapter(FragmentManager fm, List<Integer> titleIds,
                                        List<Integer> valueIds, List<String> viewPagerTitles) {
        super(fm);
        this.titleIds = titleIds;
        this.valueIds = valueIds;
        this.viewPagerTitles = viewPagerTitles;

        if (titleIds.size() != viewPagerTitles.size()) {
            throw new IllegalArgumentException("DynamicViewPagerArrayAdapter will break");
        }
    }

    @Override
    public Fragment getItem(int position) {
        return InflaterFragment.getInstance(titleIds.get(position), valueIds.get(position));
    }

    @Override
    public int getCount() {
        return titleIds.size();
    }

    public CharSequence getPageTitle(int position) {
        return viewPagerTitles.get(position);
    }


    /*Parses a xml String[] int forms it into a inflateable list
     *Adds to every "titles" as many "texts", until a text starts with 'xxx'*/
    private static List<ArrayParentStruct> prepareList(int titleId, int textId, Context context) {
        List<ArrayParentStruct> list = new ArrayList<>();
        CharSequence[] titles = context.getResources().getTextArray(titleId);
        CharSequence[] text = context.getResources().getTextArray(textId);
        int i = 0;
        ArrayParentStruct parentStruct;
        for (CharSequence s : titles) {
            parentStruct = new ArrayParentStruct(s);

            while (i < text.length && !text[i].subSequence(0, 3).toString().equals("xxx")) {
                parentStruct.texts.add(new ExpandableDescription(text[i++], ""));
            }
            parentStruct.texts.add(
                    new ExpandableDescription(text[i].subSequence(3, text[i].length()), ""));
            i++;
            list.add(parentStruct);
        }
        return list;
    }

    /*This Fragment gets as argument the Ids of the XML-String[] it will inflate in a Recyclerview*/
    public static class InflaterFragment extends Fragment {

        private int titleId;
        private int valueId;

        public static InflaterFragment getInstance(int titleId, int valueId) {
            InflaterFragment frag = new InflaterFragment();
            frag.titleId = titleId;
            frag.valueId = valueId;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(
                    R.layout.fragment_inflate_array, container, false);

            final Context context = getActivity();

            final RecyclerView rv1 = (RecyclerView) rootView.findViewById(R.id.inflater_rec);
            rv1.setHasFixedSize(true);
            rv1.setLayoutManager(new LinearLayoutManager(context));
            rv1.setAdapter(
                    new RecycleStringArrayAdapter(prepareList(titleId, valueId, context), context));

            return rootView;
        }

    }

}
