package Termine;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import java.util.concurrent.CopyOnWriteArrayList;

public class Terminspeicher {

    private List<Termin> termine = new ArrayList<>();

    public Terminspeicher() {

        initObjekte();

        int jahr = LocalDate.now().getYear();

        // Feiertage für letztes, aktuelles und nächstes Jahr
        feiertageFuerJahr(jahr - 1);
        feiertageFuerJahr(jahr);
        feiertageFuerJahr(jahr + 1);
    }

    /* =========================
       Öffentliche Methoden
       ========================= */

    public void addTermin(Termin t) {

        termine.add(t);
        notifyListeners();
    }

    public void removeTermin(Termin termin) {

        termine.remove(termin);
        notifyListeners();
    }

    public List<Termin> getTermine() {                                                                                  //wird später für Filter wichtig
        return termine;
    }

    public Termin getNaechsterTermin() {
        LocalDateTime jetzt = LocalDateTime.now();
        Termin naechster = null;

        for (Termin t : termine) {
            if (t.getDatum().isAfter(jetzt)) {
                if (naechster == null || t.getDatum().isBefore(naechster.getDatum())) {
                    naechster = t;
                }
            }
        }
        return naechster;
    }

    public List<Termin> getTermineAmTag(LocalDate datum) {
        List<Termin> result = new ArrayList<>();

        for (Termin t : termine) {
            if (t.getDatum().toLocalDate().equals(datum)) {
                result.add(t);
            }
        }
        return result;
    }


    private void feiertageFuerJahr(int jahr) {

        // Feste Feiertage
        feiertag("Neujahr", 1, 1, jahr);
        feiertag("Tag der Arbeit", 5, 1, jahr);
        feiertag("Tag der Deutschen Einheit", 10, 3, jahr);
        feiertag("1. Weihnachtstag", 12, 25, jahr);
        feiertag("2. Weihnachtstag", 12, 26, jahr);
        feiertag("Valentinstag", 2, 14, jahr);

        // Bewegliche Feiertage (abhängig von Ostern)
        LocalDate ostern = berechneOstersonntag(jahr);

        feiertag("Karfreitag", ostern.minusDays(2));
        feiertag("Ostermontag", ostern.plusDays(1));
        feiertag("Christi Himmelfahrt", ostern.plusDays(39));
        feiertag("Pfingstmontag", ostern.plusDays(50));
    }

    private void feiertag(String name, int monat, int tag, int jahr) {                                                  //feste Feiertage
        termine.add(new Termin(
                name,
                LocalDateTime.of(jahr, monat, tag, 0, 0),
                "Gesetzlicher Feiertag",
                FeiertagFarbe,
                true
        ));
    }

    private void feiertag(String name, LocalDate datum) {                                                               //bewegliche Feiertage
        termine.add(new Termin(
                name,
                datum.atStartOfDay(),
                "Gesetzlicher Feiertag",
                FeiertagFarbe,
                true
        ));
    }

    private LocalDate berechneOstersonntag(int jahr) {                                                                  //Ostern tut meinem Kopf weh...
        int a = jahr % 19;                                                                                              //Gruß geht raus an Herrn Berchtold
        int b = jahr / 100;
        int c = jahr % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int monat = (h + l - 7 * m + 114) / 31;
        int tag = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(jahr, monat, tag);
    }

    private static final Color FeiertagFarbe = new Color(155, 89, 182);

    private final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        SwingUtilities.invokeLater(() -> {
            for (Runnable r : listeners) {
                try { r.run(); } catch (Exception ignored) {}
            }
        });
    }

    public void initObjekte(){
        termine.add(new Termin(
                "Doomsday",
                LocalDateTime.of(2026, 1, 16, 23, 59),
                "Abgabetermin Projektarbeit",
                Color.RED,
                false
        ));

        termine.add(new Termin(
                "Geb. von Felix",
                LocalDateTime.of(2008, 2, 17, 0, 0),
                "Geburtstag von Simon Felix Ben",
                Color.BLUE,
                true
        ));

        termine.add(new Termin(
                "Geb. von Markus",
                LocalDateTime.of(2007, 7, 7, 0, 0),
                "Der beste Tag, den die Erde je erfahren durfte",
                Color.BLACK,
                true
        ));
    }
}