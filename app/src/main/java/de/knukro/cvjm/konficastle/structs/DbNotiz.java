package de.knukro.cvjm.konficastle.structs;

public class DbNotiz {

    private final int id_event;
    private final int id_Instanz;
    private final int tag_Termin;
    private final String uhrzeit_Termin;
    private final String termin_Beschreibung;

    public DbNotiz(int id_event, int id_Instanz, int tag_Termin, String uhrzeit_Termin, String termin_Beschreibung) {
        this.id_event = id_event;
        this.id_Instanz = id_Instanz;
        this.tag_Termin = tag_Termin;
        this.uhrzeit_Termin = uhrzeit_Termin;
        this.termin_Beschreibung = termin_Beschreibung;
    }

    @Override
    public String toString() {
        return "( " + id_event + ", " + id_Instanz + ", " + tag_Termin + ", \"" + uhrzeit_Termin + "\", \"" + termin_Beschreibung + "\", 0 ) ,";
    }
}
