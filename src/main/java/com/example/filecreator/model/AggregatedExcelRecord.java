package com.example.filecreator.model;

import java.math.BigDecimal;

public class AggregatedExcelRecord {
    private String importerName;
    private String address; // First address found for this importer
    private String aggregatedHsCodes; // Comma-separated unique HS codes
    private String aggregatedBeNumbers; // Comma-separated BE numbers
    private String aggregatedBeDates; // Comma-separated BE dates
    private String aggregatedDescriptions; // Comma-separated descriptions
    private String aggregatedBcdRates; // Comma-separated BCD rates
    private String aggregatedIgstRates; // Comma-separated IGST rates
    private BigDecimal totalDifferentialDuty; // Sum of all differential duties
    private BigDecimal totalAssessableValue; // Sum of assessable values
    private BigDecimal totalDutyPaid; // Sum of total duty paid
    private BigDecimal totalDutyPayable; // Sum of duty payable
    private String aggregatedEffectiveRates; // Comma-separated effective rates
    private String aggregatedSrNos; // Comma-separated Sr. Nos
    private String aggregatedChaDetails; // Comma-separated CHA details

    // Getters and Setters
    public String getImporterName() {
        return importerName;
    }

    public void setImporterName(String importerName) {
        this.importerName = importerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAggregatedHsCodes() {
        return aggregatedHsCodes;
    }

    public void setAggregatedHsCodes(String aggregatedHsCodes) {
        this.aggregatedHsCodes = aggregatedHsCodes;
    }

    public String getAggregatedBeNumbers() {
        return aggregatedBeNumbers;
    }

    public void setAggregatedBeNumbers(String aggregatedBeNumbers) {
        this.aggregatedBeNumbers = aggregatedBeNumbers;
    }

    public String getAggregatedBeDates() {
        return aggregatedBeDates;
    }

    public void setAggregatedBeDates(String aggregatedBeDates) {
        this.aggregatedBeDates = aggregatedBeDates;
    }

    public String getAggregatedDescriptions() {
        return aggregatedDescriptions;
    }

    public void setAggregatedDescriptions(String aggregatedDescriptions) {
        this.aggregatedDescriptions = aggregatedDescriptions;
    }

    public String getAggregatedBcdRates() {
        return aggregatedBcdRates;
    }

    public void setAggregatedBcdRates(String aggregatedBcdRates) {
        this.aggregatedBcdRates = aggregatedBcdRates;
    }

    public String getAggregatedIgstRates() {
        return aggregatedIgstRates;
    }

    public void setAggregatedIgstRates(String aggregatedIgstRates) {
        this.aggregatedIgstRates = aggregatedIgstRates;
    }

    public BigDecimal getTotalDifferentialDuty() {
        return totalDifferentialDuty;
    }

    public void setTotalDifferentialDuty(BigDecimal totalDifferentialDuty) {
        this.totalDifferentialDuty = totalDifferentialDuty;
    }

    public BigDecimal getTotalAssessableValue() {
        return totalAssessableValue;
    }

    public void setTotalAssessableValue(BigDecimal totalAssessableValue) {
        this.totalAssessableValue = totalAssessableValue;
    }

    public BigDecimal getTotalDutyPaid() {
        return totalDutyPaid;
    }

    public void setTotalDutyPaid(BigDecimal totalDutyPaid) {
        this.totalDutyPaid = totalDutyPaid;
    }

    public BigDecimal getTotalDutyPayable() {
        return totalDutyPayable;
    }

    public void setTotalDutyPayable(BigDecimal totalDutyPayable) {
        this.totalDutyPayable = totalDutyPayable;
    }

    public String getAggregatedEffectiveRates() {
        return aggregatedEffectiveRates;
    }

    public void setAggregatedEffectiveRates(String aggregatedEffectiveRates) {
        this.aggregatedEffectiveRates = aggregatedEffectiveRates;
    }

    public String getAggregatedSrNos() {
        return aggregatedSrNos;
    }

    public void setAggregatedSrNos(String aggregatedSrNos) {
        this.aggregatedSrNos = aggregatedSrNos;
    }

    public String getAggregatedChaDetails() {
        return aggregatedChaDetails;
    }

    public void setAggregatedChaDetails(String aggregatedChaDetails) {
        this.aggregatedChaDetails = aggregatedChaDetails;
    }

    @Override
    public String toString() {
        return "AggregatedExcelRecord{" +
                "importerName='" + importerName + '\'' +
                ", address='" + address + '\'' +
                ", aggregatedHsCodes='" + aggregatedHsCodes + '\'' +
                ", totalDifferentialDuty=" + totalDifferentialDuty +
                ", totalAssessableValue=" + totalAssessableValue +
                ", aggregatedChaDetails='" + aggregatedChaDetails + '\'' +
                '}';
    }
}