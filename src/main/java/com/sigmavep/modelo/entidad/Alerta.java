package com.sigmavep.modelo.entidad;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Representa una alerta de mantenimiento preventivo generada para un móvil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class Alerta extends BaseEntity {

    private Movil movil;
    private TipoMantenimiento tipoMantenimiento;
    private EstadoAlerta estadoAlerta;
    private int kmDisparo;
    private LocalDateTime fechaGeneracion;
    private String observaciones;
    private Usuario usuario;
    private LocalDateTime fechaProcesamiento;

    /** Comparator para ordenar alertas por fecha de generación (ascendente). */
    public static final Comparator<Alerta> POR_FECHA = Comparator.comparing(Alerta::getFechaGeneracion);

    public Alerta() { super(); }

    public Alerta(Movil movil, TipoMantenimiento tipoMantenimiento,
            EstadoAlerta estadoAlerta, int kmDisparo) {
        super();
        this.movil = movil;
        this.tipoMantenimiento = tipoMantenimiento;
        this.estadoAlerta = estadoAlerta;
        this.kmDisparo = kmDisparo;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public Alerta(int id, Movil movil, TipoMantenimiento tipoMantenimiento,
            EstadoAlerta estadoAlerta, int kmDisparo, LocalDateTime fechaGeneracion,
            String observaciones, Usuario usuario, LocalDateTime fechaProcesamiento) {
        super(id);
        this.movil = movil;
        this.tipoMantenimiento = tipoMantenimiento;
        this.estadoAlerta = estadoAlerta;
        this.kmDisparo = kmDisparo;
        this.fechaGeneracion = fechaGeneracion;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public Movil getMovil() { return movil; }
    public void setMovil(Movil movil) { this.movil = movil; }

    public TipoMantenimiento getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(TipoMantenimiento t) { this.tipoMantenimiento = t; }

    public EstadoAlerta getEstadoAlerta() { return estadoAlerta; }
    public void setEstadoAlerta(EstadoAlerta estadoAlerta) { this.estadoAlerta = estadoAlerta; }

    public int getKmDisparo() { return kmDisparo; }
    public void setKmDisparo(int kmDisparo) { this.kmDisparo = kmDisparo; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaProcesamiento() { return fechaProcesamiento; }
    public void setFechaProcesamiento(LocalDateTime fp) { this.fechaProcesamiento = fp; }

    @Override
    public String getDescripcionCorta() {
        return "Alerta #" + id +
                " | " + (tipoMantenimiento != null ? tipoMantenimiento.getNombre() : "N/A") +
                " | Móvil: " + (movil != null ? movil.getPatente() : "N/A") +
                " | Estado: " + (estadoAlerta != null ? estadoAlerta.getNombre() : "N/A") +
                " | KM disparo: " + kmDisparo;
    }
}
