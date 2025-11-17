/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

//Importamos todas las bibliotecas
import Models.Pedido;
import Models.Estado;
import Models.TipoEnvio;
import Models.Empresa;
import Models.EstadoEnvio;
import Models.Envio;
import Service.EnvioServiceImpl;
import java.time.LocalDate; 
import java.util.Scanner;
import Service.PedidoServiceImpl; 
import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 */
public class MenuHandler {
    
    private final Scanner scanner;
    private final PedidoServiceImpl pedidoService;
    private final EnvioServiceImpl envioService;
    
    //Constructor con inyección de dependencias, valida que las dependencias no sean null.
    public MenuHandler(Scanner scanner, PedidoServiceImpl pedidoService, EnvioServiceImpl envioService) {
        if(scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if(pedidoService == null) {
            throw new IllegalArgumentException("PedidoService no puede ser null");
        }
        if(envioService == null) {
            throw new IllegalArgumentException("EnvioService no puede ser null");
        }   
        this.scanner = scanner;
        this.pedidoService = pedidoService;
        this.envioService = envioService;
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
            //Implementamos el método para crear un Envío
            Envio envio = null;
            System.out.print("¿Desea agregar un nuevo envio? (s/n): ");
            if(scanner.nextLine().trim().equalsIgnoreCase("s")) {
                envio = crearEnvio();
            }
            LocalDate fechaPedido = LocalDate.now(); //Generamos la fecha como "hoy"
            
            //Llamamos al constructor de Pedido
            Pedido pedido = new Pedido(numero, fechaPedido, nombreCliente, total, estado, envio, 0L);
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
    
    //Método para actualizar pedidos:
    public void actualizarPedidos() {
        try {
            System.out.print("ID del pedido a actualizar: ");
            long id = Long.parseLong(scanner.nextLine().trim());
            Pedido p = pedidoService.getById(id);
            
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
        public void eliminarPedido(){
            try {
                System.out.print("ID del pedido a eliminar: ");
                long id = Long.parseLong(scanner.nextLine().trim());
                pedidoService.eliminar(id);
                System.out.println("Pedido eliminado exitosamente.");
            } catch(NumberFormatException e) {
                System.err.println("Error al eliminar Pedido" + e.getMessage());
            }
        }
        
        public void buscarPedidoPorId () {
            try {
                System.out.print("Ingrese el ID del pedido a buscar: ");
                long id = Long.parseLong(scanner.nextLine().trim());
                Pedido pedido = pedidoService.getById(id);
                if(pedido == null) {
                    System.out.println("Pedido con id " + id + " no encontrado.");  
                    return;
                }
                //Mostramos la información del pedido encontrado:
                System.out.println("PEDIDO ENCONTRADO: ");
                System.out.println("---------------------------------");
                System.out.println("ID: " + pedido.getId());
                System.out.println("Nro: " + pedido.getNumero());
                System.out.println("Cliente: " + pedido.getClienteNombre());
                System.out.println("Total: $ " + pedido.getTotal());
                System.out.println("Estado: " + pedido.getEstado().name());
                
                //Mostramos la información del Envío asociado (si es que tiene) al pedido.
                if(pedido.getEnvio() != null) {
                    System.out.println("Envio asociado: " + pedido.getEnvio().toString());
                    //En caso que no tenga envío asociado:
                } else {
                    System.out.println("Sin Envio asociado.");
                }
                System.out.println("---------------------------------");
            } catch(NumberFormatException e) {
                System.err.println("❌ Error: El ID ingresado debe ser un número entero válido.");
            } catch (Exception e) {
                System.err.println("❌ Error al buscar pedido: " + e.getMessage());
            }
        }
    
    
    
        //Método para crear Envío
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
                System.out.println("Fecha del despacho (mañana): " + fechaEstimada);
            
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
        
        // CRUD para la clase Envio
        
        public void listarEnvios() {
            try {
                List<Envio> envios = envioService.getAll();
                if(envios.isEmpty()) {
                    System.out.println("No se encontraron Envios.");
                    return;
                }
                System.out.println("Envios registrados: ");
                for (Envio e : envios) {
                    System.out.println("----------------------------");
                    System.out.println(e.toString());
                }
                System.out.println("=================");
            } catch(Exception e) {
                System.out.println("Error al listar Envios " + e.getMessage());
            }
        }
        //Método para buscar Envios por ID
        public void buscarEnvioPorId() {
            try {
                System.out.print("Ingrese el ID del Envio a buscar: ");
                long id = Long.parseLong(scanner.nextLine().trim());
                Envio envio = envioService.getById(id);
                
                if(envio == null) {
                    System.out.println("Envio con id " + id + " no encontrado.");
                    return;
                }
                System.out.println("ENVIO ENCONTRADO: ");
                System.out.println("----------------------------");
                System.out.println(envio.toString()); //Utilizamos el toString para mostrar la informacion.
                System.out.println("----------------------------");
                
            } catch(NumberFormatException e) {
                System.err.println("Error: El ID ingresado debe ser un número válido.");
            } catch (Exception e) {
                System.err.println("Error al buscar envío: " + e.getMessage());
            }
        }
        
        public void actualizarEnvio() {
            try {
                System.out.print("ID del Envio a actualizar: ");
                long id = Long.parseLong(scanner.nextLine().trim());
                Envio envio = envioService.getById(id);
                
                if(envio == null) {
                    System.out.println("Envio con id " + id + " no encontrado.");
                    return;
                }
                //TRACKING.
                System.out.println("Tracking (actual: " + envio.getTracking() + " Enter para mantener): ");
                String tracking = scanner.nextLine().trim();
                if(!tracking.isEmpty()) {
                    envio.setTracking(tracking);
                }
                //COSTO.
                System.out.println("Costo (actual: $" + String.format("%.2f", envio.getCosto()) + ", Enter para mantener): ");
                String costoString = scanner.nextLine().trim();
                if (!costoString.isEmpty()) {
                    try {
                        envio.setCosto(Double.parseDouble(costoString));
                    } catch (NumberFormatException e) {
                        System.err.println("Error: Costo inválido. Se mantiene el anterior.");
                    }
                }
                //EMPRESA
                System.out.println("Empresa (actual: " + envio.getEmpresa().name() + ", Enter para mantener. Opciones: ANDREANI, OCA, CORREO_ARG): ");
                String empresaString = scanner.nextLine().trim();
                if (!empresaString.isEmpty()) {
                    try {
                        envio.setEmpresa(Empresa.valueOf(empresaString.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: Empresa inválida. Se mantiene la anterior.");
                    }
                }
                //ESTADO DEL ENVIO
                System.out.println("Estado (actual: " + envio.getEstado().name() + ", Enter para mantener. Opciones: EN_PREPARACION, DESPACHADO, EN_REPARTO, ENTREGADO): ");
                String estadoString = scanner.nextLine().trim();
                if (!estadoString.isEmpty()) {
                    try {
                        envio.setEstado(EstadoEnvio.valueOf(estadoString.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: Estado de envío inválido. Se mantiene el anterior.");
                    }
                }
                envioService.actualizar(envio);
                System.out.println("Envío ID " + envio.getId() + " actualizado exitosamente.");
            } catch (NumberFormatException e) {
                System.err.println("Error: El ID debe ser un número entero válido.");
            } catch (Exception e) {
                System.err.println("Error al actualizar el envío: " + e.getMessage());
            }
        }
        
        //Método para eliminar Envios.
        public void eliminarEnvio() {
            try {
                System.out.print("ID del Envio a eliminar: ");
                long id = Long.parseLong(scanner.nextLine());
                // Confirmación de seguridad
                System.out.print("¿Confirma la eliminación del Envío ID " + id + "? (s/n): ");
                String confirmacion = scanner.nextLine().trim();

                if (!confirmacion.equalsIgnoreCase("s")) {
                    System.out.println("Eliminación cancelada.");
                    return;
                }
                //De confirmarlo, se procede al Soft Delete.
                envioService.eliminar(id);
                System.out.println("Envio ID " + id + " eliminado exitosamente.");
            }catch (NumberFormatException e) {
                System.err.println("Error: El ID ingresado debe ser un número entero válido.");
            } catch(Exception e) {
                System.err.println("Error al eliminar el Envio" + e.getMessage());
            }
        }

    public void gestionarPedidos() {
        int opcion = -1;
        do {
            MenuDisplay.mostrarMenuPedidos(); // Muestra el submenú

            try {
                String entrada = scanner.nextLine().trim();
                opcion = Integer.parseInt(entrada);

                // Llama a los métodos CRUD (crearPedido, listarPedidos, etc.)
                switch (opcion) {
                    case 1: crearPedido(); break;
                    case 2: listarPedidos(); break;
                    case 3: buscarPedidoPorId(); break;
                    case 4: actualizarPedidos(); break;
                    case 5: eliminarPedido(); break;
                    case 9: System.out.println("Volviendo..."); break; // Detiene el bucle interno
                    case 0: System.exit(0); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Error: Ingrese un número.");
                opcion = -1; 
            } // ////////////////////////////////////Capturar otras Exceptions...

        } while (opcion != 9 && opcion != 0);
    }


public void gestionarEnvios() {
    int opcion = -1;
    do {
        // 1. Muestra las opciones al usuario
       MenuDisplay.mostrarMenuEnvios(); 

        try {
            String entrada = scanner.nextLine().trim();
            opcion = Integer.parseInt(entrada);

            switch (opcion) {
                case 1: listarEnvios();  // Llama a listar todos los envíos
                    break;
                case 2: buscarEnvioPorId(); // Llama a buscar por ID
                    break;
                case 3:
                    actualizarEnvio();  // Llama a actualizar datos
                    break;
                case 4:
                    eliminarEnvio();    // Llama al Soft Delete
                    break;
                case 9:
                    System.out.println("Volviendo al Menú Principal...");
                    break; // Sale del bucle interno
                case 0:
                    System.out.println("Cerrando aplicación...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("⚠️ Opción no válida. Intente de nuevo.");
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Ingrese un número válido para la opción.");
            opcion = -1; 
        } catch (Exception e) {
            System.err.println("❌ Ocurrió un error inesperado en gestión de envíos: " + e.getMessage());
        }

    } while (opcion != 9 && opcion != 0);
}
             
}
    
