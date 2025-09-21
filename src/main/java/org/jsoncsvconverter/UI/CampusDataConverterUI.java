package org.jsoncsvconverter.UI;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class CampusDataConverterUI extends javax.swing.JFrame implements ActionListener{

    private JButton uploadJsonFileBtn, selectOutputLocationBtn;

    public CampusDataConverterUI(){
        setLayout(null);
        setTitle("Campus Data Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon imagen = new ImageIcon("C:\\Users\\migue\\IdeaProjects\\CampusDataConverter\\src\\main\\java\\org\\jsoncsvconverter\\Assets\\CampusDataConverter_Logo.png");
        JLabel mainImage = new JLabel(imagen);
        mainImage.setBounds(100, 20, 384, 200);
        add(mainImage);

        JLabel descriptionLabel = new JLabel("Parse your JSON files into CSV files");
        descriptionLabel.setBounds(45, 240, 300, 30);
        add(descriptionLabel);

        uploadJsonFileBtn = new JButton("Upload Json File");
        uploadJsonFileBtn.setBounds(45, 280, 200, 80);
        uploadJsonFileBtn.addActionListener(this);
        add(uploadJsonFileBtn);

        selectOutputLocationBtn = new JButton("Output Folder");
        selectOutputLocationBtn.setBounds(300, 280, 200, 80);
        add(selectOutputLocationBtn);
    }

    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadJsonFileBtn) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this, "Selected: " + selectedFilePath);
            }
        }
    }
}
