package com.example.filecreator;

import com.example.filecreator.service.ExcelParserService;
import com.example.filecreator.model.ExcelRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class FileCreatorApplication implements CommandLineRunner {

    @Autowired
    private ExcelParserService parserService;

    public static void main(String[] args) {
        SpringApplication.run(FileCreatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Demo: if a Rough_csv.csv exists in the project root, parse and print first few rows
        File demo = new File("Rough_csv.csv");
        if (demo.exists()) {
            System.out.println("Found Rough_csv.csv - parsing demo file...");
            List<ExcelRecord> list = parserService.parse(demo);
            System.out.println("Parsed " + list.size() + " records.");
            list.stream().limit(5).forEach(System.out::println);
        } else {
            System.out.println("No Rough_csv.csv found in project root. Start the app and POST a file to /api/upload to test uploads.");
        }
    }
}
