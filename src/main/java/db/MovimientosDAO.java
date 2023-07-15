package db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import model.Empleado;
import model.Movimientos;

public class MovimientosDAO {
    //Crear objeto de la clase de conexion a la base de datos
    private DatabaseConnector dbConnector = new DatabaseConnector();
    
    
    //Procedimiento almacenado para guardar los datos del modelo empleado
    public void guardar(Movimientos movimientos) {
         String sql = "{CALL InsertarMovimiento(?, ?, ?, ?)}";

        try (Connection conn = dbConnector.connectToDatabase();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, movimientos.getIdEmployee());
            cstmt.setString(2, movimientos.getMonth());
            cstmt.setInt(3, movimientos.getDeliveries());
            cstmt.setInt(4, movimientos.getTotalHours());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
