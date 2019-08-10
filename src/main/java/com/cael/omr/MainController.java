package com.cael.omr;

import com.cael.omr.exception.FTPErrors;
import com.cael.omr.ftpclient.FTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 *
 */

@RestController
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);
    @Autowired
    private FTPService ftpService;

    @RequestMapping( value = "/upload", method = RequestMethod.GET)
    public void uploadExample() {

        try {
            logger.info("Start upload file");

            ftpService.connectToFTP("192.168.253.209", "cael", "cael@123");
            logger.info("Login successfully");
            ftpService.uploadFileToFTP(new File("C:\\Users\\IronMan\\Pictures\\longdq.png"), "/home/cael/ftp/upload/", "foto.png");
            ftpService.downloadFileFromFTP("/home/cael/ftp/upload//foto.png", "C:\\Users\\IronMan\\Pictures\\kaka.png");

            ftpService.disconnectFTP();

            logger.info("Finish upload file");

        } catch (FTPErrors ftpErrors) {
            logger.error(ftpErrors.getMessage());
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
