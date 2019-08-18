package com.cael.omr.service.impl;

import com.cael.omr.service.BaseImportService;
import com.cael.omr.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.util.List;

@Service
@Slf4j
public class ImportSingleService extends BaseImportService {

    @Override
    public void execute(List<String> originalList) {
        String filePath;
        for (String fileName : originalList) {
            filePath = String.format("%s%s%s", getFtpHostDir(), File.separator, fileName);
            try {
                CsvUtils.readUseCSVReader(filePath, getInfluxDbService());
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
