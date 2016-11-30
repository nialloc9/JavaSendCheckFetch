/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.fetchEmails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class FetchEmails {
    
    private final String user;
    private final String pass;
    private final String host;//for google this is smtp.gmail.com
    private Session session;
    private Store store;
    private Folder folder;
    private Message[] message;
    HandleFetchedEmails handler;
    
    //username, password, host
    public FetchEmails(String u, String p, String h) {
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
        
        //Below is needed for reading emails
        props.put("mail.pop3.user" , user);
        // Start SSL connection
        props.put("mail.pop3.socketFactory" , 995 );
        props.put("mail.pop3.socketFactory.class" , "javax.net.ssl.SSLSocketFactory" );
        props.put("mail.pop3.port" , 995);
        
        //for fetching
        props.put("mail.pop3.host" , "pop.gmail.com");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.starttls.enable", "true");
        props.put("mail.pop3.socketFactory.fallback", "false");
        
        
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
    public Message[] fetchMessages(String folderName){
        
        try {
            //create folder object and open it
            folder = null;
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            
            //create buffer and chain to input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            //get the messages from and store them in an array
            message = folder.getMessages();
            System.out.println("No of messages--- " + message.length);
            
            //initialize handler
            handler = new HandleFetchedEmails();
            
            //loop through and print out data
            for(int i=0;i<message.length;i++){
                System.out.println("---------------------------------" + i);
                handler.writePart(message[i]);
                String line = reader.readLine();
                if("YES".equals(line)){
                    message[i].writeTo(System.out);
                }else if("QUIT".equals(line)){
                    break;
                }
            }
            
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        
        return message;
    }
}
