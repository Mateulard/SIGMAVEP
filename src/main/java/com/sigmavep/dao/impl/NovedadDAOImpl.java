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
 * DAO JDBC para la entidad Novedad.
 *
 * @author Mateo German Ruiz Díaz
 */
public class NovedadDAOImpl implements BaseDAO<Novedad> {

    private static final String SELECT_BASE =
        "SELECT n.*, m.numero_interno, m.patente, m.marca, m.modelo, m.anio, m.km_actual, " +
        "m.activo, m.id_dependencia, m.id_estado, m.fecha_alta AS movil_fecha_alta, " +
        "tn.nombre AS tipo_nombre, tn.descripcion AS tipo_desc, " +
        "u.nombre AS u_nombre, u.apellido AS u_apellido, u.username " +
        "FROM novedad n " +
        "INNER JOIN movil m ON n.id_movil = m.id_movil " +
        "INNER JOIN tipo_novedad tn ON n.id_tipo_novedad = tn.id_tipo_novedad " +
        "INNER JOIN usuario u ON n.id_usuario = u.id_usuario ";

    @Override
    public Novedad insertar(Novedad entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO novedad (id_movil, id_tipo_novedad, descripcion, km_novedad, fecha_hora, id_usuario) " +
                     "VALUES (?,?,?,?,NOW(),?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entidad.getMovil().getId());
            ps.setInt(2, entidad.getTipoNovedad().getId());
            ps.setString(3, entidad.getDescripcion());
            ps.setInt(4, entidad.getKmNovedad());
            ps.setInt(5, entidad.getUsuario().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al insertar novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public Novedad actualizar(Novedad entidad) throws SIGMAVEPException {
        String sql = "UPDATE novedad SET descripcion=?, km_novedad=? WHERE id_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getDescripcion());
            ps.setInt(2, entidad.getKmNovedad());
            ps.setInt(3, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "DELETE FROM novedad WHERE id_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al eliminar novedad: " + e.getMessage(), e);
        }
    }

    @Override
    public Novedad buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE n.id_novedad=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar novedad: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Novedad con ID " + id + " no encontrada.");
    }

    @Override
    public List<Novedad> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY n.fecha_hora DESC";
        List<Novedad> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar novedades: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Novedad> listarPorMovil(int idMovil) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE n.id_movil=? ORDER BY n.fecha_hora DESC";
        List<Novedad> lista = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMovil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar novedades por móvil: " + e.getMessage(), e);
        }
        return lista;
    }

    private Novedad mapear(ResultSet rs) throws SQLException {
        Movil movil = new Movil();
        movil.setId(rs.getInt("id_movil"));
        movil.setNumeroInterno(rs.getString("numero_interno"));
        movil.setPatente(rs.getString("patente"));
        movil.setMarca(rs.getString("marca"));
        movil.setModelo(rs.getString("modelo"));
        movil.setAnio(rs.getInt("anio"));
        movil.setKmActual(rs.getInt("km_actual"));

        TipoNovedad tipo = new TipoNovedad(
            rs.getInt("id_tipo_novedad"),
            rs.getString("tipo_nombre"),
            rs.getString("tipo_desc")
        );

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("u_nombre"));
        usuario.setApellido(rs.getString("u_apellido"));
        usuario.setUsername(rs.getString("username"));

        Timestamp ts = rs.getTimestamp("fecha_hora");
        return new Novedad(
            rs.getInt("id_novedad"), movil, tipo,
            rs.getString("descripcion"),
            rs.getInt("km_novedad"),
            ts != null ? ts.toLocalDateTime() : null,
            usuario
        );
    }
}
