/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import message.ClientMessage;
import view.CaroGame;

/**
 *
 * @author Nam
 */
public class CaroGameController {

    MainController mainControl;
    CaroGame gameScreen;
    boolean turn;   //only turn is true then user can tick X or O
    JLabel[][] board;
    boolean[][] clicked;
    int size;

    public CaroGameController(MainController mainControl) {
        this.mainControl = mainControl;
        this.gameScreen = new CaroGame(this);
        gameScreen.setVisible(true);
        gameScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }

        });
    }

    public void createBoard(int size) {
        this.size = size;
        board = new JLabel[size][size];
        clicked = new boolean[size][size];
        Border border = new LineBorder(Color.BLACK, 1, true);
        
        gameScreen.setSize(size * 60 + 5 * (size - 1) + 10, size * 60 + 5 * (size - 1) + 100);
        gameScreen.setResizable(false);
        gameScreen.panelBoard.setPreferredSize(new Dimension(size * 60, size * 60));
        gameScreen.panelBoard.setLayout(new GridLayout(size, size, 5, 5));
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JLabel label = new JLabel();
                label.setSize(60, 60);
                label.setOpaque(true);
                label.setBorder(border);
                label.setFont(new Font("default", Font.BOLD, 60));
                Point p = new Point(i, j);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tick(label, p.x, p.y);
                    }

                });
                board[i][j] = label;
                gameScreen.panelBoard.add(board[i][j]);
            }
        }
    }

    public void tick(JLabel label, int row, int col) {
        if (turn && label.getText().equals("")) {

            label.setText(gameScreen.lblTurn.getText());
            clicked[row][col] = true;
            ClientMessage message = new ClientMessage();
            message.setRow(row);
            message.setCol(col);
            message.setCommand(ClientMessage.TICK);
            mainControl.sendMessageToServer(message);

            if (checkWin()) {
                message.setCommand(ClientMessage.WIN);
                mainControl.sendMessageToServer(message);
                closeScreen();
                mainControl.roomControl.visibleScreen();
                mainControl.roomControl.showAlert("You win");
                mainControl.roomControl.enableInvite();
            } else {
                changeTurn();
                turn = false;
            }
        }
    }

//    public void printClick() {
//        for (int i = 0; i < clicked.length; i++) {
//            for (int j = 0; j < clicked.length; j++) {
//                System.out.print(clicked[i][j] ? " 1 " : " 0 ");
//            }
//            System.out.println("");
//        }
//    }
    public boolean checkWin() {
        //check if have 5 cells horizontally, vertically, or diagonally
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (j < size - 4 && clicked[i][j] && clicked[i][j + 1] && clicked[i][j + 2]
                        && clicked[i][j + 3] && clicked[i][j + 4]) {
                    //ngang
                    return true;
                } else if (i < size - 4 && clicked[i][j] && clicked[i + 1][j] && clicked[i + 2][j]
                        && clicked[i + 3][j] && clicked[i + 4][j]) {
                    //doc
                    return true;
                }
                if (i < size - 4 && j < size - 4 && clicked[i][j] && clicked[i + 1][j + 1] && clicked[i + 2][j + 2]
                        && clicked[i + 3][j + 3] && clicked[i + 4][j + 4]) {
                    //cheo sang ben phai
                    return true;
                } else if (i < size - 4 && j > 3 && clicked[i][j] && clicked[i + 1][j - 1] && clicked[i + 2][j - 2]
                        && clicked[i + 3][j - 3] && clicked[i + 4][j - 4]) {
                    //cheo sang ben trai
                    return true;
                }

            }
        }

        return false;
    }

    public void takeTick(int row, int col) {
        board[row][col].setText(gameScreen.lblTurn.getText());
        changeTurn();
        turn = true;
    }

    public void setUsername() {
        gameScreen.lblUserName.setText(mainControl.username);
    }

    public void setOpposite(String opposite) {
        gameScreen.lblOpposite.setText(opposite);
    }

    public void setTurn(boolean x_turn) {
        gameScreen.lblRole.setText(x_turn ? "X-player" : "O-player");
        gameScreen.lblTurn.setText("X");
        turn = x_turn;  //X will go first
    }

    public void changeTurn() {
        //if text is X then change to O, otherwise change it to X
        gameScreen.lblTurn.setText(gameScreen.lblTurn.getText().equals("X") ? "O" : "X");
    }

    public void exitGame() {
        //send message that exit the game
        ClientMessage message = new ClientMessage();
        message.setFrom(mainControl.username);
        message.setTo(gameScreen.lblOpposite.getText());
        message.setCommand(ClientMessage.EXIT_GAME);
        mainControl.sendMessageToServer(message);
        closeScreen();
        mainControl.roomControl.visibleScreen();
        mainControl.roomControl.enableInvite();
    }

    public void closeScreen() {
        gameScreen.dispose();
    }

}
