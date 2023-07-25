package db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Empleado;
import model.Movimientos;

public class MovimientosDAO {

    //Crear objeto de la clase de conexion a la base de datos
    private DatabaseConnector dbConnector = new DatabaseConnector();
    //Objeto movement para guardar temporalmente los valores de horas y entregas al momento de hacer una actualización
    Movimientos movement = new Movimientos();

    //Validar sí ya hay registros de datos para un empleado en un mes especifico
    public boolean getMovementsByMonthAndEmployee(String month, int idEmployee) {
        String sql = "{CALL GetMovementsByMonthAndEmployee(?, ?)}";
        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, month);
            cstmt.setInt(2, idEmployee);
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                movement.setDeliveries(rs.getInt("deliveries"));
                movement.setTotalHours(rs.getInt("totalHours"));
                return true;
            } else {
                System.out.println("No hay registros.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Actualizar las horas y las entregas de un registro existente
    public void updateMovementsByMonthAndEmployee(String month, int idEmployee, int deliveries, int totalHours) {
        String sql = "{CALL UpdateMovementsByMonthAndEmployee(?, ?, ?, ?)}";

        int newDeliveries, newTotalHours;
        newDeliveries = deliveries + movement.getDeliveries();
        newTotalHours = totalHours + movement.getTotalHours();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, newDeliveries);
            cstmt.setInt(2, newTotalHours);
            cstmt.setString(3, month);
            cstmt.setInt(4, idEmployee);
            int updatedRows = cstmt.executeUpdate();
            if (updatedRows == 0) {
                System.out.println("Error al guardar.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Procedimiento almacenado para guardar los datos del modelo empleado
    public void guardar(Movimientos movimientos) {
        String sql = "{CALL InsertarMovimiento(?, ?, ?, ?)}";

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, movimientos.getIdEmployee());
            cstmt.setString(2, movimientos.getMonth());
            cstmt.setInt(3, movimientos.getDeliveries());
            cstmt.setInt(4, movimientos.getTotalHours());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Recuperar los datos de un empleado en base al ID, y guardarlos en un arreglo, esto se muestra al momento de registrar un movimiento
    public List<Empleado> getEmployeeByNumber(int empNumber) {
        String sql = "{CALL GetEmployeeByNumber(?)}";
        List<Empleado> employees = new ArrayList<>();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, empNumber);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Empleado employee = new Empleado();
                    employee.setName(rs.getString("name"));
                    employee.setEmployeeType(rs.getString("idEmployeesType"));
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    
    
    
    //Recuperar todos los registros de un empleado en base a un ID usando un join entre tablas, para calcular los datos de nómina
    public List<Movimientos> getMovementsByEmployeeNumber(int numeroEmpleado) {
        String sql = "{CALL GetMovementsByEmployeeNumber(?)}";
        List<Movimientos> movimientos = new ArrayList<>();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, numeroEmpleado);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Movimientos movimiento = new Movimientos();
                    movimiento.setNombreEmpleado(rs.getString("nombre"));
                    movimiento.setEmployeeType(rs.getString("employeeType"));
                    movimiento.setMonth(rs.getString("months"));
                    movimiento.setDeliveries(rs.getInt("deliveries"));
                    movimiento.setTotalHours(rs.getInt("totalHours"));
                    movimientos.add(movimiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movimientos;
    }
    
    //Recuperar todos los registros de un empleado en base a un ID usando un join entre tablas, para calcular los datos de nómina
    public List<Movimientos> getMovementsByEmployeeNumberAndMonth(int numeroEmpleado, String mes) {
        String sql = "{CALL GetMovementsByEmployeeNumberAndMonth(? ,  ?)}";
        List<Movimientos> movimientos = new ArrayList<>();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, numeroEmpleado);
            cstmt.setString(2, mes);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Movimientos movimiento = new Movimientos();
                    movimiento.setNombreEmpleado(rs.getString("nombre"));
                    movimiento.setEmployeeType(rs.getString("employeeType"));
                    movimiento.setMonth(rs.getString("months"));
                    movimiento.setDeliveries(rs.getInt("deliveries"));
                    movimiento.setTotalHours(rs.getInt("totalHours"));
                    movimientos.add(movimiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movimientos;
    }
    
    //Recuperar todos los registros de un empleado en base a un ID usando un join entre tablas, para calcular los datos de nómina
    public List<Movimientos> getAllMovements() {
        String sql = "{CALL GetAllMovements()}";
        List<Movimientos> movimientos = new ArrayList<>();
        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Movimientos movimiento = new Movimientos();
                    movimiento.setNombreEmpleado(rs.getString("nombre"));
                    movimiento.setEmployeeType(rs.getString("employeeType"));
                    movimiento.setMonth(rs.getString("months"));
                    movimiento.setDeliveries(rs.getInt("deliveries"));
                    movimiento.setTotalHours(rs.getInt("totalHours"));
                    movimientos.add(movimiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movimientos;
    }
}

