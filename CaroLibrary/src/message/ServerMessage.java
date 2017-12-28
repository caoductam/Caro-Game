/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Nam
 */
public class ServerMessage extends Message implements Serializable {

    private ArrayList<String> users;
    private boolean x_turn;
    public static final int LOGIN_SUCCESS = -1;
    public static final int LOGIN_FAIL = -2;
    public static final int NEW_LOGIN = -3;
    public static final int NEW_LOGOUT = -4;
    public static final int NEW_INVITE = -5;
    public static final int USER_BUSY = -6;
    public static final int START_GAME = -7;
    public static final int INVITE_REJECT = -8;
    public static final int INVITE_CANCELED = -9;
    public static final int OPPOSITE_EXIT_GAME = -10;
    public static final int TICK = -11;
    public static final int LOSE = -12;

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(Iterator<String> users) {
        this.users = new ArrayList<>();
        while (users.hasNext()) {
            this.users.add(users.next());
        }
    }

    public boolean isX_turn() {
        return x_turn;
    }

    public void setX_turn(boolean x_turn) {
        this.x_turn = x_turn;
    }

    //khi ai bi ban thi tn tu server gui se co turn la true, ban xong se set lai turn la false
}
