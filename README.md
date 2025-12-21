# filecreator

Spring Boot application with React frontend that parses uploaded Excel (.xlsx/.xls) or CSV files and generates Word documents from templates by replacing placeholders with actual data.

## Project Structure

```
filecreator/
├── frontend/                   # React frontend application
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── styles/             # CSS styling
│   │   └── App.js              # Main app component
│   ├── public/                 # Static assets
│   ├── package.json           # Frontend dependencies
│   └── README.md              # Frontend documentation
├── src/main/java/              # Spring Boot backend
│   └── com/example/filecreator/
│       ├── controller/         # REST controllers
│       ├── model/              # Data models
│       └── service/            # Business logic
├── Rough_csv.csv              # Sample data file
├── Rough.docx                 # Word template (user-provided)
├── pom.xml                    # Backend dependencies
└── README.md                  # This file
```

## Features

1. **Modern Web Interface** - React frontend with drag-and-drop file upload
2. **Parse Excel/CSV files** - Converts Excel/CSV data into Java objects
3. **Generate Word Documents** - Creates documents using templates with placeholder replacement
4. **Individual Processing** - One document per Excel/CSV row
5. **Aggregated Processing** - Groups by importer name with combined data

## Quick Start

### Prerequisites
- Java 11+ and Maven
- Node.js 16+ and npm
- A `Rough.docx` template file in the project root

### 1. Start the Backend (Spring Boot)

```powershell
# Build and run Spring Boot application
mvn clean package
mvn spring-boot:run
```

The backend will start on http://localhost:8080

### 2. Start the Frontend (React)

```powershell
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will open at http://localhost:3000

### 3. Use the Application

1. **Open http://localhost:3000** in your browser
2. **Upload your Excel/CSV file** using the drag-and-drop interface
3. **Choose processing type**:
   - Individual: One document per row
   - Aggregated: Group by importer name
4. **Click "Generate Documents"** to process
5. **Download the ZIP file** containing generated Word documents

## API Endpoints

### 1. Parse File
Parse Excel/CSV and return JSON data:

```powershell
# replace path with your file; the endpoint accepts CSV or XLSX
curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/upload
```

### 2. Generate Individual Word Documents
Parse Excel/CSV and generate one Word document per record:

```powershell
# Upload file and download ZIP containing individual Word documents (one per row)
curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/generate-docs -o individual_documents.zip
```

### 3. Generate Aggregated Word Documents (NEW!)
Parse Excel/CSV and generate aggregated Word documents grouped by importer name:

```powershell
# Upload file and download ZIP containing aggregated Word documents (one per unique importer)
curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/generate-aggregated-docs -o aggregated_documents.zip
```

**Aggregation Rules:**
- Groups all records by importer name
- Uses first address found for each importer
- Sums up differential duty amounts for same importer
- Combines all unique HS codes with comma separation
- Combines other fields (BE numbers, dates, descriptions, rates) with comma separation
- Totals numerical values (assessable value, duty paid, duty payable)

## Template Setup

Place a `Rough.docx` file in the project root directory. Use placeholders in the format `{{placeholder_name}}` where you want data to be replaced.

**Supported placeholders (based on your template):**
- `{{Importer Name}}` - Replaced with importer name from CSV
- `{{ADDRESS}}` - Replaced with address from CSV  
- `{{DESCRIPTION}}` - Replaced with full item description from CSV
- `{{Eight Digit HS Code}}` - Replaced with HS code from CSV
- `{{BCD Rate}}` - Replaced with BCD rate from CSV
- `{{IGST Rate}}` - Replaced with IGST rate from CSV
- `{{Differential Duty}}` - Replaced with differential duty amount from CSV

**Additional available placeholders:**
- `{{Sr. No.}}` - Serial number
- `{{BE Number}}` - BE number
- `{{BE Date}}` - BE date
- `{{Assessable Value Amount}}` - Assessable value
- `{{Total Duty Paid Amount}}` - Total duty amount
- `{{Effective Rate of Duty}}` - Effective rate
- `{{Duty Payable}}` - Duty payable amount

The endpoint returns JSON array of objects matching the Excel columns.

## Notes
- If a `Rough_csv.csv` file exists in the project root, the app prints a small demo parse on startup.
- The parser is defensive and tolerates a few formatting variations (percent signs, commas in numbers).
- Generated Word documents are returned as a ZIP file containing one document per Excel/CSV record.