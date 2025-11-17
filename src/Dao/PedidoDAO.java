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
    
    private static final String SELECT_BASE = "SELECT p.*, " + "e.* " + 
            "FROM pedidos p LEFT JOIN envios e ON p.id = e.id_pedido " +
            "WHERE p.eliminado = 0";

    private static final String SELECT_BY_ID_SQL = SELECT_BASE + " AND p.id = ?";
    private static final String SELECT_ALL_SQL = SELECT_BASE + " ORDER BY p.fecha DESC";
    private static final String SELECT_NUMERO_PEDIDO = "SELECT numero_pedido FROM pedidos WHERE eliminado = FALSE ORDER BY numero_pedido DESC LIMIT 1";
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
        stmt.setBigDecimal(index++, pedido.getTotal());
        stmt.setString(index++, pedido.getEstado().name()); // Enum a String
        
        // Manejo de FK envio_id
        setEnvioId(stmt, index++, pedido.getEnvio());
        // Manejo de FK cliente_id (Placeholder)
        stmt.setNull(index++, java.sql.Types.INTEGER); 

        return index;
    }
    
    private void setEnvioId(PreparedStatement stmt, int parameterIndex, Envio envio) throws SQLException {
        if (envio != null && envio.getId() > 0) {
            stmt.setLong(parameterIndex, envio.getId());
        } else {
            stmt.setNull(parameterIndex, java.sql.Types.INTEGER); 
        }
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
            pedido.setTotal(rs.getBigDecimal("total"));
            // Conversión de String (de la DB) a Enum (Models.Estado)
            String estadoString = rs.getString("estado");
            if (estadoString != null) {
                pedido.setEstado(Estado.valueOf(estadoString));
            }
            // 2. Mapeo de Envío (Eager Loading)
            long envioId = rs.getLong("e.id"); // e_id viene del SELECT_BASE
            // rs.wasNull() comprueba si el valor leído (e_id) era NULL en la BD
            if (!rs.wasNull()) { 
                Envio envio = new Envio();
            
            
            envio.setId(envioId);
            envio.setTracking(rs.getString("e.tracking"));
            envio.setCosto(rs.getBigDecimal("e.costo"));
            
            java.sql.Date sqlFechaDespacho = rs.getDate("e.fechaDespacho");
            if (sqlFechaDespacho != null) {
                envio.setFechaDespacho(sqlFechaDespacho.toLocalDate());
            }
            java.sql.Date sqlFechaEstimada = rs.getDate("e.fechaEstimada");
            if (sqlFechaEstimada != null) {
                envio.setFechaEstimada(sqlFechaEstimada.toLocalDate());
            }
            
            // Mapeo de Enums de Envío (asumo que están en Models)
            envio.setEmpresa(Empresa.valueOf(rs.getString("e.empresa")));
            envio.setEstado(EstadoEnvio.valueOf(rs.getString("e.estado"))); 
            envio.setTipo(TipoEnvio.valueOf(rs.getString("e.tipo")));

            pedido.setEnvio(envio);
        }
    return pedido;
}

    @Override
    public void insertarTx(Pedido pedido, Connection conn) throws Exception {
         if (conn == null) {
             throw new IllegalArgumentException("La conexión no puede ser null.");
         }
       try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, new String[] {"id"})) {

            setPedidoParameters(stmt, pedido, 1);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                 throw new Exception("Error al insertar el pedido: ninguna fila afectada.");
            }

            setGeneratedId(stmt, pedido);
        }    
    }

    @Override
    public void actualizarTx(Pedido pedido, Connection conn) throws Exception {
            if (conn == null) {
            throw new IllegalArgumentException("La conexión para la transacción no puede ser nula.");
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
        
            // 1. Setear campos SET (Parámetros 1 a 5)
            stmt.setString(1, pedido.getNumero());
            stmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha())); 
            stmt.setString(3, pedido.getClienteNombre());
            stmt.setBigDecimal(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado().name());
        
            // 2. Setear la Cláusula WHERE (Parámetro 6)
            stmt.setLong(6, pedido.getId()); 

            int rowsAffected = stmt.executeUpdate();
        
            if (rowsAffected == 0) {
                // Si rowsAffected es 0, el ID no se encontró o no se actualizó.
                throw new SQLException("No se encontró el Pedido con ID: " + pedido.getId() + " para actualizar.");
            }
        
        } catch (SQLException e) {
            // Relanzar la excepción para que el Service pueda hacer el rollback
            throw new Exception("Error al ejecutar UPDATE en PedidoDAO: " + e.getMessage(), e);
        }
}

    @Override
    public void eliminarTx(long id, Connection conn) throws Exception {
          if (conn == null) throw new IllegalArgumentException("La conexión no puede ser null.");

        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
                int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id);
            }
        }
    }
    
    public String getLastPedidoNumber() throws Exception {
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_NUMERO_PEDIDO);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("numero_pedido");
            }
            return null; // No hay pedidos en la DB
            
        } catch (SQLException e) {
            throw new Exception("Error al obtener el último número de pedido: " + e.getMessage(), e);
        }
    }
}