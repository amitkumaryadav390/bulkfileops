package com.example.filecreator;

import com.example.filecreator.model.AggregatedExcelRecord;
import com.example.filecreator.model.ExcelRecord;
import com.example.filecreator.service.DataAggregationService;
import com.example.filecreator.service.WordDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class TestWordGeneration implements CommandLineRunner {

    @Autowired
    private WordDocumentService wordDocumentService;

    @Autowired
    private DataAggregationService dataAggregationService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "test-word".equals(args[0])) {
            System.out.println("Testing Word document generation...");
            
            // Create test records matching your CSV data - including duplicate importer names
            ExcelRecord testRecord1 = createTestRecord(1, "ABC", "Machines", "85444999", "6390.9024");
            ExcelRecord testRecord2 = createTestRecord(2, "ABC", "Chemicals", "83249099", "45123.28");
            ExcelRecord testRecord3 = createTestRecord(3, "UTT", "Banana", "69852480", "4232.1168");
            
            List<ExcelRecord> testRecords = Arrays.asList(testRecord1, testRecord2, testRecord3);
            
            try {
                // Test individual document generation
                byte[] individualResult = wordDocumentService.generateWordDocuments(testRecords);
                System.out.println("Successfully generated individual Word documents! ZIP size: " + individualResult.length + " bytes");
                
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream("test_individual_output.zip")) {
                    fos.write(individualResult);
                    System.out.println("Individual test output saved to test_individual_output.zip");
                }
                
                // Test aggregated document generation
                List<AggregatedExcelRecord> aggregatedRecords = dataAggregationService.aggregateByImporter(testRecords);
                System.out.println("Aggregated " + testRecords.size() + " records into " + aggregatedRecords.size() + " aggregated records:");
                for (AggregatedExcelRecord agg : aggregatedRecords) {
                    System.out.println("  Importer: " + agg.getImporterName() + 
                                     ", HS Codes: " + agg.getAggregatedHsCodes() + 
                                     ", Total Differential Duty: " + agg.getTotalDifferentialDuty());
                }
                
                byte[] aggregatedResult = wordDocumentService.generateAggregatedWordDocuments(aggregatedRecords);
                System.out.println("Successfully generated aggregated Word documents! ZIP size: " + aggregatedResult.length + " bytes");
                
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream("test_aggregated_output.zip")) {
                    fos.write(aggregatedResult);
                    System.out.println("Aggregated test output saved to test_aggregated_output.zip");
                }
                
            } catch (Exception e) {
                System.out.println("Error generating Word documents: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private ExcelRecord createTestRecord(int srNo, String importer, String description, String hsCode, String diffDuty) {
        ExcelRecord record = new ExcelRecord();
        record.setSrNo(srNo);
        record.setBeNumber("8568569");
        record.setBeDate("03-06-2021 00:00");
        record.setImporterName(importer);
        record.setAddress("S ABD NT");
        record.setEightDigitHsCode(hsCode);
        record.setFullItemDescription(description);
        record.setAssessableValueAmount(new BigDecimal("8658"));
        record.setBcdRate("0%");
        record.setIgstRate("0");
        record.setTotalDutyPaidAmount(new BigDecimal("300"));
        record.setEffectiveRateOfDuty("77.28%");
        record.setDutyPayable(new BigDecimal("6690.9024"));
        record.setDifferentialDuty(new BigDecimal(diffDuty));
        return record;
    }
}