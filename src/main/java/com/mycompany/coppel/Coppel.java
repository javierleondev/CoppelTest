package com.mycompany.coppel;

import controller.MainUIController;
import javax.swing.JFrame;
import view.MainUI;

/**
 *
 * @author Pc
 */
public class Coppel {

    public static void main(String[] args) {
        //Crear objeto de la interfaz principal
        MainUI mainUI = new MainUI();
        MainUIController mainUIController = new MainUIController(mainUI);
    }
}
