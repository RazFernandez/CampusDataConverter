<img width="1536" height="1024" alt="Diseño sin título (3)" src="https://github.com/user-attachments/assets/554ca211-f16e-4239-bf84-e24b2b72446d" />

# JSON to CSV Automation Program

## 📋 Table of Contents
- [🎯 Main Objective](#-main-objective)
- [🌍 Vision](#-vision)
- [📌 Problem Context](#-problem-context)
- [🛠️ Technologies Used](#️-technologies-used)
- [🏗️ Project Structure](#️-project-structure)
- [📦 Package Organization](#-package-organization)
- [🔧 Key Components](#-key-components)
- [📋 Design Decisions](#-design-decisions)
- [📚 Documentation](#-documentation)
- [🧪 Testing Strategy](#-testing-strategy)

## 🎯 Main Objective
The objective of this project is to develop an automation program written in **Java** that converts **JSON files into CSV format** in a reliable, efficient, and scalable way.

The program is designed for academic environments where structured data management is crucial, enabling universities to streamline data processing and improve accessibility for analysis and reporting.

## 🌍 Vision
Our vision is to provide a robust and user-friendly tool that bridges the gap between **hierarchical JSON data** and **tabular CSV structures**.

By doing so, we aim to support better decision-making in educational institutions through simplified data transformation, ensuring interoperability and adaptability in diverse systems.

## 📌 Problem Context
At universities, data is often exchanged in **JSON format** due to its flexibility and compatibility with modern applications.

However, many academic and administrative processes, including reporting and statistical analysis, require data in **CSV format** for integration with spreadsheets, research tools, and legacy systems.

The lack of an efficient conversion mechanism creates **bottlenecks, manual errors, and inconsistencies** in data handling.

This project addresses this problem by automating the **JSON-to-CSV conversion process**.

## 🛠️ Technologies Used
- **Java**: Core programming language for implementation
- **GSON**: JSON parsing and processing library
- **OpenCSV**: CSV writing and formatting utilities
- **Swing**: GUI framework for user interface
- **JUnit 3.8.1**: Unit testing framework for quality assurance
- **Maven**: Package manager and build automation
- **GitHub**: Version control and collaboration platform
- **JavaDoc**: Code documentation and API reference

## 🏗️ Project Structure
The project follows **MVC (Model-View-Controller)** architecture pattern to ensure separation of concerns and maintainability:

```
src/main/java/org/jsoncsvconverter/
├── Assets/           # Application resources and images
├── Logic/            # Core business logic (Model)
├── UI/               # User interface components (View & Controller)
└── Main/             # Application entry point

src/test/java/
└── Logic/            # Unit tests for core components
```

## 📦 Package Organization

### `org.jsoncsvconverter.Logic`
**Core business logic and data processing:**
- `JSONParser.java` - Flattens JSON structures into tabular format
- `JsonFileReader.java` - Handles JSON file reading operations
- `CSVWriterFile.java` - Manages CSV file creation and writing

### `org.jsoncsvconverter.UI`
**User interface layer:**
- `CampusDataConverterUI.java` - Main graphical interface with file selection and conversion controls

### `org.jsoncsvconverter.Assets`
**Application resources:**
- Logo and branding images
- UI icons and graphics

## 🔧 Key Components

### JSONParser
- **Flattens nested JSON objects** using double underscore (`__`) separator
- **Handles primitive arrays** by creating separate rows for each element
- **Processes object arrays** by flattening each object into individual rows
- **Maintains consistent column structure** across all output rows

### JsonFileReader
- **Validates JSON file extensions** before processing
- **Efficiently reads files** using BufferedReader
- **Preserves original formatting** including line breaks
- **Handles I/O errors gracefully** with proper exception management

### CSVWriterFile
- **Creates CSV files** with automatic header management
- **Supports directory creation** for nested output paths
- **Handles data validation** and proper CSV formatting
- **Uses OpenCSV library** for reliable CSV generation

### CampusDataConverterUI
- **File selection dialogs** with extension filtering
- **Real-time status updates** and progress indication
- **Error handling and user feedback** with informative messages
- **Intelligent button state management** based on user selections

## 📋 Design Decisions

### 1. GSON vs org.json
**Decision:** Switched from org.json to GSON library
**Rationale:** GSON provides guaranteed order preservation when reading JSON objects, ensuring consistent and predictable output structure that meets user requirements.

### 2. Maven Package Manager
**Decision:** Used Maven for dependency management
**Rationale:** Project size and external library dependencies (GSON, OpenCSV) require robust build automation and dependency resolution.

### 3. GUI vs CLI Interface
**Decision:** Implemented Swing-based graphical user interface
**Rationale:** Target users (university staff) typically lack CLI experience. GUI provides intuitive, accessible interaction for non-technical users.

### 4. MVC Architecture
**Decision:** Structured project using Model-View-Controller pattern
**Rationale:** Separates concerns, enables easier maintenance, allows focused development on specific components without creating tightly coupled code.

## 📚 Documentation
Here is a list of resources that documents the implementation of this code.
1) Flowchart: [Link🔗](https://drive.google.com/file/d/1ZHUcUtuwWHyt28agMgQo_4cuuU3Sj6kT/view?usp=sharing)
2) Explanation of parsing algorithm: [Link🔗](https://github.com/RazFernandez/CampusDataConverter/blob/main/Docs/algorithm_explanation.md)
3) User manual: [Link🔗](https://github.com/RazFernandez/CampusDataConverter/blob/main/Docs/user_manual.md)
4) Backlog: [link🔗](https://github.com/users/RazFernandez/projects/5/views/3)
5) Roadmap: [link🔗](https://github.com/users/RazFernandez/projects/5/views/1)
6) Reflexion: [link🔗](https://github.com/RazFernandez/CampusDataConverter/blob/main/Docs/reflexion.md)

## 🧪 Testing Strategy
Comprehensive **JUnit 3.8.1** test suite covering:
- **Unit tests** for all core logic components
- **Edge case handling** including malformed JSON and empty files
- **Error scenario testing** for file I/O and parsing failures
- **Data integrity validation** ensuring consistent output structure
- **Integration testing** for component interaction verification

The testing framework ensures reliability and maintainability through automated validation of all critical functionality.
