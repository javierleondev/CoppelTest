package controller;

import db.MovimientosDAO;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Movimientos;
import view.ReporteView;

public class ReporteController implements ActionListener, KeyListener {

    //Bindear el modelo y la vista a este controlador
    private ReporteView reporteView;
    private Movimientos movimientos;
    private MovimientosDAO movimientosDAO;

    //Variables constantes para las operaciones de nómina
    private final float sueldoBase;
    private final float bonoPorEntrega;
    private final float bonoChofer;
    private final float bonoCargadores;
    private final float ISRfijo;
    private final float ISRadicional;
    private final float bonoDespensa;

    //Validate which table model to create and query to call, 
    private boolean showAllPayroll;
    private float PayrollAccumulative;
    
    //Formatear números
    DecimalFormat decimalFormat;

    //Objecto para guardar temporalmente los datos calculados de nómina del empleado
    private Object[] nomina;

    public ReporteController(ReporteView reporteView, Movimientos movimientos, MovimientosDAO movimientosDAO) {
        //Inicialización de variables para nómina
        this.sueldoBase = 30.0f;
        this.bonoPorEntrega = 5.0f;
        this.bonoChofer = 10.0f;
        this.bonoCargadores = 5.0f;
        this.ISRfijo = .09f;
        this.ISRadicional = .12f;
        this.bonoDespensa = .04f;
        this.showAllPayroll = false;
        this.PayrollAccumulative = 0.0f;
        
        this.decimalFormat = new DecimalFormat("#,###.00");

        //Inicialización del objeto nómina
        nomina = new Object[7];
        //0 = sueldo base
        //1 = dinero por bono
        //2 = dinero por entregas
        //3 = retencion ISR
        //4 = subtotal, sin retencion de ISR o suma de despensa.
        //5 = vale despensa, calculado del subtotal sin considerar retenciones de ISR
        //6 = Total = subtotal menos retenciones de ISR, sin considerar vales de despensa

        //Inicializacion de variables vista-modelo y asignación de listeners
        this.reporteView = reporteView;
        this.movimientos = movimientos;
        this.movimientosDAO = movimientosDAO;
        this.reporteView.txtNumeroEmpleado.addKeyListener(this);
        this.reporteView.btnClose.addActionListener(this);
        this.reporteView.btnShowAllPayroll.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.reporteView.btnClose) {
            Container contenedor = this.reporteView.getParent();
            // Eliminar el JPanel del contenedor.
            contenedor.remove(this.reporteView);
            // Revalidar y repintar el contenedor para reflejar el cambio.
            contenedor.revalidate();
            contenedor.repaint();
        } else if (e.getSource() == this.reporteView.btnShowAllPayroll) {
            this.resetView();
            this.showAllPayroll = true;
            this.checkPayroll();
        }
    }

    public void resetView(){
        this.PayrollAccumulative = 0.0f;
        this.reporteView.comboMeses.setSelectedIndex(0);
        this.reporteView.txtNombreEmpleado.setText("");
        this.reporteView.txtNumeroEmpleado.setText("");
        this.reporteView.txtRol.setText("");
        this.reporteView.txtTotalNomina.setText("");
    }
    
    public void clearNomina(){
        this.reporteView.txtTotalNomina.setText("");
        this.PayrollAccumulative = 0.0f;
        this.showAllPayroll = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == reporteView.txtNumeroEmpleado) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if ("".equals(this.reporteView.txtNumeroEmpleado.getText())) {
                    JOptionPane.showMessageDialog(this.reporteView, "Ingresa un número de empleado, por favor");
                }else{
                    //Al presionar enter, se buscan los datos del nómina del empleado cuyo ID se ingresó al sistema
                    this.clearNomina();
                    this.checkPayroll();
                }
            }
        }
    }

    public void checkPayroll() {
        //Crea un arreglo de movimientos donde se guardaran los resultados retornados por la BD
        List<Movimientos> movimientos;
        String monthToSeach = (String) this.reporteView.comboMeses.getSelectedItem();
        DefaultTableModel modelo;
        String[] registros;
        //Validar si se pretendian mostrar toda la nómina
        if (this.showAllPayroll) {
            movimientos = this.movimientosDAO.getAllMovements();
            //Añade el campo empleado y tipo a la tabla, y crea su modelo
            String[] titulos = {"Empleado", "Tipo", "Mes", "Horas Trabajadas", "Cant. Entregas", "Pago por entregas", "Bono por hora", "Subtotal", "Retenciones ISR", "Vales", "Sueldo Total"}; // campos de la tabla
            registros = new String[11];
            modelo = new DefaultTableModel(null, titulos);
            System.out.println("Getting all movements");
        } else {
            //Muestra el nombre del empleado y tipo en cajas de texto, y crea su modelo
            String[] titulos = {"Mes", "Horas Trabajadas", "Cant. Entregas", "Pago por entregas", "Bono por hora", "Subtotal", "Retenciones ISR", "Vales", "Sueldo Total"}; // campos de la tabla
            registros = new String[9];
            modelo = new DefaultTableModel(null, titulos);
            //Ver si se muestra solo por un es especifico, o todos los registros
            if ("-Seleccionar-".equals(monthToSeach)) {
                movimientos = this.movimientosDAO.getMovementsByEmployeeNumber(Integer.parseInt(this.reporteView.txtNumeroEmpleado.getText()));
            } else {
                movimientos = this.movimientosDAO.getMovementsByEmployeeNumberAndMonth(Integer.parseInt(this.reporteView.txtNumeroEmpleado.getText()), monthToSeach);
            }
        }
        //Validar sí se encontraron movimientos en la BD con ese empleado
        if (!movimientos.isEmpty()) {
            //Validar cual de los dos modelos de las tablas se usuará.
            if (showAllPayroll) {
                this.reporteView.txtNombreEmpleado.setText("Varios");
                this.reporteView.txtRol.setText("Chofer, Cargador, Auxiliar");
                //Asignar los valores del reporte a la tabla
                for (int x = 0; x < movimientos.size(); x++) {
                    //calcular y guardar los valores de la nómina dentro de un objeto usando los valores recuperados de la base de datos
                    this.nomina[0] = this.getBaseSalary(movimientos.get(x).getTotalHours()); //0 = sueldo base
                    this.nomina[1] = this.hourlyBonuses(movimientos.get(x).getTotalHours(), movimientos.get(0).getEmployeeType());//1 = dinero por bono
                    this.nomina[2] = this.calculatePaymentPerDelivery(movimientos.get(x).getDeliveries());//2 = dinero por entregas
                    this.nomina[3] = this.calculateISR((float) this.nomina[0], (float) this.nomina[2], (float) this.nomina[1]); //ISR
                    this.nomina[4] = this.getSubtotal((float) this.nomina[0], (float) this.nomina[1], (float) this.nomina[2]);// subtotal, sin retencion de ISR
                    this.nomina[5] = this.calculatePantryBonus((float) this.nomina[4]);
                    this.nomina[6] = this.calculateTotalSalary((float) this.nomina[4], (float) this.nomina[3]);

                    //Asignar los valores del objeto nomina a la al registro del modelo de la tabla
                    registros[0] = String.valueOf(movimientos.get(x).getNombreEmpleado()); //Nombre
                    registros[1] = String.valueOf(movimientos.get(x).getEmployeeType()); //Tipo Empleado
                    registros[2] = String.valueOf(movimientos.get(x).getMonth()); //Mes
                    registros[3] = String.valueOf(movimientos.get(x).getTotalHours()); //Horas trabajadas por mes
                    registros[4] = String.valueOf(movimientos.get(x).getDeliveries()); //Entregas por mes
                    registros[5] = String.valueOf(decimalFormat.format(this.nomina[2])); //Pago por entregas
                    registros[6] = String.valueOf(decimalFormat.format(this.nomina[1])); //Bono por hora 
                    registros[7] = String.valueOf(decimalFormat.format(this.nomina[4])); //Subtotal del sueldo, Salario = sueldo + bono + entregas
                    registros[8] = String.valueOf(decimalFormat.format(this.nomina[3])); // Retencion ISR = (sueldo + bono + entregas) * ISR
                    registros[9] = String.valueOf(decimalFormat.format(this.nomina[5])); //Total de vale de despensa
                    registros[10] = String.valueOf(decimalFormat.format(this.nomina[6])); // Total, con descuentos de ISR, sin considerar vales de despensa
                    this.PayrollAccumulative += (Float) this.nomina[6];
                    modelo.addRow(registros);
                }
                this.reporteView.txtTotalNomina.setText(decimalFormat.format(this.PayrollAccumulative));

            } else {
                //Asignar el nombre y rol a la vista del reporte
                this.reporteView.txtNombreEmpleado.setText(movimientos.get(0).getNombreEmpleado());
                this.reporteView.txtRol.setText(movimientos.get(0).getEmployeeType());
                //Asignar los valores del reporte a la tabla
                for (int x = 0; x < movimientos.size(); x++) {
                    //calcular y guardar los valores de la nómina dentro de un objeto usando los valores recuperados de la base de datos
                    this.nomina[0] = this.getBaseSalary(movimientos.get(x).getTotalHours()); //0 = sueldo base
                    this.nomina[1] = this.hourlyBonuses(movimientos.get(x).getTotalHours(), movimientos.get(0).getEmployeeType());//1 = dinero por bono
                    this.nomina[2] = this.calculatePaymentPerDelivery(movimientos.get(x).getDeliveries());//2 = dinero por entregas
                    this.nomina[3] = this.calculateISR((float) this.nomina[0], (float) this.nomina[2], (float) this.nomina[1]); //ISR
                    this.nomina[4] = this.getSubtotal((float) this.nomina[0], (float) this.nomina[1], (float) this.nomina[2]);// subtotal, sin retencion de ISR
                    this.nomina[5] = this.calculatePantryBonus((float) this.nomina[4]);
                    this.nomina[6] = this.calculateTotalSalary((float) this.nomina[4], (float) this.nomina[3]);

                    //Asignar los valores del objeto nomina a la al registro del modelo de la tabla
                    registros[0] = String.valueOf(movimientos.get(x).getMonth()); //Mes
                    registros[1] = String.valueOf(movimientos.get(x).getTotalHours()); //Horas trabajadas por mes
                    registros[2] = String.valueOf(movimientos.get(x).getDeliveries()); //Entregas por mes
                    registros[3] = String.valueOf(decimalFormat.format(this.nomina[2])); //Pago por entregas
                    registros[4] = String.valueOf(decimalFormat.format(this.nomina[1])); //Bono por hora 
                    registros[5] = String.valueOf(decimalFormat.format(this.nomina[4])); //Subtotal del sueldo, Salario = sueldo + bono + entregas
                    registros[6] = String.valueOf(decimalFormat.format(this.nomina[3])); // Retencion ISR = (sueldo + bono + entregas) * ISR
                    registros[7] = String.valueOf(decimalFormat.format(this.nomina[5])); //Total de vale de despensa
                    registros[8] = String.valueOf(decimalFormat.format(this.nomina[6])); // Total, con descuentos de ISR, sin considerar vales de despensa
                    modelo.addRow(registros);
                }
            }
            //añade el registro al cuerpo de la tabla
            this.reporteView.tableNomina.setModel(modelo);
        } else {
            //Si la consulta no retorna registros, quiere decir que no hay datos de pago para ese empleado.
            JOptionPane.showMessageDialog(this.reporteView, "No hay movimientos registrados con este empleado");
        }
    }

    //Calcular cuando se paga al empleado por las entregas realizadas en el mes
    public float calculatePaymentPerDelivery(int cantEntregas) {
        float pago, horarioLaboral;
        pago = cantEntregas * bonoPorEntrega;
        return pago;
    }

    //Calcular los bonos adicionales que se pagaron al sueldo base de 30 pesos por hora dependiendo del rol del empleado
    public float hourlyBonuses(int horas, String tipoEmpleado) {
        float bonoPorHora;
        if ("Chofer".equals(tipoEmpleado)) {
            bonoPorHora = horas * this.bonoChofer;
        } else if ("Cargador".equals(tipoEmpleado)) {
            bonoPorHora = horas * this.bonoCargadores;
        } else {
            bonoPorHora = 0.0f;
        }
        return bonoPorHora;
    }

    //calcular el sueldo base en base a las horas trabajadas al mes
    public float getBaseSalary(int horasTrabajadas) {
        float sueldo;
        sueldo = horasTrabajadas * this.sueldoBase;
        return sueldo;
    }

    //Calcular el ISR dependiedo del sueldo del empleado
    public float calculateISR(float sueldo, float pagoEntregas, float pagoBonos) {
        float isr, totalSueldo;
        totalSueldo = sueldo + pagoEntregas + pagoBonos;
        if (totalSueldo >= 10000.f) {
            isr = (sueldo + pagoEntregas + pagoBonos) * this.ISRadicional;
        } else {
            isr = (sueldo + pagoEntregas + pagoBonos) * this.ISRfijo;
        }
        return isr;
    }

    //Calcular el subtotal del sueldo previo al descuento del ISR
    public float getSubtotal(float sueldoBase, float bonos, float entregas) {
        float subtotal;
        subtotal = sueldoBase + bonos + entregas;
        return subtotal;
    }

    //Calcular el bono del vale de despensa en base al subtotal
    public float calculatePantryBonus(float subtotal) {
        float bonoDeDespensa;
        bonoDeDespensa = subtotal * this.bonoDespensa;
        return bonoDeDespensa;
    }

    //Calcular el sueldo total restando las retenciones del ISR
    public float calculateTotalSalary(float subtotal, float isr) {
        float sueldoTotal = subtotal - isr;
        return sueldoTotal;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
