/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author Daniela Nahir Romero
 */

import java.sql.Connection;
import java.sql.SQLException;
import Config.DatabaseConnection;

public class TestConexion {
    public static void main(String[] args) {
        /**
         * 🔹 Se usa un bloque try-with-resources para asegurar que la conexión
         *     se cierre automáticamente al salir del bloque.
         * 🔹 No es necesario llamar explícitamente a conn.close().
         */
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión establecida con éxito.");
            } else {
                System.out.println("❌ No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
