package com.cael.omr.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties

public class AppConfigurator {


    public String srcFolder;
    public String desFolder;
    public String tempFolder;

    public String getSrcFolder() {
        return srcFolder;
    }

    public void setSrcFolder(String srcFolder) {
        this.srcFolder = srcFolder;
    }

    public String getDesFolder() {
        return desFolder;
    }

    public void setDesFolder(String desFolder) {
        this.desFolder = desFolder;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public AppConfigurator() {
    }
}
