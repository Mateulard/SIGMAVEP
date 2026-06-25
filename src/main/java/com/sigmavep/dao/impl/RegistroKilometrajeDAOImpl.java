package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.*;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC para la entidad RegistroKilometraje.
 *
 * @author Mateo German Ruiz Díaz
 */
public class RegistroKilometrajeDAOImpl implements BaseDAO<RegistroKilometraje> {

    @Override
    public RegistroKilometraje insertar(RegistroKilometraje entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) " +
                     "VALUES (?,?,?,NOW(),?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entidad.getMovil().getId());
            ps.setInt(2, entidad.getKmAnterior());
            ps.setInt(3, entidad.getKmNuevo());
            ps.setInt(4, entidad.getUsuario().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar registro de km: " + e.getMessage(), e);
        }
    }

    @Override
    public RegistroKilometraje actualizar(RegistroKilometraje entidad) throws SIGMAVEPException {
        throw new SIGMAVEPException("Los registros de kilometraje no se pueden modificar.");
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM registro_kilometraje WHERE id_registro=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar registro de km: " + e.getMessage(), e);
        }
    }

    @Override
    public RegistroKilometraje buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = "SELECT rk.*, m.numero_interno, m.patente, m.marca, m.modelo, m.anio, m.km_actual, " +
                     "m.activo, m.id_dependencia, m.id_estado, m.fecha_alta, " +
                     "u.nombre AS u_nombre, u.apellido AS u_apellido, u.username " +
                     "FROM registro_kilometraje rk " +
                     "INNER JOIN movil m ON rk.id_movil = m.id_movil " +
                     "INNER JOIN usuario u ON rk.id_usuario = u.id_usuario " +
                     "WHERE rk.id_registro=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar registro de km: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("RegistroKM con ID " + id + " no encontrado.");
    }

    @Override
    public List<RegistroKilometraje> listarTodos() throws SIGMAVEPException {
        return listarPorMovil(0); // todos
    }

    public List<RegistroKilometraje> listarPorMovil(int idMovil) throws SIGMAVEPException {
        String sql = "SELECT rk.*, m.numero_interno, m.patente, m.marca, m.modelo, m.anio, m.km_actual, " +
                     "m.activo, m.id_dependencia, m.id_estado, m.fecha_alta, " +
                     "u.nombre AS u_nombre, u.apellido AS u_apellido, u.username " +
                     "FROM registro_kilometraje rk " +
                     "INNER JOIN movil m ON rk.id_movil = m.id_movil " +
                     "INNER JOIN usuario u ON rk.id_usuario = u.id_usuario " +
                     (idMovil > 0 ? "WHERE rk.id_movil=? " : "") +
                     "ORDER BY rk.fecha_hora DESC";
        List<RegistroKilometraje> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idMovil > 0) ps.setInt(1, idMovil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar registros de km: " + e.getMessage(), e);
        }
        return lista;
    }

    private RegistroKilometraje mapear(ResultSet rs) throws SQLException {
        Movil movil = new Movil();
        movil.setId(rs.getInt("id_movil"));
        movil.setNumeroInterno(rs.getString("numero_interno"));
        movil.setPatente(rs.getString("patente"));
        movil.setMarca(rs.getString("marca"));
        movil.setModelo(rs.getString("modelo"));
        movil.setAnio(rs.getInt("anio"));
        movil.setKmActual(rs.getInt("km_actual"));

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("u_nombre"));
        usuario.setApellido(rs.getString("u_apellido"));
        usuario.setUsername(rs.getString("username"));

        Timestamp ts = rs.getTimestamp("fecha_hora");
        return new RegistroKilometraje(
            rs.getInt("id_registro"), movil,
            rs.getInt("km_anterior"), rs.getInt("km_nuevo"),
            ts != null ? ts.toLocalDateTime() : null, usuario
        );
    }
}
