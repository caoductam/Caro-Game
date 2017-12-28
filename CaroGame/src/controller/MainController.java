/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.JOptionPane;
import message.ClientMessage;
import message.ServerMessage;

/**
 *
 * @author Nam
 */
public class MainController {

    CaroGameController gameControl;
    CaroRoomController roomControl;
    LoginController loginControl;
    WaitingController waitControl;
    HashMap<String, InviteController> inviters;

    String username;
    Socket socket;
    boolean running;

    public void start() {
        //where to start a program
        this.loginControl = new LoginController(this);
    }

    public void connectServer() {
        try {
            socket = new Socket("localhost", 9999);
            //create obj to read object from server
            ServerListener listener = new ServerListener();
            listener.start();
        } catch (IOException ex) {
            System.out.println("IO error");
        }
    }

    public void sendMessageToServer(ClientMessage message) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException ex) {
            System.out.println("IO error");
        }
    }

    public void createCaroRoom() {
        roomControl = new CaroRoomController(this);
    }

    public void createWaitingDialog() {
        waitControl = new WaitingController(this);
    }

    public void createInviteDialog(String opposite, int size) {
        InviteController inviteControl = new InviteController(this);
        inviteControl.setInformation(opposite, size);
        inviters.put(opposite, inviteControl);
        roomControl.disableInvite();
    }

    public void createCaroGameScreen(int size) {
        gameControl = new CaroGameController(this);
        gameControl.createBoard(size);
        gameControl.setUsername();
    }

    public void setUserName() {
        username = loginControl.loginForm.txtUserName.getText();
    }

    public void shutdownProgram(String notify) {
        //shut down immediately 
        roomControl.closeScreen();
        loginControl.visibleLoginForm();
        JOptionPane.showMessageDialog(loginControl.loginForm, notify);
    }

    public void closeInviteDialog(String opposite) {
        inviters.get(opposite).closeDialog();
        inviters.remove(opposite);
    }

    public void rejectOther(String chooser) {
        //when user accept "chooser" client to go play game
        //send message to other who also send invite to play game
        for (String other : inviters.keySet()) {
            if (!other.equals(chooser)) {
                inviters.get(other).reject();
                inviters.remove(other);
            }
        }
    }

    class ServerListener extends Thread {

        @Override
        public void run() {
            running = true;
            try {
                while (running) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ServerMessage message = (ServerMessage) ois.readObject();
                    //handle each case of server response
                    switch (message.getCommand()) {
                        case ServerMessage.LOGIN_SUCCESS:
                            //show caro room and list of other users
                            createCaroRoom();
                            loginControl.invisibleLoginForm();
                            setUserName();
                            roomControl.setUserName();
                            roomControl.loadUserList(message.getUsers());
                            inviters = new HashMap<>();
                            break;
                        case ServerMessage.LOGIN_FAIL:
                            loginControl.showLoginFail();
                            running = false;
                            break;
                        case ServerMessage.NEW_LOGIN:
                            roomControl.addNewLogin(message.getFrom());
                            break;
                        case ServerMessage.NEW_LOGOUT:
                            roomControl.removeNewLogout(message.getFrom());
                            break;
                        case ServerMessage.NEW_INVITE:
                            //crate Invite dialog to accept or reject
                            createInviteDialog(message.getFrom(), message.getSize());
                            break;
                        case ServerMessage.USER_BUSY:
                            waitControl.closeDialog();
                            roomControl.enableInvite();
                            roomControl.showAlert(
                                    (String) roomControl.room.listUser.getSelectedValue()
                                    + "is playing another game, cant send invite now");
                            break;
                        case ServerMessage.INVITE_CANCELED:
                            //close invite dialog
                            closeInviteDialog(message.getFrom());
                            roomControl.enableInvite();
                            roomControl.showAlert("user " + message.getFrom()
                                    + "has canceled the invite");
                            break;
                        case ServerMessage.INVITE_REJECT:
                            //close waiting dialog
                            waitControl.closeDialog();
                            roomControl.enableInvite();
                            roomControl.showAlert("user " + message.getFrom()
                                    + "has rejected the invite");
                            break;
                        case ServerMessage.START_GAME:
                            //open game
                            //create game screen
                            createCaroGameScreen(message.getSize());
                            if (waitControl != null && waitControl.waitingDialog.isDisplayable()) {
                                //if this user is inviter, close waiting dialog
                                waitControl.closeDialog();
                            }
                            gameControl.setOpposite(message.getFrom());
                            gameControl.setTurn(message.isX_turn());
                            roomControl.invisibleScreen();
                            break;
                        case ServerMessage.OPPOSITE_EXIT_GAME:
                            gameControl.closeScreen();
                            roomControl.showAlert("Your opposite has exited the game");
                            roomControl.visibleScreen();
                            roomControl.enableInvite();
                            break;
                        case ServerMessage.TICK:
                            gameControl.takeTick(message.getRow(), message.getCol());
                            break;
                        case ServerMessage.LOSE:
                            roomControl.showAlert("You lose");
                            gameControl.closeScreen();
                            roomControl.visibleScreen();
                            roomControl.enableInvite();
                            break;
                    }
                }
            } catch (IOException ex) {
                shutdownProgram("Server suddenly shutdown");
            } catch (ClassNotFoundException ex) {
                shutdownProgram("Can't found class when read from server");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
