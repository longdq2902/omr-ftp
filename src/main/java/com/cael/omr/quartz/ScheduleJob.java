package com.cael.omr.quartz;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.component.ComponentFactory;
import com.cael.omr.config.ThreadConfig;
import com.cael.omr.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@DisallowConcurrentExecution
public class ScheduleJob extends BaseJob implements Job {

    @Autowired
    private ComponentFactory componentFactory;

    @Value("${job.type}")
    private String type;

    @Autowired
    private ThreadConfig config;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        if("true".equalsIgnoreCase(config.getDebug())) {
            CsvUtils.setDebug("true");
            log.info("---ScheduleJob----");
        }

        BaseComponent component = componentFactory.getComponent(type);

        if (component == null) {
            log.error("Not found component with type {}", type);
            return;
        }

        try {
            if (!checkLicense()) {
                log.error("No have license...");
                return;
            }
            component.process();
        } catch (Exception ex) {
            log.error("ERROR checkLicense: ", ex);
            return;
        }
    }
}
