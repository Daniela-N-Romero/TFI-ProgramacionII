/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Service;
import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 */
public interface GenericService<T> {
    //interfaz generica que define las operaciones basicas que deben implementar todos los servicios
    void insertar (T entidad) throws Exception;
    void actualizar (T entidad) throws Exception;
    void eliminar (long id_dom)throws Exception;
    T getByID (long id) throws Exception;
    List<T> getAll() throws Exception;
         
}
