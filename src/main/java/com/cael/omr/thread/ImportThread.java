package com.cael.omr.thread;

import com.cael.omr.service.InfluxDbService;
import com.cael.omr.utils.CsvUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
@Data
public class ImportThread implements Runnable {

    private InfluxDbService influxDbService;
    private String ftpHostDir;

    private List<String> lis;

    public ImportThread(List<String> list, InfluxDbService influxDbService, String ftpHostDir) {
        this.lis = list;
        this.influxDbService = influxDbService;
        this.ftpHostDir = ftpHostDir;
    }

    @Override
    public void run() {
        String filePath;
        for (String fileName : lis) {
            try {
                log.info("{} process {}", Thread.currentThread().getId(), fileName);
                filePath = String.format("%s%s%s", ftpHostDir, File.separator, fileName);
                CsvUtils.readUseCSVReader(filePath, influxDbService);
                log.warn("--------done {} process {}", Thread.currentThread().getId(), fileName);
            } catch (Exception ex) {
                log.error(String.format("ERROR process {%s}: ", fileName), ex);
            }
        }
    }
}
