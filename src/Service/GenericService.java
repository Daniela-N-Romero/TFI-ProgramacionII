/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Service;
import java.util.List;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 * @param <T>
 */
public interface GenericService<T> {
    //interfaz generica que define las operaciones basicas que deben implementar todos los servicios
    void insertar (T entidad) throws Exception;
    void actualizar (T entidad) throws Exception;
    void eliminar (long id_dom)throws Exception;
    T getById (long id) throws Exception;
    List<T> getAll() throws Exception;
    
         
}
