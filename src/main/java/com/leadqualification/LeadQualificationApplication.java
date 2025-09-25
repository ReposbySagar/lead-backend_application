package com.leadqualification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { SystemMetricsAutoConfiguration.class })
public class LeadQualificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeadQualificationApplication.class, args);
    }
}

