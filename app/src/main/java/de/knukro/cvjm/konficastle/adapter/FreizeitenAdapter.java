package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.helper.GetImages;
import de.knukro.cvjm.konficastle.helper.ImageStorage;
import de.knukro.cvjm.konficastle.structs.Category;
import de.knukro.cvjm.konficastle.structs.ParsedEvent;

import static de.knukro.cvjm.konficastle.fragments.FreizeitenFragment.FreizeitenRecycleFragment.categories;


public class FreizeitenAdapter extends RecyclerView.Adapter<FreizeitenAdapter.FreizeitenViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final Category list;


    static class FreizeitenViewHolder extends RecyclerView.ViewHolder {

        final ImageView image;
        final CardView card;
        final TextView date, titel;

        private FreizeitenViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.element_freizeit_image);
            card = (CardView) view.findViewById(R.id.element_freizeit_card);
            date = (TextView) view.findViewById(R.id.element_freizeit_datum);
            titel = (TextView) view.findViewById(R.id.element_freizeit_titel);
        }
    }

    public FreizeitenAdapter(Context context, int position) {
        inflater = LayoutInflater.from(context);
        this.context = context;

        if (categories != null) {
            list = categories.get(position);
        } else {
            this.list = new Category();
            this.list.events.add(new ParsedEvent("Keine Verbindung", "-------------------", "www.cvjm-bayern.de", false));
        }
    }

    @Override
    public FreizeitenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FreizeitenViewHolder(inflater.inflate(R.layout.element_freizeit, parent, false));
    }

    @Override
    public void onBindViewHolder(FreizeitenViewHolder holder, int position) {
        final ParsedEvent event = list.events.get(position);
        if (event.available) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.GREEN));
        } else {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(event.link));
                context.startActivity(i);
            }
        });
        holder.date.setText(event.date);
        holder.titel.setText(event.eventTitle);
        if (event.imagename != null && ImageStorage.checkifImageExists(event.imagename)) {
            File file = ImageStorage.getImage(event.imagename);
            if (file == null) {
                holder.image.setImageResource(R.drawable.onlineplaceholder);
            } else {
                try {
                    String path = file.getAbsolutePath();
                    holder.image.setImageBitmap(BitmapFactory.decodeFile(path));
                } catch (Exception e) {
                    holder.image.setImageResource(R.drawable.onlineplaceholder);
                }
            }
        } else {
            new GetImages(holder.image, event).execute();
        }
    }

    @Override
    public int getItemCount() {
        return list.events.size();
    }

}
