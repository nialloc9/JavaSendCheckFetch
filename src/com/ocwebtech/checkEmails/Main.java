/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.checkEmails;

/**
 *
 * @author Niall
 */
public class Main {
    public static void main(String[] args) {
        CheckEmails ce = new CheckEmails("emailAddress", "EmailPassword", "smtp.gmail.com"); //smtp.gmail.com -> host
        ce.setUpSession();
        ce.createStore();
        ce.checkMessages("INBOX");
    }
}
