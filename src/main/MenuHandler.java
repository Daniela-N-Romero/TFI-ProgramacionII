/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import Models.Pedido;
import Models.Estado;
import Service.PedidoServiceImpl;
import Models.Envio;
import Models.TipoEnvio;
import Models.Empresa;
import Models.EstadoEnvio;
import java.time.LocalDate; // << NECESITAS IMPORTAR LocalDate
import java.util.InputMismatchException;
import java.util.Scanner;
import Service.PedidoServiceImpl; // Asumo que esta es tu implementación
import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 */
public class MenuHandler {
    
    private final Scanner scanner;
    private final PedidoServiceImpl pedidoService;
    
    //Constructor con inyección de dependencias, valida que las dependencias no sean null.
    public MenuHandler(Scanner scanner, PedidoServiceImpl pedidoService) {
        if(scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if(pedidoService == null) {
            throw new IllegalArgumentException("PedidoService no puede ser null");
        }
        this.scanner = scanner;
        this.pedidoService = pedidoService;
    }
    
    
    public void crearPedido() {
        try { 
            System.out.print("Numero de pedido: ");
            String numero = scanner.nextLine().trim();
            System.out.print("Nombre del cliente: ");
            String nombreCliente = scanner.nextLine().trim();
            System.out.print("Total: ");
            double total = Double.parseDouble(scanner.nextLine().trim());
            Estado estado = Estado.NUEVO;
            
            Envio envio = null;
            System.out.print("¿Desea agregar un nuevo envio? (s/n): ");
            if(scanner.nextLine().trim().equalsIgnoreCase("s")) {
                envio = crearEnvio();
            }
            LocalDate fechaPedido = LocalDate.now(); //Generamos la fecha como "hoy"
            
            //Llamamos al constructor de Pedido
            Pedido pedido = new Pedido(numero, fechaPedido, numero, total, estado, envio, 0L);
            //Insertamos el pedido
            pedidoService.insertar(pedido);
            //Mostramos el mensaje de exito por pantalla
            System.out.println("Pedido creado exitosamente con ID: " + pedido.getId());
        } catch (NumberFormatException e) {
            System.err.println("Error: El campo 'total' debe ser un número válido");
        } catch (Exception e) {
            System.err.println("Error al crear el pedido " + e.getMessage());
        }
    }
    
    public void listarPedidos() {
        try {
            List<Pedido> pedidos;
            pedidos = pedidoService.getAll();
            if(pedidos.isEmpty()) {
                System.out.println("No se encontraron pedidos");
            }
            for (Pedido p : pedidos) {
                System.out.println("ID: " + p.getId() + 
                        ", Numero de pedido: " + p.getNumero() + 
                        ", Fecha: " + p.getFecha() + 
                        ", Nombre de cliente: " + p.getClienteNombre() + 
                        ", Total: " + p.getTotal() + 
                        ", Estado del pedido: " + p.getEstado());
                if(p.getEnvio() != null) {
                    System.out.println(" Envio: " + p.getEnvio().toString());
                }
            }
            
        } catch(Exception e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
        }
    }
    
    public void actualizarPedidos() {
        try {
            System.out.print("ID del pedido a actualizar: ");
            long id = Long.parseLong(scanner.nextLine().trim());
            Pedido p = pedidoService.getByID((long)id);
            
            if(p == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }
            // Actualizar Número de Pedido y Cliente 
            System.out.println("Numero de pedido (actual: " + p.getNumero() + ", Enter para mantener): ");
            String numero = scanner.nextLine().trim();
            if(!numero.isEmpty()) {
                p.setNumero(numero);
            }
            System.out.println("Nombre de cliente (actual: " + p.getClienteNombre()+ ", Enter para mantener): ");
            String nombre = scanner.nextLine().trim();
            if(!nombre.isEmpty()) {
                p.setClienteNombre(nombre);
            }
            //Para actualizar el Total, debemos convertir el double en String para poder ser deferenciado, 
            // de lo contrario no nos deja llamar al método isEmpty()
            System.out.println("Total del pedido (actual: $" + String.format("%.2f", p.getTotal()) + ", Enter para mantener): ");
            String totalString = scanner.nextLine().trim();
            if(!totalString.isEmpty()) {
                try {
                    double nuevoTotal = Double.parseDouble(totalString);
                    p.setTotal(nuevoTotal);
                } catch(NumberFormatException e) {
                    System.err.println("Error: El valor ingresado no es un número válido. Se mantendrá el total anterior.");
                }
            }
             //Actualizar Estado 
            System.out.println("Estado del pedido (actual: " + p.getEstado().name() + ", Enter para mantener. Opciones: NUEVO, FACTURADO, ENVIADO): ");
            String estadoString = scanner.nextLine().trim();
            if (!estadoString.isEmpty()) {
                try {
                    // Convertir la entrada del usuario (String) a Enum
                    p.setEstado(Estado.valueOf(estadoString.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: El estado ingresado no es válido. Se mantendrá el estado anterior.");
                }
            }
            //Manejo de errores
        } catch (NumberFormatException e) {
        System.err.println("Error: El ID ingresado debe ser un número entero válido.");
        } catch (Exception e) {
        System.err.println("Error al actualizar el pedido: " + e.getMessage());
        }
    }
        /* Flujo:
        1. Solicita el ID del pedido.
        2. Invoca pedidoService.eliminar() que:
            - Marca pedido.eliminado = TRUE
        */
        public void eliminarPedido() {
            try {
                System.out.print("ID del pedido a eliminar: ");
                long id = Long.parseLong(scanner.nextLine());
                pedidoService.eliminar(id);
                System.out.println("Pedido eliminado exitosamente.");
            } catch(NumberFormatException e) {
                System.err.println("Error al eliminar Pedido" + e.getMessage());
            }
        }
        
        
    
    
    
    
    public Envio crearEnvio() {
        try {
            System.out.println("Codigo alfanumerico de tracking: ");
            String tracking = scanner.nextLine().trim();
            System.out.println("Costo del envio: ");
            double costo = Double.parseDouble(scanner.nextLine().trim());
            System.out.println("Empresa de envio (ANDREANI, OCA, CORREO_ARG)");
            Empresa empresa = Empresa.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.println("Tipo de envio (ESTANDAR, EXPRESS)");
            TipoEnvio tipo = TipoEnvio.valueOf(scanner.nextLine().trim().toUpperCase());
            //Para el estado del envio, siempre comenzará por EN_PREPARACION
            EstadoEnvio estado = EstadoEnvio.EN_PREPARACION;
            System.out.println("Estado inicial del envio: " + estado.name());
            //Para la fechaDespacho le ponemos por defecto 1 día despues de "hoy"
            LocalDate fechaDespacho = LocalDate.now().plusDays(1);
            System.out.println("Fecha del despacho (mañana): " + fechaDespacho);
            //Para la fecha estimada le ponemos por defecto 5 días despues de "hoy"
            LocalDate fechaEstimada = LocalDate.now().plusDays(5);
            System.out.println("\"Fecha del despacho (mañana): " + fechaEstimada);
            
            //Creamos el objeto Envio con su Constructor completo.
            Envio envio = new Envio(tracking, costo, fechaDespacho, fechaEstimada, empresa, estado, tipo, 0L);
            System.out.println("Datos de Envío recopilados correctamente.");
            return envio;
            
        } catch (NumberFormatException e) {
            System.err.println("Error: El campo 'total' debe ser un número válido");
            return null;
        } catch(IllegalArgumentException e) {
            System.err.println("Error de Enum: La empresa o el tipo de envío ingresado no es válido.");
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear el Envio " + e.getMessage());
            return null;
        }
    }
}

    
