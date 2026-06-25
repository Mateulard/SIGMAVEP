package com.sigmavep.modelo.entidad;

/**
 * Clase abstracta base para todas las entidades del sistema.
 * Provee un identificador único y un método abstracto de descripción.
 *
 * Aplica abstracción y herencia: todas las entidades extienden esta clase
 * y están obligadas a implementar {@link #getDescripcionCorta()}.
 *
 * @author Mateo German Ruiz Díaz
 */
public abstract class BaseEntity {

    /**
     * Identificador único de la entidad. Equivale a la PK AUTO_INCREMENT de la tabla.
     */
    protected int id;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método abstracto que retorna una descripción corta de la entidad.
     * Cada subclase lo implementa de manera diferente (polimorfismo).
     *
     * @return Cadena descriptiva de la entidad.
     */
    public abstract String getDescripcionCorta();

    @Override
    public String toString() {
        return getDescripcionCorta();
    }
}
