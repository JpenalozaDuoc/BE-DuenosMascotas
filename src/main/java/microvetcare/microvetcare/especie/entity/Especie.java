package microvetcare.microvetcare.especie.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import microvetcare.microvetcare.raza.entity.Raza;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/*
Descripción de la tabla especie:
 * Nombre         ¿Nulo?   Tipo               
-------------- -------- ------------------ 
ESTADO         NOT NULL NUMBER(1)          
ID_ESPECIE     NOT NULL NUMBER(19)         
NOMBRE_ESPECIE NOT NULL VARCHAR2(100 CHAR) 
NOMBRE         NOT NULL VARCHAR2(100 CHAR) 

*/
@Entity
@Table(name = "especie")
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especie")
    private Long id;

    @Column(name = "NOMBRE_ESPECIE", nullable = false, length = 100)
    private String nombreEspecie;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "estado", nullable = false) // 'A' para Activo, 'I' para Inactivo
    private Integer estado;

  
    @OneToMany(mappedBy = "especie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Raza> razas = new ArrayList<>();


    // Constructor vacío (necesario para JPA)
    public Especie() {}

    public Especie(Long id, String nombreEspecie, String nombre, Integer estado) {
        this.id = id;
        this.nombreEspecie = nombreEspecie;
        this.nombre = nombre;
        this.estado = estado;
    }
    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEspecie() {
        return nombreEspecie;
    }

    public void setNombreEspecie(String nombreEspecie) {
        this.nombreEspecie = nombreEspecie;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Especie)) return false;
        Especie especie = (Especie) o;
        return Objects.equals(id, especie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Especie{" +
                "id=" + id +
                ", nombreEspecie='" + nombreEspecie + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }

}
