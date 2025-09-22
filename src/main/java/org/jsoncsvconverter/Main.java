package org.jsoncsvconverter;
import org.jsoncsvconverter.UI.CampusDataConverterUI;

// Entry point for JSON converter to CSV application

public class Main {
    public static void main(String[] args) {

        CampusDataConverterUI campusUI = new CampusDataConverterUI();
        campusUI.setBounds(0, 0, 600, 800);
        campusUI.setVisible(true);
        campusUI.setLocationRelativeTo(null);
        campusUI.setResizable(false);
    }
}
