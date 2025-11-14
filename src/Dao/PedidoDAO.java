/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.PreparedStatement;
import Config.DatabaseConnection;
import java.sql.ResultSet;
import Models.Pedido;
import Models.Envio; 
import Models.Estado;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import org.mariadb.jdbc.Statement;
/**
 *
 * @author Daniela Nahir Romero
 */
public class PedidoDAO implements GenericDAO<Pedido> {
    //Queries SQL:
    private static final String INSERT_SQL = "INSERT INTO pedidos (numero, fecha, cliente_nombre, total, estado, envio_id, cliente_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE pedidos SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ?, envio_id = ?, cliente_id = ? WHERE id = ?";
    private static final String DELETE_SQL = "UPDATE pedidos SET eliminado = TRUE WHERE id = ?"; // Soft Delete
    
    private static final String SELECT_BASE = "SELECT p.*, " +
            "e.id AS e_id, e.tracking, e.costo, e.fecha_despacho, e.fecha_estimada, e.empresa, e.estado AS e_estado, e.tipo " + 
            "FROM pedidos p LEFT JOIN envios e ON p.envio_id = e.id " +
            "WHERE p.eliminado = FALSE";

    private static final String SELECT_BY_ID_SQL = SELECT_BASE + " AND p.id = ?";
    private static final String SELECT_ALL_SQL = SELECT_BASE + " ORDER BY p.fecha DESC";
    
    // Métodos para CRUD.
    @Override
    public void insertar(Pedido pedido) throws Exception {
    String sql = "INSERT INTO Pedido (id, numero, fecha, nombre_del_cliente, total, estado, envio) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    
    // Usamos 'try-with-resources' para asegurar que la conexión se cierre automáticamente
    try (Connection conn = DatabaseConnection.getConnection();
         // Creamos el PreparedStatement con la conexión y SQL
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        // Asignamos los valores del objeto 'pedido' a los parámetros del SQL
        stmt.setInt(1, pedido.getId()); 
        stmt.setString(2, pedido.getNumero()); 
        stmt.setDate(3, java.sql.Date.valueOf(pedido.getFecha())); 
        stmt.setString(4, pedido.getClienteNombre()); 
        stmt.setDouble(5, pedido.getTotal()); 
        stmt.setString(6, pedido.getEstado().name()); 
        if (pedido.getEnvio() != null && pedido.getEnvio().getId() > 0) {
            stmt.setInt(7, pedido.getEnvio().getId());
        } else {
            stmt.setNull(4, java.sql.Types.INTEGER);
        }
        stmt.executeUpdate();
        
//        // Ejecutamos la sentencia (executeUpdate() se usa para INSERT, UPDATE, DELETE)
//        int filasAfectadas = stmt.executeUpdate();

        // Opcional: Verificar que la inserción se realizó
        if (filasAfectadas == 0) {
            throw new Exception("Error al insertar el pedido: ninguna fila afectada.");
        }
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getInt(1));
                System.out.println("Persona insertada con ID: " + pedido.getId());
            } else {
                throw new SQLException("La inserción de la persona falló, no se obtuvo ID generado");
            }
        }
    }     
 }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        //Modifica los datos de un pedido ya existente.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            int index = setPedidoParameters(stmt, pedido, 1);
            stmt.setInt(index, pedido.getId()); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                 throw new SQLException("No se pudo actualizar el pedido con ID: " + pedido.getId());
            }
        }
    }
    

    @Override
    public void eliminar(int id) throws Exception {
        // Implementación de Soft Delete
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id);
            }
        }
    }

    @Override
    public Pedido getById(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener pedido por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Pedido> getAll() throws Exception {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todos los pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }
    

      
    //Métodos auxiliares:
    
    private int setPedidoParameters(PreparedStatement stmt, Pedido pedido, int startIndex) throws SQLException {
        int index = startIndex;

        stmt.setString(index++, pedido.getNumero());
        // java.time.LocalDate a java.sql.Date
        stmt.setDate(index++, java.sql.Date.valueOf(pedido.getFecha()));
        
        stmt.setString(index++, pedido.getClienteNombre());
        stmt.setDouble(index++, pedido.getTotal());
        stmt.setString(index++, pedido.getEstado().name()); // Enum a String

        // Manejo de FK envio_id
        setEnvioId(stmt, index++, pedido.getEnvio());

        // Manejo de FK cliente_id (Placeholder)
        stmt.setNull(index++, java.sql.Types.INTEGER); 

        return index;
    }
    
    private void setEnvioId(PreparedStatement stmt, int parameterIndex, Envio envio) throws SQLException {
        if (envio != null && envio.getId() > 0) {
            stmt.setInt(parameterIndex, envio.getId());
        } else {
            stmt.setNull(parameterIndex, java.sql.Types.INTEGER); 
        }
    }
    
    //Obtiene el ID autogenerado y lo asigna.
     
    private void setGeneratedId(PreparedStatement stmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("La inserción del pedido falló, no se obtuvo ID generado");
            }
        }
    }
    
}
