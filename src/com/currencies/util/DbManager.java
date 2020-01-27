/*
 * DB Manager Utility
 * 
 * Comments added by Ali Malik - 25.01.2020
 */
package com.currencies.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {

    public static Connection getConnection(String user, String pass, String url) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
