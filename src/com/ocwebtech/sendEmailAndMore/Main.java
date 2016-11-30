/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.sendEmailAndMore;

/**
 *
 * @author Niall
 */
public class Main {
    public static void main(String[] args) {
        Send s = new Send("emailAddress", "EmailPassword", "smtp.gmail.com"); //smtp.gmail.com -> host
        s.setUpSession();
        s.sendMessage("recepiantsEmailAddress", "Yo", "cool");
        s.sendAttachment("recepiantsEmailAddress", "FromEmailAddress", "attachment", "attachment", "c:/java/useful_sites.txt");
        s.sendHtmlEmail("recepiantsEmailAddress", "FromEmailAddress", "html message", "<h1>This is a kick as message embedded in</h1><br /><p>html tags.</p>");
        s.sendHtmlWithImage("recepiantsEmailAddress", "FromEmailAddress", "Email with image", "<h1>This is an awesome image</h1>", "c:/java/emailReceived.png");
        
        
    }
}
