package reframe.dao;

import java.sql.*;

public class ConnessioneDB 
{

    private static final String URL = "jdbc:mysql://[INCOLLA_L_INDIRIZZO_TIDB]:4000/ReFrame_DB";
    private static final String USER = "3Tq7AyB63PKxx34.root";
    private static final String PASSWORD = "la_tua_password_reale";

    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}