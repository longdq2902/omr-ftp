//package com.cael.omr;
//
//import com.cael.omr.model.Device;
//import com.cael.omr.service.InfluxDbService;
//import org.influxdb.dto.Point;
//import org.influxdb.dto.Query;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.influxdb.InfluxDBTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootApplication
//public class Application implements CommandLineRunner {
//    private static Logger logger = LoggerFactory.getLogger(Application.class);
//
////    @Autowired
////    private InfluxDBTemplate<Point> influxDBTemplate;
//
//    @Autowired
//    private InfluxDbService influxDbService;
//
//    @Override
//    public void run(final String... args) throws Exception
//    {
//
//        List<Device> deviceList = new ArrayList<>();
//        deviceList.add(Device.builder()
//                .time(System.currentTimeMillis())
//                .name("test1")
//                .value(12L)
//                .build());
//
//        deviceList.add(Device.builder()
//                .time(System.currentTimeMillis())
//                .name("test2")
//                .value(18L)
//                .build());
//
//        influxDbService.insertPoints(deviceList);
//
//
////        // Create database...
////        influxDBTemplate.createDatabase();
////
////        // Create some data...
////        final Point p1 = Point.measurement("logs")
////                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
////                .tag("device", "default")
////                .addField("value", 9L)
////                .build();
////        final Point p2 = Point.measurement("disk")
////                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
////                .tag("device", "default")
////                .addField("value", 17L)
////                .build();
////        influxDBTemplate.write(p1, p2);
////
////        // ... and query the latest data
////        final Query q = new Query("SELECT * FROM logs GROUP BY device", influxDBTemplate.getDatabase());
////        influxDBTemplate.query(q, 10, queryResult -> logger.info(queryResult.toString()));
//    }
//
//    public static void main(String[] args)
//    {
//        SpringApplication.run(Application.class, args);
//    }
//}
