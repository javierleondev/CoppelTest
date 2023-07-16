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

    //Recuperar los datos de un empleado en base al ID, y guardarlos en un arreglo
    public List<Empleado> getEmployeeByNumber(int empNumber ) {
        String sql = "{CALL GetEmployeeByNumber(?)}";
        List<Empleado> employees = new ArrayList<>();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, empNumber );
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
    
    //Recuperar todos los registros de un empleado en base a un ID usando un join entre tablas
    public List<Movimientos> getMovementsByEmployeeNumber(int numeroEmpleado) {
        String sql = "{CALL GetMovementsByEmployeeNumber(?)}";
        List<Movimientos> movimientos = new ArrayList<>();

        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, numeroEmpleado);
            try(ResultSet rs = cstmt.executeQuery()){
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
