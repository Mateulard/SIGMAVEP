package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.EstadoMovil;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC para la entidad EstadoMovil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class EstadoMovilDAOImpl implements BaseDAO<EstadoMovil> {

    @Override
    public EstadoMovil insertar(EstadoMovil entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO estado_movil (nombre) VALUES (?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar estado_movil: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoMovil actualizar(EstadoMovil entidad) throws SIGMAVEPException {
        String sql = "UPDATE estado_movil SET nombre=? WHERE id_estado=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar estado_movil: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM estado_movil WHERE id_estado=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar estado_movil: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoMovil buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT * FROM estado_movil WHERE id_estado=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar estado_movil: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("EstadoMovil con ID " + id + " no encontrado.");
    }

    @Override
    public List<EstadoMovil> listarTodos() throws SIGMAVEPException {
        String sql = "SELECT * FROM estado_movil ORDER BY id_estado";
        List<EstadoMovil> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar estados_movil: " + e.getMessage(), e);
        }
        return lista;
    }

    private EstadoMovil mapear(ResultSet rs) throws SQLException {
        return new EstadoMovil(rs.getInt("id_estado"), rs.getString("nombre"));
    }
}
