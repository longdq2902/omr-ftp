package com.cael.omr.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Device {
    private long time;
    private String name;
    private float value;
}
