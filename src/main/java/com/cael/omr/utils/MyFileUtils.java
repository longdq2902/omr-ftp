package com.cael.omr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MyFileUtils {


    private List<String> walk(String path) {

        List<String> rets = new ArrayList<>();

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return null;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
                //  System.out.println( "Dir:" + f.getAbsoluteFile() );
            } else {
                rets.add(f.getAbsolutePath());
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
        return rets;
    }

    private static List<String> getAllFiles(String pathDir) {

        Stream<Path> walk = null;
        try {
            walk = Files.walk(Paths.get(pathDir));
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            walk.close();
            return result;

        } catch (IOException e) {
            log.error(e.getMessage());
            if (walk != null) {
                walk.close();
            }

            return null;
        } finally {
            if (walk != null) {
                walk.close();
            }
        }

    }

    public static List<String> getAllFilesByList(String pathDirs) {
        List<String> myPaths = new ArrayList<>();
        List<String> rets = new ArrayList<>();

        myPaths = Arrays.asList(pathDirs.split(","));
        if (myPaths != null && myPaths.size() > 0) {

            for (String p : myPaths) {
                List<String> tmp = null;
                tmp = getAllFiles(p);
                if (tmp != null) {
                    rets.addAll(tmp);
                }
            }
        }
        return rets;

    }

    public static boolean createFolderIfNotExit(String directoty) {
        File folder = new File(directoty);
        if (!folder.exists()) {
            log.info("createFolderIfNotExit {}", directoty);
            return folder.mkdirs();
        }
        return true;
    }

    public static boolean copy(String src, String desc) throws IOException {
        Path source = Paths.get(src);
        Path destination = Paths.get(desc);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public static List<String> readTxtFile(String fileName) {
        List<String> list = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            list = stream
                    .filter(line -> !line.startsWith("#") && line.contains("="))
                    .map(line -> {
                        String[] tmp = line.split("=");
                        List lstString = new ArrayList(tmp.length);

                        for (String i : tmp) {
                            lstString.add(i.trim());
                        }

                        return StringUtils.join(lstString, "=");
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("ERROR readTxtFile: ", e);
            return null;
        }

        return list;
    }

}
