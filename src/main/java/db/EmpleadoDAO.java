package db;

import model.Empleado;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EmpleadoDAO {
    //Crear objeto de la clase de conexion a la base de datos
    private DatabaseConnector dbConnector = new DatabaseConnector();
    
    
    //Procedimiento almacenado para guardar los datos del modelo empleado
    public void guardar(Empleado empleado) {
         String sql = "{CALL InsertarEmpleado(?, ?, ?, ?)}";

        try (Connection conn = dbConnector.connectToDatabase();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, empleado.getName());
            cstmt.setDouble(2, empleado.getNumber());
            cstmt.setInt(3, empleado.getEstatus());
            cstmt.setString(4, empleado.getEmployeeType());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //Validar sí ya hay un empleado registrado con ese numero de empleado
    public boolean getEmployeeIDByNumber(int idEmployee) {
        String sql = "{CALL GetEmployeeIDByNumber(?)}";
        try (Connection conn = dbConnector.connectToDatabase(); CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idEmployee);
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
