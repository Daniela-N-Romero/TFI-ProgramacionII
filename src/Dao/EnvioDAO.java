/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.PreparedStatement;
import Config.DatabaseConnection;
import java.sql.ResultSet;
import Models.Envio; 
import Models.EstadoEnvio;
import Models.Empresa;
import Models.TipoEnvio;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.util.ArrayList;
/**
/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public class EnvioDAO implements GenericDAO<Envio> {

    private static final String INSERT_SQL = "INSERT INTO envios (tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado, id_pedido) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE envios SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fechaDespacho = ?, fechaEstimada = ?, estado = ?, id_pedido = ? WHERE id = ?";
    private static final String SOFT_DELETE_SQL = "UPDATE envios SET eliminado = 1 WHERE id = ?"; 
    private static final String GET_BY_ID_SQL = "SELECT * FROM envios WHERE id = ? AND eliminado = 0";
    private static final String GET_ALL_SQL = "SELECT * FROM envios WHERE eliminado = 0";
    private static final String SOFT_DELETE_BY_PEDIDO_SQL = "UPDATE envios SET eliminado = 1 WHERE id_pedido = ?";
    private static final String VERIFICAR_TRACKING = "SELECT COUNT(*) FROM envios WHERE tracking = ? AND eliminado = FALSE";
    private static final String RESTORE_ELIMINADO = "UPDATE pedidos SET eliminado = 0 WHERE id = ?";
    private static final String SELECT_ELIMINADOS = "SELECT e.* , p.* " + 
            "FROM envios e LEFT JOIN pedidos p ON p.id = e.id_pedido " +
            "WHERE e.eliminado = 1";

    
 // --- Mapeo de Resultados ---

    private Envio mapResultSetToEnvio(ResultSet rs) throws SQLException {
        Envio envio = new Envio();
        envio.setId(rs.getLong("id"));
        envio.setTracking(rs.getString("tracking"));
        envio.setCosto(rs.getBigDecimal("costo"));
        
        // Mapear Fechas (posiblemente nulas)
        if (rs.getDate("fechaDespacho") != null) {
            envio.setFechaDespacho(rs.getDate("fechaDespacho").toLocalDate());
        }
        if (rs.getDate("fechaEstimada") != null) {
            envio.setFechaEstimada(rs.getDate("fechaEstimada").toLocalDate());
        }
        
        // Mapear ENUMs
        envio.setEmpresa(Empresa.valueOf(rs.getString("empresa")));
        envio.setTipo(TipoEnvio.valueOf(rs.getString("tipo")));
        envio.setEstado(EstadoEnvio.valueOf(rs.getString("estado")));
        
        envio.setIdPedido(rs.getLong("id_pedido"));
        
        return envio;
    }
    
    // --- MÉTODOS CRUD ESTÁNDARES ---
    
    @Override
    public void insertar(Envio entidad) throws Exception {
        throw new UnsupportedOperationException("Usar insertarTx() para asegurar la integridad transaccional.");
    }

    @Override
    public void actualizar(Envio entidad) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, entidad.getTracking());
            stmt.setString(2, entidad.getEmpresa().name());
            stmt.setString(3, entidad.getTipo().name());
            stmt.setBigDecimal(4, entidad.getCosto());
            stmt.setDate(5, java.sql.Date.valueOf(entidad.getFechaDespacho()));
            
            if (entidad.getFechaEstimada() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(entidad.getFechaEstimada()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setString(7, entidad.getEstado().name());
            stmt.setLong(8, entidad.getIdPedido()); 
            stmt.setLong(9, entidad.getId());   
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el Envío con ID: " + entidad.getId() + " para actualizar.");
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el Envío: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SOFT_DELETE_SQL)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el Envío con ID: " + id + " para eliminar.");
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar (Soft Delete) el Envío: " + e.getMessage(), e);
        }
    }

    @Override
    public Envio getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_SQL)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Envío por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Envio> getAll() throws Exception {
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                envios.add(mapResultSetToEnvio(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar todos los Envios: " + e.getMessage(), e);
        }
        return envios;
    }
    
    // --- MÉTODOS TRANSACCIONALES (Tx) ---
    
    /**
     * Inserta un nuevo envío, usando la conexión proporcionada (transaccional).
     * @param envio
     * @param conn
     * @throws java.lang.Exception
     */
    @Override
    public void insertarTx(Envio envio, Connection conn) throws Exception {
        if (conn == null) throw new IllegalArgumentException("La conexión transaccional no puede ser null.");

        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, 
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, envio.getTracking());
            stmt.setString(2, envio.getEmpresa().name());
            stmt.setString(3, envio.getTipo().name());
            stmt.setBigDecimal(4, envio.getCosto());
            stmt.setDate(5, java.sql.Date.valueOf(envio.getFechaDespacho()));
            
            if (envio.getFechaEstimada() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(envio.getFechaEstimada()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setString(7, envio.getEstado().name());
            stmt.setLong(8, envio.getIdPedido()); // FK id_pedido
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    envio.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw e; 
        }
    }

    @Override
    public void actualizarTx(Envio envio, Connection conn) throws Exception {
        if (conn == null) throw new IllegalArgumentException("La conexión transaccional no puede ser null.");
        
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, envio.getTracking());
            stmt.setString(2, envio.getEmpresa().name());
            stmt.setString(3, envio.getTipo().name());
            stmt.setBigDecimal(4, envio.getCosto());
            stmt.setDate(5, java.sql.Date.valueOf(envio.getFechaDespacho()));
            
            if (envio.getFechaEstimada() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(envio.getFechaEstimada()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setString(7, envio.getEstado().name());
            stmt.setLong(8, envio.getId()); // WHERE id = ?
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw e; 
        }
    }

    /**
     * Elimina (Soft Delete) el Envío usando la FK id_pedido.
     * @param id_pedido
     * @param conn
     * @throws java.lang.Exception
     */
    @Override
    public void eliminarTx(long id_pedido, Connection conn) throws Exception {
        if (conn == null) throw new IllegalArgumentException("La conexión transaccional no puede ser null.");

        try (PreparedStatement stmt = conn.prepareStatement(SOFT_DELETE_BY_PEDIDO_SQL)) {
            stmt.setLong(1, id_pedido);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean isTrackingUnique(String tracking) throws Exception {
         
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(VERIFICAR_TRACKING)) {
            
            stmt.setString(1, tracking);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si el conteo es 0, el tracking es único.
                    return rs.getInt(1) == 0; 
                }
                return true;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar la unicidad del tracking: " + e.getMessage(), e);
        }
    }

     @Override
    public List<Envio> getEliminados() throws SQLException {
    List<Envio> enviosEliminados = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ELIMINADOS);
             ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            enviosEliminados.add(mapResultSetToEnvio(rs)); 
        }
    } 
    return enviosEliminados;
    }
    
    @Override
    public void restaurarEliminadoTx(long idEnvio, Connection conn) throws Exception {
    if (conn == null) throw new IllegalArgumentException("La conexión transaccional no puede ser null.");
    int filasAfectadas = 0;

    try (PreparedStatement stmt = conn.prepareStatement(RESTORE_ELIMINADO)) {
        stmt.setLong(1, idEnvio);
        filasAfectadas = stmt.executeUpdate();
    }
    if (filasAfectadas == 0) {
         throw new Exception("Error al actualizar el envio: ninguna fila afectada.");
    }
    }

}
