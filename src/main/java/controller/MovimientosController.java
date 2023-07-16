package controller;

import db.MovimientosDAO;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JOptionPane;
import model.Empleado;
import model.Movimientos;
import view.MovimientosView;

public class MovimientosController implements ActionListener, KeyListener {

    //Variables para bindear el modelo y la vista
    private MovimientosView movimientosView;
    private Movimientos movimientos;
    private MovimientosDAO movimientosDAO;

    public MovimientosController(MovimientosView movimientosView, Movimientos movimientos, MovimientosDAO movimientosDAO) {
        //Bindear los objetos del constructor con los objetos locales
        this.movimientosView = movimientosView;
        this.movimientos = movimientos;
        this.movimientosDAO = movimientosDAO;
        //Añadir evento de listener a los botones de la interfaz
        this.movimientosView.btnGuardar.addActionListener(this);
        this.movimientosView.btnSalir.addActionListener(this);
        this.movimientosView.txtNumeroEmpleado.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.movimientosView.btnGuardar) {
            //Validaciones, seleccionar mes, solo digitos en número empleado, ingresar cantidad entregas, ingresar cantidad horas, validar tipo de dato para entrega y horas
            if ("Seleccionar".equals((String) this.movimientosView.ComboMeses.getSelectedItem())) {
                JOptionPane.showMessageDialog(this.movimientosView, "Selecciona un mes válido, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!this.movimientosView.txtNumeroEmpleado.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this.movimientosView, "Favor de ingresar sólo digitos en el número de empleado", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(this.movimientosView.txtNombre.getText())) {
                JOptionPane.showMessageDialog(this.movimientosView, "Asegurese de haber ingresado un ID de usuario válido", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(this.movimientosView.txtCantEntregas.getText())) {
                JOptionPane.showMessageDialog(this.movimientosView, "Recuerde ingresar una cantidad de entregas realizadas", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(this.movimientosView.txtHorasTrabajadas.getText())) {
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese la cantidad de horas que laboró el empleado, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!this.movimientosView.txtHorasTrabajadas.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese sólo números para registrar las horas, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!this.movimientosView.txtCantEntregas.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese sólo números para indicar la cantidad de entregas, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                //Validar si ya hay registros para ese empleado en el mes seleccionado
                boolean existeRegistro;
                existeRegistro = this.movimientosDAO.getMovementsByMonthAndEmployee((String) this.movimientosView.ComboMeses.getSelectedItem(), Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
                if (existeRegistro) {
                    //Hacer un update al registro existente y sumar las horas trabajadas y el número de entregas
                    this.movimientosDAO.updateMovementsByMonthAndEmployee((String) this.movimientosView.ComboMeses.getSelectedItem(),
                            Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()), Integer.parseInt(this.movimientosView.txtCantEntregas.getText()),
                            Integer.parseInt(this.movimientosView.txtHorasTrabajadas.getText()));
                } else {
                    //De lo contrario, hacer un insert con los valores del form
                    //Bindear información de la vista con el modelo
                    this.setDeliveryData();
                    //Mandar la info del modelo para guardar en la base de datos
                    this.movimientosDAO.guardar(movimientos);
                }
                JOptionPane.showMessageDialog(null, "¡Movimiento registrado con éxito!", "Aviso", JOptionPane.DEFAULT_OPTION);
                this.clearFields();
            }
        } else if (e.getSource() == this.movimientosView.btnSalir) {
            Container contenedor = this.movimientosView.getParent();
            // Eliminar el JPanel del contenedor.
            contenedor.remove(this.movimientosView);
            // Revalidar y repintar el contenedor para reflejar el cambio.
            contenedor.revalidate();
            contenedor.repaint();
        }
    }

    //Enlazar la información de la vista con el modelo de movimientos
    void setDeliveryData() {
        this.movimientos.setIdEmployee(Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
        this.movimientos.setMonth((String) this.movimientosView.ComboMeses.getSelectedItem());
        this.movimientos.setDeliveries(Integer.parseInt(this.movimientosView.txtCantEntregas.getText()));
        this.movimientos.setTotalHours(Integer.parseInt(this.movimientosView.txtHorasTrabajadas.getText()));
    }

    //Limpiar los valores de las cajas de texto de la vista
    void clearFields() {
        this.movimientosView.ComboMeses.setSelectedIndex(0);
        this.movimientosView.txtCantEntregas.setText("");
        this.movimientosView.txtHorasTrabajadas.setText("");
        this.movimientosView.txtNombre.setText("");
        this.movimientosView.txtNumeroEmpleado.setText("");
        this.movimientosView.txtRol.setText("");
    }

    public void keyPressed(KeyEvent e) {
        //Escuchar cuando se presione enter despues de ingresar el ID para buscar en la BD
        if (e.getSource() == movimientosView.txtNumeroEmpleado) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                List<Empleado> employees;
                //Buscar empleado en base a su número (ingresado en la interfaz)
                employees = this.movimientosDAO.getEmployeeByNumber(Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
                if (!employees.isEmpty()) {
                    //Si existe registro, muestra el nombre y el cargo del empleado en la interfaz al cual se registrará el movimiento
                    Empleado employee = employees.get(0);
                    this.movimientosView.txtNombre.setText(employee.getName());
                    this.movimientosView.txtRol.setText(String.valueOf(employee.getEmployeeType()));
                } else {
                    //Si no se encuentras records en la base de datos, se indica que no existe ese empleado
                    JOptionPane.showMessageDialog(this.movimientosView, "No existe ese empleado en la base de datos, intenta de nuevo");
                    this.clearFields();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
