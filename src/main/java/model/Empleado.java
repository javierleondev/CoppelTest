package model;

/**
 *
 * @author Pc
 */
public class Empleado {
    // Atributos del objeto empleado
    private int idEmployee;
    private String name;
    private double number;
    private String dateRegister;
    private int status;
    private int idEmployeesType;
    
    // Constructor por defecto de la clase empleado
    public Empleado(int idEmployee, String name, double number, String dateRegister, int status, int idEmployeesType) {
        this.idEmployee = idEmployee;
        this.name = name;
        this.number = number;
        this.dateRegister = dateRegister;
        this.status = status;
        this.idEmployeesType = idEmployeesType;
    } 
    
    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIdEmployeesType() {
        return idEmployeesType;
    }

    public void setIdEmployeesType(int idEmployeesType) {
        this.idEmployeesType = idEmployeesType;
    }

}
