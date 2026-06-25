package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.EstadoAlerta;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC para la entidad EstadoAlerta.
 *
 * @author Mateo German Ruiz Díaz
 */
public class EstadoAlertaDAOImpl implements BaseDAO<EstadoAlerta> {

    @Override
    public EstadoAlerta insertar(EstadoAlerta entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO estado_alerta (nombre) VALUES (?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar estado_alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoAlerta actualizar(EstadoAlerta entidad) throws SIGMAVEPException {
        String sql = "UPDATE estado_alerta SET nombre=? WHERE id_estado_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar estado_alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM estado_alerta WHERE id_estado_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar estado_alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoAlerta buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT * FROM estado_alerta WHERE id_estado_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar estado_alerta: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("EstadoAlerta con ID " + id + " no encontrado.");
    }

    @Override
    public List<EstadoAlerta> listarTodos() throws SIGMAVEPException {
        String sql = "SELECT * FROM estado_alerta ORDER BY id_estado_alerta";
        List<EstadoAlerta> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar estados_alerta: " + e.getMessage(), e);
        }
        return lista;
    }

    private EstadoAlerta mapear(ResultSet rs) throws SQLException {
        return new EstadoAlerta(rs.getInt("id_estado_alerta"), rs.getString("nombre"));
    }
}
