package com.cael.omr.service.impl;

import com.cael.omr.service.FTPClientService;
import com.cael.omr.utils.MyFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FTPClientServiceImpl implements FTPClientService {

    @Value("${ftp.host}")
    private String server;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.user}")
    private String user;
    @Value("${ftp.password}")
    private String pass;
    @Value("${ftp.fixHost}")
    private String fixHost;
    @Value("${ftp.bufferSize}")
    private int bufferSize;
    @Value("${ftp.ftpHostDir}")
    private String dir;
    @Value("${ftp.mode}")
    private String mode;
    @Value("${ftp.backupFolder}")
    private String backupFolder;
    private String backupFolderDaily;

    private void createFolderBackup() {
        if (!StringUtils.isBlank(backupFolder)) {
            File theDir = new File(backupFolder);

            // if the directory does not exist, create it
            if (!theDir.exists()) {
                log.info("creating directory: " + theDir.getName());
                boolean result = false;

                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    log.info("DIR created");
                }
            }
        }
    }

    public void createFolderBackupDaily(String date) {
        if (!StringUtils.isBlank(backupFolder)) {
            createFolderBackup();
            backupFolderDaily = backupFolder + File.separator + date;
            File theDir = new File(backupFolderDaily);

            // if the directory does not exist, create it
            if (!theDir.exists()) {
                log.info("creating directory: " + backupFolderDaily);
                boolean result = false;

                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    log.info("DIR created");
                }
            }
        }
    }


    @Override
    public boolean uploadFolderToRemoteServer(String localFolder) throws IOException {
        List<String> listFiles = MyFileUtils.getAllFilesByList(localFolder);
        if (listFiles == null || listFiles.isEmpty()) {
            log.warn("{} is emply", localFolder);
            return true;
        }

        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        File firstLocalFile = null;
        boolean backupSuccess = false;
        try {
            ftpClient.connect(server, port);

            log.info("ftp login {}/{}", user, pass);
            boolean login = ftpClient.login(user, pass);

            if (!login) {
                log.warn("Operation failed. Server reply code: {}-{}", ftpClient.getReply(), ftpClient.getReplyString());
                return false;
            }

            if (mode.equalsIgnoreCase("PassiveMode")) {
                ftpClient.enterLocalPassiveMode();
            } else if (mode.equalsIgnoreCase("ActiveMode")) {
                ftpClient.enterLocalActiveMode();
            }

            if (fixHost.equalsIgnoreCase("true")) {
                ftpClient.setUseEPSVwithIPv4(true);
                ftpClient.setRemoteVerificationEnabled(false);
            }
            //End Add

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //Set BufferSize
            if (bufferSize > ftpClient.getBufferSize()) {
                log.warn("BufferSize too small: " + ftpClient.getBufferSize());
                ftpClient.setBufferSize(bufferSize);
            }

            //Set WorkingDirectory
            log.info("printWorkingDirectory: " + ftpClient.printWorkingDirectory());
            if (!dir.equalsIgnoreCase(ftpClient.printWorkingDirectory())) {
                ftpClient.changeWorkingDirectory(dir);
                log.info("changeWorkingDirectory: " + ftpClient.printWorkingDirectory());
                showServerReply(ftpClient);
            }

            for (String path : listFiles) {
                result = uploadFile(ftpClient, path);
            }
        } catch (IOException ex) {
            log.error("IOException uploadFileToRemoteServer: ", ex);
            result = false;
        } finally {
            if (backupSuccess && firstLocalFile != null) {
                firstLocalFile.delete();
            }
            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                log.error("IOException uploadFileToRemoteServer: ", ex);
            }
        }
        return result;
    }

    private boolean uploadFile(FTPClient ftpClient, String absolutePath) throws IOException {
        log.info("Begin upload file {}", absolutePath);
        boolean result = false;
        File firstLocalFile = new File(absolutePath);

        //Backup file
//            backupSuccess = backupFile(firstLocalFile, localFileName);

        FileInputStream inputStream = new FileInputStream(firstLocalFile);

        String remoteFile = firstLocalFile.getName();
        String remoteFileName = remoteFile;

        if (remoteFile.contains(File.separator)) {
            String remotePath = remoteFile.substring(0, remoteFile.lastIndexOf(File.separator));
            remoteFileName = remoteFile.substring(remoteFile.lastIndexOf(File.separator) + 1);

            if (!remotePath.endsWith(File.separator)) {
                remotePath += File.separator;
            }
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                if (!makeDirectories(ftpClient, remotePath)) {
                    log.error("ERROR uploadFileToRemoteServer: makeDirectories " + remotePath + " failure.");
                    return false;
                }
            }
        }

        boolean done = ftpClient.storeFile(remoteFileName, inputStream);
        inputStream.close();
        if (done) {
            String rtn_tmp = ftpClient.printWorkingDirectory().replaceAll("\"", "");
            rtn_tmp = rtn_tmp + "/" + remoteFileName;
            ftpClient.sendSiteCommand("chmod " + "755 " + rtn_tmp);
            result = true;
        } else {
            showServerReply(ftpClient);
            log.warn("storeFile: " + remoteFileName + " fail..");
            result = false;
        }
        return result;
    }

    @Override
    public boolean uploadFileToRemoteServer(String localFileName, String absolutePath, String remoteFile) throws IOException {
        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        File firstLocalFile = null;
        boolean backupSuccess = false;
        try {
            ftpClient.connect(server, port);

            log.info("ftp login {}/{}", user, pass);
            boolean login = ftpClient.login(user, pass);

            if (!login) {

                System.out.println("Operation failed. Server reply code: "
                        + ftpClient.getReply() + ftpClient.getReplyString()
                        + ftpClient.getBufferSize());
                System.out.println("--------------");
                return false;
            }

            System.out.println("--------------mode: " + mode);

            if (mode.equalsIgnoreCase("PassiveMode")) {
                ftpClient.enterLocalPassiveMode();
            } else if (mode.equalsIgnoreCase("ActiveMode")) {
                ftpClient.enterLocalActiveMode();
            }

            if (fixHost.equalsIgnoreCase("true")) {
                ftpClient.setUseEPSVwithIPv4(true);
                ftpClient.setRemoteVerificationEnabled(false);
            }
            //End Add

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //Set BufferSize
            if (bufferSize > ftpClient.getBufferSize()) {
                log.warn("BufferSize too small: " + ftpClient.getBufferSize());
                ftpClient.setBufferSize(bufferSize);
            }

            //Set WorkingDirectory
            log.info("printWorkingDirectory: " + ftpClient.printWorkingDirectory());
            if (!dir.equalsIgnoreCase(ftpClient.printWorkingDirectory())) {
                ftpClient.changeWorkingDirectory(dir);
                log.info("changeWorkingDirectory: " + ftpClient.printWorkingDirectory());
                showServerReply(ftpClient);
            }

            result = uploadFile(ftpClient, absolutePath);
        } catch (IOException ex) {
            log.error("IOException uploadFileToRemoteServer: ", ex);
            result = false;
        } finally {
            if (backupSuccess && firstLocalFile != null) {
                firstLocalFile.delete();
            }
            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                log.error("IOException uploadFileToRemoteServer: ", ex);
            }
        }
        return result;
    }

    private boolean backupFile(File source, String fileName) {
        if (!StringUtils.isBlank(backupFolderDaily)) {
            try {
                File dest = new File(backupFolderDaily + File.separator + fileName);
                FileUtils.copyFile(source, dest);
            } catch (Exception ex) {
                log.error("ERROR backupFile: " + fileName, ex);
                return false;
            }
        }
        return true;
    }

    public boolean makeDirectories(FTPClient ftpClient, String dirPath)
            throws IOException {
        try {
            String[] pathElements = dirPath.split("\\" + File.separator);
            if (pathElements != null && pathElements.length > 0) {
                for (String singleDir : pathElements) {
                    if (singleDir == null || singleDir.length() <= 0) {
                        continue;
                    }
                    boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                    if (!existed) {
                        boolean created = ftpClient.makeDirectory(singleDir);
                        if (created) {
                            ftpClient.changeWorkingDirectory(singleDir);
                        } else {
                            log.error("makeDirectories false: " + singleDir + " + " + ftpClient.getReplyCode() + " - " + ftpClient.getReplyString());
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            log.error("Exception makeDirectories: ", ex);
            return false;
        }
    }

    public boolean deleteFile(String filePath) {
        FTPClient client = new FTPClient();
        try {
            client.connect(server, port);
            client.login(user, pass);

            if (!client.printWorkingDirectory().equals(dir)) {
                log.info("changeWorkingDirectory " + dir + ": " + client.changeWorkingDirectory(dir));
                showServerReply(client);
            }

            client.sendSiteCommand("chmod " + "755" + filePath);

            if (client.deleteFile(filePath)) {
                log.info("File deleted " + filePath);
                return true;
            }
            log.info("File deleted false " + filePath);
        } catch (IOException ex) {
            log.error("IOException uploadFileToRemoteServer: ", ex);
            return false;
        } catch (Exception ex) {
            log.error("Exception uploadFileToRemoteServer: ", ex);
            return false;
        } finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }

            } catch (Exception ex) {
                log.error("Exception disconnect deleteFile: ", ex);
                return false;
            }
        }
        return false;
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                log.warn("SERVER: " + aReply);
            }
        }
    }
}
