/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Config.TransactionManager;
import Dao.EnvioDAO;
import Dao.PedidoDAO;
import Models.Envio;
import Models.Pedido;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 */


//Implementación del servicio de negocio para la entidad Pedido
//Capa intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
public class PedidoServiceImpl implements GenericService<Pedido> {

    // 1. Declarar las dependencias como final (siempre inicializadas)
    private final PedidoDAO pedidoDAO;
    private final EnvioDAO envioDAO;
    // Asumiendo que TransactionManager también es una dependencia
    private final TransactionManager txManager; 

    // 2. Constructor para inyectar las dependencias
    // Se asegura que una instancia de PedidoServiceImpl siempre tendrá sus DAOs y su gestor de transacciones.
    public PedidoServiceImpl(PedidoDAO pedidoDAO, EnvioDAO envioDAO, TransactionManager txManager) {
          if (pedidoDAO == null){
            throw new IllegalArgumentException("PedidoDAO no puede ser null");
         }
        if (envioDAO == null){
            throw new IllegalArgumentException("EnvioDAO no puede ser null");
        }
        
        if (txManager == null){
            throw new IllegalArgumentException("TransactionManager no puede ser null");
        }
        
        this.pedidoDAO = pedidoDAO;
        this.envioDAO = envioDAO;
        this.txManager = txManager;
    }
    
    
   @Override
    public void insertar(Pedido pedido) throws Exception {

        validatePedido(pedido);

        try (txManager) { 
            Connection conn = txManager.getConnection(); 
            txManager.startTransaction(); 
            pedidoDAO.insertarTx(pedido, conn); 

            if (pedido.getEnvio() != null) {
                Envio envio = pedido.getEnvio();
                envioDAO.insertarTx(envio, conn); 
                pedido.setEnvio(envio); 
                pedidoDAO.actualizarTx(pedido, conn); 
            }
            txManager.commit();
            System.out.println("Pedido insertado correctamente.");
            
        } catch (Exception e) {
            txManager.rollback(); 
            throw new Exception("Error transaccional al insertar el pedido: " + e.getMessage(), e); 

        } finally {
            txManager.close(); 
        }
    }

     @Override
    public void actualizar(Pedido entidades) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
        
    
   @Override
    public void eliminar(long id) {
        validarId(id);       
        try (txManager) {
            Connection conn = txManager.getConnection();
            txManager.startTransaction();
            
            pedidoDAO.eliminarTx(id,conn); // Usa el DAO inyectado
            envioDAO.eliminarTx(id,conn); // Usa el DAO inyectado

            txManager.commit();
            
        } catch (Exception e) {
            txManager.rollback();
            System.out.println("Error en el servicio al eliminar el pedido."+ e.getMessage());
        } finally {
            txManager.close(); 
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
        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser un valor positivo");
        }
        if (pedido.getEstado() == null) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio.");
        }
        if (pedido.getEnvio() == null) {
            throw new IllegalArgumentException("El Pedido debe tener un Envío asociado para la inserción.");
        }
        
    }
    //Valida que el 'numero' del pedido sea único en el sistema
    //PedidoDAO tiene un método para buscar por 'numero'
    private void validatePedidoUnique(String numero, long pedidoId) throws Exception {
//        Pedido existente = 
//        if (existente != null) {
//            if (pedidoId == null || !pedidoId.equals(Long.valueOf(existente.getId()))) {
//                throw new IllegalArgumentException("Ya existe un pedido con el número: " + numero);
//                
//            }
//            
//        }
 }

    private void validarId(long id){
        if(id <= 0){
            throw new NumberFormatException("La id deber ser mayor a 0.");
        }
    }
}
    
    

