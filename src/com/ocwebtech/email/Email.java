package com.ocwebtech.email;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Properties;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
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
public class Email {

    private final String user;
    private final String pass;
    private final String host;//for google this is smtp.gmail.com
    private Session session;
    private Store store;
    private Folder folder;
    private Message[] message;
    
    //username, password, host
    public Email(String u, String p, String h) {
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
            
            //loop through and print out data
            for(int i=0;i<message.length;i++){
                System.out.println("---------------------------------" + i);
                writePart(message[i]);
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
