package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.TipoNovedad;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC para la entidad TipoNovedad.
 *
 * @author Mateo German Ruiz Díaz
 */
public class TipoNovedadDAOImpl implements BaseDAO<TipoNovedad> {

    @Override
    public TipoNovedad insertar(TipoNovedad entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO tipo_novedad (nombre, descripcion) VALUES (?,?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar tipo_novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoNovedad actualizar(TipoNovedad entidad) throws SIGMAVEPException {
        String sql = "UPDATE tipo_novedad SET nombre=?, descripcion=? WHERE id_tipo_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getDescripcion());
            ps.setInt(3, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar tipo_novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM tipo_novedad WHERE id_tipo_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar tipo_novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoNovedad buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT * FROM tipo_novedad WHERE id_tipo_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar tipo_novedad: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("TipoNovedad con ID " + id + " no encontrado.");
    }

    @Override
    public List<TipoNovedad> listarTodos() throws SIGMAVEPException {
        String sql = "SELECT * FROM tipo_novedad ORDER BY nombre";
        List<TipoNovedad> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar tipos_novedad: " + e.getMessage(), e);
        }
        return lista;
    }

    private TipoNovedad mapear(ResultSet rs) throws SQLException {
        return new TipoNovedad(
            rs.getInt("id_tipo_novedad"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
}
