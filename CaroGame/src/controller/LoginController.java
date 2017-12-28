/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.swing.JOptionPane;
import message.ClientMessage;
import view.Login;

/**
 *
 * @author Nam
 */
public class LoginController {

    MainController mainControl;
    Login loginForm;

    public LoginController(MainController mainControl) {
        this.mainControl = mainControl;
        this.loginForm = new Login(this);
        loginForm.setVisible(true);
    }

    public void login() {
        mainControl.connectServer();
        ClientMessage message = new ClientMessage();
        message.setFrom(loginForm.txtUserName.getText());
        message.setCommand(ClientMessage.LOGIN);
        mainControl.sendMessageToServer(message);
    }

    public void invisibleLoginForm() {
        loginForm.setVisible(false);
    }

    public void visibleLoginForm() {
        loginForm.setVisible(true);
    }

    public void showLoginFail() {
        JOptionPane.showMessageDialog(loginForm, "login fail");
    }

}
