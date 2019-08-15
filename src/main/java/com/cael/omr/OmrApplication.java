package com.cael.omr;

import com.cael.omr.utils.AppConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppConfigurator.class, InfluxDBProperties.class})
@Configuration

public class OmrApplication {
    public static void main(String[] args) {
        SpringApplication.run(OmrApplication.class, args);
    }


}

