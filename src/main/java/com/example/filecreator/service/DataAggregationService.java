package com.example.filecreator.service;

import com.example.filecreator.model.AggregatedExcelRecord;
import com.example.filecreator.model.ExcelRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataAggregationService {

    public List<AggregatedExcelRecord> aggregateByImporter(List<ExcelRecord> records) {
        Map<String, List<ExcelRecord>> groupedByImporter = records.stream()
                .filter(record -> record.getImporterName() != null && !record.getImporterName().trim().isEmpty())
                .collect(Collectors.groupingBy(ExcelRecord::getImporterName));

        List<AggregatedExcelRecord> aggregatedRecords = new ArrayList<>();

        for (Map.Entry<String, List<ExcelRecord>> entry : groupedByImporter.entrySet()) {
            String importerName = entry.getKey();
            List<ExcelRecord> importerRecords = entry.getValue();

            AggregatedExcelRecord aggregated = createAggregatedRecord(importerName, importerRecords);
            aggregatedRecords.add(aggregated);
        }

        return aggregatedRecords;
    }

    private AggregatedExcelRecord createAggregatedRecord(String importerName, List<ExcelRecord> records) {
        AggregatedExcelRecord aggregated = new AggregatedExcelRecord();
        aggregated.setImporterName(importerName);

        // Use first address found for this importer
        aggregated.setAddress(records.stream()
                .map(ExcelRecord::getAddress)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(""));

        // Aggregate unique HS codes with comma separation
        String hsCodes = records.stream()
                .map(ExcelRecord::getEightDigitHsCode)
                .filter(Objects::nonNull)
                .filter(code -> !code.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedHsCodes(hsCodes);

        // Aggregate CHA details (comma-separated, distinct)
                String chaDetails = records.stream()
                        .map(ExcelRecord::getChaDetails)
                        .filter(Objects::nonNull)
                        .filter(detail -> !detail.trim().isEmpty())
                        .collect(Collectors.joining(", "));
                aggregated.setAggregatedChaDetails(chaDetails);

        // Aggregate BE numbers
        String beNumbers = records.stream()
                .map(ExcelRecord::getBeNumber)
                .filter(Objects::nonNull)
                .filter(num -> !num.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedBeNumbers(beNumbers);

        // Aggregate BE dates
        String beDates = records.stream()
                .map(ExcelRecord::getBeDate)
                .filter(Objects::nonNull)
                .filter(date -> !date.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedBeDates(beDates);

        // Aggregate descriptions
        String descriptions = records.stream()
                .map(ExcelRecord::getFullItemDescription)
                .filter(Objects::nonNull)
                .filter(desc -> !desc.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedDescriptions(descriptions);

        // Aggregate BCD rates
        String bcdRates = records.stream()
                .map(ExcelRecord::getBcdRate)
                .filter(Objects::nonNull)
                .filter(rate -> !rate.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedBcdRates(bcdRates);

        // Aggregate IGST rates
        String igstRates = records.stream()
                .map(ExcelRecord::getIgstRate)
                .filter(Objects::nonNull)
                .filter(rate -> !rate.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedIgstRates(igstRates);

        // Aggregate effective rates
        String effectiveRates = records.stream()
                .map(ExcelRecord::getEffectiveRateOfDuty)
                .filter(Objects::nonNull)
                .filter(rate -> !rate.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedEffectiveRates(effectiveRates);

        // Aggregate Sr. Nos
        String srNos = records.stream()
                .map(ExcelRecord::getSrNo)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        aggregated.setAggregatedSrNos(srNos);

        // Sum up numerical values
        BigDecimal totalDifferentialDuty = records.stream()
                .map(ExcelRecord::getDifferentialDuty)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregated.setTotalDifferentialDuty(totalDifferentialDuty);

        BigDecimal totalAssessableValue = records.stream()
                .map(ExcelRecord::getAssessableValueAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregated.setTotalAssessableValue(totalAssessableValue);

        BigDecimal totalDutyPaid = records.stream()
                .map(ExcelRecord::getTotalDutyPaidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregated.setTotalDutyPaid(totalDutyPaid);

        BigDecimal totalDutyPayable = records.stream()
                .map(ExcelRecord::getDutyPayable)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregated.setTotalDutyPayable(totalDutyPayable);

        return aggregated;
    }
}