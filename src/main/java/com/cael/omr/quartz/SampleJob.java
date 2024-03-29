package com.cael.omr.quartz;

import com.cael.omr.MainController;
import com.cael.omr.exception.FTPErrors;
import com.cael.omr.ftpclient.FTPService;
import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.MyFileUtils;
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

public class SampleJob implements Job {

    @Autowired
    AppConfigurator appConfigurator;
    @Autowired
    FTPService ftpService;

    private final static Logger logger = LoggerFactory.getLogger(SampleJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            List<String> myFiles = new ArrayList<>();
            myFiles= MyFileUtils.getAllFilesByList(appConfigurator.getSrcFolder());

            logger.info("Start upload file");

            ftpService.connectToFTP(appConfigurator.getHost(), appConfigurator.getUser(), appConfigurator.getPassword());
            logger.info("Login successfully to FTP server ");

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
                    logger.debug("Deleted file : " + path);
                }
            }

            logger.info("Finish upload file");

        } catch (FTPErrors ftpErrors) {
            logger.error(ftpErrors.getMessage());
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }







    }
}
