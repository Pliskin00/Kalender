package Eintragung;

import Termine.Termin;
import Termine.Terminspeicher;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EintragungGUI extends JFrame {

    private final Terminspeicher terminspeicher;
    private final LocalDate datum;
    private final Runnable onSaveCallback;

    private Color ausgewaehlteFarbe = Color.BLUE;
    private LocalTime ausgewaehlteUhrzeit = LocalTime.of(0, 0);

    private JPanel Gesammt;
    private JTextField eintragungTextName;
    private JTextArea eintragungTextBeschreibung;
    private JButton eintragungAbbruch;
    private JButton eintragungSpeichern;
    private JLabel eintragungLabeltatDatum;
    private JButton eintragungButtonFarbwahl;
    private JButton eintragungButtonZeitwahl;
    private JLabel eintragungLabelUberschrift;
    private JLabel eintragungLabelDatum;
    private JLabel eintragungLabelBeschreibung;

    public EintragungGUI(
            LocalDate datum,
            Terminspeicher terminspeicher,
            Runnable onSaveCallback
    ) {
        this.datum = datum;
        this.terminspeicher = terminspeicher;
        this.onSaveCallback = onSaveCallback;

        setContentPane(Gesammt);
        setTitle("Eintragung");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        eintragungLabeltatDatum.setText(
                datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );

        eintragungButtonZeitwahl.setText("00:00");

        initButtons();
        initFarbwahl();
        initUhrzeitwahl();

        setVisible(true);
    }

    private void initButtons() {
        eintragungAbbruch.addActionListener(e -> dispose());
        eintragungSpeichern.addActionListener(e -> speichern());
    }

    private void speichern() {
        String name = eintragungTextName.getText().trim();
        String beschreibung = eintragungTextBeschreibung.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bitte einen Namen eingeben!",
                    "Fehler",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        //HIER war der Fehler vorher
        LocalDateTime datumZeit = LocalDateTime.of(datum, ausgewaehlteUhrzeit);

        Termin termin = new Termin(
                name,
                datumZeit,
                beschreibung,
                ausgewaehlteFarbe,
                false
        );

        terminspeicher.addTermin(termin);

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        dispose();
    }

    private void initFarbwahl() {
        eintragungButtonFarbwahl.addActionListener(e -> {
            Color neueFarbe = JColorChooser.showDialog(
                    this,
                    "Farbe wählen",
                    ausgewaehlteFarbe
            );

            if (neueFarbe != null) {
                ausgewaehlteFarbe = neueFarbe;
                eintragungButtonFarbwahl.setBackground(neueFarbe);
            }
        });
    }

    private void initUhrzeitwahl() {
        eintragungButtonZeitwahl.addActionListener(e -> {

            JSpinner stunden = new JSpinner(
                    new SpinnerNumberModel(ausgewaehlteUhrzeit.getHour(), 0, 23, 1)
            );
            JSpinner minuten = new JSpinner(
                    new SpinnerNumberModel(ausgewaehlteUhrzeit.getMinute(), 0, 59, 5)
            );

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Stunde:"));
            panel.add(stunden);
            panel.add(new JLabel("Minute:"));
            panel.add(minuten);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Uhrzeit wählen",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                ausgewaehlteUhrzeit = LocalTime.of(
                        (int) stunden.getValue(),
                        (int) minuten.getValue()
                );

                eintragungButtonZeitwahl.setText(
                        ausgewaehlteUhrzeit.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            }
        });
    }
}
