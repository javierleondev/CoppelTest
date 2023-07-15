
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import view.MainUI;

public class MainUIController implements ActionListener{
    //Recibe el objeto de interfaz desde la clase main, para acceder a sus botones de interfaz
    private MainUI mainUI;
    
    //Constructor por defecto
    public MainUIController(MainUI mainUI){
        //Enlazo la vista recibida desde el metodo main, con la referencia propia a esta vista y le añado sus eventos listener a los elementos de menu.
        this.mainUI = mainUI;
        this.mainUI.altaEmpleado.addActionListener(this);
        this.mainUI.altaRol.addActionListener(this);
        this.mainUI.reporteNomina.addActionListener(this);
        this.inicializarVista();
    }
    
    //Ya que se inicializa la vista, se cargar parametros por defecto y se hace visible
    void inicializarVista(){
        mainUI.setTitle("Coppel Test");
        mainUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainUI.setLocationRelativeTo(null);
        
        //Mostrar la interfaz principal
        mainUI.setVisible(true);
        System.out.println("Inicie");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == this.mainUI.altaEmpleado){
            //Mostrar interfaz para dar de alta a un cliente
        } else if ( e.getSource() == this.mainUI.altaRol){
            //Mostrar interfaz para dar de alta roles
        }else{
            //Mostrar interfaz para ver vista de reporte
        }
    }
    
}
