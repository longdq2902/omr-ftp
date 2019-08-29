package com.cael.omr.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Startup {

    @Autowired
    ResourceLoader resourceLoader;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefeshedEvent(){
        Resource resource = resourceLoader.getResource("classpath:license");
    }
}
