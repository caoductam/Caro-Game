/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import message.ClientMessage;
import view.CaroRoom;

/**
 *
 * @author Nam
 */
public class CaroRoomController {

    MainController mainControl;
    CaroRoom room;
    DefaultListModel model;

    public CaroRoomController(MainController mainControl) {
        this.mainControl = mainControl;
        this.room = new CaroRoom(this);
        room.setVisible(true);
        room.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }

        });
    }

    public void loadUserList(ArrayList<String> users) {
        model = new DefaultListModel();
        if (users != null) {
            for (String user : users) {
                model.addElement(user);
            }
        }
        room.listUser.setModel(model);
    }

    public void setUserName() {
        room.lblUserName.setText(mainControl.username);
    }

    public void closeScreen() {
        room.dispose();
    }

    public void logout() {
        //send log out message to server, close screen and show login form
        ClientMessage message = new ClientMessage();
        message.setFrom(mainControl.username);
        message.setCommand(ClientMessage.LOGOUT);
        mainControl.sendMessageToServer(message);
        closeScreen();
        mainControl.loginControl.visibleLoginForm();
        mainControl.running = false;
    }

    public void addNewLogin(String username) {
        //add new user to JList
        model.addElement(username);
    }

    public void removeNewLogout(String username) {
        model.remove(model.indexOf(username));
    }

    public void invite() {
        //send invite to another user
        String username = (String) room.listUser.getSelectedValue();
        if (username == null) {
            JOptionPane.showMessageDialog(room, "You must choose opposite to send invite");
        } else {
            try {
                int size = Integer.parseInt(room.txtSize.getText());
                if (size < 5 || size > 10) {
                    JOptionPane.showMessageDialog(room, "You must choose size between 5 and 10");
                } else {
                    //create waiting dialog to wait for accept
                    mainControl.createWaitingDialog();
                    mainControl.waitControl.setUsername(username);
                    disableInvite();
                    ClientMessage message = new ClientMessage();
                    //send message
                    message.setFrom(mainControl.username);
                    message.setTo(username);
                    message.setSize(size);
                    message.setCommand(ClientMessage.INVITE);
                    mainControl.sendMessageToServer(message);
                    
                    
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(room, "You must assign the board size");
            }
        }
    }

    public void disableInvite() {
        room.btnInvite.setEnabled(false);
    }

    public void enableInvite() {
        room.btnInvite.setEnabled(true);
    }

    public void showAlert(String notify) {
        //
        JOptionPane.showMessageDialog(room, notify);
    }

    public void invisibleScreen() {
        room.setVisible(false);
    }

    public void visibleScreen() {
        room.setVisible(true);
    }
}
