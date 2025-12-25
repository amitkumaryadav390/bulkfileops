package com.example.filecreator.model;

import java.math.BigDecimal;

public class ExcelRecord {
    private Integer srNo;
    private String beNumber;
    private String beDate;
    private String importerName;
    private String address;
    private String eightDigitHsCode;
    private String fullItemDescription;
    private BigDecimal assessableValueAmount;
    private String bcdRate;
    private String igstRate;
    private BigDecimal totalDutyPaidAmount;
    private String effectiveRateOfDuty;
    private BigDecimal dutyPayable;
    private BigDecimal differentialDuty;
    private String chaDetails;

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public String getBeNumber() {
        return beNumber;
    }

    public void setBeNumber(String beNumber) {
        this.beNumber = beNumber;
    }

    public String getBeDate() {
        return beDate;
    }

    public void setBeDate(String beDate) {
        this.beDate = beDate;
    }

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

    public String getEightDigitHsCode() {
        return eightDigitHsCode;
    }

    public void setEightDigitHsCode(String eightDigitHsCode) {
        this.eightDigitHsCode = eightDigitHsCode;
    }

    public String getFullItemDescription() {
        return fullItemDescription;
    }

    public void setFullItemDescription(String fullItemDescription) {
        this.fullItemDescription = fullItemDescription;
    }

    public BigDecimal getAssessableValueAmount() {
        return assessableValueAmount;
    }

    public void setAssessableValueAmount(BigDecimal assessableValueAmount) {
        this.assessableValueAmount = assessableValueAmount;
    }

    public String getBcdRate() {
        return bcdRate;
    }

    public void setBcdRate(String bcdRate) {
        this.bcdRate = bcdRate;
    }

    public String getIgstRate() {
        return igstRate;
    }

    public void setIgstRate(String igstRate) {
        this.igstRate = igstRate;
    }

    public BigDecimal getTotalDutyPaidAmount() {
        return totalDutyPaidAmount;
    }

    public void setTotalDutyPaidAmount(BigDecimal totalDutyPaidAmount) {
        this.totalDutyPaidAmount = totalDutyPaidAmount;
    }

    public String getEffectiveRateOfDuty() {
        return effectiveRateOfDuty;
    }

    public void setEffectiveRateOfDuty(String effectiveRateOfDuty) {
        this.effectiveRateOfDuty = effectiveRateOfDuty;
    }

    public BigDecimal getDutyPayable() {
        return dutyPayable;
    }

    public void setDutyPayable(BigDecimal dutyPayable) {
        this.dutyPayable = dutyPayable;
    }

    public BigDecimal getDifferentialDuty() {
        return differentialDuty;
    }

    public void setDifferentialDuty(BigDecimal differentialDuty) {
        this.differentialDuty = differentialDuty;
    }

    public String getChaDetails() {
        return chaDetails;
    }

    public void setChaDetails(String chaDetails) {
        this.chaDetails = chaDetails;
    }

    @Override
    public String toString() {
        return "ExcelRecord{" +
                "srNo=" + srNo +
                ", beNumber='" + beNumber + '\'' +
                ", beDate='" + beDate + '\'' +
                ", importerName='" + importerName + '\'' +
                ", address='" + address + '\'' +
                ", eightDigitHsCode='" + eightDigitHsCode + '\'' +
                ", fullItemDescription='" + fullItemDescription + '\'' +
                ", assessableValueAmount=" + assessableValueAmount +
                ", bcdRate='" + bcdRate + '\'' +
                ", igstRate='" + igstRate + '\'' +
                ", totalDutyPaidAmount=" + totalDutyPaidAmount +
                ", effectiveRateOfDuty='" + effectiveRateOfDuty + '\'' +
                ", dutyPayable=" + dutyPayable +
                ", differentialDuty=" + differentialDuty +
                ", chaDetails='" + chaDetails + '\'' +
                '}';
    }
}
