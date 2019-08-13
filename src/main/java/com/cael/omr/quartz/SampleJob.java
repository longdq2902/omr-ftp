package com.cael.omr.quartz;

import com.cael.omr.ftpclient.FTPService;
import com.cael.omr.service.FTPClientService;
import com.cael.omr.utils.AppConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class SampleJob implements Job {

    @Autowired
    AppConfigurator appConfigurator;
    @Autowired
    FTPService ftpService;

    @Autowired
    FTPClientService ftpClientService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            ftpClientService.uploadFolderToRemoteServer(appConfigurator.getSrcFolder());

            log.info("Finish upload file");

        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }
}
