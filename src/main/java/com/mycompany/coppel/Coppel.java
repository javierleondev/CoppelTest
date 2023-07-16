package com.mycompany.coppel;

import controller.MainUIController;
import view.MainUI;

public class Coppel {

    public static void main(String[] args) {
        //Crear objeto de la interfaz principal y bindearla con su controlador
        MainUI mainUI = new MainUI();
        MainUIController mainUIController = new MainUIController(mainUI);
    }
}
