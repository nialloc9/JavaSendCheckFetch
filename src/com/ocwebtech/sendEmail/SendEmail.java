package com.ocwebtech.sendEmail;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Niall
 */
public class SendEmail {

    private final String user;
    private final String pass;
    private final String host;//for google this is smtp.gmail.com
    private Session session;
    
    //username, password, host
    public SendEmail(String u, String p, String h) {
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
            
        }catch(MessagingException ex){
            ex.printStackTrace();
        }
    }
}