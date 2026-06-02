package reframe.utils;

import java.sql.*;
import java.util.*;

public class ConnessioneDB {

    private static List<Connection> freeDbConnections;
    
    // Metodo per inizzializzazione lista e caricamento driver
    static {
    	
        freeDbConnections = new LinkedList<Connection>();
        
        try {
        	
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (ClassNotFoundException e) {	System.out.println("DB driver not found!" + e);	}
    }

    // Metodo pre crezione della connessione
    private static Connection createDBConnection() throws SQLException 
    {
        Connection conn = null;

        // Credenziali DB
        String url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/ReFrame_DB";
        String username = "3Tq7AyB63PKxx34.root";
        String password = "qGRxBxv7lrqjUG4t";

        conn = DriverManager.getConnection(url, username, password);
        
        return conn;
    }

    // Metodo per estrazione della connessione dal ConnectionPool
    public static synchronized Connection getConnection() throws SQLException 
    {
        Connection conn;

        if (!freeDbConnections.isEmpty()) { // Se c'è una connessione libera, la prende e la rimuove dalla lista
            
            conn = (Connection) freeDbConnections.get(0);
            ConnessioneDB.freeDbConnections.remove(0);

            try {
                if (conn.isClosed()) {
                    conn = ConnessioneDB.getConnection();
                }
            } catch (SQLException e) {
                conn = ConnessioneDB.getConnection();
            }
        } else { // Se il ConnectionPool è vuoto, ne crea una nuova al momento
            
            conn = ConnessioneDB.createDBConnection();
        }

        return conn;
    }

    // Metodo per restituire la connessione al ConnectionPoll
    public static synchronized void releaseConnection(Connection conn) {
    	ConnessioneDB.freeDbConnections.add(conn);
    }
}