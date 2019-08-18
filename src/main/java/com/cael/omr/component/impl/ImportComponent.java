package com.cael.omr.component.impl;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.component.ImportFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImportComponent implements BaseComponent {
    @Autowired
    private ImportFactory factory;

    @Value("${import.type:'importSingleService'}")
    private String type;

    @Value("${import.ftpHostDir:#{null}}")
    private String ftpHostDir;

    @Override
    public void process() {
        try {
            factory.getService(type).process();
        } catch (InterruptedException e) {
            log.error("ERROR: ", e);
        }
    }
}
