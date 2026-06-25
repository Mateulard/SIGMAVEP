package com.sigmavep.modelo.entidad;

import java.time.LocalDateTime;

/**
 * Representa un servicio de mantenimiento realizado sobre un móvil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class ServicioRealizado extends BaseEntity {

    private Movil movil;
    private TipoMantenimiento tipoMantenimiento;
    private int kmServicio;
    private LocalDateTime fechaServicio;
    private String observaciones;
    private Usuario usuario;

    public ServicioRealizado() { super(); }

    public ServicioRealizado(Movil movil, TipoMantenimiento tipoMantenimiento,
                             int kmServicio, String observaciones, Usuario usuario) {
        super();
        this.movil = movil;
        this.tipoMantenimiento = tipoMantenimiento;
        this.kmServicio = kmServicio;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.fechaServicio = LocalDateTime.now();
    }

    public ServicioRealizado(int id, Movil movil, TipoMantenimiento tipoMantenimiento,
                             int kmServicio, LocalDateTime fechaServicio,
                             String observaciones, Usuario usuario) {
        super(id);
        this.movil = movil;
        this.tipoMantenimiento = tipoMantenimiento;
        this.kmServicio = kmServicio;
        this.fechaServicio = fechaServicio;
        this.observaciones = observaciones;
        this.usuario = usuario;
    }

    public Movil getMovil() { return movil; }
    public void setMovil(Movil movil) { this.movil = movil; }

    public TipoMantenimiento getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(TipoMantenimiento tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public int getKmServicio() { return kmServicio; }
    public void setKmServicio(int kmServicio) { this.kmServicio = kmServicio; }

    public LocalDateTime getFechaServicio() { return fechaServicio; }
    public void setFechaServicio(LocalDateTime fechaServicio) { this.fechaServicio = fechaServicio; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String getDescripcionCorta() {
        return "Servicio #" + id + ": " + (tipoMantenimiento != null ? tipoMantenimiento.getNombre() : "N/A") +
               " | Móvil: " + (movil != null ? movil.getPatente() : "N/A") +
               " | KM: " + kmServicio +
               " | Fecha: " + (fechaServicio != null ? fechaServicio.toLocalDate() : "N/A");
    }
}
