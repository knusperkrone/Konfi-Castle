package de.knukro.cvjm.konficastle.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.structs.GaestebuchEintrag;
import de.knukro.cvjm.konficastle.structs.GaestebuchSeite;
import de.knukro.cvjm.konficastle.structs.ParsedEvent;
import de.knukro.cvjm.konficastle.structs.RegisterSite;


class WebPagerParser {

    static RegisterSite getEntrys(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        Elements text = doc.select("#content .tx-guestbook-eintrag");
        Elements header = doc.select("b");
        int currSite = Integer.valueOf(doc.select("#content .tx-guestbook-pagination strong").get(0).text());
        if (currSite == 1)
            currSite--;
        RegisterSite site = new RegisterSite(doc.select("#content .tx-guestbook-pagination a").get(currSite).attr("href"));
        for (int i = 0; i < text.size(); i++) {
            site.entrys.add(new GaestebuchEintrag(header.get(i).text(), text.get(i).html()));
        }
        return site;
    }

    static List<GaestebuchSeite> getFreizeiten() {
        Document doc;
        try {
            doc = Jsoup.connect("http://www.cvjm-bayern.de/urlaub-seminare/freizeiten-und-seminare.html").get();
        } catch (Exception e) {
            return null;
        }
        //Elements headLines = doc.select("#content header");
        Elements eventTitles = doc.select("#content tr");
        List<GaestebuchSeite> camps = new ArrayList<>();
        GaestebuchSeite currGaestebuchSeite = null;
        for (Element event : eventTitles) {
            if (event.children().get(0).text().equals("Titel")) {
                currGaestebuchSeite = new GaestebuchSeite();
                camps.add(currGaestebuchSeite);
            } else if (currGaestebuchSeite != null) {
                currGaestebuchSeite.events.add(new ParsedEvent(event.child(0).text(), event.child(1).text(), "https://www.cvjm-bayern.de/" + event.child(0).child(0).attr("href"), !event.data().equals("ausgebucht ")));
            }
        }
        return camps;
    }

}