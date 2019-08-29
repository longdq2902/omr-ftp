package com.cael.omr.service.impl;

import com.cael.omr.service.BaseImportService;
import com.cael.omr.thread.CallableWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ImportFutureService extends BaseImportService {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void execute(List<String> originalList) {
        List<Future<String>> futureList = new ArrayList<>();


        int length = originalList.size();
        String fileName;
        for (int i = 0; i < length; i++) {
            try {
                fileName = originalList.get(i);
                CallableWorker callableTask = new CallableWorker(fileName, getInfluxDbService(), getFtpHostDir());
                Future<String> result = taskExecutor.submit(callableTask);
                futureList.add(result);
            } catch (Exception ex) {
                getInfoTask();
//                log.info("----Exception----");
                i--;
            }
        }

        finishTask(futureList);
    }

    private void getInfoTask() {
//        log.debug("----taskExecutor: getMaxPoolSize: {} - getPoolSize: {} - getActiveCount: {} - getQueue: {}",
//                taskExecutor.getMaxPoolSize(), taskExecutor.getPoolSize(),
//                taskExecutor.getActiveCount(), taskExecutor.getThreadPoolExecutor().getQueue().size());
    }

    private void finishTask(List<Future<String>> futureList) {
        StringBuilder msg = new StringBuilder();
        msg.setLength(0);

        HashMap<String,Integer> hsm = new HashMap<>(futureList.size());

        Integer count;
        String fileName;
        for (Future<String> future : futureList) {
            try {
                fileName = future.get();
                count = hsm.get(fileName);

                if(count == null){
                    hsm.put(fileName,1);
                }else{
                    log.warn("Found duplicate file {} - {}",fileName,count);
                    count++;
                    hsm.put(fileName,count);
                }

                msg.append(future.get()).append("#####");
            } catch (Exception e) {
            }
        }
        log.info("finishTask with {}", futureList.size());
    }
}
