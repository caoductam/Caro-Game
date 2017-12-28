/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;

/**
 *
 * @author Nam
 */
public class ClientMessage extends Message implements Serializable {

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int INVITE = 3;
    public static final int CANCEL_INVITE = 4;
    public static final int ACCEPT = 5;
    public static final int REJECT = 6;
    public static final int EXIT_GAME = 7;
    public static final int TICK = 8;
    public static final int WIN = 9;

}
