package com.cael.omr.service;

import java.io.IOException;

public interface FTPClientService {
    boolean uploadFolderToRemoteServer(String localFolder) throws IOException;

    boolean uploadFileToRemoteServer(String localFileName, String absolutePath, String remoteFile) throws IOException;
}
