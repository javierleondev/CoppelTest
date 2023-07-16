
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
    
    public MovimientosController(MovimientosView movimientosView, Movimientos movimientos, MovimientosDAO movimientosDAO){
        this.movimientosView = movimientosView;
        this.movimientos = movimientos;
        this.movimientosDAO = movimientosDAO;
        this.movimientosView.btnGuardar.addActionListener(this);
        this.movimientosView.btnSalir.addActionListener(this);
        this.movimientosView.txtNumeroEmpleado.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.movimientosView.btnGuardar){
            if((String) this.movimientosView.ComboMeses.getSelectedItem() == "Seleccionar"){
                JOptionPane.showMessageDialog(this.movimientosView, "Selecciona un mes válido, por favor","Error", JOptionPane.ERROR_MESSAGE);
            }else if (!this.movimientosView.txtNumeroEmpleado.getText().matches("\\d+")) { 
                JOptionPane.showMessageDialog(this.movimientosView, "Favor de ingresar sólo digitos en el número de empleado", "Error", JOptionPane.ERROR_MESSAGE);
            }else if(this.movimientosView.txtNombre.getText() == ""){
                JOptionPane.showMessageDialog(this.movimientosView, "Asegurese de haber ingresado un ID de usuario válido","Error", JOptionPane.ERROR_MESSAGE);
            }else if(this.movimientosView.txtCantEntregas.getText() == ""){
                JOptionPane.showMessageDialog(this.movimientosView, "Recuerde ingresar una cantidad de entregas realizadas","Error", JOptionPane.ERROR_MESSAGE);
            }else if(this.movimientosView.txtHorasTrabajadas.getText() == ""){
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese la cantidad de horas que laboró el empleado, por favor","Error", JOptionPane.ERROR_MESSAGE);
            }else if (!this.movimientosView.txtHorasTrabajadas.getText().matches("\\d+")) { 
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese sólo números para registrar las horas, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            }else if (!this.movimientosView.txtCantEntregas.getText().matches("\\d+")) { 
                JOptionPane.showMessageDialog(this.movimientosView, "Ingrese sólo números para indicar la cantidad de entregas, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            }else{
                //Validar si ya hay registros para ese empleado en el mes seleccionado
                boolean existeRegistro;
                existeRegistro = this.movimientosDAO.getMovementsByMonthAndEmployee((String) this.movimientosView.ComboMeses.getSelectedItem(), Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
                if(existeRegistro){
                    //Hacer un update al registro existente y sumar las horas trabajadas y el número de entregas
                    this.movimientosDAO.updateMovementsByMonthAndEmployee((String) this.movimientosView.ComboMeses.getSelectedItem(), 
                            Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()), Integer.parseInt(this.movimientosView.txtCantEntregas.getText()),
                            Integer.parseInt(this.movimientosView.txtHorasTrabajadas.getText()));
                }else{
                    //De lo contrario, hacer un insert con los valores del form
                    //Bindear información de la vista con el modelo
                    this.setEntregasData();
                    //Mandar la info del modelo para guardar en la base de datos
                    this.movimientosDAO.guardar(movimientos);
                }
                JOptionPane.showMessageDialog(null, "¡Movimiento registrado con éxito!", "Aviso", JOptionPane.DEFAULT_OPTION);
                this.clearFields();
            }
        }else if(e.getSource() == this.movimientosView.btnSalir){
            Container contenedor = this.movimientosView.getParent();
            // Eliminar el JPanel del contenedor.
            contenedor.remove(this.movimientosView);
            // Revalidar y repintar el contenedor para reflejar el cambio.
            contenedor.revalidate();
            contenedor.repaint();
        }
    }
    

    void setEntregasData(){
        this.movimientos.setIdEmployee(Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
        this.movimientos.setMonth((String) this.movimientosView.ComboMeses.getSelectedItem());
        this.movimientos.setDeliveries(Integer.parseInt(this.movimientosView.txtCantEntregas.getText()));
        this.movimientos.setTotalHours(Integer.parseInt(this.movimientosView.txtHorasTrabajadas.getText()));
    }
    
    void clearFields(){
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
                System.out.println("ID A BUSCAR: "+this.movimientosView.txtNumeroEmpleado.getText());
                employees = this.movimientosDAO.getEmployeeByNumber(Integer.parseInt(this.movimientosView.txtNumeroEmpleado.getText()));
                if (!employees.isEmpty()) {
                    Empleado employee = employees.get(0);
                    this.movimientosView.txtNombre.setText(employee.getName());
                    this.movimientosView.txtRol.setText(String.valueOf(employee.getEmployeeType()));
                }else{
                    JOptionPane.showMessageDialog(this.movimientosView, "No existe ese empleado en la base de datos, intenta de nuevo");
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
