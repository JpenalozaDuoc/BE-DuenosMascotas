package microvetcare.microvetcare.mascota.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.raza.entity.Raza;

/*
 * Descripción de la tabla mascota:
Nombre           ¿Nulo?   Tipo               
---------------- -------- ------------------ 
ESTADO           NOT NULL NUMBER(1)          
FECHA_NACIMIENTO          DATE               
ID_MASCOTA       NOT NULL NUMBER(19)         
GENERO           NOT NULL VARCHAR2(15 CHAR)  
CHIP             NOT NULL VARCHAR2(30 CHAR)  
NOMBRE           NOT NULL VARCHAR2(100 CHAR) 
ID_DUENO         NOT NULL NUMBER(19)         
ID_RAZA          NOT NULL NUMBER(19)         

 */
@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "estado", nullable = false, length = 1) // 'A' para Activo, 'I' para Inactivo
    private Integer estado;

    @Column(name = "chip", nullable = false, length = 30) // Asumo que es un identificador único de la mascota
    private String chip;

    @Column(name = "genero", nullable = false, length = 15) // Genero de la mascota (Macho o Hembra)
    private String genero;

    // Relación ManyToOne con Dueno (muchas mascotas pertenecen a un dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dueno", nullable = false) // Columna FK en la tabla MASCOTA
    @JsonBackReference  // Evita la recursión infinita y es el "lado de atrás" de la relación.
    private Dueno dueno;

    // Relación ManyToOne con Raza (muchas mascotas pueden ser de la misma raza)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raza", nullable = false) // <--- ¡IMPORTANTE! Columna FK en la tabla MASCOTA
    private Raza raza; // <--- ¡IMPORTANTE! Campo para la entidad Raza

    // Constructor vacío (necesario para JPA)
    public Mascota() {
    }

    // Constructor con campos esenciales (sin ID, será generado)
    // Incluye 'dueno' y 'raza' para la creación completa

    public Mascota(Long id, String nombre, LocalDate fechaNacimiento, Integer estado, String chip, String genero,
            Dueno dueno, Raza raza) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = estado;
        this.chip = chip;
        this.genero = genero;
        this.dueno = dueno;
        this.raza = raza;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Dueno getDueno() {
        return dueno;
    }

    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
    }

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }

    // --- Métodos equals() y hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mascota mascota = (Mascota) o;
        return Objects.equals(id, mascota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Mascota [id=" + id + 
                ", nombre=" + nombre + 
                ", fechaNacimiento=" + fechaNacimiento + 
                ", estado=" + estado + 
                ", chip=" + chip + 
                ", genero=" + genero + 
                ", dueno=" + dueno + 
                ", raza=" + raza + 
                "]";
    }

    

}
