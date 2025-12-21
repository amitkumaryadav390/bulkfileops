# Aggregation Example

## Original CSV Data:
```
Sr. No.,BE Number,BE Date,Importer Name,ADDRESS,Eight Digit HS Code,Full Item Description,Assessable Value Amount,BCD Rate,IGST Rate,Total Duty Paid Amount,Effective Rate of duty (BCD@35% + SWS@10% + IGST@28%),Duty Payable,Differential Duty
1,8568569,03-06-2021 00:00,ABC,S ABD NT,85444999,Machines,8658,0%,0,300,77.28%,6690.9024,6390.9024
2,8568569,03-06-2021 00:00,ABC,S ABD NT,83249099,Chemicals,69475,5%,5,8567,77.28%,53690.28,45123.28
3,9002685,03-08-2021 00:00,UTT,S TURO FMN,69852480,Banana,12856,7.50%,18,5703,77.28%,9935.1168,4232.1168
```

## After Aggregation by Importer Name:

### ABC (2 records combined):
- **Importer Name**: ABC
- **Address**: S ABD NT (first address found)
- **HS Codes**: 85444999, 83249099 (comma-separated unique codes)
- **Descriptions**: Machines, Chemicals (comma-separated)
- **BCD Rates**: 0%, 5% (comma-separated unique rates)
- **IGST Rates**: 0, 5 (comma-separated unique rates)
- **Total Differential Duty**: 51513.1824 (6390.9024 + 45123.28)
- **Total Assessable Value**: 78133 (8658 + 69475)
- **BE Numbers**: 8568569 (same number, so appears once)
- **BE Dates**: 03-06-2021 00:00 (same date, so appears once)

### UTT (1 record):
- **Importer Name**: UTT
- **Address**: S TURO FMN
- **HS Codes**: 69852480
- **Descriptions**: Banana
- **BCD Rates**: 7.50%
- **IGST Rates**: 18
- **Total Differential Duty**: 4232.1168
- **Total Assessable Value**: 12856
- **BE Numbers**: 9002685
- **BE Dates**: 03-08-2021 00:00

## Result:
Instead of generating 3 individual documents, the system will generate 2 documents:
1. **Aggregated_1_ABC.docx** - containing aggregated data for ABC importer
2. **Aggregated_2_UTT.docx** - containing data for UTT importer

Each document will have placeholders replaced with the appropriate aggregated values.