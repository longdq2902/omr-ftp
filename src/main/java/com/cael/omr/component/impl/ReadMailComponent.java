package com.cael.omr.component.impl;

import com.cael.omr.service.EmailAttachmentReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class ReadMailComponent {

    @Autowired
    EmailAttachmentReceiver emailAttachmentReceiver;

    @Value("${mail.host:'pop.gmail.com'}")
    private String host;

    @Value("${mail.port:'995'}")
    private String port;


    @Value("${mail.user:'app.htaviet@gmail.com'}")
    private String userName;

    @Value("${mail.password:'bjwopumzdrpxikbm'}")
    private String password;

    @Value("${mail.dir:'/Volumes/DATA/tmp/tmp'}")
    private String saveDirectory;

    @Value("${mail.properties.protocol}")
    private String propertiesProtocol;

    @Value("${mail.properties.host}")
    private String propertiesHost;

    @Value("${mail.properties.port}")
    private String propertiesPort;

    @Value("${mail.properties.socketFactory}")
    private String propertiesSocketFactory;

    @Value("${mail.properties.socketFallback}")
    private String propertiesSocketFallback;

    @Value("${mail.properties.socketFactoryPort}")
    private String propertiesSocketPort;

    public void process() {
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", propertiesProtocol);

            // server setting
            properties.put(propertiesHost, host);
            properties.put(propertiesPort, port);

            // SSL setting
            properties.setProperty(propertiesSocketFactory, "javax.net.ssl.SSLSocketFactory");
            properties.setProperty(propertiesSocketFallback, "false");
            properties.setProperty(propertiesSocketPort, port);

            emailAttachmentReceiver.setSaveDirectory(saveDirectory);
            emailAttachmentReceiver.setProperty(properties);
            emailAttachmentReceiver.downloadEmailAttachments(propertiesProtocol, userName, password);
            log.debug("Finish read mail");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
