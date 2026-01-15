import Kalender.KalenderGUI;
import Tagesansicht.TagesansichtGUI;
import Termine.Terminspeicher;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main {
   static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         new KalenderGUI().setVisible(true);
         });
   }
}