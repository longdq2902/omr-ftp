package com.cael.omr;

import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.InetAddressUtil;
import com.cael.omr.utils.PassTranformerUtil;
import com.cael.omr.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableConfigurationProperties({AppConfigurator.class, InfluxDBProperties.class})
@Configuration
@Slf4j
public class OmrApplication {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String... args) throws Exception {

        genLicense(args);

//        SpringApplication.run(OmrApplication.class, args);genLicense
    }

    private static void genLicense(String... args) throws Exception {
        String genLicense = Utils.getValueOfArgs("genLicense", "", args);
        if ("true".equalsIgnoreCase(genLicense)) {
            String macAddress = Utils.getValueOfArgs("clientMac", "", args);
            String startDate = Utils.getValueOfArgs("startDate", "", args);
            String endDate = Utils.getValueOfArgs("endDate", "", args);

            if (StringUtils.isBlank(macAddress)) {
                macAddress = InetAddressUtil.getMacAddress();
            }

            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now();
            start = start.minusDays(1);
            if (StringUtils.isBlank(startDate)) {
                startDate = start.format(formatter);
            }

            if (StringUtils.isBlank(endDate)) {
                end = start.plusDays(32);
                endDate = end.format(formatter);
            }

            String input = String.format("%s|%s|%s", macAddress, startDate, endDate);

            String encrypt = PassTranformerUtil.encrypt(input);
            log.info(encrypt);
        }
    }

    private static void test() throws Exception {
        String macAdd = InetAddressUtil.getMacAddress();
        log.info(macAdd);

        LocalDate start = LocalDate.now();
        start = start.minusDays(1);
        LocalDate end = start.plusDays(32);

        String input = String.format("%s|%s|%s", macAdd, start.format(formatter), end.format(formatter));

        String encrypt = PassTranformerUtil.encrypt(input);
        log.info(encrypt);

        String decrypt = PassTranformerUtil.decrypt("00809dc331c9722437ad99f9013ba7445b29a7caaabb446275bf1e38b80abf2d192e3f14a82df19e5e804fc70cbd7026");
        log.info(decrypt);
    }
}

