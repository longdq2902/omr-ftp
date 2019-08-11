package com.cael.omr;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication

public class OmrApplication {
    private final static Logger logger = LoggerFactory.getLogger(OmrApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(OmrApplication.class, args);


    }


}

