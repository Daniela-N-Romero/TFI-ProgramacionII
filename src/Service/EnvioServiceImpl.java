/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Config.DatabaseConnection;
import Config.TransactionManager;
import Dao.EnvioDAO;
import Models.Envio;
import java.math.BigDecimal;
import java.util.List;
import java.sql.Connection;


/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public class EnvioServiceImpl implements GenericService<Envio> {
    
    private final EnvioDAO envioDAO;
  
    //constructor
    public EnvioServiceImpl(EnvioDAO envioDAO){
        if(envioDAO == null) {
            throw new IllegalArgumentException ("EnvioDAO no puede ser null");
        }
                
        this.envioDAO = envioDAO;
    }
    
    //Inserta un nuevo envio en la base de datos.
    
    @Override
    public void insertar(Envio envio) throws Exception {

        // VALIDACIÓN DE REGLA DE NEGOCIO (Usando el getter de tu clase)
        // El valor debe ser > 0, ya que los IDs válidos comienzan en 1.
        if(envio.getIdPedido() <= 0){
            throw new IllegalArgumentException("Error de Regla de Negocio: El Envío debe tener un ID de Pedido asociado y válido.");
        }
        try (TransactionManager txManager = 
         new TransactionManager(DatabaseConnection.getConnection())) {
        
            txManager.startTransaction();
            Connection conn = txManager.getConnection();      // Inicia la transacción (auto-commit=false)

            // Llama al DAO para insertar el Envío. 
            // Esto usa el método que acepta la conexión, garantizando atomicidad.
            envioDAO.insertarTx(envio, conn); 

            txManager.commit(); // Confirma la operación
            System.out.println("Envío insertado correctamente.");
            
        } catch (Exception e) {
            throw new Exception("Error transaccional al insertar el envío: " + e.getMessage(), e);
        }
    }
        //Actualiza un envio existente en la base de datos.
        @Override
        public void actualizar(Envio envio) throws Exception {
            validateEnvio(envio);
        try (TransactionManager txManager = 
           new TransactionManager(DatabaseConnection.getConnection())) {

           txManager.startTransaction();
           Connection conn = txManager.getConnection(); 

           envioDAO.actualizarTx(envio, conn); // ⭐️ Llamar al método Tx
           txManager.commit();
           System.out.println("Envío con ID " + envio.getId() + " actualizado correctamente.");

        } catch (Exception e) {
            throw new Exception("Error transaccional al actualizar el envío: " + e.getMessage(), e);
        }
    }

        //Elimina lógicamente un envio
        @Override
        public void eliminar(long id_dom) throws Exception {
            if (id_dom <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        envioDAO.eliminar(id_dom);
    }

         // Obtiene un envio por su ID.
        @Override
        public Envio getById(long id) throws Exception {
            if(id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return envioDAO.getById(id);
    }
        
        //Obtiene todos los envios activos (eliminado=FALSE).
        @Override
        public List<Envio> getAll() throws Exception {
            return envioDAO.getAll();
        }
    
    
    //Valida que un envio tenga datos correctos.
    //validar campo tracking
    //validar empresa
    //campo costo
    //validar estado inicial
    
    private void validateEnvio(Envio envio) {
        if(envio == null) {
            throw new IllegalArgumentException("El envio no puede ser null");
            
        }
        if(envio.getTracking()== null || envio.getTracking().trim().isEmpty()){
            throw new IllegalArgumentException("El número de tracking no puede estar vacío.");
        }
        if(envio.getEmpresa() == null) {
            throw new IllegalArgumentException("Debe especificarse la empresa de envío (ej: ANDREANI, OCA).");   
        }
        if(envio.getCosto() == null || envio.getCosto().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("El costo de envío debe ser un valor positivo.");
        }
        if(envio.getEstado() == null){
            throw new IllegalArgumentException("El estado del Envío es obligatorio.");
        }
           
    }   
} 
    

 

