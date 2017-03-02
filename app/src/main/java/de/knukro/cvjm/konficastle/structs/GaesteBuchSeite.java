package de.knukro.cvjm.konficastle.structs;

import java.util.ArrayList;
import java.util.List;


public class GaesteBuchSeite {
    public final String nextSite;
    public final List<GaestebuchEintrag> entrys;

    public GaesteBuchSeite(String nextSite) {
        this.nextSite = "https://www.cvjm-bayern.de/" + nextSite;
        entrys = new ArrayList<>();
    }

    public static class GaestebuchEintrag {
        public final String author, text;

        public GaestebuchEintrag(String author, String text) {
            this.author = author;
            if (text.endsWith("-->")) {
                this.text = text.substring(3, text.indexOf("<!--") - 6).replace("<br>", "\n");
            } else {
                this.text = text.substring(3, text.length() - 4).replace("<br> ", "\n");
            }
        }

    }
}
