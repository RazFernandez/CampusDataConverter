package org.jsoncsvconverter.UI;
import java.util.*;

import org.jsoncsvconverter.Logic.CSVWriterFile;
import org.jsoncsvconverter.Logic.JSONParser;
import org.jsoncsvconverter.Logic.JsonFileReader;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class CampusDataConverterUI extends javax.swing.JFrame implements ActionListener{

    private JButton uploadJsonFileBtn, selectOutputLocationBtn;

    // Created instance of JsonFileReader
    private JsonFileReader jsonFileReader;

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

                // Opens the file selected in UI
                jsonFileReader = new JsonFileReader(selectedFilePath);
                System.out.println(jsonFileReader.getJsonString());

                // Save the JSON object into
                String jsonString = jsonFileReader.getJsonString();

                // Retrieves a value key for testing purposes
                JSONParser parser = new JSONParser(jsonString);

                System.out.println("Headers (List): " + parser.getHeaders());
                System.out.println("Headers (Array): " + Arrays.toString(parser.getHeadersArray()));

                CSVWriterFile csvWriterFile = new CSVWriterFile(parser.getHeadersArray());
                csvWriterFile.createCSVWithData("C:\\Users\\migue\\Desktop\\Test\\createdfiles\\hola.csv",
                        parser.getRows());
                //csvWriterFile.createNewCSVFile("C:\\Users\\migue\\Desktop\\Test\\createdfiles\\hola.csv");

            }
        }
    }
}
