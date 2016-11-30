package com.ocwebtech.email;

/**
 *
 * @author Niall
 */
public class Main {
    
    public static void main(String[] args) {
        
        //set up
        Email e = new Email("emailAddress", "EmailPassword", "smtp.gmail.com"); //smtp.gmail.com -> host
        e.setUpSession();
        
        /*
        //send
        e.sendMessage("recepiantsEmailAddress", "Yo", "cool");
        e.sendAttachment("recepiantsEmailAddress", "FromEmailAddress", "attachment", "attachment", "c:/java/useful_sites.txt");
        e.sendHtmlEmail("recepiantsEmailAddress", "FromEmailAddress", "html message", "<h1>This is a kick as message embedded in</h1><br /><p>html tags.</p>");
        e.sendHtmlWithImage("recepiantsEmailAddress", "FromEmailAddress", "Email with image", "<h1>This is an awesome image</h1>", "c:/java/emailReceived.png");
        */
        
        //read
        e.createStore();
        //e.checkMessages("INBOX");
        e.fetchMessages("INBOX");
        
        
    }
}
