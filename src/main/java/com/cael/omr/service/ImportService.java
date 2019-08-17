package com.cael.omr.service;

import com.cael.omr.thread.ImportThread;
import com.cael.omr.utils.CsvUtils;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ImportService {

    @Value("${thread.numOfthreads}")
    private int numOfthreads;

    @Value("${import.fileExtend}")
    private String fileExtend;

    @Value("${import.ftpHostDir:#{null}}")
    private String ftpHostDir;

    @Value("${import.backup:#{null}}")
    private String backup;

    @Autowired
    private InfluxDbService influxDbService;

    public void executeAsynchronously() throws InterruptedException {
        log.info("--------executeAsynchronously------");

        File folder = new File(ftpHostDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length <= 0)
            return;


        boolean hasData = false;
        long startTime = System.currentTimeMillis();
        log.info("Begin import " + listOfFiles.length);
        CsvUtils.setBackup(backup);


        List<String> originalList = new ArrayList<String>();

        String fileName;
        for (int i = 0; i < listOfFiles.length; i++) {
            fileName = listOfFiles[i].getName();
            if (listOfFiles[i].isFile()) {
                log.info("File " + fileName);
                if (StringUtils.endsWith(fileName.toLowerCase(), fileExtend)) {
                    try {
                        hasData = true;
                        originalList.add(fileName);
                    } catch (Exception ex) {
                        log.error("ERROR Exception readUseCSVReader: ", ex);
                    }
                }
            } else if (listOfFiles[i].isDirectory()) {
                log.warn("Directory " + fileName);
            }
        }


        int partitionSize = IntMath.divide(originalList.size(), numOfthreads, RoundingMode.UP);

        log.info("partitionSize: {}", partitionSize);

        List<List<String>> partitions = Lists.partition(originalList, partitionSize);

        List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

        ExecutorService taskExecutor = Executors.newFixedThreadPool(numOfthreads);

        for (List<String> partition : partitions) {
            ImportThread it = new ImportThread(partition, influxDbService, ftpHostDir);
            todo.add(Executors.callable(it));
        }

        taskExecutor.invokeAll(todo);


        if (hasData) {
            log.info("Time to processReadFile: " + (System.currentTimeMillis() - startTime) + " ms");
        }


    }

}
