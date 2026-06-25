package com.sigmavep.modelo.entidad;

import java.time.LocalDateTime;

/**
 * Representa un usuario del sistema con credenciales de acceso y rol asignado.
 * La contraseña se almacena como hash SHA-256 (nunca en texto plano).
 *
 * @author Mateo German Ruiz Díaz
 */
public class Usuario extends BaseEntity {

    private String nombre;
    private String apellido;
    private String username;
    private String passwordHash;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public Usuario() { super(); }

    public Usuario(String nombre, String apellido, String username, String passwordHash, Rol rol) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Usuario(int id, String nombre, String apellido, String username,
                   String passwordHash, Rol rol, boolean activo, LocalDateTime fechaCreacion) {
        super(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreCompleto() { return nombre + " " + apellido; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String getDescripcionCorta() {
        return username + " | " + nombre + " " + apellido +
               " | Rol: " + (rol != null ? rol.getNombre() : "N/A") +
               " | " + (activo ? "Activo" : "Inactivo");
    }
}
