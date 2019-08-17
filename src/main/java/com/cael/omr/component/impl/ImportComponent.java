package com.cael.omr.component.impl;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImportComponent implements BaseComponent {
    @Autowired
    private ImportService service;


    @Override
    public void process() {
        try {
            service.executeAsynchronously();
        } catch (InterruptedException e) {
            log.error("ERROR: ", e);
        }
    }
}
