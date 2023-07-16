package controller;

import db.EmpleadoDAO;
import db.MovimientosDAO;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import model.Empleado;
import model.Movimientos;
import view.EmpleadoView;
import view.MainUI;
import view.MovimientosView;
import view.ReporteView;

public class MainUIController implements ActionListener {

    //Recibe el objeto de interfaz desde la clase main, para acceder a sus botones de interfaz
    private MainUI mainUI;
    ImageIcon imgIcon = new ImageIcon("src/main/java/images/Coppel.png");


    //Constructor por defecto
    public MainUIController(MainUI mainUI) {
        //Enlazo la vista recibida desde el metodo main, con la referencia propia a esta vista y le añado sus eventos listener a los elementos de menu.
        this.mainUI = mainUI;
        this.mainUI.setLayout(new FlowLayout());
        //Agregar listener a los botones
        this.mainUI.altaEmpleado.addActionListener(this);
        this.mainUI.altaRol.addActionListener(this);
        this.mainUI.reporteNomina.addActionListener(this);
        //Inicializar los parametros del frame
        this.initializeView();
    }

    //Ya que se inicializa la vista, se cargar parametros por defecto y se hace visible
    void initializeView() {

        mainUI.setTitle("Examen Coppel");
        mainUI.setIconImage(imgIcon.getImage());
        mainUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background color
        mainUI.getContentPane().setBackground(new Color(255, 227, 11));
        mainUI.setLocationRelativeTo(null);

        //Mostrar la interfaz principal
        mainUI.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.mainUI.altaEmpleado) {
            //Crear objetos para bindearlos al controlador
            EmpleadoView empleadoView = new EmpleadoView();
            Empleado empleado = new Empleado();
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            EmpleadoController empleadoController = new EmpleadoController(empleadoView, empleado, empleadoDAO);

            //remover un panel previamente abierto
            this.mainUI.getContentPane().removeAll();
            //Añadir el panel que da de alta usuarios, al mainUI
            this.mainUI.add(empleadoView);
            this.mainUI.revalidate();
            this.mainUI.repaint();

        } else if (e.getSource() == this.mainUI.altaRol) {
            //Crear objetos para bindearlos al controlador
            MovimientosView movimientosView = new MovimientosView();
            Movimientos movimientos = new Movimientos();
            MovimientosDAO movimientosDAO = new MovimientosDAO();
            MovimientosController movimientosController = new MovimientosController(movimientosView, movimientos, movimientosDAO);

            //remover un panel previamente abierto
            this.mainUI.getContentPane().removeAll();
            //Añadir el panel que da de alta usuarios, al mainUI
            this.mainUI.add(movimientosView);
            this.mainUI.revalidate();
            this.mainUI.repaint();
        } else if (e.getSource() == this.mainUI.reporteNomina) {
            //Crear objetos para bindearlos al controlador
            ReporteView reporteView = new ReporteView();
            Movimientos movimientos = new Movimientos();
            MovimientosDAO movimientosDAO = new MovimientosDAO();
            ReporteController reporteController = new ReporteController(reporteView, movimientos, movimientosDAO);

            //remover un panel previamente abierto
            this.mainUI.getContentPane().removeAll();
            //Añadir el panel que da de alta usuarios, al mainUI
            this.mainUI.add(reporteView);
            this.mainUI.revalidate();
            this.mainUI.repaint();
        }
    }
}
