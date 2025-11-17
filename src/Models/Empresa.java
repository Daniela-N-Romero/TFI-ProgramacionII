/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public enum Empresa {
    ANDREANI, OCA, CORREO_ARG;
    
        public static Empresa fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        // Aplicamos la sanitización para que CORREO ARG funcione como CORREO_ARG
        String str = text.trim().toUpperCase().replace(" ", "_");
        
        // Usamos try-catch internamente, pero la capa de UI no lo ve
        try {
            return Empresa.valueOf(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
