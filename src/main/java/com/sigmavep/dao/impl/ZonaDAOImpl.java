package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.Zona;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO JDBC para la entidad Zona. Utiliza PreparedStatement y try-with-resources.
 *
 * @author Mateo German Ruiz Díaz
 */
public class ZonaDAOImpl implements BaseDAO<Zona> {

    @Override
    public Zona insertar(Zona entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO zona (nombre, sede) VALUES (?, ?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getSede());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar zona: " + e.getMessage(), e);
        }
    }

    @Override
    public Zona actualizar(Zona entidad) throws SIGMAVEPException {
        String sql = "UPDATE zona SET nombre=?, sede=? WHERE id_zona=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getSede());
            ps.setInt(3, entidad.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new RegistroNoEncontradoException("Zona ID " + entidad.getId() + " no encontrada.");
            return entidad;
        } catch (RegistroNoEncontradoException e) {
            throw new SIGMAVEPException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar zona: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM zona WHERE id_zona=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar zona: " + e.getMessage(), e);
        }
    }

    @Override
    public Zona buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT * FROM zona WHERE id_zona=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearZona(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar zona: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Zona con ID " + id + " no encontrada.");
    }

    @Override
    public List<Zona> listarTodos() throws SIGMAVEPException {
        String sql = "SELECT * FROM zona ORDER BY id_zona";
        // Uso complementario de arreglos y ArrayList (requisito académico)
        ArrayList<Zona> temporal = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) temporal.add(mapearZona(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar zonas: " + e.getMessage(), e);
        }
        Zona[] arreglo = temporal.toArray(new Zona[0]);
        return new ArrayList<>(Arrays.asList(arreglo));
    }

    private Zona mapearZona(ResultSet rs) throws SQLException {
        return new Zona(
            rs.getInt("id_zona"),
            rs.getString("nombre"),
            rs.getString("sede")
        );
    }
}
