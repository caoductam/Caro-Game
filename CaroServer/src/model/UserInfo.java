/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.net.Socket;

/**
 *
 * @author Nam
 */
public class UserInfo {
    private boolean busy;
    private Socket socket;
    private String opposite;
    private boolean x_turn;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getOpposite() {
        return opposite;
    }

    public void setOpposite(String opposite) {
        this.opposite = opposite;
    }

    public boolean isX_turn() {
        return x_turn;
    }

    public void setX_turn(boolean x_turn) {
        this.x_turn = x_turn;
    }
    
    
}
