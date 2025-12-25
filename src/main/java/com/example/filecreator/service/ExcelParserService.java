package com.example.filecreator.service;

import com.example.filecreator.model.ExcelRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ExcelParserService {

    private static final String[] HEADERS = new String[]{
            "Sr. No.", "BE Number", "BE Date", "Importer Name", "ADDRESS",
            "Eight Digit HS Code", "Full Item Description", "Assessable Value Amount",
            "BCD Rate", "IGST Rate", "Total Duty Paid Amount",
            "Effective Rate of duty (BCD@35% + SWS@10% + IGST@28%)", "Duty Payable", "Differential Duty"
    };

    public List<ExcelRecord> parse(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename != null && (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls"))) {
            return parseXlsx(file.getInputStream());
        }
        // fallback to CSV
        return parseCsv(file.getInputStream());
    }

    public List<ExcelRecord> parse(File file) throws IOException {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return parseXlsx(fis);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                return parseCsv(fis);
            }
        }
    }

    private List<ExcelRecord> parseCsv(InputStream is) throws IOException {
        List<ExcelRecord> out = new ArrayList<>();
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines()
                    .withTrim()
                    .parse(reader);

            Map<String, Integer> headerMap = parser.getHeaderMap();
            for (CSVRecord rec : parser) {
                ExcelRecord r = mapRecord(rec.toMap());
                out.add(r);
            }
        }
        return out;
    }

    private List<ExcelRecord> parseXlsx(InputStream is) throws IOException {
        List<ExcelRecord> out = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) return out;
            // header
            Row header = rows.next();
            List<String> headers = new ArrayList<>();
            for (Cell c : header) headers.add(getCellString(c));

            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell c = row.getCell(i);
                        String key = headers.get(i);
                        String value = getCellString(c);
                        if (key != null) key = key.trim();
                        if (value != null) value = value.trim();
                        map.put(key, value);
                }
                ExcelRecord r = mapRecord(map);
                out.add(r);
            }
        }
        return out;
    }

    private String getCellString(Cell c) {
        if (c == null) return null;
        switch (c.getCellType()) {
            case STRING:
                return c.getStringCellValue();
            case NUMERIC:
                // return numeric as plain string without scientific notation
                return BigDecimal.valueOf(c.getNumericCellValue()).stripTrailingZeros().toPlainString();
            case BOOLEAN:
                return Boolean.toString(c.getBooleanCellValue());
            case FORMULA:
                try {
                    return c.getStringCellValue();
                } catch (Exception e) {
                    return BigDecimal.valueOf(c.getNumericCellValue()).toPlainString();
                }
            case BLANK:
            default:
                return "";
        }
    }

    private ExcelRecord mapRecord(Map<String, String> row) {
        ExcelRecord r = new ExcelRecord();
        // Try matching by header keys; be defensive about missing keys
        r.setSrNo(parseInteger(getFirst(row, "Sr. No.", "Sr No", "Sr. No")));
        r.setBeNumber(getFirst(row, "BE Number", "BE Number"));
        r.setBeDate(getFirst(row, "BE Date", "BE Date"));
        r.setImporterName(getFirst(row, "Importer Name", "Importer Name"));
        r.setAddress(getFirst(row, "ADDRESS", "Address"));
        r.setEightDigitHsCode(getFirst(row, "Eight Digit HS Code", "Eight Digit HS Code"));
        r.setFullItemDescription(getFirst(row, "Full Item Description", "Full Item Description"));
        r.setAssessableValueAmount(parseBigDecimal(getFirst(row, "Assessable Value Amount", "Assessable Value Amount")));
        r.setBcdRate(getFirst(row, "BCD Rate", "BCD Rate"));
        r.setIgstRate(getFirst(row, "IGST Rate", "IGST Rate"));
        r.setTotalDutyPaidAmount(parseBigDecimal(getFirst(row, "Total Duty Paid Amount", "Total Duty Paid Amount")));
        r.setEffectiveRateOfDuty(getFirst(row, "Effective Rate of duty (BCD@35% + SWS@10% + IGST@28%)", "Effective Rate of duty (BCD@35% + SWS@10% + IGST@28%)"));
        r.setDutyPayable(parseBigDecimal(getFirst(row, "Duty Payable", "Duty Payable")));
        r.setDifferentialDuty(parseBigDecimal(getFirst(row, "Differential Duty", "Differential Duty")));
        r.setChaDetails(getFirst(row, "CHA details", "CHA Details", "CHA", "CHA details ", " CHA details"));
        System.out.println("Parsed CHA details: " + r.getChaDetails());
        return r;
    }

    private String getFirst(Map<String, String> map, String... keys) {
        for (String k : keys) {
            if (map.containsKey(k)) {
                String v = map.get(k);
                return (v != null) ? v.trim() : null;
            }
            // also try keys with trimmed/normalized variants
            for (String mk : map.keySet()) {
                if (mk != null && mk.trim().equalsIgnoreCase(k.trim())) {
                    String v = map.get(mk);
                    return (v != null) ? v.trim() : null;
                }
            }
        }
        return null;
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String cleaned = s.replaceAll("[^0-9-]", "");
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String cleaned = s.replaceAll("[,%]", "").trim();
            if (cleaned.isEmpty()) return null;
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }
}
