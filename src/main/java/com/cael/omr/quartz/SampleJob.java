package com.cael.omr.quartz;

import com.cael.omr.exception.FTPErrors;
import com.cael.omr.ftpclient.FTPService;
import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.MyFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SampleJob implements Job {

    @Autowired
    AppConfigurator appConfigurator;
    @Autowired
    FTPService ftpService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            List<String> myFiles = new ArrayList<>();
            myFiles= MyFileUtils.getAllFilesByList(appConfigurator.getSrcFolder());

            log.info("Start upload file");

            ftpService.connectToFTP(appConfigurator.getHost(), appConfigurator.getUser(), appConfigurator.getPassword());
            log.info("Login successfully to FTP server ");

            if(myFiles != null && myFiles.size() >0) {
                for (String path : myFiles) {
                    System.out.println(path);
                    File f = new File(path);
                    ftpService.uploadFileToFTP(f, appConfigurator.ftpHostDir,f.getName());

                }
            }


            ftpService.disconnectFTP();

            if(myFiles != null) {
                for (String path : myFiles) {
                    Files.delete(Paths.get(path));
                    log.debug("Deleted file : " + path);
                }
            }

            log.info("Finish upload file");

        } catch (FTPErrors ftpErrors) {
            log.error(ftpErrors.getMessage());
        }
        catch (IOException e){
            log.error(e.getMessage());
        }







    }
}
