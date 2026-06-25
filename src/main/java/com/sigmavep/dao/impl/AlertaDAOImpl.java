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
 * DAO JDBC para la entidad Alerta.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AlertaDAOImpl implements BaseDAO<Alerta> {

    private static final String SELECT_BASE =
        "SELECT a.*, m.numero_interno, m.patente, m.marca, m.modelo, m.anio, m.km_actual, " +
        "m.activo, m.id_dependencia, m.id_estado, m.fecha_alta AS movil_fecha_alta, " +
        "tm.nombre AS tipo_nombre, tm.limite_km, tm.descripcion AS tipo_desc, " +
        "ea.nombre AS estado_nombre, " +
        "u.nombre AS u_nombre, u.apellido AS u_apellido, u.username " +
        "FROM alerta a " +
        "INNER JOIN movil m ON a.id_movil = m.id_movil " +
        "INNER JOIN tipo_mantenimiento tm ON a.id_tipo_mantenimiento = tm.id_tipo_mantenimiento " +
        "INNER JOIN estado_alerta ea ON a.id_estado_alerta = ea.id_estado_alerta " +
        "LEFT JOIN usuario u ON a.id_usuario = u.id_usuario ";

    @Override
    public Alerta insertar(Alerta entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO alerta (id_movil, id_tipo_mantenimiento, id_estado_alerta, km_disparo, " +
                     "fecha_generacion, observaciones) VALUES (?,?,?,?,NOW(),?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entidad.getMovil().getId());
            ps.setInt(2, entidad.getTipoMantenimiento().getId());
            ps.setInt(3, entidad.getEstadoAlerta().getId());
            ps.setInt(4, entidad.getKmDisparo());
            ps.setString(5, entidad.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public Alerta actualizar(Alerta entidad) throws SIGMAVEPException {
        String sql = "UPDATE alerta SET id_estado_alerta=?, observaciones=?, id_usuario=?, " +
                     "fecha_procesamiento=NOW() WHERE id_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entidad.getEstadoAlerta().getId());
            ps.setString(2, entidad.getObservaciones());
            if (entidad.getUsuario() != null) {
                ps.setInt(3, entidad.getUsuario().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM alerta WHERE id_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar alerta: " + e.getMessage(), e);
        }
    }

    @Override
    public Alerta buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE a.id_alerta=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar alerta: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Alerta con ID " + id + " no encontrada.");
    }

    @Override
    public List<Alerta> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY a.fecha_generacion DESC";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar alertas: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Alerta> listarPendientes() throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE a.id_estado_alerta=1 ORDER BY a.fecha_generacion DESC";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar alertas pendientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Alerta> listarPorEstado(int idEstado) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE a.id_estado_alerta=? ORDER BY a.fecha_generacion DESC";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar alertas por estado: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Alerta> listarPorMovil(int idMovil) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE a.id_movil=? ORDER BY a.fecha_generacion DESC";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMovil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar alertas por móvil: " + e.getMessage(), e);
        }
        return lista;
    }

    public int contarPendientes() throws SIGMAVEPException {
        String sql = "SELECT COUNT(*) FROM alerta WHERE id_estado_alerta=1";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al contar alertas: " + e.getMessage(), e);
        }
        return 0;
    }

    /** Verifica si ya existe una alerta pendiente del mismo tipo para el móvil. */
    public boolean existeAlertaPendiente(int idMovil, int idTipoMantenimiento) throws SIGMAVEPException {
        String sql = "SELECT COUNT(*) FROM alerta WHERE id_movil=? AND id_tipo_mantenimiento=? AND id_estado_alerta=1";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMovil);
            ps.setInt(2, idTipoMantenimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al verificar alerta: " + e.getMessage(), e);
        }
        return false;
    }

    private Alerta mapear(ResultSet rs) throws SQLException {
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

        EstadoAlerta estado = new EstadoAlerta(rs.getInt("id_estado_alerta"), rs.getString("estado_nombre"));

        Usuario usuario = null;
        int idUsuario = rs.getInt("id_usuario");
        if (!rs.wasNull()) {
            usuario = new Usuario();
            usuario.setId(idUsuario);
            usuario.setNombre(rs.getString("u_nombre"));
            usuario.setApellido(rs.getString("u_apellido"));
            usuario.setUsername(rs.getString("username"));
        }

        Timestamp tsGen = rs.getTimestamp("fecha_generacion");
        Timestamp tsProc = rs.getTimestamp("fecha_procesamiento");

        return new Alerta(
            rs.getInt("id_alerta"), movil, tipo, estado,
            rs.getInt("km_disparo"),
            tsGen != null ? tsGen.toLocalDateTime() : java.time.LocalDateTime.now(),
            rs.getString("observaciones"),
            usuario,
            tsProc != null ? tsProc.toLocalDateTime() : null
        );
    }
}
