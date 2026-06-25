package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.PatenteDuplicadaException;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.*;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO JDBC para la entidad Movil. Hace JOIN con dependencia, zona, estado_movil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MovilDAOImpl implements BaseDAO<Movil> {

    private static final String SELECT_BASE =
        "SELECT m.*, d.nombre AS dep_nombre, d.id_zona, z.nombre AS zona_nombre, z.sede, " +
        "e.nombre AS estado_nombre " +
        "FROM movil m " +
        "INNER JOIN dependencia d ON m.id_dependencia = d.id_dependencia " +
        "INNER JOIN zona z ON d.id_zona = z.id_zona " +
        "INNER JOIN estado_movil e ON m.id_estado = e.id_estado ";

    @Override
    public Movil insertar(Movil entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO movil (numero_interno, patente, marca, modelo, anio, km_actual, " +
                     "id_dependencia, id_estado, fecha_alta, activo) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNumeroInterno());
            ps.setString(2, entidad.getPatente());
            ps.setString(3, entidad.getMarca());
            ps.setString(4, entidad.getModelo());
            ps.setInt(5, entidad.getAnio());
            ps.setInt(6, entidad.getKmActual());
            ps.setInt(7, entidad.getDependencia().getId());
            ps.setInt(8, entidad.getEstadoMovil().getId());
            ps.setDate(9, Date.valueOf(entidad.getFechaAlta()));
            ps.setBoolean(10, entidad.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                throw new PatenteDuplicadaException("Ya existe un móvil con esa patente o número interno.");
            throw new SIGMAVEPException("Error al insertar móvil: " + e.getMessage(), e);
        }
    }

    @Override
    public Movil actualizar(Movil entidad) throws SIGMAVEPException {
        String sql = "UPDATE movil SET numero_interno=?, patente=?, marca=?, modelo=?, anio=?, " +
                     "km_actual=?, id_dependencia=?, id_estado=?, fecha_alta=?, activo=? WHERE id_movil=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNumeroInterno());
            ps.setString(2, entidad.getPatente());
            ps.setString(3, entidad.getMarca());
            ps.setString(4, entidad.getModelo());
            ps.setInt(5, entidad.getAnio());
            ps.setInt(6, entidad.getKmActual());
            ps.setInt(7, entidad.getDependencia().getId());
            ps.setInt(8, entidad.getEstadoMovil().getId());
            ps.setDate(9, Date.valueOf(entidad.getFechaAlta()));
            ps.setBoolean(10, entidad.isActivo());
            ps.setInt(11, entidad.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new RegistroNoEncontradoException("Móvil ID " + entidad.getId() + " no encontrado.");
            return entidad;
        } catch (RegistroNoEncontradoException e) {
            throw new SIGMAVEPException(e.getMessage(), e);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                throw new PatenteDuplicadaException("Ya existe un móvil con esa patente o número interno.");
            throw new SIGMAVEPException("Error al actualizar móvil: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "UPDATE movil SET activo=FALSE WHERE id_movil=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al dar de baja móvil: " + e.getMessage(), e);
        }
    }

    @Override
    public Movil buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE m.id_movil=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearMovil(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar móvil: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Móvil con ID " + id + " no encontrado.");
    }

    @Override
    public List<Movil> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY m.numero_interno";
        // Uso complementario de arreglos y ArrayList (requisito académico)
        ArrayList<Movil> temporal = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) temporal.add(mapearMovil(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar móviles: " + e.getMessage(), e);
        }
        Movil[] arreglo = temporal.toArray(new Movil[0]);
        return new ArrayList<>(Arrays.asList(arreglo));
    }

    public List<Movil> listarActivos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE m.activo=TRUE ORDER BY m.numero_interno";
        ArrayList<Movil> temporal = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) temporal.add(mapearMovil(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar móviles activos: " + e.getMessage(), e);
        }
        Movil[] arreglo = temporal.toArray(new Movil[0]);
        return new ArrayList<>(Arrays.asList(arreglo));
    }

    public List<Movil> buscarPorZona(int idZona) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE d.id_zona=? AND m.activo=TRUE ORDER BY m.numero_interno";
        List<Movil> resultado = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idZona);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.add(mapearMovil(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar móviles por zona: " + e.getMessage(), e);
        }
        return resultado;
    }

    public List<Movil> buscarPorEstado(int idEstado) throws SIGMAVEPException {
        String sql = SELECT_BASE + "WHERE m.id_estado=? AND m.activo=TRUE ORDER BY m.numero_interno";
        List<Movil> resultado = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.add(mapearMovil(rs));
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar móviles por estado: " + e.getMessage(), e);
        }
        return resultado;
    }

    public Movil buscarPorPatente(String patente) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE m.patente=? AND m.activo=TRUE";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearMovil(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar por patente: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Móvil con patente '" + patente + "' no encontrado.");
    }

    public boolean existePatente(String patente) throws SIGMAVEPException {
        String sql = "SELECT COUNT(*) FROM movil WHERE patente=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al verificar patente: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean existeNumeroInterno(String nro) throws SIGMAVEPException {
        String sql = "SELECT COUNT(*) FROM movil WHERE numero_interno=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al verificar número interno: " + e.getMessage(), e);
        }
        return false;
    }

    /** Actualiza solo el kilometraje de un móvil. */
    public void actualizarKm(int idMovil, int kmNuevo) throws SIGMAVEPException {
        String sql = "UPDATE movil SET km_actual=? WHERE id_movil=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kmNuevo);
            ps.setInt(2, idMovil);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar km: " + e.getMessage(), e);
        }
    }

    private Movil mapearMovil(ResultSet rs) throws SQLException {
        Zona zona = new Zona(rs.getInt("id_zona"), rs.getString("zona_nombre"), rs.getString("sede"));
        Dependencia dep = new Dependencia(rs.getInt("id_dependencia"), rs.getString("dep_nombre"), zona);
        EstadoMovil estado = new EstadoMovil(rs.getInt("id_estado"), rs.getString("estado_nombre"));
        return new Movil(
            rs.getInt("id_movil"), rs.getString("numero_interno"), rs.getString("patente"),
            rs.getString("marca"), rs.getString("modelo"), rs.getInt("anio"),
            rs.getInt("km_actual"), dep, estado,
            rs.getDate("fecha_alta").toLocalDate(), rs.getBoolean("activo")
        );
    }
}
