package com.sigmavep.dao.impl;

import com.sigmavep.dao.BaseDAO;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.Rol;
import com.sigmavep.modelo.entidad.Usuario;
import com.sigmavep.util.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO JDBC para la entidad Usuario. Hace JOIN con rol.
 *
 * @author Mateo German Ruiz Díaz
 */
public class UsuarioDAOImpl implements BaseDAO<Usuario> {

    private static final String SELECT_BASE =
        "SELECT u.id_usuario, u.nombre, u.apellido, u.username, u.password, u.activo, u.fecha_creacion, " +
        "u.id_rol, r.nombre AS rol_nombre, r.descripcion AS rol_desc " +
        "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol ";

    @Override
    public Usuario insertar(Usuario entidad) throws SIGMAVEPException {
        String sql = "INSERT INTO usuario (nombre, apellido, username, password, id_rol, activo, fecha_creacion) " +
                     "VALUES (?,?,?,?,?,?,NOW())";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getApellido());
            ps.setString(3, entidad.getUsername());
            ps.setString(4, entidad.getPasswordHash());
            ps.setInt(5, entidad.getRol().getId());
            ps.setBoolean(6, entidad.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entidad.setId(rs.getInt(1));
            }
            return entidad;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                throw new SIGMAVEPException("Ya existe un usuario con ese username.", e);
            throw new SIGMAVEPException("Error al insertar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario actualizar(Usuario entidad) throws SIGMAVEPException {
        String sql = "UPDATE usuario SET nombre=?, apellido=?, username=?, id_rol=?, activo=? WHERE id_usuario=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getApellido());
            ps.setString(3, entidad.getUsername());
            ps.setInt(4, entidad.getRol().getId());
            ps.setBoolean(5, entidad.isActivo());
            ps.setInt(6, entidad.getId());
            ps.executeUpdate();
            return entidad;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    public void actualizarPassword(int idUsuario, String nuevaHashPassword) throws SIGMAVEPException {
        String sql = "UPDATE usuario SET password=? WHERE id_usuario=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaHashPassword);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al actualizar contraseña: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws SIGMAVEPException {
        String sql = "UPDATE usuario SET activo=FALSE WHERE id_usuario=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al desactivar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE u.id_usuario=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar usuario: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Usuario con ID " + id + " no encontrado.");
    }

    public Usuario buscarPorUsername(String username) throws SIGMAVEPException, RegistroNoEncontradoException {
        String sql = SELECT_BASE + "WHERE u.username=?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al buscar usuario por username: " + e.getMessage(), e);
        }
        throw new RegistroNoEncontradoException("Usuario '" + username + "' no encontrado.");
    }

    @Override
    public List<Usuario> listarTodos() throws SIGMAVEPException {
        String sql = SELECT_BASE + "ORDER BY u.apellido, u.nombre";
        // Uso complementario de arreglos y ArrayList (requisito académico)
        ArrayList<Usuario> temporal = new ArrayList<>();
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) temporal.add(mapear(rs));
        } catch (SQLException e) {
            throw new SIGMAVEPException("Error al listar usuarios: " + e.getMessage(), e);
        }
        Usuario[] arreglo = temporal.toArray(new Usuario[0]);
        return new ArrayList<>(Arrays.asList(arreglo));
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol(rs.getInt("id_rol"), rs.getString("rol_nombre"), rs.getString("rol_desc"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("username"),
            rs.getString("password"),
            rol,
            rs.getBoolean("activo"),
            ts != null ? ts.toLocalDateTime() : java.time.LocalDateTime.now()
        );
    }
}
