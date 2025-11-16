/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author Daniela Nahir Romero
 */
public class MenuDisplay {
    
    //Para el menú pensamos trabajar en dos submenú porque de lo contrario quedaría un menú solo
    //muy extenso y molesto para buscar, de esta manera organizamos de una manera más eficiente 
    //las opciones para el usuario.
    
    public static void mostrarMenuPrincipal() {
    System.out.println("\n========= MENU PRINCIPAL =========");
    System.out.println("1. Gestión de PEDIDOS");
    System.out.println("2. Gestión de ENVIOS");
    System.out.println("0. Salir de la Aplicación");
    System.out.print("Ingrese una opcion: ");
    }
    
    public static void mostrarMenuPedidos() {
    System.out.println("\n===== GESTIÓN DE PEDIDOS =====");
    System.out.println("1. Insertar nuevo Pedido (y Envío opcional)"); //Listo
    System.out.println("2. Listar todos los Pedidos"); //Listo
    System.out.println("3. Buscar Pedido por ID"); //Listo
    System.out.println("4. Actualizar datos del Pedido"); //Listo
    System.out.println("5. Eliminar Pedido (Soft Delete)"); //Listo
    System.out.println("9. Volver al Menu Principal");
    System.out.println("0. Salir de la Aplicación");
    System.out.print("Ingrese una opcion: ");
    }
    
    public static void mostrarMenuEnvios() {
    System.out.println("\n===== GESTIÓN DE ENVIOS =====");
    System.out.println("1. Listar todos los Envíos");
    System.out.println("2. Buscar Envío por ID");
    System.out.println("3. Actualizar datos del Envío (ej: tracking, estado)");
    System.out.println("4. Eliminar Envío (Soft Delete)");
    System.out.println("9. Volver al Menu Principal");
    System.out.println("0. Salir de la Aplicación");
    System.out.print("Ingrese una opcion: ");
    }
    
}
