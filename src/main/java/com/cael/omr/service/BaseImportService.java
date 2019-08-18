package com.cael.omr.service;

import com.cael.omr.utils.CsvUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public abstract class BaseImportService {
    @Value("${import.fileExtend}")
    private String fileExtend;

    @Value("${import.ftpHostDir:#{null}}")
    private String ftpHostDir;

    @Value("${import.backup:#{null}}")
    private String backup;

    @Autowired
    private InfluxDbService influxDbService;

    public void process() throws InterruptedException {
        File folder = new File(ftpHostDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length <= 0) {
            return;
        }

        //Init
        CsvUtils.setBackup(backup);
        log.info("Begin import " + listOfFiles.length);

        //Filter list
        List<String> originalList = createOrigList(listOfFiles);

        if (originalList.size() > 0) {
            long startTime = System.currentTimeMillis();
            execute(originalList);
            //Split into task
            log.info("Time to processReadFile: " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    private List<String> createOrigList(File[] listOfFiles) {
        List<String> originalList = new ArrayList<String>();

        String fileName;
        for (int i = 0; i < listOfFiles.length; i++) {
            fileName = listOfFiles[i].getName();
            if (listOfFiles[i].isFile()) {
                log.info("File " + fileName);
                if (StringUtils.endsWith(fileName.toLowerCase(), fileExtend)) {
                    try {
                        originalList.add(fileName);
                    } catch (Exception ex) {
                        log.error("ERROR Exception readUseCSVReader: ", ex);
                    }
                }
            } else if (listOfFiles[i].isDirectory()) {
                log.warn("Directory " + fileName);
            }
        }
        return originalList;
    }

    public abstract void execute(List<String> originalList);

}
