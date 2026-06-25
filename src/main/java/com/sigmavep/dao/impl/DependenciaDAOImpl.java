package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.Dependencia;
import com.sigmavep.modelo.entidad.Zona;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC para la entidad Dependencia. Hace JOIN con zona.
 *
 * @author Mateo German Ruiz Díaz
 */
public class DependenciaDAOImpl implements BaseDAO<Dependencia> {

    private static final String SELECT_BASE =
        "SELECT d.id_dependencia, d.nombre, d.id_zona, z.nombre AS zona_nombre, z.sede " +
        "FROM dependencia d INNER JOIN zona z ON d.id_zona = z.id_zona ";

    @Override
    public Dependencia insertar(Dependencia entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO dependencia (nombre, id_zona) VALUES (?,?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getZona().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar dependencia: " + e.getMessage(), e);
        }
    }

    @Override
    public Dependencia actualizar(Dependencia entidad) throws SIGMAVEPException {
        String sql = "UPDATE dependencia SET nombre=?, id_zona=? WHERE id_dependencia=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getZona().getId());
            ps.setInt(3, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar dependencia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM dependencia WHERE id_dependencia=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar dependencia: " + e.getMessage(), e);
        }
    }

    @Override
    public Dependencia buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE d.id_dependencia=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar dependencia: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Dependencia con ID " + id + " no encontrada.");
    }

    @Override
    public List<Dependencia> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY d.nombre";
        List<Dependencia> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar dependencias: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Dependencia> listarPorZona(int idZona) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE d.id_zona=? ORDER BY d.nombre";
        List<Dependencia> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idZona);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar dependencias por zona: " + e.getMessage(), e);
        }
        return lista;
    }

    private Dependencia mapear(ResultSet rs) throws SQLException {
        Zona zona = new Zona(rs.getInt("id_zona"), rs.getString("zona_nombre"), rs.getString("sede"));
        return new Dependencia(rs.getInt("id_dependencia"), rs.getString("nombre"), zona);
    }
}
