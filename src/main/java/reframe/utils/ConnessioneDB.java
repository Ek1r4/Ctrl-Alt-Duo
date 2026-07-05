package reframe.utils;

import java.sql.*;
import java.util.*;

public class ConnessioneDB {

    private static List<Connection> freeDbConnections;
    
    /* INIZIALIZZAZIONE */
    
    // Inizializzazione statica del Connection Pool e caricamento in memoria del driver JDBC all'avvio dell'applicazione.
    static {
        freeDbConnections = new LinkedList<Connection>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("DB driver not found!" + e);
        }
    }

    /* CREAZIONE CONNESSIONE */
    
    // Istanzia fisicamente una singola connessione JDBC verso il database remoto.
    private static Connection createDBConnection() throws SQLException {
        Connection conn = null;

        String url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/ReFrame_DB";
        String username = "3Tq7AyB63PKxx34.root";
        String password = "qGRxBxv7lrqjUG4t";

        conn = DriverManager.getConnection(url, username, password);
        
        return conn;
    }

    /* GESTIONE POOL DI CONNESSIONI */
    
    // Implementazione del pattern Object Pool con blocco synchronized per garantire la thread-safety: minimizza l'overhead di rete riutilizzando connessioni idle se disponibili.
    public static synchronized Connection getConnection() throws SQLException {
        Connection conn;

        if (!freeDbConnections.isEmpty()) { 
            conn = (Connection) freeDbConnections.get(0);
            ConnessioneDB.freeDbConnections.remove(0);

            try {
                // Previene l'assegnazione di una connessione droppata per timeout o errori di rete
                if (conn.isClosed()) {
                    conn = ConnessioneDB.getConnection();
                }
            } catch (SQLException e) {
                conn = ConnessioneDB.getConnection();
            }
        } else { 
            conn = ConnessioneDB.createDBConnection();
        }

        return conn;
    }

    // Rilascia la connessione logica accodandola nuovamente al pool invece di terminarla (close()), mantenendola attiva a disposizione degli altri thread.
    public static synchronized void releaseConnection(Connection conn) {
        ConnessioneDB.freeDbConnections.add(conn);
    }
}