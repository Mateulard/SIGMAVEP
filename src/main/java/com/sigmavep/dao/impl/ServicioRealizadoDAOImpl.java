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
 * DAO JDBC para la entidad ServicioRealizado.
 *
 * @author Mateo German Ruiz Díaz
 */
public class ServicioRealizadoDAOImpl implements BaseDAO<ServicioRealizado> {

    private static final String SELECT_BASE =
        "SELECT sr.*, m.numero_interno, m.patente, m.marca, m.modelo, m.anio, m.km_actual, m.activo, " +
        "m.id_dependencia, m.id_estado, m.fecha_alta AS movil_fecha_alta, " +
        "tm.nombre AS tipo_nombre, tm.limite_km, tm.descripcion AS tipo_desc, " +
        "u.nombre AS u_nombre, u.apellido AS u_apellido, u.username " +
        "FROM servicio_realizado sr " +
        "INNER JOIN movil m ON sr.id_movil = m.id_movil " +
        "INNER JOIN tipo_mantenimiento tm ON sr.id_tipo_mantenimiento = tm.id_tipo_mantenimiento " +
        "INNER JOIN usuario u ON sr.id_usuario = u.id_usuario ";

    @Override
    public ServicioRealizado insertar(ServicioRealizado entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO servicio_realizado (id_movil, id_tipo_mantenimiento, km_servicio, " +
                     "fecha_servicio, observaciones, id_usuario) VALUES (?,?,?,NOW(),?,?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entidad.getMovil().getId());
            ps.setInt(2, entidad.getTipoMantenimiento().getId());
            ps.setInt(3, entidad.getKmServicio());
            ps.setString(4, entidad.getObservaciones());
            ps.setInt(5, entidad.getUsuario().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public ServicioRealizado actualizar(ServicioRealizado entidad) throws SIGMAVEPException {
        String sql = "UPDATE servicio_realizado SET observaciones=? WHERE id_servicio=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getObservaciones());
            ps.setInt(2, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM servicio_realizado WHERE id_servicio=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public ServicioRealizado buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE sr.id_servicio=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar servicio: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Servicio con ID " + id + " no encontrado.");
    }

    @Override
    public List<ServicioRealizado> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY sr.fecha_servicio DESC";
        List<ServicioRealizado> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar servicios: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<ServicioRealizado> listarPorMovil(int idMovil) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE sr.id_movil=? ORDER BY sr.fecha_servicio DESC";
        List<ServicioRealizado> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMovil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar servicios por móvil: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Busca el último servicio de un tipo de mantenimiento para un móvil. */
    public ServicioRealizado buscarUltimoServicio(int idMovil, int idTipoMantenimiento) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE sr.id_movil=? AND sr.id_tipo_mantenimiento=? ORDER BY sr.fecha_servicio DESC LIMIT 1";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMovil);
            ps.setInt(2, idTipoMantenimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar último servicio: " + e.getMessage(), e);
        }
        return null;
    }

    private ServicioRealizado mapear(ResultSet rs) throws SQLException {
        Movil movil = new Movil();
        movil.setId(rs.getInt("id_movil"));
        movil.setNumeroInterno(rs.getString("numero_interno"));
        movil.setPatente(rs.getString("patente"));
        movil.setMarca(rs.getString("marca"));
        movil.setModelo(rs.getString("modelo"));
        movil.setAnio(rs.getInt("anio"));
        movil.setKmActual(rs.getInt("km_actual"));

        TipoMantenimiento tipo = new TipoMantenimiento(
            rs.getInt("id_tipo_mantenimiento"),
            rs.getString("tipo_nombre"),
            rs.getInt("limite_km"),
            rs.getString("tipo_desc")
        );

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("u_nombre"));
        usuario.setApellido(rs.getString("u_apellido"));
        usuario.setUsername(rs.getString("username"));

        Timestamp ts = rs.getTimestamp("fecha_servicio");
        return new ServicioRealizado(
            rs.getInt("id_servicio"), movil, tipo,
            rs.getInt("km_servicio"),
            ts != null ? ts.toLocalDateTime() : null,
            rs.getString("observaciones"), usuario
        );
    }
}
