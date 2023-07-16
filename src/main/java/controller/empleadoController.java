package controller;

import db.EmpleadoDAO;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Empleado;
import view.EmpleadoView;

public class EmpleadoController implements ActionListener {

    //Variables para bindear el modelo y la vista
    private EmpleadoView empleadoView;
    private Empleado empleado;
    private EmpleadoDAO empleadoDAO;

    //Constructor por defecto
    public EmpleadoController(EmpleadoView empleadoView, Empleado empleado, EmpleadoDAO empleadoDAO) {
        this.empleadoView = empleadoView;
        this.empleado = empleado;
        this.empleadoDAO = empleadoDAO;
        //Añadir listeners a los botones de la interfaz
        this.empleadoView.btnCancelar.addActionListener(this);
        this.empleadoView.btnGuardar.addActionListener(this);
        //Cargar todos los empleados a la tabla al abrirse el panel
       
        this.getEmployees();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.empleadoView.btnGuardar) {
            if ("Seleccionar".equals((String) this.empleadoView.ComboRoles.getSelectedItem())) {
                JOptionPane.showMessageDialog(this.empleadoView, "Selecciona un rol válido, por favor", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!this.empleadoView.txtNumeroEmpleado.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this.empleadoView, "Favor de ingresar sólo digitos en el número de empleado", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(this.empleadoView.txtNombreEmpleado.getText())) {
                JOptionPane.showMessageDialog(this.empleadoView, "Ingrese un nombre de empleado válido", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean existeRegistro;
                existeRegistro = this.empleadoDAO.getEmployeeIDByNumber(Integer.parseInt(this.empleadoView.txtNumeroEmpleado.getText()));
                if (existeRegistro) {
                    JOptionPane.showMessageDialog(this.empleadoView, "Este número ya está en uso, prubea con otro, por favor", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    //Bindear información de la vista con el modelo
                    this.setEmployeeData();
                    //Mandar la info del modelo para guardar en la base de datos
                    this.empleadoDAO.guardar(empleado);
                    JOptionPane.showMessageDialog(null, "¡Empleado registrado con éxito!", "Aviso", JOptionPane.DEFAULT_OPTION);
                    //Limpiar campos y recargas tabla
                    this.clearFields();
                    this.getEmployees();
                }
            }
        } else if (e.getSource() == this.empleadoView.btnCancelar) {
            Container contenedor = this.empleadoView.getParent();
            // Eliminar el JPanel del contenedor.
            contenedor.remove(this.empleadoView);
            // Revalidar y repintar el contenedor para reflejar el cambio.
            contenedor.revalidate();
            contenedor.repaint();
        }
    }

    //Recuperar todos los empleados registrados en la base de datos
    public void getEmployees() {
        List<Empleado> empleados;
        empleados = this.empleadoDAO.getAllEmployees();

        //Crear el modelo de la tabla
        DefaultTableModel modelo;
        String[] titulos = {"Nombre", "Número", "Fecha Alta", "Cargo"}; // campos de la tabla
        String[] registros = new String[4];
        modelo = new DefaultTableModel(null, titulos);

        //Validar sí hay empleados
        if (!empleados.isEmpty()) {
            //Asignar los valores del reporte a la tabla
            for (int x = 0; x < empleados.size(); x++) {
                //Asignar los datos de la BD de empleados a las columans de la tabla
                registros[0] = String.valueOf(empleados.get(x).getName());
                registros[1] = String.valueOf((int)empleados.get(x).getNumber());
                registros[2] = String.valueOf(empleados.get(x).getDateRegister());
                registros[3] = String.valueOf(empleados.get(x).getEmployeeType());
                modelo.addRow(registros);
            }
        }
        //añade el registro al cuerpo de la tabla
        this.empleadoView.TableEmpleados.setModel(modelo);
    }

    //Bind valores de la view al modelo
    void setEmployeeData() {
        this.empleado.setName(this.empleadoView.txtNombreEmpleado.getText());
        this.empleado.setNumber(Integer.parseInt(this.empleadoView.txtNumeroEmpleado.getText()));
        this.empleado.setEstatus(1);
        this.empleado.setEmployeeType((String) this.empleadoView.ComboRoles.getSelectedItem());
    }

    //Limpiar los campos de las cajas de texto de la interfaz de empleados
    void clearFields() {
        this.empleadoView.txtNombreEmpleado.setText("");
        this.empleadoView.txtNumeroEmpleado.setText("");
        this.empleadoView.ComboRoles.setSelectedIndex(0);
    }
}
