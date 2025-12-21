package com.example.filecreator;

import com.example.filecreator.model.ExcelRecord;
import com.example.filecreator.service.WordDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class TestWordGeneration implements CommandLineRunner {

    @Autowired
    private WordDocumentService wordDocumentService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "test-word".equals(args[0])) {
            System.out.println("Testing Word document generation...");
            
            // Create a test record matching your CSV data
            ExcelRecord testRecord = new ExcelRecord();
            testRecord.setSrNo(1);
            testRecord.setBeNumber("8568569");
            testRecord.setBeDate("03-06-2021 00:00");
            testRecord.setImporterName("ABC");
            testRecord.setAddress("S ABD NT");
            testRecord.setEightDigitHsCode("85444999");
            testRecord.setFullItemDescription("Machines");
            testRecord.setAssessableValueAmount(new BigDecimal("8658"));
            testRecord.setBcdRate("0%");
            testRecord.setIgstRate("0");
            testRecord.setTotalDutyPaidAmount(new BigDecimal("300"));
            testRecord.setEffectiveRateOfDuty("77.28%");
            testRecord.setDutyPayable(new BigDecimal("6690.9024"));
            testRecord.setDifferentialDuty(new BigDecimal("6390.9024"));
            
            try {
                byte[] result = wordDocumentService.generateWordDocuments(Arrays.asList(testRecord));
                System.out.println("Successfully generated Word document! ZIP size: " + result.length + " bytes");
                
                // Save to file for testing
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream("test_output.zip")) {
                    fos.write(result);
                    System.out.println("Test output saved to test_output.zip");
                }
            } catch (Exception e) {
                System.out.println("Error generating Word document: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}