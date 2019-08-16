package com.cael.omr.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ComponentFactory {

    @Autowired
    private Map<String, BaseComponent> componentMap;

    public BaseComponent getComponent(String type) {
        return componentMap.get(type);
    }
}
