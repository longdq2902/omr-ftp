package com.cael.omr.service.impl;

import com.cael.omr.model.Device;
import com.cael.omr.service.InfluxDbService;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InfluxDbServiceImpl implements InfluxDbService {
    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    @Value("${spring.influxdb.measurement}")
    private String measurement;
    @Value("${spring.influxdb.tag}")
    private String tag;
    @Value("${spring.influxdb.field}")
    private String field;

    @Override
    public void insertPoint(Device device) {
//        influxDBTemplate.createDatabase();
        final Point p = Point.measurement(measurement)
                .time(device.getTime(), TimeUnit.MILLISECONDS)
                .tag(tag, device.getName())
                .addField(field, device.getValue())
                .build();
        influxDBTemplate.write(p);
    }

    @Override
    public void insertPoints(List<Device> listDevice) {
        if (listDevice == null || listDevice.isEmpty()) {
            return;
        }
//        influxDBTemplate.createDatabase();
        List<Point> listPoint = listDevice.stream().map(item -> {
            return Point.measurement(measurement)
                    .time(item.getTime(), TimeUnit.MILLISECONDS)
                    .tag(tag, item.getName())
                    .addField(field, item.getValue())
                    .build();
        }).collect(Collectors.toList());

        influxDBTemplate.write(listPoint);
    }
}
