package db;

import model.Empleado;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Pc
 */
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
            cstmt.setInt(3, empleado.getStatus());
            cstmt.setInt(4, empleado.getIdEmployeesType());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
