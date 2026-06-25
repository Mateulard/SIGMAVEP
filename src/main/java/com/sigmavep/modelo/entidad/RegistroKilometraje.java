package com.sigmavep.modelo.entidad;

import java.time.LocalDateTime;

/**
 * Representa un registro de actualización de kilometraje de un móvil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class RegistroKilometraje extends BaseEntity {

    private Movil movil;
    private int kmAnterior;
    private int kmNuevo;
    private LocalDateTime fechaHora;
    private Usuario usuario;

    public RegistroKilometraje() { super(); }

    public RegistroKilometraje(Movil movil, int kmAnterior, int kmNuevo, Usuario usuario) {
        super();
        this.movil = movil;
        this.kmAnterior = kmAnterior;
        this.kmNuevo = kmNuevo;
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
    }

    public RegistroKilometraje(int id, Movil movil, int kmAnterior, int kmNuevo,
                               LocalDateTime fechaHora, Usuario usuario) {
        super(id);
        this.movil = movil;
        this.kmAnterior = kmAnterior;
        this.kmNuevo = kmNuevo;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
    }

    public Movil getMovil() { return movil; }
    public void setMovil(Movil movil) { this.movil = movil; }

    public int getKmAnterior() { return kmAnterior; }
    public void setKmAnterior(int kmAnterior) { this.kmAnterior = kmAnterior; }

    public int getKmNuevo() { return kmNuevo; }
    public void setKmNuevo(int kmNuevo) { this.kmNuevo = kmNuevo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String getDescripcionCorta() {
        return "RegistroKM #" + id +
               " | Móvil: " + (movil != null ? movil.getPatente() : "N/A") +
               " | " + kmAnterior + " → " + kmNuevo + " km" +
               " | Fecha: " + (fechaHora != null ? fechaHora.toLocalDate() : "N/A");
    }
}
