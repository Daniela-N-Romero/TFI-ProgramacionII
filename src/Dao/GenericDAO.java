/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Dao;

import java.util.List;

/**
 *
 * @author Daniela Nahir Romero
 * @param <T>
 */
public interface GenericDAO<T> {
    
    void insertar(T entidad) throws Exception;
    
    void actualizar(T entidad) throws Exception;
    
    void eliminar(long id) throws Exception;
    
    T getById(long id) throws Exception;
    
    List<T> getAll() throws Exception;
    
    
}
