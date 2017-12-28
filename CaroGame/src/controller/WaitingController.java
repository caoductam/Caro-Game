/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import message.ClientMessage;
import view.Waiting;

/**
 *
 * @author Nam
 */
public class WaitingController {
    
    MainController mainControl;
    Waiting waitingDialog;
    
    public WaitingController(MainController mainControl) {
        this.mainControl = mainControl;
        this.waitingDialog = new Waiting(this);
        waitingDialog.setVisible(true);
        this.waitingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
            
        });
    }
    
    public void setUsername(String opposite) {
        waitingDialog.lblUsername.setText(opposite);
    }
    
    public void cancel() {
        //send message to cancel invite
        ClientMessage message = new ClientMessage();
        message.setFrom(mainControl.username);
        message.setTo(waitingDialog.lblUsername.getText());
        message.setCommand(ClientMessage.CANCEL_INVITE);
        mainControl.sendMessageToServer(message);
        //close waiting screen
        closeDialog();
        //enable invite function
        mainControl.roomControl.enableInvite();
    }
    
    public void closeDialog() {
        waitingDialog.dispose();
    }
}
