package com.cael.omr;

import com.cael.omr.utils.AppConfigurator;
import com.cael.omr.utils.InetAddressUtil;
import com.cael.omr.utils.MyFileUtils;
import com.cael.omr.utils.PassTranformerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({AppConfigurator.class, InfluxDBProperties.class})
@Configuration
@Slf4j
public class OmrApplication {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String... args) throws Exception {
        test();

        SpringApplication.run(OmrApplication.class, args);
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

