package com.cael.omr.quartz;

import com.cael.omr.component.impl.ReadMailComponent;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DisallowConcurrentExecution
public class MailJob extends BaseJob implements Job {
    @Autowired
    private ReadMailComponent readMailComponent;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            if (!checkLicense()) {
                log.error("No have license...");
                return;
            }

            readMailComponent.process();
        } catch (Exception ex) {
            log.error("ERROR checkLicense: ", ex);
        }
    }
}
