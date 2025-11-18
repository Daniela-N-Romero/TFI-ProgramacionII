/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Config.DatabaseConnection;
import Config.TransactionManager;
import Dao.EnvioDAO;
import Dao.PedidoDAO;
import Models.Envio;
import Models.Pedido;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */


//Implementación del servicio de negocio para la entidad Pedido
//Capa intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
public class PedidoServiceImpl implements GenericService<Pedido> {

    // 1. Declarar las dependencias como final (siempre inicializadas)
    private final PedidoDAO pedidoDAO;
    private final EnvioDAO envioDAO;

    public PedidoServiceImpl(PedidoDAO pedidoDAO, EnvioDAO envioDAO) {
        if (pedidoDAO == null){
            throw new IllegalArgumentException("PedidoDAO no puede ser null");
         }
        if (envioDAO == null){
            throw new IllegalArgumentException("EnvioDAO no puede ser null");
        }
        this.pedidoDAO = pedidoDAO;
        this.envioDAO = envioDAO;

    }
    
    
   @Override
public void insertar(Pedido pedido) throws Exception {
    validatePedido(pedido);
    try (TransactionManager txManager = 
         new TransactionManager(DatabaseConnection.getConnection())) {
        
        txManager.startTransaction();
        Connection conn = txManager.getConnection();
        // Insertar Pedido y obtener su ID generado.
        pedidoDAO.insertarTx(pedido, conn);

        if (pedido.getEnvio() != null) {
            Envio envio = pedido.getEnvio();
            
            // Asignación de la Clave Foránea (FK).
            envio.setIdPedido(pedido.getId());
            
            // Insertar Envío usando el ID del Pedido.
            envioDAO.insertarTx(envio, conn);
            
            // Opcional: actualizar el objeto Pedido con el Envío que ya tiene ID de Envío (si aplica).
            pedido.setEnvio(envio);
        }
        txManager.commit();
        System.out.println("Pedido insertado correctamente.");
        
    } catch (Exception e) {
        throw new Exception("Error transaccional al insertar el pedido: " + e.getMessage(), e);
    }
}

     @Override
    public void actualizar(Pedido pedido) throws Exception {
        // validatePedido(pedido); // Asumiendo que tienes un método de validación
    
        // Usamos el TransactionManager para garantizar la gestión correcta de la conexión.
       try (TransactionManager txManager = 
         new TransactionManager(DatabaseConnection.getConnection())) {
        
        txManager.startTransaction();
        Connection conn = txManager.getConnection(); // Opcional, si el DAO lo requiere para setear auto-commit=false
        
            // 1. Llama al DAO para ejecutar el UPDATE
            pedidoDAO.actualizarTx(pedido, conn); 

            txManager.commit(); // 2. Confirma los cambios en la base de datos
            System.out.println("Pedido con ID " + pedido.getId() + " actualizado correctamente.");
        
        } catch (Exception e) {
            throw new Exception("Error transaccional al actualizar el pedido: " + e.getMessage(), e);
        }
    }
        
    
   @Override
    public void eliminar(long id){
        validarId(id);       
        try (TransactionManager txManager = 
         new TransactionManager(DatabaseConnection.getConnection())) {
        
        txManager.startTransaction();
        Connection conn = txManager.getConnection();
            
            pedidoDAO.eliminarTx(id,conn); // Usa el DAO inyectado
            envioDAO.eliminarTx(id,conn); // Usa el DAO inyectado

            txManager.commit();
            
        } catch(NumberFormatException e) {
                System.err.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) { 
            System.err.println("Error al eliminar Pedido: " + e.getMessage());
        }
    }
        
    @Override
    public Pedido getById(long id) throws Exception {
        validarId(id);  
        return pedidoDAO.getById(id);
    }
    
    
    // Obtiene todas los pedidps activos (eliminado=FALSE).
 
    @Override
    public List getAll() throws Exception {
        return pedidoDAO.getAll();
    }
    
    public  List<Pedido> obtenerPedidosEliminados() throws Exception {
        return pedidoDAO.getEliminados();
    }
    
    public boolean activarPedido(long idPedido) throws Exception {
        validarId(idPedido);
        try (TransactionManager txManager = 
         new TransactionManager(DatabaseConnection.getConnection())) {
        
            txManager.startTransaction();
            Connection conn = txManager.getConnection();
            
            pedidoDAO.restaurarEliminadoTx(idPedido,conn);  // Usa el DAO inyectado
            envioDAO.restaurarEliminadoTx(idPedido,conn);// Usa el DAO inyectado

            txManager.commit();
            return true;
            
        } catch (Exception e) {
            System.out.println("Error en el servicio al activar el pedido."+ e.getMessage());
            return false;
        } 


    }
    
    
        
      
    // Valida que una pedido tenga datos correctos.
    private void validatePedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser null");
        }
        if (pedido.getNumero() == null || pedido.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido es obligatorio.");
        }
        if (pedido.getClienteNombre() == null || pedido.getClienteNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (pedido.getTotal()== null || pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser un valor positivo");
        }
        if (pedido.getEstado() == null) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio.");
        }
        
    }

    private void validarId(long id){
        if(id <= 0){
            throw new NumberFormatException("La id deber ser mayor a 0.");
        }
    }
    

    
}
    
    

