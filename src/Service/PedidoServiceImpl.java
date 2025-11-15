/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Dao.PedidoDAO;
import Models.Pedido;
import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 */

//Implementación del servicio de negocio para la entidad Pedido
//Capa intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
public class PedidoServiceImpl implements GenericService<Pedido> {
    private final PedidoDAO pedidoDAO;
    
    
    private final EnvioServiceImpl envioServiceImpl;
    
    //contructor

    public PedidoServiceImpl(PedidoDAO pedidoDAO, EnvioServiceImpl envioServiceImpl) {
        if (pedidoDAO == null){
            throw new IllegalArgumentException("PedidoDAO no puede ser null");
        }
        if (envioServiceImpl == null){
            throw new IllegalArgumentException("EnvioServiceImpl no puede ser null");
        }
        this.pedidoDAO = pedidoDAO;
        this.envioServiceImpl = envioServiceImpl;
    }
    
    
    //Inserta una nuevo pedido en la base de datos.
    public void insertar(Pedido pedido) throws Exception {
        validatePedido(pedido);
        validateEnvio(pedido.getEnvio(), null);
        
        
        
        
        
        
        
       
        
    }
    
    
    
    
    
    
    
    
    
}
