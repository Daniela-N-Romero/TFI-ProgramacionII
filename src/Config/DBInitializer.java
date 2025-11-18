package Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.sql.ResultSet;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */

public class DBInitializer {

    private static final String SQL_FILE_NAME = "init.sql";
    private static final String SEED_DATA_SQL =    
            "INSERT IGNORE INTO pedidos (numero_pedido, fecha, cliente_nombre, total, estado) VALUES "+
            "('P0001', '2025-11-01 10:30:00', 'Juan Pérez', 15000.50, 'FACTURADO'), "+
            "('P0002', '2025-11-02 14:45:00', 'María López', 5200.00, 'ENVIADO'), "+
            "('P0003', '2025-11-03 09:00:00', 'Carlos García', 850.75, 'NUEVO'), "+
            "('P0004', '2025-11-04 18:20:00', 'Laura Torres', 22500.99, 'FACTURADO'), "+
            "('P0005', '2025-11-05 11:15:00', 'Roberto Sánchez', 3100.00, 'ENVIADO');"+

            "INSERT INTO envios (tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado, id_pedido) VALUES "+
            "('TRK0001A', 'ANDREANI', 'ESTANDAR', 950.00, '2025-11-02', '2025-11-08', 'EN_TRANSITO', 1), "+
            "('TRK0002O', 'OCA', 'EXPRESS', 1500.00, '2025-11-03', '2025-11-05', 'ENTREGADO', 2), " +
            "('TRK0003C', 'CORREO_ARG', 'ESTANDAR', 720.50, '2025-11-05', '2025-11-12', 'EN_TRANSITO', 4), "+
            "('TRK0004O', 'OCA', 'ESTANDAR', 800.00, '2025-11-06', '2025-11-13', 'EN_PREPARACION', 3);";

    public static void initialize() throws Exception {
        System.out.println("Iniciando BBDD: Verificando esquema y datos...");
        try (Connection connServer = DatabaseConnection.getSystemConnection();
         Statement stmt = connServer.createStatement()) {
        
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.DB_NAME); 
        System.out.println("Base de datos '" + DatabaseConnection.DB_NAME + "' asegurada.");
    }
    
    // Ejecutamos DDL/DML (con conexión directa a la nueva base de datos)
    try (Connection connDB = DatabaseConnection.getConnection(); 
         Statement stmt = connDB.createStatement()) {

        String sqlScript = readSqlFile();
        
        String[] statements = sqlScript.split(";\\s*\\n*");
        for (String sql : statements) {
            if (!sql.trim().isEmpty()) {
                stmt.execute(sql.trim());
            }
        }
        // VERIFICACIÓN: ¿Hay datos?
            if (isTableEmpty(connDB, "pedidos")) {
                System.out.println("La base de datos está vacía. Cargando datos iniciales...");
                // Ejecutamos los inserts separados
                String[] inserts = SEED_DATA_SQL.split(";");
                for (String insert : inserts) {
                     if (!insert.trim().isEmpty()) stmt.execute(insert.trim());
                }
                System.out.println("Datos iniciales cargados.");
            } else {
                System.out.println("La base de datos ya contiene datos. Se omite la carga inicial.");
            }

            System.out.println("Inicialización completa.");

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
    
    // Método auxiliar para verificar si una tabla tiene registros
    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total") == 0; // Retorna true si el conteo es 0
            }
        }
        return true; // Por defecto asumimos vacía si falla algo raro
    }
}
