/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public enum TipoEnvio {
    ESTANDAR, EXPRESS;
    
        /**
     * Intenta convertir una cadena a TipoEnvio. Retorna el TipoEnvio 
     * o null si la cadena no coincide.
     */
    public static TipoEnvio fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return TipoEnvio.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
