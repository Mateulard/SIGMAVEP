package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.TipoMantenimiento;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO JDBC para la entidad TipoMantenimiento.
 *
 * @author Mateo German Ruiz Díaz
 */
public class TipoMantenimientoDAOImpl implements BaseDAO<TipoMantenimiento> {

    @Override
    public TipoMantenimiento insertar(TipoMantenimiento entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO tipo_mantenimiento (nombre, limite_km, descripcion) VALUES (?,?,?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getLimiteKm());
            ps.setString(3, entidad.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar tipo_mantenimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoMantenimiento actualizar(TipoMantenimiento entidad) throws SIGMAVEPException {
        String sql = "UPDATE tipo_mantenimiento SET nombre=?, limite_km=?, descripcion=? WHERE id_tipo_mantenimiento=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getLimiteKm());
            ps.setString(3, entidad.getDescripcion());
            ps.setInt(4, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar tipo_mantenimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM tipo_mantenimiento WHERE id_tipo_mantenimiento=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar tipo_mantenimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoMantenimiento buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT * FROM tipo_mantenimiento WHERE id_tipo_mantenimiento=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar tipo_mantenimiento: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("TipoMantenimiento con ID " + id + " no encontrado.");
    }

    @Override
    public List<TipoMantenimiento> listarTodos() throws SIGMAVEPException {
        String sql = "SELECT * FROM tipo_mantenimiento ORDER BY nombre";
        // Uso complementario de arreglos y ArrayList (requisito académico)
        ArrayList<TipoMantenimiento> temporal = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) temporal.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar tipos de mantenimiento: " + e.getMessage(), e);
        }
        TipoMantenimiento[] arreglo = temporal.toArray(new TipoMantenimiento[0]);
        return new ArrayList<>(Arrays.asList(arreglo));
    }

    private TipoMantenimiento mapear(ResultSet rs) throws SQLException {
        return new TipoMantenimiento(
            rs.getInt("id_tipo_mantenimiento"),
            rs.getString("nombre"),
            rs.getInt("limite_km"),
            rs.getString("descripcion")
        );
    }
}
