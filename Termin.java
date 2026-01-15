package Termine;

import java.time.LocalDateTime;
import java.awt.Color;

public class Termin {
    private String name;
    private LocalDateTime datum;
    private String beschreibung;
    private Color farbe;
    private boolean feiertag;

    public Termin(String name, LocalDateTime datum, String beschreibung,Color farbe, boolean feiertag) {
        this.name = name;
        this.datum = datum;
        this.beschreibung = beschreibung;
        this.farbe = farbe;
        this.feiertag = feiertag;
    }

    public String getName() { return name; }
    public LocalDateTime getDatum() { return datum; }
    public String getBeschreibung() { return beschreibung; }
    public Color getFarbe() { return farbe;}
    public boolean Feiertag() { return feiertag; };


}