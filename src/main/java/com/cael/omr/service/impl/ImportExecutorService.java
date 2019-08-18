package com.cael.omr.service.impl;

import com.cael.omr.service.BaseImportService;
import com.cael.omr.thread.ImportThread;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ImportExecutorService extends BaseImportService {
    @Value("${thread.numOfthreads}")
    private int numOfthreads;

    @Override
    public void process() throws InterruptedException {
        log.info("ImportExecutorService");
    }

    @Override
    public void execute(List<String> originalList) {
        int partitionSize = IntMath.divide(originalList.size(), numOfthreads, RoundingMode.UP);

        log.info("partitionSize: {}", partitionSize);

        List<List<String>> partitions = Lists.partition(originalList, partitionSize);

        List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

        ExecutorService taskExecutor = Executors.newFixedThreadPool(numOfthreads);

        for (List<String> partition : partitions) {
            ImportThread it = new ImportThread(partition, getInfluxDbService(), getFtpHostDir());
            todo.add(Executors.callable(it));
        }

        try {
            taskExecutor.invokeAll(todo);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
