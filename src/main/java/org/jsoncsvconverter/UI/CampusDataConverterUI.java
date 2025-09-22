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

/**
 * The main graphical user interface for the Campus Data Converter application.
 * This class provides a Swing-based desktop application that allows users to convert
 * JSON files into CSV format through an intuitive visual interface.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>File selection dialog for JSON input files with extension validation</li>
 *   <li>Directory selection dialog for CSV output location</li>
 *   <li>Real-time status updates and error reporting</li>
 *   <li>Intelligent button state management based on user selections</li>
 *   <li>Custom file naming with automatic .csv extension handling</li>
 *   <li>Professional styling with color-coded buttons and status messages</li>
 * </ul>
 *
 * <p>Application workflow:</p>
 * <ol>
 *   <li>User selects a JSON file using the "Upload JSON File" button</li>
 *   <li>User selects an output directory using the "Select Output Folder" button</li>
 *   <li>User clicks "Convert to CSV" and provides a filename</li>
 *   <li>Application processes the conversion and displays results</li>
 * </ol>
 *
 * <p>The UI uses absolute positioning for precise control over component layout
 * and provides comprehensive error handling with user-friendly messages.</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 *
 * @see JSONParser
 * @see JsonFileReader
 * @see CSVWriterFile
 * @see JFrame
 * @see ActionListener
 */
public class CampusDataConverterUI extends javax.swing.JFrame implements ActionListener {

    /** Button for JSON file upload functionality */
    private JButton uploadJsonFileBtn;

    /** Button for output directory selection */
    private JButton selectOutputLocationBtn;

    /** Button for triggering the conversion process */
    private JButton convertBtn;

    /** Label displaying current application status and messages */
    private JLabel statusLabel;

    /** Label showing the selected output directory path */
    private JLabel outputPathLabel;

    /** File reader instance for processing the selected JSON file */
    private JsonFileReader jsonFileReader;

    /** The selected output directory path where CSV files will be saved */
    private String selectedOutputPath = "";

    /** JSON parser instance containing the processed JSON data ready for conversion */
    private JSONParser parser = null;

    /**
     * Constructs and initializes the Campus Data Converter user interface.
     * This constructor sets up all UI components, applies styling, and configures
     * the main application window with professional appearance and layout.
     *
     * <p>UI Components created:</p>
     * <ul>
     *   <li>Main application logo and branding</li>
     *   <li>Three functional buttons with distinct color schemes</li>
     *   <li>Status and path display labels with dynamic content</li>
     *   <li>Proper window sizing, positioning, and behavior</li>
     * </ul>
     *
     * <p>The constructor uses absolute positioning (null layout) for precise
     * component placement and applies professional styling including:</p>
     * <ul>
     *   <li>Color-coded buttons for different functions</li>
     *   <li>Consistent fonts and sizing</li>
     *   <li>Centered window positioning</li>
     *   <li>Proper focus and visual feedback</li>
     * </ul>
     *
     * <p><strong>Note:</strong> The logo path is currently hardcoded and should be
     * updated to use relative paths or resource loading for distribution.</p>
     */
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

    /**
     * Handles all button click events in the application.
     * This method serves as the central event dispatcher, routing button clicks
     * to their respective handler methods based on the event source.
     *
     * <p>Supported actions:</p>
     * <ul>
     *   <li>JSON file upload and processing</li>
     *   <li>Output directory selection</li>
     *   <li>CSV conversion execution</li>
     * </ul>
     *
     * @param e The ActionEvent containing information about the button click,
     *          including the source component that triggered the event
     *
     * @see #handleJsonFileUpload()
     * @see #handleOutputLocationSelection()
     * @see #handleConversion()
     */
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

    /**
     * Handles the JSON file upload process including file selection, validation, and parsing.
     * This method presents a file chooser dialog filtered for JSON files, processes the
     * selected file, and updates the UI based on the results.
     *
     * <p>Process flow:</p>
     * <ol>
     *   <li>Display file chooser with JSON extension filter</li>
     *   <li>Validate user selection and file accessibility</li>
     *   <li>Read and parse JSON content using {@link JsonFileReader} and {@link JSONParser}</li>
     *   <li>Update UI status and enable/disable conversion button accordingly</li>
     *   <li>Handle and display any errors encountered during processing</li>
     * </ol>
     *
     * <p>The method provides comprehensive error handling for common issues such as:</p>
     * <ul>
     *   <li>File not found or inaccessible</li>
     *   <li>Invalid JSON format or structure</li>
     *   <li>I/O errors during file reading</li>
     *   <li>Memory issues with large files</li>
     * </ul>
     *
     * <p>Upon successful processing, the method outputs debug information to the console
     * including the discovered headers and their structure.</p>
     *
     * @see JsonFileReader#JsonFileReader(String)
     * @see JSONParser#JSONParser(String)
     * @see #updateConvertButtonState()
     */
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

    /**
     * Handles the output directory selection process.
     * This method presents a directory chooser dialog allowing users to select
     * where their converted CSV files should be saved, with intelligent defaults
     * and user-friendly feedback.
     *
     * <p>Features:</p>
     * <ul>
     *   <li>Directory-only selection mode for clear user intent</li>
     *   <li>Default starting location at user's home directory</li>
     *   <li>Visual feedback showing selected path in the UI</li>
     *   <li>Automatic enabling/disabling of conversion button</li>
     * </ul>
     *
     * <p>The method updates the {@link #selectedOutputPath} variable and refreshes
     * the UI display to show the chosen directory path. The path display uses
     * visual cues (color changes) to indicate successful selection.</p>
     *
     * @see #updateConvertButtonState()
     */
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

    /**
     * Handles the complete CSV conversion process from user input to file creation.
     * This method orchestrates the final conversion step, including filename input,
     * validation, CSV generation, and user feedback with comprehensive error handling.
     *
     * <p>Conversion workflow:</p>
     * <ol>
     *   <li>Validate that both JSON file and output directory are selected</li>
     *   <li>Prompt user for CSV filename with input validation</li>
     *   <li>Automatically handle .csv extension addition if needed</li>
     *   <li>Create full output path and initiate CSV generation</li>
     *   <li>Provide success confirmation with file location</li>
     * </ol>
     *
     * <p>User interaction features:</p>
     * <ul>
     *   <li>Input dialog for custom filename specification</li>
     *   <li>Automatic .csv extension handling (adds if missing)</li>
     *   <li>Cancellation support with appropriate status updates</li>
     *   <li>Success dialog showing exact file location</li>
     * </ul>
     *
     * <p>Error handling covers:</p>
     * <ul>
     *   <li>Missing prerequisites (JSON file or output directory)</li>
     *   <li>Invalid or empty filenames</li>
     *   <li>File system errors (permissions, disk space, etc.)</li>
     *   <li>Data processing errors during CSV generation</li>
     * </ul>
     *
     * <p>The method uses {@link CSVWriterFile} to perform the actual file creation
     * and provides real-time status updates throughout the process.</p>
     *
     * @see CSVWriterFile#createCSVWithData(String, java.util.List)
     * @see JSONParser#getHeadersArray()
     * @see JSONParser#getRows()
     */
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

    /**
     * Updates the state and appearance of the conversion button based on application readiness.
     * This method implements intelligent UI state management by enabling the conversion button
     * only when both required inputs (JSON file and output directory) are available.
     *
     * <p>State management logic:</p>
     * <ul>
     *   <li><strong>Enabled:</strong> When both JSON parser and output path are ready</li>
     *   <li><strong>Disabled:</strong> When either prerequisite is missing</li>
     * </ul>
     *
     * <p>Visual feedback includes:</p>
     * <ul>
     *   <li>Button enabled/disabled state changes</li>
     *   <li>Color changes to indicate availability (red when ready, gray when not)</li>
     *   <li>Consistent visual cues for user understanding</li>
     * </ul>
     *
     * <p>This method should be called whenever the application state changes that might
     * affect conversion readiness, such as after successful JSON loading or directory selection.</p>
     *
     * @see #handleJsonFileUpload()
     * @see #handleOutputLocationSelection()
     */
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