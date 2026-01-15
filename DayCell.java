package Kalender;

import Termine.Termin;
import Termine.Terminspeicher;
import Tagesansicht.TagesansichtGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.DayOfWeek;


public class DayCell extends JPanel {

    private LocalDate datum;
    private Terminspeicher terminspeicher;
    private Color normalBackground;
    private Color hoverBackground = new Color(235, 235, 235);
    private final Runnable repaintRunnable = this::repaint;

    public DayCell(LocalDate datum, Terminspeicher speicher) {
        this.datum = datum;
        this.terminspeicher = speicher;
        terminspeicher.addListener(repaintRunnable);


        if (datum.equals(LocalDate.now())) {
            normalBackground = new Color(220, 235, 255); // Heute
            setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 3));
        } else {
            normalBackground = Color.WHITE;
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        }

        setBackground(normalBackground);
        setOpaque(true);

        setLayout(new BorderLayout());

        JLabel tagLabel = new JLabel(String.valueOf(datum.getDayOfMonth()));
        tagLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tagLabel, BorderLayout.NORTH);

        if (datum.getDayOfWeek() == DayOfWeek.SUNDAY) {                                                                 //Macht alle Sonntage Rot yay
            tagLabel.setForeground(Color.RED);
        }


        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackground);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalBackground);
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new TagesansichtGUI(datum, terminspeicher);
            }
        });

        addHierarchyListener(e -> {
            // wenn Component nicht mehr displayable ist -> abmelden
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    terminspeicher.removeListener(repaintRunnable);
                }
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int y = getHeight() - 10;
        int x = 5;

        for (Termin t : terminspeicher.getTermineAmTag(datum)) {
            g.setColor(t.getFarbe());
            g.fillRect(x, y, 8, 8);
            x += 12;
        }
    }

}