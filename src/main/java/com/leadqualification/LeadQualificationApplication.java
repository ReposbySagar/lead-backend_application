package com.leadqualification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;

@SpringBootApplication(exclude = { 
    SystemMetricsAutoConfiguration.class,
    TomcatMetricsAutoConfiguration.class 
})
public class LeadQualificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeadQualificationApplication.class, args);
    }
}
