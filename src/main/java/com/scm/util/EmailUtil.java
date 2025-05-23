package com.scm.util;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class for sending emails.
 * Supports both actual SMTP sending and mock mode for development.
 */
public class EmailUtil {
    
    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    private static final boolean USE_MOCK = AppConfig.getBooleanProperty("mail.use.mock", false);
    private static final String SMTP_HOST = AppConfig.getProperty("mail.smtp.host", "localhost");
    private static final String SMTP_PORT = AppConfig.getProperty("mail.smtp.port", "25");
    private static final boolean SMTP_AUTH = AppConfig.getBooleanProperty("mail.smtp.auth", false);
    private static final boolean SMTP_STARTTLS = AppConfig.getBooleanProperty("mail.smtp.starttls.enable", false);
    private static final String USERNAME = AppConfig.getProperty("mail.username", "");
    private static final String PASSWORD = AppConfig.getProperty("mail.password", "");
    private static final String FROM_ADDRESS = AppConfig.getProperty("mail.from", "noreply@scm-system.com");
    
    // Private constructor to prevent instantiation
    private EmailUtil() {
    }
    
    /**
     * Send an email.
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param body email body content (can be HTML)
     * @return true if the email was sent successfully
     */
    public static boolean sendEmail(String to, String subject, String body) {
        return sendEmail(to, null, null, subject, body, true);
    }
    
    /**
     * Send an email with CC and BCC recipients.
     * 
     * @param to primary recipient email address
     * @param cc carbon copy recipient email addresses (comma-separated)
     * @param bcc blind carbon copy recipient email addresses (comma-separated)
     * @param subject email subject
     * @param body email body content
     * @param isHtml true if the body contains HTML
     * @return true if the email was sent successfully
     */
    public static boolean sendEmail(String to, String cc, String bcc, String subject, String body, boolean isHtml) {
        if (USE_MOCK) {
            return mockSendEmail(to, cc, bcc, subject, body);
        }
        
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", String.valueOf(SMTP_AUTH));
            props.put("mail.smtp.starttls.enable", String.valueOf(SMTP_STARTTLS));
            
            // Create a mail session with or without authentication
            Session session;
            if (SMTP_AUTH) {
                session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
            } else {
                session = Session.getInstance(props);
            }
            
            // Create and configure the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_ADDRESS));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            
            if (cc != null && !cc.isEmpty()) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            
            if (bcc != null && !bcc.isEmpty()) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
            }
            
            message.setSubject(subject);
            message.setSentDate(new Date());
            
            // Set the message content
            if (isHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }
            
            // Send the message
            Transport.send(message);
            LOGGER.log(Level.INFO, "Email sent successfully to {0}", to);
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
            return false;
        }
    }
    
    /**
     * Mock implementation of email sending for development/testing.
     * 
     * @param to recipient email address
     * @param cc carbon copy recipient email addresses
     * @param bcc blind carbon copy recipient email addresses
     * @param subject email subject
     * @param body email body content
     * @return always returns true
     */
    private static boolean mockSendEmail(String to, String cc, String bcc, String subject, String body) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== MOCK EMAIL ==========\n");
        logMessage.append("From: ").append(FROM_ADDRESS).append("\n");
        logMessage.append("To: ").append(to).append("\n");
        
        if (cc != null && !cc.isEmpty()) {
            logMessage.append("CC: ").append(cc).append("\n");
        }
        
        if (bcc != null && !bcc.isEmpty()) {
            logMessage.append("BCC: ").append(bcc).append("\n");
        }
        
        logMessage.append("Subject: ").append(subject).append("\n");
        logMessage.append("Sent Date: ").append(new Date()).append("\n");
        logMessage.append("Body:\n").append(body).append("\n");
        logMessage.append("================================\n");
        
        LOGGER.log(Level.INFO, logMessage.toString());
        return true;
    }
} 