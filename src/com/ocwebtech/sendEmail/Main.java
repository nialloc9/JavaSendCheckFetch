/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.sendEmail;

/**
 *
 * @author Niall
 */
public class Main {
    public static void main(String[] args) {
        SendEmail se = new SendEmail("emailAddress", "EmailPassword", "smtp.gmail.com"); //smtp.gmail.com -> host
        se.setUpSession();
        se.sendMessage("nialloc9@gmail.com", "Yo", "cool");
    }
    
}
