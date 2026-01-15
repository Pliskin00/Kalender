# Kalender
Der Kalender für die Projektarbeit von Markus N. &amp; Simon F. B. G. für die HNU

Dies ist ein einfacher Kalender mit Monatsansicht (Wochentage + einzelne Tage als DayCell), Tagesübersicht (Tagesansicht-Fenster) und einer Eintragungs-Oberfläche zum Anlegen von Terminen. Termine haben Name, Datum+Uhrzeit, Beschreibung, Farbe und ein Flag für "Feiertag". Feiertage werden automatisch für ein, das vorherige und das nächste Jahr erzeugt. Die App verwendet reines Swing; keine externe Bibliothek ist nötig.


Features

Monatsansicht mit Wochentagen (Mo–So) und einzelnen Tageszellen.

Jeder Tag kann farbige Rechtecke anzeigen — jeweils ein Rechteck pro gespeicherten Termin dieses Tages.

Heute wird hervorgehoben (andere Hintergrundfarbe + dickerer Rand).

Sonntage (Wochentag-Label und Tagzahl) werden rot dargestellt.

Hover-Effekt für Tageszellen und für Zeilen in der Tagesansicht.

Klick auf eine Tageszelle öffnet die Tagesansicht (Liste aller Termine dieses Tages).

Tagesansicht zeigt Termine sortiert nach Zeit, mit Farbfeld, Name, Uhrzeit (oder — bei Feiertag) und optionaler Löschschaltfläche.

Detaildialog für einzelne Termine (Name, Farbe, Datum, Uhrzeit, Beschreibung).

Eintragungsdialog (Datum wird übergeben), Farbauswahl (JColorChooser), Uhrzeitauswahl (Spinner), Speichern in Terminspeicher.

Terminspeicher enthält kleine Hilfsfunktionen: getTermineAmTag, getNaechsterTermin, addTermin, removeTermin, automatische Anlage von festen Feiertagen pro Jahr.

Hinweis: Momentan ist die Speicherung nur im RAM (keine Persistenz über Programmneustart).

Projektstucktur:
src/
  Kalender/
    KalenderGUI.java
    DayCell.java
  Tagesansicht/
    TagesansichtGUI.java
  Eintragung/
    EintragungGUI.java
  Termine/
    Termin.java
    Terminspeicher.java
  Main.java
