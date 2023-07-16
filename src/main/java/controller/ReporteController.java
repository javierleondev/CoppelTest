
package controller;

import db.MovimientosDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Movimientos;
import view.ReporteView;

public class ReporteController implements ActionListener, KeyListener{
    //Bindear el modelo y la vista a este controlador
    private ReporteView reporteView;
    private Movimientos movimientos;
    private MovimientosDAO movimientosDAO;
    
    //Variables constantes para las operaciones de nómina
    private final float sueldoBase;
    private final int horarioLaboral;
    private final int diasPorSemana;
    private final float bonoPorEntrega;
    private final float bonoChofer;
    private final float bonoCargadores;
    private final float ISRfijo;
    private final float ISRadicional;
    private final float bonoDespensa;
    
    //Objecto para guardar temporalmente los datos calculados de nómina del empleado
    private Object[] nomina;
    
    
    public ReporteController(ReporteView reporteView, Movimientos movimientos, MovimientosDAO movimientosDAO){
        //Inicialización de variables para nómina
        this.sueldoBase = 30.0f;
        this.horarioLaboral = 8;
        this.diasPorSemana = 6;
        this.bonoPorEntrega = 5.0f;
        this.bonoChofer = 10.0f;
        this.bonoCargadores = 5.0f;
        this.ISRfijo = .09f;
        this.ISRadicional = .12f;
        this.bonoDespensa = .04f;
        
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == reporteView.txtNumeroEmpleado) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                this.consultarNomina();
            }
        }
    }
    
    public void consultarNomina() {
        List<Movimientos> movimientos;
        movimientos = this.movimientosDAO.getMovementsByEmployeeNumber(Integer.parseInt(this.reporteView.txtNumeroEmpleado.getText()));

        //Crear el modelo de la tabla
        DefaultTableModel modelo;
        String[] titulos = {"Mes", "Horas Trabajadas", "Cant. Entregas", "Pago por entregas", "Bono por hora", "Subtotal", "Retenciones ISR", "Vales", "Sueldo Total"}; // campos de la tabla
        String[] registros = new String[9];
        modelo = new DefaultTableModel(null, titulos);
        
        //Validar sí se encontraron movimientos en la BD con ese empleado
        if (!movimientos.isEmpty()) {
            //Asignar el nombre y rol a la vista del reporte
            this.reporteView.txtNombreEmpleado.setText(movimientos.get(0).getNombreEmpleado());
            this.reporteView.txtRol.setText(movimientos.get(0).getEmployeeType());
            //Asignar los valores del reporte a la tabla
            for (int x = 0; x < movimientos.size(); x++) {
                //calcular los valores de la nómina
                this.nomina[0] = this.getSueldoBase(movimientos.get(x).getTotalHours()); //0 = sueldo base
                this.nomina[1] = this.bonosPorHoras(movimientos.get(x).getTotalHours(), movimientos.get(0).getEmployeeType());//1 = dinero por bono
                this.nomina[2] = this.calcularPagoPorEntregas(movimientos.get(x).getDeliveries());//2 = dinero por entregas
                this.nomina[3] = this.calcularISR((float) this.nomina[0], (float) this.nomina[2], (float) this.nomina[1]); //ISR
                this.nomina[4] = this.getSubtotal((float) this.nomina[0], (float) this.nomina[1], (float) this.nomina[2]);// subtotal, sin retencion de ISR
                this.nomina[5] = this.calcularBonoDespensa((float) this.nomina[4]);
                this.nomina[6] = this.calcularSueldoTotal((float) this.nomina[4], (float) this.nomina[3]);
                
                //Asignar los valores del objeto nomina a la al registro del modelo de la tabla
                registros[0] = String.valueOf(movimientos.get(x).getMonth()); //Mes
                registros[1] = String.valueOf(movimientos.get(x).getTotalHours()); //Horas trabajadas por mes
                registros[2] = String.valueOf(movimientos.get(x).getDeliveries()); //Entregas por mes
                registros[3] = String.valueOf(this.nomina[2]); //Pago por entregas
                registros[4] = String.valueOf(this.nomina[1]); //Bono por hora 
                registros[5] = String.valueOf(this.nomina[4]); //Subtotal del sueldo, Salario = sueldo + bono + entregas
                registros[6] = String.valueOf(this.nomina[3]); // Retencion ISR = (sueldo + bono + entregas) * ISR
                registros[7] = String.valueOf(this.nomina[5]); //Total de vale de despensa
                registros[8] = String.valueOf(this.nomina[6]); // Total, con descuentos de ISR, sin considerar vales de despensa
                modelo.addRow(registros);
            }
            //añade el registro al cuerpo de la tabla
            this.reporteView.tableNomina.setModel(modelo);
        } else {
            JOptionPane.showMessageDialog(this.reporteView, "No hay movimientos registrados con este empleado");
        }
    }
    
    //Calcular cuando se paga al empleado por las entregas realizadas en el mes
    public float calcularPagoPorEntregas(int cantEntregas){
        float pago, horarioLaboral;
        pago = cantEntregas * bonoPorEntrega;
        return pago;
    }
    
    //Calcular los bonos adicionales que se pagaron al sueldo base de 30 pesos por hora dependiendo del rol del empleado
    public float bonosPorHoras(int horas, String tipoEmpleado){
        float bonoPorHora;
        if("Chofer".equals(tipoEmpleado)){
            bonoPorHora = horas * this.bonoChofer;
        }else if("Cargador".equals(tipoEmpleado)){
            bonoPorHora = horas * this.bonoCargadores;
        }else{
            bonoPorHora = 0.0f;
        }
        return bonoPorHora;
    }
    
    //calcular el sueldo base en base a las horas trabajadas al mes
    public float getSueldoBase(int horasTrabajadas){
        float sueldo;
        sueldo = horasTrabajadas * this.sueldoBase;
        return sueldo;
    }
    
    //Calcular el ISR dependiedo del sueldo del empleado
    public float calcularISR(float sueldo, float pagoEntregas, float pagoBonos){
        float isr, totalSueldo;
        totalSueldo = sueldo+pagoEntregas+pagoBonos;
        if(totalSueldo >=10000.f){
            isr = (sueldo+pagoEntregas+pagoBonos) * this.ISRadicional;
        }else{
            isr = (sueldo+pagoEntregas+pagoBonos) * this.ISRfijo;
        }
        return isr;
    }
    
    //Calcular el subtotal del sueldo previo al descuento del ISR
    public float getSubtotal(float sueldoBase, float bonos, float entregas){
        float subtotal;
        subtotal = sueldoBase + bonos + entregas;
        return subtotal;
    }
    
    //Calcular el bono del vale de despensa en base al subtotal
    public float calcularBonoDespensa(float subtotal){
        float bonoDeDespensa;
        bonoDeDespensa = subtotal * this.bonoDespensa;
        return bonoDeDespensa;
    }
    
    //Calcular el sueldo total restando las retenciones del ISR
    public float calcularSueldoTotal(float subtotal, float isr){
        float sueldoTotal = subtotal - isr;
        return sueldoTotal;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
