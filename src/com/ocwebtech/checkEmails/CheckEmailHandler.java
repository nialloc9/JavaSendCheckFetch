/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.checkEmails;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 *
 * @author Niall
 */
public class CheckEmailHandler {
    
    public void handle(Message[] message){
        try{
            for(int i=0; i<message.length;i++){
                System.out.println("---------------------------------");
                System.out.println("Email Number: " + (i+1));
                System.out.println("Subject: " + message[i].getSubject());
                System.out.println("From: " + message[i].getFrom());
                System.out.println("Text: " + message[i].getContent().toString());
            }
        }catch(MessagingException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
}
