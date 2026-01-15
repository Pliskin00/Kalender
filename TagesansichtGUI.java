package Tagesansicht;

import Eintragung.EintragungGUI;
import Kalender.DayCell;
import Termine.Termin;
import Termine.Terminspeicher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;


public class TagesansichtGUI extends JFrame {
    private JPanel tagesansichtPanelDatum;
    private JPanel tagesansichtPanelTermine;
    private JPanel tagesanischtPanelKnoepfe;
    private JButton tagesansichtButtonTerminneu;
    private JButton tagesansichtButtonSchliessen;
    private JLabel tagesansichtLabelDatum;
    private JPanel tagesansichtPanelMain;

    private LocalDate datum;
    private Terminspeicher terminspeicher;


    public TagesansichtGUI(LocalDate datum, Terminspeicher terminspeicher) {
        this.datum = datum;
        this.terminspeicher = terminspeicher;

        setTitle("Tagesansicht");
        setContentPane(tagesansichtPanelMain);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        // Datum anzeigen
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        tagesansichtLabelDatum.setText(datum.format(formatter));

        // Layout für Terminliste
        tagesansichtPanelTermine.setLayout(
                new BoxLayout(tagesansichtPanelTermine, BoxLayout.Y_AXIS)
        );

        List<Termin> termine = terminspeicher
                .getTermineAmTag(datum)
                .stream()
                .sorted(Comparator.comparing(Termin::getDatum))
                .toList();


        tagesansichtButtonSchliessen.addActionListener(e ->
                dispose()
        );

        tagesansichtButtonTerminneu.addActionListener(e -> {
            new EintragungGUI(
                    datum,
                    terminspeicher,
                    this::updateTermineListe
            );
        });

        // Termine laden
        updateTermineListe();

        setVisible(true);
    }

    private void updateTermineListe() {
        tagesansichtPanelTermine.removeAll();

        List<Termin> termine = terminspeicher
                .getTermineAmTag(datum)
                .stream()
                .sorted(Comparator.comparing(Termin::getDatum))
                .toList();

        for (Termin termin : termine) {

            JPanel zeile = new JPanel();
            zeile.setLayout(new BoxLayout(zeile, BoxLayout.X_AXIS));
            zeile.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            // Farbfeld
            JPanel farbe = new JPanel();
            farbe.setBackground(termin.getFarbe());
            farbe.setPreferredSize(new Dimension(12, 12));
            farbe.setMaximumSize(new Dimension(12, 12));

            // Name
            JLabel name = new JLabel(termin.getName());

            // Uhrzeit / Feiertag
            String uhrzeit = termin.Feiertag()
                    ? "—"
                    : termin.getDatum().toLocalTime().toString();

            JLabel zeit = new JLabel(uhrzeit);

            // Minus-Button nur wenn kein Feiertag
            JButton minus = new JButton("-");
            minus.setFocusable(false);

            if (!termin.Feiertag()) {
                minus.addActionListener(e -> {
                    terminspeicher.removeTermin(termin);
                    updateTermineListe();
                });
            } else {
                minus.setVisible(false);
            }

            zeile.add(farbe);
            zeile.add(Box.createHorizontalStrut(8));
            zeile.add(name);
            zeile.add(Box.createHorizontalGlue());
            zeile.add(zeit);
            zeile.add(Box.createHorizontalStrut(8));
            zeile.add(minus);

            tagesansichtPanelTermine.add(zeile);

            Color normalBg = zeile.getBackground();
            Color hoverBg = new Color(230, 230, 230); // hellgrau

            zeile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    zeile.setBackground(hoverBg);
                    zeile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    zeile.setOpaque(true);
                    zeile.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    zeile.setBackground(normalBg);
                    zeile.setCursor(Cursor.getDefaultCursor());
                    zeile.setOpaque(false);
                    zeile.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    showTerminDetails(termin);
                }
            });


        }

        tagesansichtPanelTermine.revalidate();
        tagesansichtPanelTermine.repaint();
    }


    private JPanel createTerminZeile(Termin termin) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Farbkästchen
        JPanel farbe = new JPanel();
        farbe.setBackground(termin.getFarbe());
        farbe.setPreferredSize(new Dimension(14, 14));

        // Name + Uhrzeit
        String text = termin.getName();

        if (!termin.Feiertag()) {
            String uhrzeit = termin.getDatum().toLocalTime().toString();
            text += "  " + uhrzeit;
        }

        JLabel label = new JLabel(text);

        JPanel links = new JPanel(new FlowLayout(FlowLayout.LEFT));
        links.add(farbe);
        links.add(label);

        panel.add(links, BorderLayout.WEST);

        return panel;
    }

    private void showTerminDetails(Termin termin) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JDialog dialog = new JDialog(this, "Termin-Details", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setContentPane(root);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());

        JLabel nameLabel = new JLabel(termin.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel farbe = new JPanel();
        farbe.setBackground(termin.getFarbe());
        farbe.setPreferredSize(new Dimension(20, 20));
        farbe.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        header.add(nameLabel, BorderLayout.WEST);
        header.add(farbe, BorderLayout.EAST);

        JPanel meta = new JPanel();
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        JLabel datumLabel = new JLabel(
                "Datum: " + termin.getDatum().toLocalDate().format(df)
        );
        datumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(datumLabel);

        JLabel zeitLabel;
        if (termin.Feiertag()) {
            zeitLabel = new JLabel("Uhrzeit: —");
        } else {
            zeitLabel = new JLabel(
                    "Uhrzeit: " + termin.getDatum().toLocalTime().toString()
            );
        }
        zeitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(zeitLabel);


        // ===== BESCHREIBUNG =====
        JTextArea beschreibung = new JTextArea(termin.getBeschreibung());
        beschreibung.setLineWrap(true);
        beschreibung.setWrapStyleWord(true);
        beschreibung.setEditable(false);
        beschreibung.setFocusable(false);
        beschreibung.setOpaque(false); // ← GANZ WICHTIG
        beschreibung.setBorder(null);

        JScrollPane scroll = new JScrollPane(beschreibung);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // ===== BUTTON =====
        JButton schliessen = new JButton("Schließen");
        schliessen.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(schliessen);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setAlignmentX(Component.LEFT_ALIGNMENT);

        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(meta);

        center.add(Box.createVerticalStrut(10));

        JLabel beschriftung = new JLabel("Beschreibung:");
        beschriftung.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(beschriftung);

        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));
        center.add(scroll);


        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

}