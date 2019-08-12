package com.cael.omr;

import com.cael.omr.utils.AppConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@EnableConfigurationProperties(AppConfigurator.class)
@Configuration

public class OmrApplication {
    public static void main(String[] args) {
        SpringApplication.run(OmrApplication.class, args);
    }


}

