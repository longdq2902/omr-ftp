package com.cael.omr.quartz;

import com.cael.omr.component.BaseComponent;
import com.cael.omr.component.ComponentFactory;
import com.cael.omr.service.ImportService;
import com.cael.omr.utils.InetAddressUtil;
import com.cael.omr.utils.MyFileUtils;
import com.cael.omr.utils.PassTranformerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@DisallowConcurrentExecution
public class ScheduleJob implements Job {

    @Autowired
    private ComponentFactory componentFactory;

    @Value("${job.type}")
    private String type;

    @Value("${app.key:''}")
    private String key;

    @Value("${app.keyFile:''}")
    private String keyFile;

    public static final String KEY_FOREVER = "For@Ever";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    private ImportService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.debug("---type {}", type);


        BaseComponent component = componentFactory.getComponent(type);

        if (component == null) {
            log.error("Not found component with type {}", type);
            return;
        }

        try {
            if (!checkLicense()) {
                log.error("No have license...");
                return;
            }
            component.process();
        } catch (Exception ex) {
            log.error("ERROR checkLicense: ", ex);
            return;
        }


    }

    private boolean checkLicense() throws Exception {
        String key = null;
        if (StringUtils.isNotBlank(this.keyFile) && !this.keyFile.trim().equalsIgnoreCase("''")) {
            List<String> lst = MyFileUtils.readTxtFile(this.keyFile);
            if (lst != null && lst.size() > 0) {
                key = lst.stream().filter(s -> StringUtils.startsWith(s, "app.key="))
                        .map(s -> s.replace("app.key=", ""))
                        .findFirst()
                        .orElse(null);
            }
        }

        if (StringUtils.isBlank(key))
            key = this.key;

        if (StringUtils.isBlank(key) || key.trim().equalsIgnoreCase("''"))
            return false;

        key = key.trim();

        if (key.equalsIgnoreCase(PassTranformerUtil.encrypt(KEY_FOREVER)))
            return true;

        //Decode key
        String decode = PassTranformerUtil.decrypt(key);

        if (decode == null || !decode.contains("|") || decode.split("\\|").length < 3)
            return false;

        log.info("Decode: {}", decode);

        String[] tmp = decode.split("\\|");


        //Check Mac
        String realMac = InetAddressUtil.getMacAddress();
        if (!realMac.equalsIgnoreCase(tmp[0])) return false;

        //Check start
        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.parse(tmp[1], formatter);
        LocalDate endDate = LocalDate.parse(tmp[2], formatter);

        if (startDate == null || endDate == null) return false;

        return (startDate.isBefore(now) || startDate.isEqual(now))
                && (endDate.isAfter(now) || endDate.isEqual(now));
    }
}
