package microvetcare.microvetcare.dueno.entity;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import microvetcare.microvetcare.mascota.entity.Mascota;


@Entity
@Table(name = "dueno")
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dueno")
    private Long id;

    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT no es válido")
    @NotBlank(message = "El RUT es obligatorio")
    @Column(name = "rut", nullable = false, length = 15, unique = true)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Pattern(regexp = "\\d{11}", message = "El teléfono debe contener exactamente 11 dígitos")
    @Column(name = "telefono", length = 11) 
    private String telefono;

    @Email(message = "El formato del correo electrónico no es válido")
    @Column(name = "email", length = 250, unique = true)
    private String email;

    @Column(name = "estado", nullable = false, length = 1)
    private Boolean estado;

    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference  
    private Set<Mascota> mascotas; 

    public Dueno() {}

    public Dueno(String rut, String nombre, String apellido, String direccion,
                 String telefono, String email, Boolean estado) {
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Set<Mascota> getMascotas() { return mascotas; }
    public void setMascotas(Set<Mascota> mascotas) { this.mascotas = mascotas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dueno dueno = (Dueno) o;
        return Objects.equals(id, dueno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dueno{" +
               "id=" + id +
               ", rut='" + rut + '\'' +
               ", nombre='" + nombre + '\'' +
               ", apellido='" + apellido + '\'' +
               ", direccion='" + direccion + '\'' +
               ", telefono='" + telefono + '\'' +
               ", email='" + email + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }

}
