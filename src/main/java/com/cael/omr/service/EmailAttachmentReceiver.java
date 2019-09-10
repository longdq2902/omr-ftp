package com.cael.omr.service;

import com.cael.omr.utils.MyFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
public class EmailAttachmentReceiver {
    private String saveDirectory;

    private Properties properties;

    /**
     * Sets the directory where attached files will be stored.
     *
     * @param dir absolute path of the directory
     */
    public void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
        MyFileUtils.createFolderIfNotExit(saveDirectory);
    }

    public void setProperty(Properties properties) {
        this.properties = properties;
    }

    private SearchTerm buildSearch() {
        // search for all "unseen" messages
        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date minDate = new Date(cal.getTimeInMillis());   //get today date

        ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, minDate);

//        Message messages[] = folderInbox.search(term);
        return new AndTerm(unseenFlagTerm, minDateTerm);
    }

    /**
     * Downloads new messages and saves attachments to disk if any.
     *
     * @param protocol
     * @param userName
     * @param password
     */
    public void downloadEmailAttachments(String protocol,
                                         String userName, String password) {
        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            // search for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

            Message messages[] = folderInbox.search(unseenFlagTerm);

            if (folderInbox.getUnreadMessageCount() > 0
                    || (messages != null && messages.length > 0)) {
                log.info("==>folderInbox search getUnreadMessageCount: {} -  Message: {}", folderInbox.getUnreadMessageCount(), messages.length);
            }

            int i = 0;
            Address[] fromAddress;
            String from, subject, sentDate, contentType, messageContent, attachFiles, fileName;
            Multipart multiPart;
            int numberOfParts;
            for (Message message : messages) {
                message.setFlag(Flags.Flag.SEEN, true);

                fromAddress = message.getFrom();
                from = fromAddress[0].toString();
                subject = message.getSubject();
                sentDate = message.getSentDate().toString();

                contentType = message.getContentType();
                messageContent = "";

                // store attachment file name, separated by comma
                attachFiles = "";

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    multiPart = (Multipart) message.getContent();
                    numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            fileName = part.getFileName();
                            attachFiles += fileName + ", ";
                            part.saveFile(saveDirectory + File.separator + fileName);
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

                if(attachFiles.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\n\tMessage #").append(i++).append(": ")
                            .append("\n\t From: ").append(from)
                            .append("\n\t Subject: ").append(subject)
                            .append("\n\t Sent Date: ").append(sentDate)
                            .append("\n\t Content: ").append(messageContent)
                            .append("\n\t Attachments: ").append(attachFiles);

                    log.info(sb.toString());
                }
            }

            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            log.error("No provider for pop3.", ex);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            log.error("Could not connect to the message store", ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            log.error("IOException", ex);
            ex.printStackTrace();
        }
    }
}
