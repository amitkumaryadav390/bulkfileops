# filecreator

Spring Boot demo app that parses an uploaded Excel (.xlsx/.xls) or CSV file and returns a list of records mapped to a Java POJO. Also generates Word documents from a template by replacing placeholders with actual data.

## Features

1. **Parse Excel/CSV files** - Converts Excel/CSV data into Java objects
2. **Generate Word Documents** - Creates individual Word documents for each record using a template

## Quick start (Windows PowerShell):

1. Build and run

```powershell
mvn clean package
mvn spring-boot:run
```

## API Endpoints

### 1. Parse File
Parse Excel/CSV and return JSON data:

```powershell
# replace path with your file; the endpoint accepts CSV or XLSX
curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/upload
```

### 2. Generate Word Documents
Parse Excel/CSV and generate Word documents from template:

```powershell
# Upload file and download ZIP containing generated Word documents
curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/generate-docs -o generated_documents.zip
```

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