/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.fetchEmails;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 *
 * @author Niall
 */
public class HandleFetchedEmails {
    
    //checks content-type based on which, it proesses and fetches the content to of the message
    public void writePart(Part p){
        try{
            if(p instanceof Message){
                //call writeEnvelope method to handle headers
                writeEnvelope((Message) p); //cast to Message
            }
        
            //print content type
            System.out.println("----------------------------");
            System.out.println("CONTENT-TYPE " +p.getContentType());
            
            //check if content is plain text
            if(p.isMimeType("text/plain")){
                System.out.println("This is plain text");
                System.out.println("---------------------------");
                System.out.println((String) p.getContent());
            }
            
            //check if content has attachment
            else if(p.isMimeType("multipart/*")){
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                
                //get bodyparts
                Multipart mp = (Multipart) p.getContent();
                
                //loop through and call function again with each part
                for(int i=0;i<mp.getCount();i++){
                    writePart(mp.getBodyPart(i));
                }
            }
            
            //check if content has a nested message
            else if(p.isMimeType("message/rfc822")){
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                
                //call function with content from p.. This is to find out what type is the nested message
                writePart((Part) p.getContent());
            }
            
            //check if content is an inline image
            else if(p.isMimeType("image/jpeg")){
                System.out.println("--------> image/jpeg");
                Object o = p.getContent();

                InputStream x = (InputStream) o;
                // Construct the required byte array
                System.out.println("x.length = " + x.available());
                
                int i = 0;
                byte[] bArray = new byte[x.available()];
                while ((i = (int) ((InputStream) x).available()) > 0) {
                   int result = (int) (((InputStream) x).read(bArray));
                   if (result == -1){
                       break;
                   }
                }
                FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
                f2.write(bArray);
            } 
            
            //other images
            else if(p.getContentType().contains("image/")){
                System.out.println("CONTENT TYPE: " + p.getContentType());
                //create file object
                File file = new File("image" + new Date().getTime());
                
                //set up output stream and output file
                DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead = test.read(buffer)) != -1){
                    output.write(buffer, 0, bytesRead);
                }
            }
            //all other objects
            else{
                Object o = p.getContent();
                if(o instanceof String){
                    System.out.println("This is just an input stream");
                    System.out.println("---------------------------");
                    System.out.println((String) o);
                }else if(o instanceof InputStream){
                    //set up stream
                    InputStream is = (InputStream) o;
                    
                    int c;
                    while((c = is.read()) != -1){
                        System.out.write(c);
                    }
                }else {
                    System.out.println("This is an unknown type");
                    System.out.println("---------------------------");
                    System.out.println(o.toString());
                 }
            }
            
            
            
        }catch(MessagingException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    public void writeEnvelope(Message m) throws Exception{
        System.out.println("This is the message envelope");
        System.out.println("---------------------------");
        Address[] a;
        
        //FROM
        if((a = m.getFrom()) != null){
            for(int i=0;i<a.length;i++){
                System.out.println("From: " + a[i].toString());
            }
        }
        
        //TO
        if((a = m.getRecipients(Message.RecipientType.TO)) != null){
            for(int j=0;j<a.length;j++){
                System.out.println("To: " + a[j].toString());
            }
        }
        
        //SUBJECT
        if(m.getSubject() != null){
            System.out.println("Subject: " + m.getSubject());
        }
    }
}
