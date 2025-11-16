/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.PreparedStatement;
import Config.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import Models.Pedido;
import Models.Envio; 
import Models.Estado;
import Models.EstadoEnvio;
import Models.TipoEnvio;
import Models.Empresa;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;
import java.sql.Connection;
import java.util.ArrayList;
/**
 *
 * @author Daniela Nahir Romero
 */
public class PedidoDAO implements GenericDAO<Pedido> {
    //Queries SQL:
    private static final String INSERT_SQL = "INSERT INTO pedidos (numero_pedido, fecha, cliente_nombre, total, estado) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE pedidos SET numero_pedido = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ? WHERE id = ?";
    private static final String DELETE_SQL = "UPDATE pedidos SET eliminado = 1 WHERE id = ?"; // Soft Delete
    
    private static final String SELECT_BASE = "SELECT p.*, " +
            "e.id AS e_id, e.tracking, e.costo, e.fechaDespacho, e.fechaEstimada, e.empresa, e.estado AS e_estado, e.tipo " + 
            "FROM pedidos p LEFT JOIN envios e ON p.id = e.id_pedido " +
            "WHERE p.eliminado = 0";

    private static final String SELECT_BY_ID_SQL = SELECT_BASE + " AND p.id = ?";
    private static final String SELECT_ALL_SQL = SELECT_BASE + " ORDER BY p.fecha DESC";
    
    // Métodos para CRUD.
    @Override
    public void insertar(Pedido pedido) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setPedidoParameters(stmt, pedido, 1);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                 throw new Exception("Error al insertar el pedido: ninguna fila afectada.");
            }

            setGeneratedId(stmt, pedido);
        }
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        //Modifica los datos de un pedido ya existente.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            int index = setPedidoParameters(stmt, pedido, 1);
            stmt.setLong(index, pedido.getId()); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                 throw new SQLException("No se pudo actualizar el pedido con ID: " + pedido.getId());
            }
        }
    }
    

    @Override
    public void eliminar(long id) throws Exception {
        // Implementación de Soft Delete
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id);
            }
        }
    }

    @Override
    public Pedido getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

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
        stmt.setDate(index++, Date.valueOf(pedido.getFecha()));
        
        stmt.setString(index++, pedido.getClienteNombre());
        stmt.setDouble(index++, pedido.getTotal());
        stmt.setString(index++, pedido.getEstado().name()); // Enum a String

        return index;
    }
    
    
    //Obtiene el ID autogenerado y lo asigna.
     
    private void setGeneratedId(PreparedStatement stmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del pedido falló, no se obtuvo ID generado");
            }
        }
    }
    
        private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        // Mapeo de atributos de Base (asumo que Base tiene setId y isEliminado)
        pedido.setId(rs.getLong("id"));
        //  Mapeo de atributos de Pedido
        pedido.setNumero(rs.getString("numero_pedido"));
        // Conversión de java.sql.Date a java.time.LocalDate
        java.sql.Date sqlDate = rs.getDate("fecha");
        if (sqlDate != null) {
            pedido.setFecha(sqlDate.toLocalDate());
        }
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        // Se lee como double y se convierte a String.
        pedido.setTotal(rs.getDouble("total"));
        // Conversión de String (de la DB) a Enum (Models.Estado)
        String estadoString = rs.getString("estado");
        if (estadoString != null) {
            pedido.setEstado(Estado.valueOf(estadoString));
        }
        // 2. Mapeo de Envío (Eager Loading)
        long envioId = rs.getLong("e_id"); // e_id viene del SELECT_BASE
        // rs.wasNull() comprueba si el valor leído (e_id) era NULL en la BD
        if (!rs.wasNull()) { 
            Envio envio = new Envio();
            
            
            envio.setId(envioId);
            envio.setTracking(rs.getString("tracking"));
            envio.setCosto(rs.getDouble("costo"));
            
            java.sql.Date sqlFechaDespacho = rs.getDate("fechaDespacho");
            if (sqlFechaDespacho != null) {
                envio.setFechaDespacho(sqlFechaDespacho.toLocalDate());
            }
            java.sql.Date sqlFechaEstimada = rs.getDate("fechaEstimada");
            if (sqlFechaEstimada != null) {
                envio.setFechaEstimada(sqlFechaEstimada.toLocalDate());
            }
            
            // Mapeo de Enums de Envío (asumo que están en Models)
            envio.setEmpresa(Empresa.valueOf(rs.getString("empresa")));
            envio.setEstado(EstadoEnvio.valueOf(rs.getString("e_estado"))); 
            envio.setTipo(TipoEnvio.valueOf(rs.getString("tipo")));

            pedido.setEnvio(envio);
        }
    return pedido;
}
    
}
