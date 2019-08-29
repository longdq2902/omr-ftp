package com.cael.omr.config;

import com.cael.omr.quartz.MailJob;
import com.cael.omr.quartz.ScheduleJob;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzSubmitJobs {
    private static final String CRON_EVERY_FIVE_MINUTES = "0 0/5 * ? * * *";

    @Value("${job.frequency}")
    private Long frequenceJob;

    @Value("${job.frequenceReadMail}")
    private Long frequenceReadMail;

    @Bean(name = "scheduleJob")
    public JobDetailFactoryBean jobMemberStats() {
        return QuartzConfig.createJobDetail(ScheduleJob.class, "ScheduleJob");
    }

    @Bean(name = "scheduleJobTrigger")
    public SimpleTriggerFactoryBean triggerMemberStats(@Qualifier("scheduleJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, frequenceJob * 1000, "scheduleJob Trigger");
    }

    @Bean(name = "mailJob")
    public JobDetailFactoryBean jobMemberClassStats() {
        return QuartzConfig.createJobDetail(MailJob.class, "MailJob");
    }

    @Bean(name = "mailJobTrigger")
    public SimpleTriggerFactoryBean triggerMemberClassStats(@Qualifier("mailJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, frequenceReadMail * 1000, "MailJob Trigger");
    }
}
