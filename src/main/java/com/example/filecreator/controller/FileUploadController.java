package com.example.filecreator.controller;

import com.example.filecreator.model.AggregatedExcelRecord;
import com.example.filecreator.model.ExcelRecord;
import com.example.filecreator.service.DataAggregationService;
import com.example.filecreator.service.ExcelParserService;
import com.example.filecreator.service.WordDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private ExcelParserService parserService;

    @Autowired
    private WordDocumentService wordDocumentService;

    @Autowired
    private DataAggregationService dataAggregationService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a non-empty file as 'file' multipart part.");
        }
        try {
            List<ExcelRecord> records = parserService.parse(file);
            return ResponseEntity.ok(records);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse file: " + e.getMessage());
        }
    }

    @PostMapping(value = "/generate-docs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateDocuments(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a non-empty file as 'file' multipart part.");
        }
        try {
            // Parse the uploaded file
            List<ExcelRecord> records = parserService.parse(file);
            
            // Generate Word documents
            byte[] zipBytes = wordDocumentService.generateWordDocuments(records);
            
            // Return as downloadable ZIP file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "generated_documents.zip");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate documents: " + e.getMessage());
        }
    }

    @PostMapping(value = "/generate-aggregated-docs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateAggregatedDocuments(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a non-empty file as 'file' multipart part.");
        }
        try {
            // Parse the uploaded file
            List<ExcelRecord> records = parserService.parse(file);
            
            // Aggregate records by importer name
            List<AggregatedExcelRecord> aggregatedRecords = dataAggregationService.aggregateByImporter(records);
            
            // Generate Word documents with aggregated data
            byte[] zipBytes = wordDocumentService.generateAggregatedWordDocuments(aggregatedRecords);
            
            // Return as downloadable ZIP file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "aggregated_documents.zip");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate aggregated documents: " + e.getMessage());
        }
    }
}
