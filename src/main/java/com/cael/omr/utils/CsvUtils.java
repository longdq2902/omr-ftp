package com.cael.omr.utils;

import com.cael.omr.model.Device;
import com.cael.omr.service.InfluxDbService;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvUtils {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final int MAX_LENGTH = 2000;

    private static final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("MM/dd/yyHH:mm:ss");

    private static void processData(InfluxDbService influxDbService, List<Device> listData) {
        influxDbService.insertPoints(listData);
    }

    //Bester
    public static void readUseCSVReader(String csvFile, InfluxDbService influxDbService) throws ParseException {
        long startTime = System.currentTimeMillis();
        int numRecord = 0;
        CSVReader reader = null;
        try {
            List<String> lstDevice = new ArrayList<>();
            String date = "", time = "";
            int idx = 0, skipCol = 2;
            long timeData = 0;
            LocalDate dDate;
            Timestamp timestamp;

            reader = new CSVReader(new FileReader(csvFile));
            String[] line;

            List<Device> listData = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                if (line.length < skipCol) {
                    log.warn("ERROR DATA FROM file: " + csvFile + " idx: " + numRecord + " data: " + StringUtils.join(line, ","));
                    continue;
                }
                idx = 0;
                if (numRecord == 0) {
                    //Doc header
                    for (String device : line) {
                        idx++;
                        if (idx > skipCol) {
                            //Doc du lieu tbi
                            lstDevice.add(device);
                        }
                    }
                } else {
                    //Data
                    date = formatDate(line[0]);
                    time = formatTime(line[1]);
                    dDate = LocalDate.parse(date + time, customFormatter);

                    timestamp = Timestamp.valueOf(dDate.atStartOfDay());

                    timeData = timestamp.getTime();

                    for (String value : line) {
                        idx++;
                        if (idx > skipCol) {
                            if (!StringUtils.isBlank(value)) {
                                try {
                                    Device device = convertToDevice(timeData, lstDevice.get(idx - skipCol - 1), value);
                                    listData.add(device);

                                    if (listData.size() >= MAX_LENGTH) {
                                        processData(influxDbService, listData);
                                        listData.clear();
                                    }
                                } catch (Exception ex) {
                                    log.error("ERROR ProcessImportIfl getInstance: ", ex);
                                    listData.clear();
                                }
                            }
                        }
                    }
                }
                numRecord++;
            }

            if (listData.size() >= 0) {
                processData(influxDbService, listData);
                listData.clear();
            }

        } catch (IOException e) {
            log.error("ERROR readUseCSVReader: ", e);
        } finally {
            if (numRecord > 1) {
                deleteFile(csvFile);
            }
            log.info("Time to readUseCSVReader " + numRecord + " record: " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    private static Device convertToDevice(long time, String deviceName, String value) {
        return Device.builder()
                .time(time)
                .name(deviceName)
                .value(Float.parseFloat(value))
                .build();
    }

    public static void deleteFile(String filePath) {
        try {

            File file = new File(filePath);

            if (file.delete()) {
                log.debug(filePath + " is deleted!");
            } else {
                log.warn(filePath + " Delete operation is failed.");
            }
        } catch (Exception ex) {
            log.error("ERROR deleteFile " + filePath + " ", ex);
        }
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

    //Format ngay ve dang MM/DD/YY
    private static String formatDate(String input){
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);

        String[] tmp = input.split("/");

        if(tmp.length >= 3){
            sb.append(StringUtils.leftPad(tmp[0], 2, "0"))
                    .append("/")
                    .append(StringUtils.leftPad(tmp[1], 2, "0"))
                    .append("/")
                    .append(tmp[2]);
        }
        return sb.toString();
    }

    //Format time ve HH:mm:ss
    private static String formatTime(String input){
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);

        String[] tmp = input.split(":");

        if(tmp.length >= 3){
            sb.append(StringUtils.leftPad(tmp[0], 2, "0"))
                    .append(":")
                    .append(StringUtils.leftPad(tmp[1], 2, "0"))
                    .append(":")
                    .append(StringUtils.leftPad(tmp[2], 2, "0"));
        }
        return sb.toString();
    }
}
