package com.ocwebtech.setUp;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author Niall
 */
public class SetUp {

    private String user;
    private String pass;
    private String host;//for google this is smtp.gmail.com
    private Session session;
    private Store store;
    private Folder folder;
    private Message[] message;
    
    public SetUp(String u, String p, String h) {
        user = u;
        pass = p;
        host = h;
    }
    
    
    public void setUpSession(){
        
        //create properties object
        Properties props = new Properties();
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.post", "587");
        
        //Create session instance add in the props and authenticate using Authenticator
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication(){ //overrided method
                return new javax.mail.PasswordAuthentication(user, pass); //authenticates pass and username
            }
        });
        
    }
}
