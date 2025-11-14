/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.PreparedStatement;
import Config.DatabaseConnection;
import Models.Pedido;
import java.util.List;
import java.sql.Connection;
/**
 *
 * @author Daniela Nahir Romero
 */
public class PedidoDAO implements GenericDAO<Pedido>{
    
    
    @Override
    public void insertar(Pedido pedido) throws Exception {
    String sql = "INSERT INTO Pedido (id, numero, fecha, nombre_del_cliente, total, estado) VALUES (?, ?, ?, ?, ?, ?)";
    
    // Usamos 'try-with-resources' para asegurar que la conexión se cierre automáticamente
    try (Connection conn = DatabaseConnection.getConnection();
         // Creamos el PreparedStatement con la conexión y SQL
         PreparedStatement ps = conn.prepareStatement(sql)) {

        // Asignamos los valores del objeto 'pedido' a los parámetros (?) del SQL
        ps.setInt(1, pedido.getId()); 
        ps.setString(2, pedido.getNumero()); 
        // Asumiendo que getFecha() devuelve un java.sql.Date o similar. 
        // Si devuelve java.util.Date, necesitas convertirlo: new java.sql.Date(pedido.getFecha().getTime())
        ps.setDate(3, java.sql.Date.valueOf(pedido.getFecha())); 
        ps.setString(4, pedido.getClienteNombre()); 
        ps.setDouble(5, pedido.getTotal()); 
        ps.setString(6, pedido.getEstado().name()); 

        // 2. Ejecutar la sentencia (executeUpdate() se usa para INSERT, UPDATE, DELETE)
        int filasAfectadas = ps.executeUpdate();

        // Opcional: Verificar que la inserción se realizó
        if (filasAfectadas == 0) {
            throw new Exception("Error al insertar el pedido: ninguna fila afectada.");
        }
        
    } catch (Exception e) {
        // En un DAO real, podrías registrar el error o envolverlo en una excepción de capa de persistencia.
        e.printStackTrace();
        throw new Exception("Error en la inserción de Pedido: " + e.getMessage(), e);
    }

           
    }

    @Override
    public void actualizar(Pedido entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(int id_dom) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Pedido getById(int id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Pedido> getAll() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

      
    
}
