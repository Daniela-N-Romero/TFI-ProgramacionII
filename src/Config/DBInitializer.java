package Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */

public class DBInitializer {

    private static final String SQL_FILE_NAME = "init.sql";

    public static void initialize() throws Exception {
        System.out.println("Iniciando BBDD: Verificando esquema y datos...");
        try (Connection connServer = DatabaseConnection.getSystemConnection();
         Statement stmt = connServer.createStatement()) {
        
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.DB_NAME); 
        System.out.println("Base de datos '" + DatabaseConnection.DB_NAME + "' asegurada.");
    }
    
    // 2. Ejecutar DDL/DML (con conexión directa a la nueva base de datos)
    try (Connection connDB = DatabaseConnection.getConnection(); 
         Statement stmt = connDB.createStatement()) {

        String sqlScript = readSqlFile();
        
        String[] statements = sqlScript.split(";\\s*\\n*");
        for (String sql : statements) {
            if (!sql.trim().isEmpty()) {
                stmt.execute(sql.trim());
            }
        }
        
        System.out.println("Estructura de BBDD y datos iniciales cargados con éxito.");

    } catch (SQLException e) {
        System.err.println("Error fatal al inicializar la base de datos.");
        throw new Exception("Error en la inicialización de la DB: " + e.getMessage(), e);
    }
    }

    private static String readSqlFile() throws Exception {
        try (InputStream is = DBInitializer.class.getResourceAsStream(SQL_FILE_NAME)) {
            if (is == null) {
                throw new Exception("No se encontró el archivo de inicialización: " + SQL_FILE_NAME);
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                // Leemos todas las líneas y las unimos en un solo String para ejecutar
                return reader.lines().collect(Collectors.joining("\n"));
            }
            
        } catch (Exception e) {
            throw new Exception("Error al leer el archivo SQL: " + e.getMessage(), e);
        }
    }
}