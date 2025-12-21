## Troubleshooting Word Document Generation

### Common Issues and Solutions

1. **Placeholders not being replaced:**
   - Ensure your `Rough.docx` template uses the exact format `{{placeholder_name}}`
   - Check that placeholder names match exactly (case-insensitive)
   - Verify `Rough.docx` is in the project root directory

2. **Template file not found:**
   - Ensure `Rough.docx` exists in: `c:\Users\komal\Desktop\amit\project\filecreator\Rough.docx`
   - Check file permissions

3. **Empty or incorrect values:**
   - Check that your CSV/Excel data contains the expected columns
   - Verify column headers match the expected format

### Debug Steps

1. **Check console output for placeholder detection:**
   When you run the app, it will print found placeholders like:
   ```
   Found placeholder: 'Importer Name'
   Replacement value: 'ABC'
   ```

2. **Test with sample data:**
   Run the app and check the startup output to see if it parses your CSV correctly.

3. **Verify template structure:**
   Open `Rough.docx` and ensure placeholders are formatted as `{{Importer Name}}` not `{Importer Name}` or other variations.

### Your Template Placeholders

Based on your template content, these placeholders should work:
- `{{Importer Name}}` → maps to importerName field
- `{{ADDRESS}}` → maps to address field  
- `{{DESCRIPTION}}` → maps to fullItemDescription field
- `{{Eight Digit HS Code}}` → maps to eightDigitHsCode field
- `{{BCD Rate}}` → maps to bcdRate field
- `{{IGST Rate}}` → maps to igstRate field
- `{{Differential Duty}}` → maps to differentialDuty field

### Testing Steps

1. Start the application:
   ```powershell
   mvn spring-boot:run
   ```

2. Test document generation:
   ```powershell
   curl -F "file=@.\Rough_csv.csv" http://localhost:8080/api/generate-docs -o generated_documents.zip
   ```

3. Extract the ZIP file and check if placeholders were replaced with actual values.

4. If placeholders aren't replaced, check the console output for debug messages showing which placeholders were found and what values were used for replacement.