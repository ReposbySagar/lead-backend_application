package com.leadqualification.util;

import com.leadqualification.entity.Lead;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

    private static final String[] EXPECTED_HEADERS = {
        "name", "role", "company", "industry", "location", "linkedin_bio"
    };

    public List<Lead> processLeadsCsv(MultipartFile file) throws IOException, CsvException {
        logger.info("Processing CSV file: {}", file.getOriginalFilename());

        List<Lead> leads = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            String[] headers = records.get(0);
            validateHeaders(headers);

            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                try {
                    Lead lead = createLeadFromRecord(record, i + 1);
                    if (lead != null) {
                        leads.add(lead);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to process record at line {}: {}", i + 1, e.getMessage());
                }
            }
        }

        logger.info("Successfully processed {} leads from CSV", leads.size());
        return leads;
    }

    private void validateHeaders(String[] headers) {
        if (headers.length < EXPECTED_HEADERS.length) {
            throw new IllegalArgumentException(
                "CSV must contain at least " + EXPECTED_HEADERS.length + " columns: " + 
                String.join(", ", EXPECTED_HEADERS)
            );
        }

        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            String expectedHeader = EXPECTED_HEADERS[i];
            String actualHeader = headers[i].trim().toLowerCase().replace(" ", "_");
            
            if (!expectedHeader.equals(actualHeader)) {
                logger.warn("Header mismatch at position {}: expected '{}', found '{}'", 
                           i, expectedHeader, actualHeader);
            }
        }
    }

    private Lead createLeadFromRecord(String[] record, int lineNumber) {
        if (record.length < EXPECTED_HEADERS.length) {
            logger.warn("Insufficient columns in record at line {}", lineNumber);
            return null;
        }

        String name = StringUtils.trimToNull(record[0]);
        if (name == null) {
            logger.warn("Name is required but missing at line {}", lineNumber);
            return null;
        }

        String role = StringUtils.trimToNull(record[1]);
        String company = StringUtils.trimToNull(record[2]);
        String industry = StringUtils.trimToNull(record[3]);
        String location = StringUtils.trimToNull(record[4]);
        String linkedinBio = StringUtils.trimToNull(record[5]);

        return new Lead(name, role, company, industry, location, linkedinBio);
    }

    public boolean isValidCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        return filename.toLowerCase().endsWith(".csv");
    }

    public String generateCsvContent(List<Lead> leads) {
        StringBuilder csv = new StringBuilder();
        
        csv.append("name,role,company,industry,location,linkedin_bio,score,intent,reasoning\n");
        
        for (Lead lead : leads) {
            csv.append(escapeCsvValue(lead.getName())).append(",");
            csv.append(escapeCsvValue(lead.getRole())).append(",");
            csv.append(escapeCsvValue(lead.getCompany())).append(",");
            csv.append(escapeCsvValue(lead.getIndustry())).append(",");
            csv.append(escapeCsvValue(lead.getLocation())).append(",");
            csv.append(escapeCsvValue(lead.getLinkedinBio())).append(",");
            csv.append(lead.getTotalScore() != null ? lead.getTotalScore() : "").append(",");
            csv.append(lead.getIntent() != null ? lead.getIntent().getDisplayName() : "").append(",");
            csv.append(escapeCsvValue(lead.getReasoning()));
            csv.append("\n");
        }
        
        return csv.toString();
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}

