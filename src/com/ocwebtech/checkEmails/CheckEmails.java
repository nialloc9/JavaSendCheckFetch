package com.ocwebtech.checkEmails;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author Niall
 */
public class CheckEmails {

    private String user;
    private String pass;
    private String host;//for google this is smtp.gmail.com
    private Session session;
    private Store store;
    private Folder folder;
    private Message[] message;
    
    public CheckEmails(String u, String p, String h) {
        user = u;
        pass = p;
        host = h;
    }
    
    
    public void setUpSession(){
        
        //create properties object
        Properties props = new Properties();
        
        //Below is needed for reading emails
        props.put("mail.pop3.user" , user);
        // Start SSL connection
        props.put("mail.pop3.socketFactory" , 995 );
        props.put("mail.pop3.socketFactory.class" , "javax.net.ssl.SSLSocketFactory" );
        props.put("mail.pop3.port" , 995);
        
        //Create session instance add in the props and authenticate using Authenticator
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication(){ //overrided method
                return new javax.mail.PasswordAuthentication(user, pass); //authenticates pass and username
            }
        });
        
    }
    
    //create store.. An abstract class that models a message store and its access protocol, for storing and retrieving messages.
    public void createStore(){
        
        try{
            //get store
           store = session.getStore("pop3"); 
           
           
        }catch(NoSuchProviderException ex){
            ex.printStackTrace();
        }
        
        try{
            //connect to store
           store.connect(host, user, pass);
        }catch(MessagingException ex){
            ex.printStackTrace();
        }
        
    }
    
    //get folder.. Folder is an abstract class that represents a folder for mail messages. example folderName= INBOX
    public Message[] checkMessages(String folderName){
        
        try{
            //create folder object and open it
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY); //can also use READ_WRITE
            
            //retreive messages
            message = folder.getMessages();
            System.out.println("No of messages in folder: " + message.length);
            
            /* This throws an IO exception that needs to be caught
            for(int i=0; i<message.length;i++){
                System.out.println("---------------------------------");
                System.out.println("Email Number: " + (i+1));
                System.out.println("Subject: " + message[i].getSubject());
                System.out.println("From: " + message[i].getFrom());
                System.out.println("Text: " + message[i].getContent().toString());
            }
            */
            
            //close folder and store
            folder.close(false);
            store.close();
        }catch(MessagingException ex){
            ex.printStackTrace();
        }
        
        return message;
    }
    
}
