package de.knukro.cvjm.konficastle.structs;

import java.util.ArrayList;
import java.util.List;


public class RegisterSite {
    public final String nextSite;
    public final List<Entry> entrys;

    public RegisterSite(String nextSite) {
        this.nextSite = "https://www.cvjm-bayern.de/" + nextSite;
        entrys = new ArrayList<>();
    }
}
