package com.sigmavep.modelo.entidad;

import java.time.LocalDateTime;

/**
 * Representa una novedad registrada sobre un móvil (accidente, falla, etc.).
 *
 * @author Mateo German Ruiz Díaz
 */
public class Novedad extends BaseEntity {

    private Movil movil;
    private TipoNovedad tipoNovedad;
    private String descripcion;
    private int kmNovedad;
    private LocalDateTime fechaHora;
    private Usuario usuario;

    public Novedad() { super(); }

    public Novedad(Movil movil, TipoNovedad tipoNovedad, String descripcion,
                   int kmNovedad, Usuario usuario) {
        super();
        this.movil = movil;
        this.tipoNovedad = tipoNovedad;
        this.descripcion = descripcion;
        this.kmNovedad = kmNovedad;
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
    }

    public Novedad(int id, Movil movil, TipoNovedad tipoNovedad, String descripcion,
                   int kmNovedad, LocalDateTime fechaHora, Usuario usuario) {
        super(id);
        this.movil = movil;
        this.tipoNovedad = tipoNovedad;
        this.descripcion = descripcion;
        this.kmNovedad = kmNovedad;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
    }

    public Movil getMovil() { return movil; }
    public void setMovil(Movil movil) { this.movil = movil; }

    public TipoNovedad getTipoNovedad() { return tipoNovedad; }
    public void setTipoNovedad(TipoNovedad tipoNovedad) { this.tipoNovedad = tipoNovedad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getKmNovedad() { return kmNovedad; }
    public void setKmNovedad(int kmNovedad) { this.kmNovedad = kmNovedad; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String getDescripcionCorta() {
        return "Novedad #" + id +
               " | Tipo: " + (tipoNovedad != null ? tipoNovedad.getNombre() : "N/A") +
               " | Móvil: " + (movil != null ? movil.getPatente() : "N/A") +
               " | KM: " + kmNovedad +
               " | Fecha: " + (fechaHora != null ? fechaHora.toLocalDate() : "N/A");
    }
}
