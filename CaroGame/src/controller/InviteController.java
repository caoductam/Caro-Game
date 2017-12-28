/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import message.ClientMessage;
import view.Invite;

/**
 *
 * @author Nam
 */
public class InviteController {

    MainController mainControl;
    Invite inviteDialog;

    public InviteController(MainController mainControl) {
        this.mainControl = mainControl;
        this.inviteDialog = new Invite(this);
        this.inviteDialog.setVisible(true);
        this.inviteDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                reject();
            }

        });
    }

    public void setInformation(String opposite, int size) {
        inviteDialog.lblSize.setText(size + " x " + size);
        inviteDialog.lblUsername.setText(opposite);
    }

    public void accept() {
        String chooser = inviteDialog.lblUsername.getText();
        //create board game and reject other
        ClientMessage message = new ClientMessage();
        message.setFrom(mainControl.username);
        message.setTo(chooser);
        message.setSize(Integer.parseInt(inviteDialog.lblSize.getText().split(" x ")[0]));
        message.setCommand(ClientMessage.ACCEPT);
        mainControl.sendMessageToServer(message);
        mainControl.rejectOther(chooser);
        mainControl.inviters.remove(chooser);
        closeDialog();
    }

    public void reject() {
        //send message to server
        ClientMessage message = new ClientMessage();
        message.setFrom(mainControl.username);
        message.setTo(inviteDialog.lblUsername.getText());
        message.setCommand(ClientMessage.REJECT);
        mainControl.sendMessageToServer(message);
        closeDialog();
        mainControl.roomControl.enableInvite();
    }

    public void closeDialog() {
        inviteDialog.dispose();
    }
}
