package com.cael.omr.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(MyFileUtils.class);


    public static List<String> walk( String path ) {

        List<String> rets = new ArrayList<>();

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return null;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );
              //  System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                rets.add(f.getAbsolutePath());
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
        return  rets;
    }

    public static List<String> getAllFiles(String pathDir){

        try {
            Stream<Path> walk = Files.walk(Paths.get(pathDir));
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            return  result;

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }

    }



}
