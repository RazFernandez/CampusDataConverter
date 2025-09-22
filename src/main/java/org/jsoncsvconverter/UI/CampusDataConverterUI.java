package org.jsoncsvconverter.UI;

import java.util.*;
import org.jsoncsvconverter.Logic.CSVWriterFile;
import org.jsoncsvconverter.Logic.JSONParser;
import org.jsoncsvconverter.Logic.JsonFileReader;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class CampusDataConverterUI extends javax.swing.JFrame implements ActionListener {
    private JButton uploadJsonFileBtn, selectOutputLocationBtn, convertBtn;
    private JLabel statusLabel, outputPathLabel;

    // Created instance of JsonFileReader
    private JsonFileReader jsonFileReader;

    // Variables para mantener las rutas y datos
    private String selectedOutputPath = "";
    private JSONParser parser = null;

    public CampusDataConverterUI() {
        setLayout(null);
        setTitle("Campus Data Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // Centrar la ventana

        // Logo principal
        ImageIcon imagen = new ImageIcon("C:\\Users\\migue\\IdeaProjects\\CampusDataConverter\\src\\main\\java\\org\\jsoncsvconverter\\Assets\\CampusDataConverter_Logo.png");
        JLabel mainImage = new JLabel(imagen);
        mainImage.setBounds(100, 20, 384, 200);
        add(mainImage);

        // Descripción
        JLabel descriptionLabel = new JLabel("Parse your JSON files into CSV files");
        descriptionLabel.setBounds(45, 240, 300, 30);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(descriptionLabel);

        // Botón para subir archivo JSON
        uploadJsonFileBtn = new JButton("Upload JSON File");
        uploadJsonFileBtn.setBounds(45, 280, 200, 50);
        uploadJsonFileBtn.addActionListener(this);
        uploadJsonFileBtn.setBackground(new Color(70, 130, 180));
        uploadJsonFileBtn.setForeground(Color.WHITE);
        uploadJsonFileBtn.setFocusPainted(false);
        add(uploadJsonFileBtn);

        // Botón para seleccionar carpeta de salida
        selectOutputLocationBtn = new JButton("Select Output Folder");
        selectOutputLocationBtn.setBounds(300, 280, 200, 50);
        selectOutputLocationBtn.addActionListener(this);
        selectOutputLocationBtn.setBackground(new Color(60, 179, 113));
        selectOutputLocationBtn.setForeground(Color.WHITE);
        selectOutputLocationBtn.setFocusPainted(false);
        add(selectOutputLocationBtn);

        // Label para mostrar la ruta seleccionada
        outputPathLabel = new JLabel("No output folder selected");
        outputPathLabel.setBounds(45, 340, 500, 25);
        outputPathLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        outputPathLabel.setForeground(Color.GRAY);
        add(outputPathLabel);

        // Botón de conversión (inicialmente deshabilitado)
        convertBtn = new JButton("Convert to CSV");
        convertBtn.setBounds(200, 380, 200, 50);
        convertBtn.addActionListener(this);
        convertBtn.setBackground(new Color(220, 20, 60));
        convertBtn.setForeground(Color.WHITE);
        convertBtn.setFocusPainted(false);
        convertBtn.setEnabled(false);
        add(convertBtn);

        // Label de estado
        statusLabel = new JLabel("Ready to convert files");
        statusLabel.setBounds(45, 440, 500, 25);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(34, 139, 34));
        add(statusLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadJsonFileBtn) {
            handleJsonFileUpload();
        } else if (e.getSource() == selectOutputLocationBtn) {
            handleOutputLocationSelection();
        } else if (e.getSource() == convertBtn) {
            handleConversion();
        }
    }

    private void handleJsonFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Select JSON File to Convert");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                statusLabel.setText("Loading JSON file...");
                statusLabel.setForeground(Color.BLUE);

                // Opens the file selected in UI
                jsonFileReader = new JsonFileReader(selectedFilePath);
                String jsonString = jsonFileReader.getJsonString();

                // Parse the JSON
                parser = new JSONParser(jsonString);

                statusLabel.setText("JSON file loaded successfully: " + fileChooser.getSelectedFile().getName());
                statusLabel.setForeground(new Color(34, 139, 34));

                // Update convert button state
                updateConvertButtonState();

                System.out.println("Headers (List): " + parser.getHeaders());
                System.out.println("Headers (Array): " + Arrays.toString(parser.getHeadersArray()));

            } catch (Exception ex) {
                statusLabel.setText("Error loading JSON file: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
                parser = null;
                updateConvertButtonState();
            }
        }
    }

    private void handleOutputLocationSelection() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setDialogTitle("Select Output Folder");
        folderChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedOutputPath = folderChooser.getSelectedFile().getAbsolutePath();
            outputPathLabel.setText("Output folder: " + selectedOutputPath);
            outputPathLabel.setForeground(new Color(34, 139, 34));

            // Update convert button state
            updateConvertButtonState();
        }
    }

    private void handleConversion() {
        if (parser == null || selectedOutputPath.isEmpty()) {
            statusLabel.setText("Please select both JSON file and output folder");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            // Crear nombre del archivo CSV
            String fileName = JOptionPane.showInputDialog(this,
                    "Enter CSV file name (without extension):",
                    "CSV File Name",
                    JOptionPane.QUESTION_MESSAGE);

            if (fileName == null || fileName.trim().isEmpty()) {
                statusLabel.setText("Conversion cancelled");
                statusLabel.setForeground(Color.ORANGE);
                return;
            }

            // Asegurar que el nombre termina en .csv
            if (!fileName.toLowerCase().endsWith(".csv")) {
                fileName += ".csv";
            }

            String fullOutputPath = selectedOutputPath + File.separator + fileName;

            statusLabel.setText("Converting to CSV...");
            statusLabel.setForeground(Color.BLUE);

            // Crear el archivo CSV
            CSVWriterFile csvWriterFile = new CSVWriterFile(parser.getHeadersArray());
            csvWriterFile.createCSVWithData(fullOutputPath, parser.getRows());

            statusLabel.setText("CSV file created successfully at: " + fullOutputPath);
            statusLabel.setForeground(new Color(34, 139, 34));

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "CSV file has been created successfully!\nLocation: " + fullOutputPath,
                    "Conversion Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            statusLabel.setText("Error creating CSV file: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);

            JOptionPane.showMessageDialog(this,
                    "Error creating CSV file:\n" + ex.getMessage(),
                    "Conversion Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateConvertButtonState() {
        boolean canConvert = (parser != null) && (!selectedOutputPath.isEmpty());
        convertBtn.setEnabled(canConvert);

        if (canConvert) {
            convertBtn.setBackground(new Color(220, 20, 60));
        } else {
            convertBtn.setBackground(Color.GRAY);
        }
    }
}