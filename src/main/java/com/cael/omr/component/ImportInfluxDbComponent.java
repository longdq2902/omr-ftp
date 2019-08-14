package com.cael.omr.component;

import com.cael.omr.service.InfluxDbService;
import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class ImportInfluxDbComponent {
    @Autowired
    private InfluxDbService influxDbService;

    @Autowired
    AppConfigurator appConfigurator;

    public void processImport() {
        long startTime = System.currentTimeMillis();
        boolean hasData = false;
        File folder = new File(appConfigurator.getFtpHostDir());
        File[] listOfFiles = folder.listFiles();

        String fileName, filePath;
        for (int i = 0; i < listOfFiles.length; i++) {
            fileName = listOfFiles[i].getName();
            if (listOfFiles[i].isFile()) {
                log.info("File " + fileName);
                if (StringUtils.endsWith(fileName.toLowerCase(), appConfigurator.getFileExtend())) {
                    try {
                        hasData = true;
                        filePath = String.format("%s%s%s", appConfigurator.getFtpHostDir(), File.separator, fileName);
                        CsvUtils.readUseCSVReader(filePath, influxDbService);
                    } catch (Exception ex) {
                        log.error("ERROR Exception readUseCSVReader: ", ex);
                    }
                }
            } else if (listOfFiles[i].isDirectory()) {
                log.warn("Directory " + fileName);
            }
        }
        if (hasData) {
            log.info("Time to processReadFile: " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }
}
