package model;

public class Empleado {

    // Atributos del objeto empleado
    private int idEmployee;
    private String name;
    private double number;
    private String dateRegister;
    private int estatus;
    private String employeeType;

    //Constructor por defecto, para cuando se crear la el objeto la primera vez, sin inicializar datos
    public Empleado() {
        this.idEmployee = 0;
        this.name = "";
        this.number = 0;
        this.dateRegister = "";
        this.estatus = 0;
        this.employeeType = "";
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

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public String setEmployeeType(String employeeType) {
        return this.employeeType = employeeType;
    }
}
