/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Dao.EnvioDAO;
import Dao.GenericDAO;
import Models.Envio;

import java.util.List;




/**
 *
 * @author Daniela Nahir Romero
 */
public class EnvioServiceImpl implements GenericService<Envio> {
    
    private final GenericDAO<Envio> envioDAO;
    
    
    //constructor
    public EnvioServiceImpl(GenericDAO<Envio> envioDAO) {
        if(envioDAO == null) {
            throw new IllegalArgumentException ("EnvioDAO no puede ser null");
        }
        this.envioDAO = envioDAO;
    }
    
    //Inserta un nuevo envio en la base de datos.
    
    public void insertar (Envio envio) throws Exception{
        validateEnvio(envio);
        envioDAO.insertar(envio);
        
    }
    //Actualiza un envio existente en la base de datos.
    
    public void actualizar(Envio envio) throws Exception {
        validateEnvio(envio);
        if(envio.getId()<= 0){
            throw new IllegalArgumentException("El ID del envio debe ser mayor a 0 para actualizar");
        }
        envioDAO.actualizar(envio);
        
        
    }
    
    //Elimina lógicamente un envio
    
    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        envioDAO.eliminar(id);
    }
    
    // Obtiene un envio por su ID.
    
    public Envio getById(int id) throws Exception {
        if(id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
            
        }
        return envioDAO.getById(id);
    }
    //Obtiene todos los envios activos (eliminado=FALSE).
    
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
        if(envio.getCosto() <= 0){
            throw new IllegalArgumentException("El costo de envío debe ser un valor positivo.");
        }
        if(envio.getEstado() == null){
            throw new IllegalArgumentException("El estado del Envío es obligatorio.");
        }
           
    }   
    
}
