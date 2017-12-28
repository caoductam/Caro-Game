/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.IOController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.ClientMessage;
import message.ServerMessage;
import model.UserInfo;

/**
 *
 * @author Nam
 */
public class Server {

    ServerSocket server;
    ArrayList<String> users;
    HashMap<String, UserInfo> onlines;

    public Server() {
        onlines = new HashMap<>();
        users = IOController.getUsers("files/users.txt");
    }

    public void start() {
        try {
            server = new ServerSocket(9999);
            ClientAccepter accepter = new ClientAccepter();
            accepter.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class ClientAccepter extends Thread {

        @Override
        public void run() {
            System.out.println("Server starting........!");
            while (true) {
                try {
                    Socket socket = server.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    handler.start();
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public class ClientHandler extends Thread {

        //served client's information
        Socket socket;
        String username;
        boolean running;

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                running = true;
                while (running) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ClientMessage message = (ClientMessage) ois.readObject();
                    switch (message.getCommand()) {
                        case ClientMessage.LOGIN:
                            checkLogin(message.getFrom());
                            break;
                        case ClientMessage.LOGOUT:
                            setOffline();
                            notifyLogout();
                            running = false;
                            break;
                        case ClientMessage.INVITE:
                            //forward invite mess
                            processInviteMessage(message.getTo(), message.getSize());
                            break;
                        case ClientMessage.CANCEL_INVITE:
                            //send obj to other notify that inviter has canceled
                            transferMessage(message.getTo(), ServerMessage.INVITE_CANCELED);
                            break;
                        case ClientMessage.REJECT:
                            transferMessage(message.getTo(), ServerMessage.INVITE_REJECT);
                            break;
                        case ClientMessage.ACCEPT:
                            //user name is accepter, message.to is inviter
                            setGameParameters(message.getFrom(), message.getTo(), message.getSize());
                            break;
                        case ClientMessage.EXIT_GAME:
                            setExitGame();
                            transferMessage(onlines.get(username).getOpposite(), ServerMessage.OPPOSITE_EXIT_GAME);
                            break;
                        case ClientMessage.TICK:
                            transferTick(message.getRow(), message.getCol());
                            break;
                        case ClientMessage.WIN:
                            //set free for both
                            setExitGame();
                            transferMessage(onlines.get(username).getOpposite(), ServerMessage.LOSE);
                            break;
                    }
                }
            } catch (IOException ex) {
                handleError("user " + username + " suddenly lost connection");
            } catch (ClassNotFoundException ex) {
                handleError("Class not found error");
            }
        }

        public void handleError(String error) {
            System.out.println(error);
            if (onlines.get(username).isBusy()) {
                setExitGame();
                transferMessage(onlines.get(username).getOpposite(), ServerMessage.OPPOSITE_EXIT_GAME);
            }
            setOffline();
            notifyLogout();
        }

        public void sendMessageToClient(ServerMessage message) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(message);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void sendMessageToOtherClient(ServerMessage message, String other) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(
                        onlines.get(other).getSocket().getOutputStream());
                oos.writeObject(message);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void checkLogin(String username) {
            ServerMessage message = new ServerMessage();
            //check on database if have username on registered
            //check on onlines list to sure that no duplicate username
            if (!users.contains(username) || onlines.containsKey(username)) {
                message.setCommand(ServerMessage.LOGIN_FAIL);
                running = false;
            } else {
                //set information send to new login
                this.username = username;
                message.setUsers(onlines.keySet().iterator());    //list of others
                message.setCommand(ServerMessage.LOGIN_SUCCESS);
                //notify to other onlines that a new user login
                notifyLogin();
                //add new login to list map to socket
                UserInfo info = new UserInfo();
                info.setSocket(socket);
                info.setBusy(false);
                onlines.put(username, info);
            }
            sendMessageToClient(message);
        }

        public void setOffline() {
            onlines.remove(username);
        }

        public void setExitGame() {
            //user exit the game and now can playing
            onlines.get(username).setBusy(false);
            onlines.get(onlines.get(username).getOpposite()).setBusy(false);
        }

        public void notifyLogin() {
            //send obj to others
            ServerMessage message = new ServerMessage();
            message.setCommand(ServerMessage.NEW_LOGIN);
            message.setFrom(username);
            for (String other : onlines.keySet()) {
                sendMessageToOtherClient(message, other);
            }
        }

        public void notifyLogout() {
            //send obj to others notify that a user just log out
            ServerMessage message = new ServerMessage();
            message.setCommand(ServerMessage.NEW_LOGOUT);
            message.setFrom(username);
            for (String other : onlines.keySet()) {
                sendMessageToOtherClient(message, other);
            }
        }

        public void processInviteMessage(String opposite, int size) {
            //if opposite already in game, then return the notify playing, else send invite
            ServerMessage message = new ServerMessage();
            if (onlines.get(opposite).isBusy()) {
                //opposite is playing another game, cant send invite
                message.setCommand(ServerMessage.USER_BUSY);
                sendMessageToClient(message);
            } else {
                message.setFrom(username);
                message.setSize(size);
                message.setCommand(ServerMessage.NEW_INVITE);
                sendMessageToOtherClient(message, opposite);
            }
        }

        public void transferMessage(String other, int command) {
            ServerMessage message = new ServerMessage();
            message.setFrom(username);
            message.setCommand(command);
            sendMessageToOtherClient(message, other);
        }

        public void setGameParameters(String accepter, String inviter, int size) {
            onlines.get(accepter).setBusy(true);
            onlines.get(inviter).setBusy(true);
            onlines.get(accepter).setOpposite(inviter);
            onlines.get(inviter).setOpposite(accepter);
            ServerMessage messageToAccepter = new ServerMessage();
            ServerMessage messageToInviter = new ServerMessage();
            messageToAccepter.setCommand(ServerMessage.START_GAME);
            messageToInviter.setCommand(ServerMessage.START_GAME);
            messageToAccepter.setFrom(inviter);
            messageToInviter.setFrom(accepter);
            //set size for both to create board
            messageToAccepter.setSize(size);
            messageToInviter.setSize(size);
            //check inviter turn is x or not? if not then random the X or O turn
            if (onlines.get(inviter).isX_turn()) {
                onlines.get(inviter).setX_turn(false);
                onlines.get(accepter).setX_turn(true);
                messageToAccepter.setX_turn(true);
                messageToInviter.setX_turn(false);
            } else {
                onlines.get(inviter).setX_turn(true);
                onlines.get(accepter).setX_turn(false);
                messageToAccepter.setX_turn(false);
                messageToInviter.setX_turn(true);
            }
            //send message go to game successfully
            sendMessageToClient(messageToAccepter);
            sendMessageToOtherClient(messageToInviter, inviter);
        }

        public void transferTick(int row, int col) {
            ServerMessage message = new ServerMessage();
            message.setRow(row);
            message.setCol(col);
            message.setCommand(ServerMessage.TICK);
            sendMessageToOtherClient(message, onlines.get(username).getOpposite());
        }
    }
}
