package com.cael.omr.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties
@Getter
@Setter

public class AppConfigurator {


    public String srcFolder;
    public String desFolder;
    public String tempFolder;
    public String ftpHostDir;
    public String host;
    public String user;
    public String password;



    public AppConfigurator() {
    }
}
