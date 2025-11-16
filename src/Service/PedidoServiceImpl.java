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
    
    
    //Inserta un nuevo pedido en la base de datos.
    //public void insertar(Pedido pedido) throws Exception {
        //validatePedido(pedido);
        //validateEnvio(pedido.getEnvio(), null);
    
    //Actualiza una pedido existente en la base de datos
    //Validaciones:
    @Override
    public void actualizar(Pedido pedido) throws Exception {
        validatePedido(pedido);
        if(pedido.getId() <= 0){
            throw new IllegalArgumentException("El ID del pedidp debe ser mayor a 0 para actualizar");
            validatePedidoUnique(pedido.getNumero(), pedido.getId());
            pedidoDAO.actualizar(pedido);
        }
        
    }
        
    
    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        pedidoDAO.eliminar(id);     
    }
        //Obtiene una pedidp por su ID.
        
    public Pedido getById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
            return pedidoDAO.getById(id);
    }
    // Obtiene todas los pedidps activos (eliminado=FALSE).
    public List<Pedido> getAll() throws Exception {
        return pedidoDAO.getAll();
    }
    //Expone el servicio de envio para que MenuHandler pueda usarlo.
    public EnvioServiceImpl getEnvioService() {
        return this.envioServiceImpl;
    }
    //Busca pedido por numero
    public List<Pedido> buscarPorNumeroPedido(String filtro) throws Exception {
        if (filtro == null || filtro.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }
        return pedidoDAO.buscarPorNumeroPedido(filtro);        
    }
    //buscar pedido por Id
    public Pedido buscarPorId(long id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");    
        }
         return pedidoDAO.getById(id);
    }
    // Elimina un pedido de forma SEGURA actualizando primero la FK del pedido.
    public void eliminarEnvioDePedido(int envioId, int pedidoId) throws Exception {
        if (pedidoId <= 0 || envioId <= 0) {
            throw new IllegalArgumentException("Los IDs deben ser mayores a 0");      
        }
        Pedido pedido = pedidoDAO.getById(pedidoId);
        if (pedido == null){
            throw new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId);
        }
        if (pedido.getEnvio() == null || pedido.getEnvio().getId() != envioId) {
            throw new IllegalArgumentException("El envio no pertenece a ese pedido");
        }
        pedido.setEnvio(null);
        pedidoDAO.actualizar(pedido);
        envioServiceImpl.eliminar(envioId);
                       
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
    private void validateNumeroUnique(String numero, Long pedidoId) throws Exception {
        if (existente != null) {
            if (pedidoId == null || !pedidoId.equals(Long.valueOf(existente.getId()))) {
                throw new IllegalArgumentException("Ya existe un pedido con el número: " + numero);
                
            }
            
        }
    }
    
}
