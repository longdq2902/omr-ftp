package com.cael.omr.thread;

import com.cael.omr.service.InfluxDbService;
import com.cael.omr.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.Callable;

@Slf4j
public class CallableWorker implements Callable<String> {

    private InfluxDbService influxDbService;
    private String ftpHostDir;

    private String fileName;

//    public CallableWorker(String name) {
//        this.fileName = name;
//    }

    public CallableWorker(String fileName, InfluxDbService influxDbService, String ftpHostDir) {
        this.fileName = fileName;
        this.influxDbService = influxDbService;
        this.ftpHostDir = ftpHostDir;
    }

    @Override
    public String call() throws Exception {
        process();
//        String message = String.format("CallableWorker name: %s is Done", fileName);
        return fileName;
    }

    private void process() {
        try {
//            log.info("--- {} Begin process {} ", Thread.currentThread().getName(), fileName);
            String filePath = String.format("%s%s%s", ftpHostDir, File.separator, fileName);
            CsvUtils.readUseCSVReader(filePath, influxDbService);
//            log.info("{} process done {}", Thread.currentThread().getName(), fileName);
        } catch (Exception ex) {
            log.error(String.format("ERROR process {%s}: ", fileName), ex);
        }
    }

}
