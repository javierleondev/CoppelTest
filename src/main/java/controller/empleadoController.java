
package controller;

import db.EmpleadoDAO;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import model.Empleado;
import view.EmpleadoView;

public class EmpleadoController implements ActionListener {
    //Variables para bindear el modelo y la vista
    private EmpleadoView empleadoView;
    private Empleado empleado;
    private EmpleadoDAO empleadoDAO;
    
    //Constructor por defecto
    public EmpleadoController(EmpleadoView empleadoView, Empleado empleado, EmpleadoDAO empleadoDAO){
        this.empleadoView = empleadoView;
        this.empleado = empleado;
        this.empleadoDAO = empleadoDAO;
        this.empleadoView.btnCancelar.addActionListener(this);
        this.empleadoView.btnGuardar.addActionListener(this);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.empleadoView.btnGuardar){
            if((String) this.empleadoView.ComboRoles.getSelectedItem() == "Seleccionar"){
                JOptionPane.showMessageDialog(this.empleadoView, "Selecciona un rol válido, por favor","Error", JOptionPane.ERROR_MESSAGE);
            }else if (!this.empleadoView.txtNumeroEmpleado.getText().matches("\\d+")) { 
                JOptionPane.showMessageDialog(this.empleadoView, "Favor de ingresar sólo digitos en el número de empleado", "Error", JOptionPane.ERROR_MESSAGE);
            }else if(this.empleadoView.txtNombreEmpleado.getText() == ""){
                JOptionPane.showMessageDialog(this.empleadoView, "Ingrese un nombre de empleado válido","Error", JOptionPane.ERROR_MESSAGE);
            }else{
                //Bindear información de la vista con el modelo
                this.setEmployeeData();
                //Mandar la info del modelo para guardar en la base de datos
                this.empleadoDAO.guardar(empleado);
                JOptionPane.showMessageDialog(null, "¡Empleado registrado con éxito!", "Aviso", JOptionPane.DEFAULT_OPTION);
                this.clearFields();
            }
        }else if (e.getSource() == this.empleadoView.btnCancelar){
            Container contenedor = this.empleadoView.getParent();
            // Eliminar el JPanel del contenedor.
            contenedor.remove(this.empleadoView);
            // Revalidar y repintar el contenedor para reflejar el cambio.
            contenedor.revalidate();
            contenedor.repaint();
        }
    }
    
    //Bind valores de la view al modelo
    void setEmployeeData(){
        this.empleado.setName(this.empleadoView.txtNombreEmpleado.getText());
        this.empleado.setNumber(Integer.parseInt(this.empleadoView.txtNumeroEmpleado.getText()));
        this.empleado.setEstatus(1);
        this.empleado.setEmployeeType((String) this.empleadoView.ComboRoles.getSelectedItem());
    }
    
    void clearFields(){
        this.empleadoView.txtNombreEmpleado.setText("");
        this.empleadoView.txtNumeroEmpleado.setText("");
        this.empleadoView.ComboRoles.setSelectedIndex(0);
    }
}
