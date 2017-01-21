package de.knukro.cvjm.konficastle.structs;

public class Entry {
    public final String author, text;

    public Entry(String author, String text) {
        this.author = author;
        if (text.endsWith("-->")) {
            this.text = text.substring(3, text.indexOf("<!--") - 6).replace("<br>", "\n");
        } else {
            this.text = text.substring(3, text.length() - 4).replace("<br> ", "\n");
        }
    }

}
