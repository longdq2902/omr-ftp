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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(MyFileUtils.class);


    private  List<String> walk( String path ) {

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

    private static List<String> getAllFiles(String pathDir){

        Stream<Path> walk = null;
        try {
            walk = Files.walk(Paths.get(pathDir));
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            walk.close();
            return  result;

        } catch (IOException e) {
            logger.error(e.getMessage());
            if(walk != null){
                walk.close();
            }

            return null;
        }
        finally {
            if(walk != null){
                walk.close();
            }
        }

    }
    public static List<String> getAllFilesByList(String pathDirs){
        List<String> myPaths = new ArrayList<>();
        List<String> rets = new ArrayList<>();

        myPaths = Arrays.asList(pathDirs.split(","));
        if(myPaths != null && myPaths.size()>0){

            for (String p : myPaths) {
                List<String> tmp = null;
                tmp = getAllFiles(p);
                if (tmp != null) {
                    rets.addAll(tmp);
                }
            }
        }
        return  rets;

    }


}
