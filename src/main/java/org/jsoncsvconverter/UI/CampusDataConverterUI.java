package org.jsoncsvconverter.UI;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class CampusDataConverterUI extends javax.swing.JFrame implements ActionListener{

    public CampusDataConverterUI(){
        setLayout(null);
        setTitle("Campus Data Converter");

        ImageIcon imagen = new ImageIcon("C:\\Users\\migue\\IdeaProjects\\CampusDataConverter\\src\\main\\java\\org\\jsoncsvconverter\\Assets\\CampusDataConverter_Logo.png");
        JLabel mainImage = new JLabel(imagen);
        mainImage.setBounds(100, 20, 384, 200);
        add(mainImage);

        JLabel descriptionLabel = new JLabel("Parse your JSON files into CSV files");
        descriptionLabel.setBounds(45, 240, 300, 30);
        add(descriptionLabel);

        JButton uploadJsonFileBtn = new JButton("Upload Json File");
        uploadJsonFileBtn.setBounds(45, 280, 200, 80);
        add(uploadJsonFileBtn);

        JButton selectOutputLocationBtn = new JButton("Output Folder");
        selectOutputLocationBtn.setBounds(300, 280, 200, 80);
        add(selectOutputLocationBtn);
    }

    //
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
