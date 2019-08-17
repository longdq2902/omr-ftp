package com.cael.omr.component.impl;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.service.FTPClientService;
import com.cael.omr.utils.AppConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class FTPComponent implements BaseComponent {

    @Autowired
    AppConfigurator appConfigurator;

    @Autowired
    FTPClientService ftpClientService;

    @Override
    public void process() {
        try {
            ftpClientService.uploadFolderToRemoteServer(appConfigurator.getSrcFolder());
            log.debug("Finish upload file");
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
