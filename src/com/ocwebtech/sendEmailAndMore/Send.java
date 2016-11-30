package com.ocwebtech.sendEmailAndMore;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Niall
 */
public class Send {

    private final String user;
    private final String pass;
    private final String host;//for google this is smtp.gmail.com
    private Session session;
    private Store store;
    private Folder folder;
    private Message[] message;
    
    //username, password, host
    public Send(String u, String p, String h) {
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
    
    //send message
    public void sendMessage(String to, String subject, String msg){
        //create message
        MimeMessage message = new MimeMessage(session);
        
        //set message properties
        try{
            //set who message is from
            message.setFrom(user);
            //add recepiants.. can be array.. must be changed to internet address.. can also do Address address = new InternetAddress("manisha@gmail.com", Manisha); 
            message.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            //set subject
            message.setSubject(subject);
            //set message
            message.setText(msg);
            
            
            //Transport class is used as a message transport mechanism. This class normally uses the SMTP protocol to send a message.
            Transport.send(message);
            
            System.out.println("Email sent successfully");
        }catch(MessagingException ex){
            ex.printStackTrace();
        }
    }
    
    //send attachment
    public void sendAttachment(String to, String from, String sub, String text, String filename){
        
        try{
            //create default MimeMessage object
            Message msg = new MimeMessage(session);
            
            //set from: header field of the header
            msg.setFrom(new InternetAddress(from));
            
            //set TO: header
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            
            //set Subject: header
            msg.setSubject(sub);
            
            //create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            
            //Now set the actual message
            messageBodyPart.setText(text);
            
            //Create a multi part message
            Multipart multipart = new MimeMultipart();
            
            //set text message part
            multipart.addBodyPart(messageBodyPart);
            
            //Now part two is the attachment
            messageBodyPart = new MimeBodyPart();
            
            //create a data source object
            DataSource source = new FileDataSource(filename); //e.g c:/Users/nikos7/Desktop/myFile.txt
            
            //set the data handler
            messageBodyPart.setDataHandler(new DataHandler(source));
            
            //set the file name to be added
            messageBodyPart.setFileName(filename);
            
            //add attachment to message object to be sent
            multipart.addBodyPart(messageBodyPart);
            
            //add the multipart objecta as the message conteny
            msg.setContent(multipart);
            
            //send the message
            Transport.send(msg);
            
            System.out.println("Email with attachment sent successfully..");
        }catch(MessagingException ex){
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    //"<h1>This is actual message embedded in HTML tags</h1>", "text/html" --> TWO PARTS TO HTML STRING
    public void sendHtmlEmail(String to, String from, String sub, String htmlString){
        //create a default MimeMessage object
        Message msg = new MimeMessage(session);
        
        //set from to and sub and content headers
        try{
            msg.setFrom(new InternetAddress(from));
            msg.setSubject(sub);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            
            //set content
            msg.setContent(
              htmlString,
             "text/html");
            
            Transport.send(msg);
            
            System.out.println("HTML email sent successfully..");
        }catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    //send image in email
    public void sendHtmlWithImage(String to, String from, String sub, String htmlString, String imageLocationString){
        
        //set html to include image
        htmlString = htmlString + "<img src=\"cid:image\">";
        
        try{
            //create default messge object
            Message message = new MimeMessage(session);
            
            //set to, from, and sub headers
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(sub);
            
            //This mail has 2 parts the body and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
            
            //first part(html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlString, "text/html");
            //add it
            multipart.addBodyPart(messageBodyPart);
            
            //second part(image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(imageLocationString);
            
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
            
            //add image to multipart
            multipart.addBodyPart(messageBodyPart);
            
            //put message together
            message.setContent(multipart);
            
            //send message
            Transport.send(message);
            
            
            System.out.println("Email with image sent successfully..");
            
            
        //add message oart
        }catch(MessagingException  ex){
            throw new RuntimeException(ex);
        }
        
    }
}
