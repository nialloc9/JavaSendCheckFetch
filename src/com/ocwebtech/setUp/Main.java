/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocwebtech.setUp;

/**
 *
 * @author Niall
 */
public class Main {
    public static void main(String[] args) {
        SetUp su = new SetUp("nialloc9@gmail.com", "ug1actic28", "smtp.gmail.com");
        su.setUpSession();
    }
}
