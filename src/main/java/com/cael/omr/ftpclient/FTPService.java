package com.cael.omr.ftpclient;


import java.io.File;
import java.io.IOException;

import com.cael.omr.exception.FTPErrors;

public interface FTPService {
    void connectToFTP(String host, String user, String pass) throws FTPErrors;
    void uploadFileToFTP(File file, String ftpHostDir , String serverFilename) throws FTPErrors;
    void downloadFileFromFTP(String ftpRelativePath, String copytoPath) throws FTPErrors, IOException;
    void disconnectFTP() throws FTPErrors;
}
