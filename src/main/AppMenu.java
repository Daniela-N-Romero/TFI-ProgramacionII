/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import Config.DatabaseConnection;
import Config.TransactionManager;
import Dao.EnvioDAO;
import Dao.PedidoDAO;
import Service.EnvioServiceImpl;
import Service.PedidoServiceImpl;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.SQLException;
import utils.uniquesGenerator;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public class AppMenu {

    private final MenuHandler menuHandler;
    private boolean running;
    private final Scanner scanner;
    private PedidoServiceImpl pedidoService;
    private EnvioServiceImpl envioService;
    private uniquesGenerator uniquesGenerator;

    public AppMenu(){
        try {
            initializeServices();
        } catch (SQLException ex) {
            System.out.println("Error al iniciar los servicios"+ ex.getMessage());
        }
        this.scanner = new Scanner(System.in);
        this.menuHandler = new MenuHandler(this.scanner, this.pedidoService, this.envioService, this.uniquesGenerator);
        this.running = true;
                
    }

 
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int eleccionMain = Integer.parseInt(scanner.nextLine());
                processMainOption(eleccionMain);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor, ingrese un numero.");
            }
        }
        scanner.close();
    }
    
    private void processMainOption(int opcion) {
        switch (opcion) {
            case 1 -> menuHandler.gestionarPedidos();
            case 2 -> menuHandler.gestionarEnvios();
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> System.out.println("Opcion no valida.");
        }
    }

     private void initializeServices() throws SQLException {
        // 1. Capa DAO
        EnvioDAO envioDAO = new EnvioDAO();
        PedidoDAO pedidoDAO = new PedidoDAO();

        // 2. Capa Config/Transacciones
        Connection conn = DatabaseConnection.getConnection();
        TransactionManager txManager = new TransactionManager(conn);

        // 3. Capa Service y asignación a campos 
        this.envioService = new EnvioServiceImpl(envioDAO, txManager); 
        this.pedidoService = new PedidoServiceImpl(pedidoDAO, envioDAO, txManager); 
        this.uniquesGenerator = new uniquesGenerator(pedidoDAO,envioDAO);
    }
    
    
    
}
