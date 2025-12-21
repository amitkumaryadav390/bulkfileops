package com.example.filecreator.service;

import com.example.filecreator.model.ExcelRecord;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WordDocumentService {

    private static final String TEMPLATE_FILE = "Rough.docx";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    public byte[] generateWordDocuments(List<ExcelRecord> records) throws IOException {
        // Create a ZIP file containing all generated Word documents
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipOutputStream)) {
            
            for (int i = 0; i < records.size(); i++) {
                ExcelRecord record = records.get(i);
                byte[] docBytes = generateSingleDocument(record);
                
                // Add document to ZIP
                String filename = String.format("Document_%d_%s.docx", 
                    (i + 1), 
                    sanitizeFilename(record.getImporterName()));
                    
                ZipEntry entry = new ZipEntry(filename);
                zip.putNextEntry(entry);
                zip.write(docBytes);
                zip.closeEntry();
            }
        }
        
        return zipOutputStream.toByteArray();
    }

    private byte[] generateSingleDocument(ExcelRecord record) throws IOException {
        // Read template document
        File templateFile = new File(TEMPLATE_FILE);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template file not found: " + TEMPLATE_FILE);
        }

        try (FileInputStream fis = new FileInputStream(templateFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            // Replace placeholders in paragraphs
            replacePlaceholdersInParagraphs(document.getParagraphs(), record);
            
            // Replace placeholders in tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        replacePlaceholdersInParagraphs(cell.getParagraphs(), record);
                    }
                }
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void replacePlaceholdersInParagraphs(List<XWPFParagraph> paragraphs, ExcelRecord record) {
        for (XWPFParagraph paragraph : paragraphs) {
            // Get the full text of the paragraph first
            String fullText = paragraph.getText();
            if (fullText == null || !fullText.contains("{{")) {
                continue;
            }

            // Replace placeholders in the full text
            String replacedText = replacePlaceholders(fullText, record);
            
            // If text was changed, we need to rebuild the paragraph
            if (!fullText.equals(replacedText)) {
                // Clear all existing runs
                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
                
                // Add new run with replaced text
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(replacedText);
            }
        }
    }

    private String replacePlaceholders(String text, ExcelRecord record) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            System.out.println("Found placeholder: '" + placeholder + "'");
            String replacement = getFieldValue(placeholder, record);
            System.out.println("Replacement value: '" + replacement + "'");
            
            // Escape special regex characters in replacement
            String safeReplacement = replacement.replaceAll("\\$", "\\\\\\$");
            matcher.appendReplacement(result, safeReplacement);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    private String getFieldValue(String placeholder, ExcelRecord record) {
        // Map placeholder names to actual field values - handle exact matches from your template
        String normalizedPlaceholder = placeholder.toLowerCase().trim();
        
        switch (normalizedPlaceholder) {
            // Primary placeholders from your Rough.docx template (exact matches)
            case "importer name":
                return record.getImporterName() != null ? record.getImporterName() : "";
            case "address":
                return record.getAddress() != null ? record.getAddress() : "";
            case "description":
                return record.getFullItemDescription() != null ? record.getFullItemDescription() : "";
            case "eight digit hs code":
                return record.getEightDigitHsCode() != null ? record.getEightDigitHsCode() : "";
            case "bcd rate":
                return record.getBcdRate() != null ? record.getBcdRate() : "";
            case "igst rate":
                return record.getIgstRate() != null ? record.getIgstRate() : "";
            case "differential duty":
                return record.getDifferentialDuty() != null ? record.getDifferentialDuty().toString() : "";
                
            // Additional field mappings for variations
            case "sr. no.":
            case "sr no":
            case "srno":
                return record.getSrNo() != null ? record.getSrNo().toString() : "";
            case "be number":
            case "benumber":
                return record.getBeNumber() != null ? record.getBeNumber() : "";
            case "be date":
            case "bedate":
                return record.getBeDate() != null ? record.getBeDate() : "";
            case "full item description":
            case "fullitemdescription":
            case "item description":
                return record.getFullItemDescription() != null ? record.getFullItemDescription() : "";
            case "assessable value amount":
            case "assessablevalueamount":
            case "assessable value":
                return record.getAssessableValueAmount() != null ? record.getAssessableValueAmount().toString() : "";
            case "total duty paid amount":
            case "totaldutypaidamount":
            case "duty paid":
                return record.getTotalDutyPaidAmount() != null ? record.getTotalDutyPaidAmount().toString() : "";
            case "effective rate of duty":
            case "effectiverateofduty":
            case "effective rate":
                return record.getEffectiveRateOfDuty() != null ? record.getEffectiveRateOfDuty() : "";
            case "duty payable":
            case "dutypayable":
                return record.getDutyPayable() != null ? record.getDutyPayable().toString() : "";
                
            default:
                // Log unmatched placeholders for debugging
                System.out.println("Unmatched placeholder: '" + placeholder + "' (normalized: '" + normalizedPlaceholder + "')");
                return "{{" + placeholder + "}}"; // Keep original if not found
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "Unknown";
        }
        // Remove invalid filename characters
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
}