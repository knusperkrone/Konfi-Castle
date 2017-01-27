package de.knukro.cvjm.konficastle.structs;

public class DbDescription {

    private final int id_Instanz;
    private final int tag_Termin;
    private final String uhrzeit_Termin;
    private final String termin_Beschreibung;

    public DbDescription(int id_Instanz, int tag_Termin, String uhrzeit_Termin, String termin_Beschreibung) {
        this.id_Instanz = id_Instanz;
        this.tag_Termin = tag_Termin;
        this.uhrzeit_Termin = uhrzeit_Termin;
        this.termin_Beschreibung = termin_Beschreibung;
    }

    @Override
    public String toString() {
        return "( " + id_Instanz + ", " + tag_Termin + ", \"" + uhrzeit_Termin + "\", \"" + termin_Beschreibung + "\" ) ,";
    }
}
