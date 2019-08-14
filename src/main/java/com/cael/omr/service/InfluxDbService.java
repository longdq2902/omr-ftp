package com.cael.omr.service;

import com.cael.omr.model.Device;

import java.util.List;

public interface InfluxDbService {
    void insertPoint(Device device);

    void insertPoints(List<Device> listDevice);
}
