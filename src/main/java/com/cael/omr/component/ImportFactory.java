package com.cael.omr.component;

import com.cael.omr.service.BaseImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImportFactory {

    @Autowired
    private Map<String, BaseImportService> serviceMap;

    public BaseImportService getService(String type) {
        return serviceMap.get(type);
    }
}
