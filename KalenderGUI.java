package Kalender;

import Filter.FilterGUI;
import Termine.Terminspeicher;
import Termine.Termin;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class KalenderGUI extends JFrame {

    private JPanel kalenderPanelTage;
    private JPanel kalenderPanelFilterundzeit;
    private JPanel kalenderPanelFenseter;
    private JButton kalenderButtonFilter;
    private JLabel kalenderLabelUberschrift;
    private JLabel kalenderLabelZeitbistermin;
    private JPanel kalenderPanelJahrmonat;
    private JLabel kalenderLabelJahr;
    private JLabel kalenderLabelMonat;
    private JButton kalenderButtonJahrzuruck;
    private JButton kalenderButtonJahrweiter;
    private JButton kalenderButtonMonatweiter;
    private JButton kalenderButtonMonatzuruck;
    private JSeparator kalenderSeperatorKopfzeile;
    private JSeparator kalenderSeperatorTage;

    private LocalDate aktuellesDatum;
    private Terminspeicher terminspeicher;

    public KalenderGUI() {

        aktuellesDatum = LocalDate.now();
        terminspeicher = new Terminspeicher();

        setContentPane(kalenderPanelFenseter);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        updateMonatJahr();
        buildKalender();
        updateCountdown();

        Timer countdownTimer = new Timer(60_000, e -> updateCountdown());
        countdownTimer.setInitialDelay(60_000);
        countdownTimer.start();

        kalenderButtonMonatweiter.addActionListener(e -> {
            aktuellesDatum = aktuellesDatum.plusMonths(1);
            updateMonatJahr();
            buildKalender();
        });

        kalenderButtonMonatzuruck.addActionListener(e -> {
            aktuellesDatum = aktuellesDatum.minusMonths(1);
            updateMonatJahr();
            buildKalender();
        });

        kalenderButtonJahrweiter.addActionListener(e -> {
            aktuellesDatum = aktuellesDatum.plusYears(1);
            updateMonatJahr();
            buildKalender();
        });

        kalenderButtonJahrzuruck.addActionListener(e -> {
            aktuellesDatum = aktuellesDatum.minusYears(1);
            updateMonatJahr();
            buildKalender();
        });

        kalenderButtonFilter.addActionListener(e -> {
            FilterGUI GUI = new FilterGUI(
                    terminspeicher
            );
            GUI.setVisible(true);
        });

    }


    private void updateMonatJahr() {
        DateTimeFormatter formatterMonat =
                DateTimeFormatter.ofPattern("MMMM", Locale.GERMAN);
        DateTimeFormatter formatterJahr =
                DateTimeFormatter.ofPattern("yyyy", Locale.GERMAN);

        kalenderLabelMonat.setText(aktuellesDatum.format(formatterMonat));
        kalenderLabelJahr.setText(aktuellesDatum.format(formatterJahr));
    }

    private void buildKalender() {
        kalenderPanelTage.removeAll();
        kalenderPanelTage.setLayout(new GridLayout(0, 7));

        String[] wochentage = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        for (String tag : wochentage) {
            JLabel lbl = new JLabel(tag, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));

            if (tag.equals("So")) {                                                                                     //Macht So rot :D
                lbl.setForeground(Color.RED);
            }


            kalenderPanelTage.add(lbl);
        }

        LocalDate ersterTagImMonat = aktuellesDatum.withDayOfMonth(1);
        int startWochentag = ersterTagImMonat.getDayOfWeek().getValue();
        int tageImMonat = aktuellesDatum.lengthOfMonth();

        for (int i = 1; i < startWochentag; i++) {
            kalenderPanelTage.add(new JPanel());
        }

        for (int tag = 1; tag <= tageImMonat; tag++) {
            LocalDate datum = ersterTagImMonat.withDayOfMonth(tag);
            DayCell dayCell = new DayCell(datum, terminspeicher);
            kalenderPanelTage.add(dayCell);
        }

        kalenderPanelTage.revalidate();
        kalenderPanelTage.repaint();
    }

    private void updateCountdown() {
        Termin naechster = terminspeicher.getNaechsterTermin();

        if (naechster == null) {
            kalenderLabelZeitbistermin.setText("Keine kommenden Termine");
            return;
        }

        LocalDateTime jetzt = LocalDateTime.now();
        Duration diff = Duration.between(jetzt, naechster.getDatum());

        long minuten = diff.toMinutes();
        long stunden = minuten / 60;
        long restMinuten = minuten % 60;
        long tage = stunden / 24;
        long restStunden = stunden % 24;

        kalenderLabelZeitbistermin.setText(
                "Zeit bis zum nächsten Termin: " +
                        tage        + " Tage " +
                        restStunden + " Stunden " +
                        restMinuten + " Minuten"
        );
    }
}