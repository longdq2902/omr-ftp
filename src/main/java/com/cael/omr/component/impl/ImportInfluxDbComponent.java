package com.cael.omr.component.impl;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.service.InfluxDbService;
import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class ImportInfluxDbComponent implements BaseComponent {
    @Autowired
    private InfluxDbService influxDbService;

    @Autowired
    AppConfigurator appConfigurator;
    @Value("${import.fileExtend}")

    private String fileExtend;

    @Value("${import.ftpHostDir}")
    private String ftpHostDir;

    @Value("${import.backup}")
    private String backup;

    @Override
    public void process() {
        File folder = new File(ftpHostDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length <= 0)
            return;

        boolean hasData = false;
        long startTime = System.currentTimeMillis();
        log.info("Begin import " + listOfFiles.length);
        CsvUtils.setBackup(backup);

        String fileName, filePath;
        for (int i = 0; i < listOfFiles.length; i++) {
            fileName = listOfFiles[i].getName();
            if (listOfFiles[i].isFile()) {
                log.info("File " + fileName);
                if (StringUtils.endsWith(fileName.toLowerCase(), fileExtend)) {
                    try {
                        hasData = true;
                        filePath = String.format("%s%s%s", ftpHostDir, File.separator, fileName);
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
