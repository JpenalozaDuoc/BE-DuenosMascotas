package microvetcare.microvetcare.raza.entity;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.mascota.entity.Mascota;

/*
 * Descripción de la tabla raza:
 * Nombre     ¿Nulo?   Tipo               
---------- -------- ------------------ 
ID_RAZA    NOT NULL NUMBER(19)         
ESTADO     NOT NULL VARCHAR2(1 CHAR)   
NOMBRE     NOT NULL VARCHAR2(100 CHAR) 
ID_ESPECIE NOT NULL NUMBER(19)         

 * 
 * 
 */

@Entity
@Table(name = "raza") 
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raza")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true) // El nombre de la raza debería ser único
    private String nombre;

    @Column(name = "estado", nullable = false, length = 1) // 'A' para Activo, 'I' para Inactivo
    private String estado;

    // Relación ManyToOne con Especie (muchas razas pertenecen a una especie)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", nullable = false)
    @JsonBackReference
    private Especie especie;


    // Relación OneToMany con Mascota (una raza puede tener muchas mascotas)
    // De nuevo, sin CascadeType.ALL ni orphanRemoval para evitar borrados en cascada no deseados.
    @OneToMany(mappedBy = "raza")
    private Set<Mascota> mascotas; // Esta línea requerirá que Mascota.java exista y tenga el campo 'raza'

    // Constructor vacío (necesario para JPA)
    public Raza() {}

    // Constructor con campos (sin ID, será generado)
    public Raza(String nombre, String estado, Especie especie) {
        this.nombre = nombre;
        this.estado = estado;
        this.especie = especie;
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public Set<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(Set<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    // --- Métodos equals() y hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Raza raza = (Raza) o;
        return Objects.equals(id, raza.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "Raza{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }
    

}
