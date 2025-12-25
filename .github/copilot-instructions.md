# Copilot Instructions for FileCreator

- **Purpose**: Convert uploaded Excel/CSV rows into Word documents (individual or aggregated) using a Rough.docx template; React frontend drives uploads, Spring Boot backend generates ZIPs.
- **Key repos**: Backend Spring Boot app lives in [src/main/java/com/example/filecreator](src/main/java/com/example/filecreator); React frontend in [frontend/src](frontend/src).

## Architecture & Data Flow
- Client posts multipart file to `/api/upload` for preview or to `/api/generate-docs` and `/api/generate-aggregated-docs` for downloads (see [FileUploadController.java](src/main/java/com/example/filecreator/controller/FileUploadController.java)).
- Parser chooses XLSX/XLS vs CSV, normalizes headers, and maps to `ExcelRecord` fields (see [ExcelParserService.java](src/main/java/com/example/filecreator/service/ExcelParserService.java)).
- Aggregated mode groups by importer name, concatenates text fields, and sums numeric amounts into `AggregatedExcelRecord` (see [DataAggregationService.java](src/main/java/com/example/filecreator/service/DataAggregationService.java)).
- `WordDocumentService` loads Rough.docx from project root, replaces `{{placeholders}}`, and streams ZIPs; filenames are sanitized and prefixed `Document_` or `Aggregated_` (see [WordDocumentService.java](src/main/java/com/example/filecreator/service/WordDocumentService.java)).
- CORS is explicitly opened to `http://localhost:3000`; keep new endpoints under `/api/**` or update [WebConfig.java](src/main/java/com/example/filecreator/config/WebConfig.java) if origins change.

## Build & Run
- Backend (Java 11, Spring Boot 2.7.12): `mvn clean package` then `mvn spring-boot:run`. On startup, if Rough_csv.csv exists in project root, it parses a demo file (see [FileCreatorApplication.java](src/main/java/com/example/filecreator/FileCreatorApplication.java)).
- Frontend (React 18, Webpack): from `frontend`, run `npm install` then `npm start`. Production bundle: `npm run build` outputs to `dist`.
- Ports: backend 8080, frontend 3000; adjust only if you also change CORS settings and client endpoint constants in [ProcessingOptions.js](frontend/src/components/ProcessingOptions.js).

## Templates & Placeholders
- Template file name is fixed as `Rough.docx` in repo root; all doc generation fails if missing.
- Placeholder format: `{{Importer Name}}`, `{{ADDRESS}}`, `{{DESCRIPTION}}`, `{{Eight Digit HS Code}}`, `{{BCD Rate}}`, `{{IGST Rate}}`, `{{Differential Duty}}` plus optional `Sr. No.`, `BE Number`, `BE Date`, `Assessable Value Amount`, `Total Duty Paid Amount`, `Effective Rate of Duty`, `Duty Payable`.
- Unrecognized placeholders are left intact with braces; add new mappings in `getFieldValue()` / `getAggregatedFieldValue()` inside [WordDocumentService.java](src/main/java/com/example/filecreator/service/WordDocumentService.java) to support custom template fields.

## Parsing Rules
- CSV parsing uses Apache Commons CSV with first-record-as-header; XLSX uses POI and builds a header list from the first row. Header lookups are case/whitespace tolerant via `getFirst()` helper.
- Numeric fields strip commas/percent signs and parse to `BigDecimal`; rows with missing/invalid numbers become null and will render empty strings in templates.
- Aggregation uses first non-null address per importer, distinct join with ", " for text fields, and sums `DifferentialDuty`, `AssessableValueAmount`, `TotalDutyPaidAmount`, `DutyPayable` as `BigDecimal`s.

## Frontend Contracts
- Processing buttons hit hardcoded endpoints: individual → `/api/generate-docs`; aggregated → `/api/generate-aggregated-docs` (see [ProcessingOptions.js](frontend/src/components/ProcessingOptions.js)). Response is `blob`; UI builds a download link without persisting to disk.
- File validation: accepts csv/xlsx/xls MIME or extension; 10 MB limit enforced server-side via `spring.servlet.multipart.max-file-size` in [application.properties](src/main/resources/application.properties).
- Result view expects a successful download URL; errors surface network/404 messages and remind users about Rough.docx presence (see [ResultDisplay.js](frontend/src/components/ResultDisplay.js)).

## Common Tasks & Tips
- Adding endpoints: keep them under `/api` and mirror CORS config; return `ResponseEntity<byte[]>` for file downloads with `Content-Disposition` attachment naming.
- Changing upload limits: update both `spring.servlet.multipart.*` and client copy in help text within [FileUpload.js](frontend/src/components/FileUpload.js) or Result cards.
- Updating template placeholders: edit Rough.docx and extend mapping switches; test by calling `/api/generate-docs` with a small CSV and inspect ZIP entries.
- Adjusting base URLs for deployment: set `REACT_APP_API_URL` and use it in `ProcessingOptions.js` if adding environment-based configuration.

## Troubleshooting
- If downloads are empty or placeholders remain, confirm Rough.docx exists and placeholder strings match mapping keys exactly.
- If CORS or 404 errors occur from the frontend, verify backend is on port 8080 and `/api/*` is reachable; health check at `/api/health` returns plain OK.
- For parsing mismatches, inspect CSV headers; normalization is tolerant but missing headers produce null fields and empty replacements.
