package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Pc
 */
public class DatabaseConnector {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/coppel";
    
    static final String USER = "root";
    static final String PASS = "";
    
    public Connection connectToDatabase(){
        Connection conn = null;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }catch(SQLException | ClassNotFoundException se){
            System.out.print(se);
        }
        return conn;
    }
}
