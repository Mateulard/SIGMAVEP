package com.sigmavep.modelo.entidad;

import java.time.LocalDate;

/**
 * Vehículo (móvil) policial del sistema SIGMAVEP.
 * Implementa {@link Comparable}{@code <Movil>} para ordenamiento natural por patente.
 *
 * @author Mateo German Ruiz Díaz
 */
public class Movil extends BaseEntity implements Comparable<Movil> {

    private String numeroInterno;
    private String patente;
    private String marca;
    private String modelo;
    private int anio;
    private int kmActual;
    private Dependencia dependencia;
    private EstadoMovil estadoMovil;
    private LocalDate fechaAlta;
    private boolean activo;

    public Movil() {
        super();
        this.activo = true;
        this.kmActual = 0;
        this.fechaAlta = LocalDate.now();
    }

    public Movil(String numeroInterno, String patente, String marca, String modelo,
            int anio, Dependencia dependencia, EstadoMovil estadoMovil) {
        super();
        this.numeroInterno = numeroInterno;
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.dependencia = dependencia;
        this.estadoMovil = estadoMovil;
        this.kmActual = 0;
        this.activo = true;
        this.fechaAlta = LocalDate.now();
    }

    public Movil(int id, String numeroInterno, String patente, String marca, String modelo,
            int anio, int kmActual, Dependencia dependencia,
            EstadoMovil estadoMovil, LocalDate fechaAlta, boolean activo) {
        super(id);
        this.numeroInterno = numeroInterno;
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kmActual = kmActual;
        this.dependencia = dependencia;
        this.estadoMovil = estadoMovil;
        this.fechaAlta = fechaAlta;
        this.activo = activo;
    }

    public String getNumeroInterno() { return numeroInterno; }
    public void setNumeroInterno(String numeroInterno) { this.numeroInterno = numeroInterno; }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getKmActual() { return kmActual; }

    public void setKmActual(int kmActual) {
        if (kmActual < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo. Valor: " + kmActual);
        }
        this.kmActual = kmActual;
    }

    public Dependencia getDependencia() { return dependencia; }
    public void setDependencia(Dependencia dependencia) { this.dependencia = dependencia; }

    public EstadoMovil getEstadoMovil() { return estadoMovil; }
    public void setEstadoMovil(EstadoMovil estadoMovil) { this.estadoMovil = estadoMovil; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public int compareTo(Movil otro) {
        return this.patente.compareToIgnoreCase(otro.patente);
    }

    @Override
    public String getDescripcionCorta() {
        return "[" + numeroInterno + "] " + marca + " " + modelo + " " + anio +
                " | Pat: " + patente +
                " | KM: " + kmActual +
                " | " + (estadoMovil != null ? estadoMovil.getNombre() : "Sin estado") +
                " | " + (dependencia != null ? dependencia.getNombre() : "Sin dependencia");
    }
}
