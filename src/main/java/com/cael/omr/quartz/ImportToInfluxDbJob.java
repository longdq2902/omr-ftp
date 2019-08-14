package com.cael.omr.quartz;

import com.cael.omr.component.ImportInfluxDbComponent;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ImportToInfluxDbJob implements Job {

    @Autowired
    private ImportInfluxDbComponent component;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("-------------Begin import----------");
            component.processImport();
        } catch (Exception ex) {
            log.error("ERROR execute ImportToInfluxDbJob: ", ex);
        }
    }
}
