/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nam
 */
public class IOController {

    public static ArrayList<String> getUsers(String path) {
        ArrayList<String> users = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String user = reader.readLine();
            while (user != null) {
                users.add(user);
                user = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }
}
