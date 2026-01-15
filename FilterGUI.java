package Filter;

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
import java.util.stream.Collectors;

public class FilterGUI extends JFrame {
    private Color ausgewaehlteFarbe = null;
    private Terminspeicher terminspeicher;

    private JPanel filterPaneGesammt;
    private JPanel filterPaneOptionen;
    private JPanel filterPaneEinträge;
    private JPanel filterPaneButtons;
    private JTextField filterTextfeldName;
    private JButton filterButtonFarbe;
    private JButton filterButtonDatum;
    private JButton filterButtonAbbrechen;
    private JButton filterButtonAnwenden;
    private JButton filterButtonAllesLöschen;
    private JCheckBox filterCheckBoxFeiertag;

    // eigener state
    private LocalDate selectedDate = null;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public FilterGUI(Terminspeicher terminspeicher) {

        final String placeholder = "Name";
        filterTextfeldName.setForeground(Color.GRAY);
        filterTextfeldName.setText(placeholder);

        filterTextfeldName.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (filterTextfeldName.getText().equals(placeholder)) {
                    filterTextfeldName.setText("");
                    filterTextfeldName.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (filterTextfeldName.getText().isBlank()) {
                    filterTextfeldName.setText(placeholder);
                    filterTextfeldName.setForeground(Color.GRAY);
                }
            }
        });

        this.terminspeicher = terminspeicher;

        setTitle("Filter");
        setContentPane(filterPaneGesammt);
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //button labels platzhaler
        if (filterButtonDatum != null) filterButtonDatum.setText("Datum");
        if (filterButtonFarbe != null) filterButtonFarbe.setText("Farbe");
        if (filterTextfeldName != null && (filterTextfeldName.getText() == null || filterTextfeldName.getText().isEmpty())) {
            filterTextfeldName.setText("");
            filterTextfeldName.setForeground(Color.DARK_GRAY);
            filterTextfeldName.setToolTipText("Name eingeben (Enter oder Anwenden)");
        }

        filterButtons();
        filterFarbwahl();
        filterDatumWahl();

        updateTermineListe();

        setVisible(true);
    }

    private void filterButtons() {
        filterButtonAbbrechen.addActionListener(e -> dispose());
        filterButtonAnwenden.addActionListener(e -> updateTermineListe());

        filterButtonAllesLöschen.addActionListener(e -> {
            List<Termin> aktuell = getFilteredTermine();

            List<Termin> löschbare = aktuell.stream()
                    .filter(t -> !t.Feiertag())
                    .collect(Collectors.toList());

            if (löschbare.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Keine (löschbaren) Einträge in der aktuellen Ansicht.",
                        "Nichts zu löschen",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Willst du wirklich " + löschbare.size() + " Einträge löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                for (Termin t : löschbare) {
                    terminspeicher.removeTermin(t);
                }
                updateTermineListe();
            }
        });
    }

    private void filterFarbwahl() {
        filterButtonFarbe.addActionListener(e -> {
            Color neueFarbe = JColorChooser.showDialog(
                    this,
                    "Farbe wählen",
                    ausgewaehlteFarbe
            );

            if (neueFarbe != null) {
                ausgewaehlteFarbe = neueFarbe;
                filterButtonFarbe.setBackground(neueFarbe);
            }
        });
    }

    private void filterDatumWahl() {
        filterButtonDatum.addActionListener(e -> {
            LocalDate current = selectedDate != null ? selectedDate : LocalDate.now();

            JSpinner tagSpinner = new JSpinner(new SpinnerNumberModel(current.getDayOfMonth(), 1, 31, 1));
            JSpinner monatSpinner = new JSpinner(new SpinnerNumberModel(current.getMonthValue(), 1, 12, 1));
            JSpinner jahrSpinner = new JSpinner(new SpinnerNumberModel(current.getYear(), current.getYear() - 50, current.getYear() + 50, 1));

            JPanel p = new JPanel(new GridLayout(2, 3, 6, 6));
            p.add(new JLabel("Tag:"));
            p.add(new JLabel("Monat:"));
            p.add(new JLabel("Jahr:"));
            p.add(tagSpinner);
            p.add(monatSpinner);
            p.add(jahrSpinner);

            int res = JOptionPane.showConfirmDialog(
                    this,
                    p,
                    "Datum wählen",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (res == JOptionPane.OK_OPTION) {
                int d = (int) tagSpinner.getValue();
                int m = (int) monatSpinner.getValue();
                int y = (int) jahrSpinner.getValue();
                try {
                    selectedDate = LocalDate.of(y, m, d);
                    filterButtonDatum.setText(selectedDate.format(dateFormat));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ungültiges Datum", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        filterButtonDatum.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    selectedDate = null;
                    filterButtonDatum.setText("Datum");
                }
            }
        });
    }

    private void updateTermineListe() {
        filterPaneEinträge.removeAll();
        filterPaneEinträge.setLayout(new BoxLayout(filterPaneEinträge, BoxLayout.Y_AXIS));

        List<Termin> termine = getFilteredTermine();

        if (termine.isEmpty()) {
            JLabel leer = new JLabel("Keine Einträge");
            leer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            filterPaneEinträge.add(leer);
        } else {
            int idx = 0;
            for (Termin termin : termine) {
                JPanel zeile = buildTerminZeile(termin);
                // leichtes Striping zur besseren Lesbarkeit
                if (idx % 2 == 0) {
                    zeile.setBackground(new Color(250, 250, 250));
                }
                filterPaneEinträge.add(zeile);
                idx++;
            }
        }

        filterPaneEinträge.revalidate();
        filterPaneEinträge.repaint();
    }

    private List<Termin> getFilteredTermine() {
        final String nameFilter = (filterTextfeldName != null)
                ? filterTextfeldName.getText()
                : null;

        final boolean showFeiertage = (filterCheckBoxFeiertag != null) && filterCheckBoxFeiertag.isSelected();
        final Color colorFilter = ausgewaehlteFarbe;
        final LocalDate dateFilter = selectedDate;

        final String nameFilterNormalized = (nameFilter == null || nameFilter.isBlank() || nameFilter.equals("Name"))
                ? null
                : nameFilter.toLowerCase();

        return terminspeicher.getTermine()
                .stream()
                .filter(t -> {
                    if (!showFeiertage && t.Feiertag()) {
                        return false;
                    }

                    if (nameFilterNormalized != null) {
                        if (t.getName() == null || !t.getName().toLowerCase().contains(nameFilterNormalized)) {
                            return false;
                        }
                    }

                    if (colorFilter != null) {
                        if (t.getFarbe() == null || !t.getFarbe().equals(colorFilter)) {
                            return false;
                        }
                    }

                    if (dateFilter != null) {
                        if (!t.getDatum().toLocalDate().equals(dateFilter)) {
                            return false;
                        }
                    }

                    return true;
                })
                .sorted(Comparator.comparing(Termin::getDatum))
                .collect(Collectors.toList());
    }

    private JPanel buildTerminZeile(Termin termin) {
        JPanel zeile = new JPanel();
        zeile.setLayout(new BoxLayout(zeile, BoxLayout.X_AXIS));
        zeile.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        zeile.setOpaque(true);
        zeile.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Farbfeld
        JPanel farbe = new JPanel();
        farbe.setBackground(termin.getFarbe());
        farbe.setPreferredSize(new Dimension(12, 12));
        farbe.setMaximumSize(new Dimension(12, 12));

        // Name (nimmt den verfügbaren Platz)
        JLabel nameLabel = new JLabel(termin.getName());
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        nameLabel.setFont(nameLabel.getFont().deriveFont(14f));
        nameLabel.setToolTipText(termin.getName());
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameLabel.getPreferredSize().height));


        JLabel datumLabel = new JLabel(termin.getDatum().toLocalDate().format(dateFormat));
        datumLabel.setFont(datumLabel.getFont().deriveFont(12f));
        datumLabel.setForeground(Color.DARK_GRAY);
        datumLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        datumLabel.setPreferredSize(new Dimension(100, datumLabel.getPreferredSize().height));
        datumLabel.setMinimumSize(new Dimension(100, datumLabel.getPreferredSize().height));

        // Uhrzeit / Feiertag
        String uhr = termin.Feiertag() ? "—" : termin.getDatum().toLocalTime().toString();
        JLabel zeit = new JLabel(uhr);
        zeit.setFont(zeit.getFont().deriveFont(12f));
        zeit.setForeground(Color.DARK_GRAY);
        zeit.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        zeit.setPreferredSize(new Dimension(70, zeit.getPreferredSize().height));
        zeit.setHorizontalAlignment(SwingConstants.RIGHT);

        // Minus-Button
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

        // Zusammensetzen: farbe | name (flex) | datum (fixed) | zeit (fixed) | minus
        zeile.add(farbe);
        zeile.add(Box.createHorizontalStrut(8));
        zeile.add(nameLabel);
        zeile.add(datumLabel);
        zeile.add(Box.createHorizontalGlue());
        zeile.add(zeit);
        zeile.add(Box.createHorizontalStrut(6));
        zeile.add(minus);


        Color normalBg = zeile.getBackground();
        Color hoverBg = new Color(230, 230, 230);
        zeile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                zeile.setBackground(hoverBg);
                zeile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                zeile.setBackground(normalBg);
                zeile.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showTerminDetails(termin);
            }
        });

        return zeile;
    }

    // Details-Dialog
    private void showTerminDetails(Termin termin) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JDialog dialog = new JDialog(this, "Termin-Details", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setContentPane(root);

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
        JLabel datumLabel = new JLabel("Datum: " + termin.getDatum().toLocalDate().format(df));
        datumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(datumLabel);
        JLabel zeitLabel = new JLabel(termin.Feiertag() ? "Uhrzeit: —" : "Uhrzeit: " + termin.getDatum().toLocalTime());
        zeitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(zeitLabel);

        JTextArea beschreibung = new JTextArea(termin.getBeschreibung());
        beschreibung.setLineWrap(true);
        beschreibung.setWrapStyleWord(true);
        beschreibung.setEditable(false);
        beschreibung.setFocusable(false);
        beschreibung.setOpaque(false);
        beschreibung.setBorder(null);
        JScrollPane scroll = new JScrollPane(beschreibung);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JButton schliessen = new JButton("Schließen");
        schliessen.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(schliessen);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(meta);
        center.add(Box.createVerticalStrut(10));
        center.add(new JLabel("Beschreibung:"));
        center.add(scroll);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}