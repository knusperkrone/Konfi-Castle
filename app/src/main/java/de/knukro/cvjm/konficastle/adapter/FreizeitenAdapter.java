package de.knukro.cvjm.konficastle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.helper.GetImages;
import de.knukro.cvjm.konficastle.helper.ImageStorage;
import de.knukro.cvjm.konficastle.structs.GaestebuchSeite;
import de.knukro.cvjm.konficastle.structs.ParsedEvent;

import static de.knukro.cvjm.konficastle.fragments.FreizeitenFragment.FreizeitenRecycleFragment.categories;


public class FreizeitenAdapter extends RecyclerView.Adapter<FreizeitenAdapter.FreizeitenViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final GaestebuchSeite list;
    private boolean isFast = false;

    public FreizeitenAdapter(Context context, int position) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isFast = activeNetwork != null && isConnectionFast(activeNetwork.getType(), activeNetwork.getSubtype());
        if (categories != null) {
            list = categories.get(position);
        } else {
            this.list = new GaestebuchSeite();
            this.list.events.add(new ParsedEvent(context.getString(R.string.adapter_freizeiten_noconnection),
                    "-------------------", "www.cvjm-bayern.de", false));
        }
    }

    private static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }
        return false;
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
        String file = ImageStorage.getImagePath(event.eventTitle, context);
        if (file != null) {
            try {
                holder.image.setImageBitmap(BitmapFactory.decodeFile(file));
            } catch (Exception e) {
                new File(file).delete();
                holder.image.setImageResource(R.drawable.onlineplaceholder);
            }
        } else if (isFast) {
            new GetImages(holder.image, event, context).execute();
        } else {
            holder.image.setImageResource(R.drawable.onlineplaceholder);
        }
    }

    @Override
    public int getItemCount() {
        return list.events.size();
    }

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

}
