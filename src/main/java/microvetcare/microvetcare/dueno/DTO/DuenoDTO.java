package microvetcare.microvetcare.dueno.DTO;

import java.util.List;
import java.util.stream.Collectors;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.mascota.entity.Mascota;
//import microvetcare.microvetcare.mascota.DTO.MascotaDTO;

public class DuenoDTO {

    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean estado;

     // Este es el campo que ahora usaremos para las mascotas
    private List<Long> mascotaIds; 

    public DuenoDTO() {}

    public DuenoDTO(Dueno dueno) {
        this.id = dueno.getId();
        this.rut = dueno.getRut();
        this.nombre = dueno.getNombre();
        this.apellido = dueno.getApellido();
        this.direccion = dueno.getDireccion();
        this.telefono = dueno.getTelefono();
        this.email = dueno.getEmail();
        this.estado = dueno.getEstado();

        if (dueno.getMascotas() != null && !dueno.getMascotas().isEmpty()) {
            this.mascotaIds = dueno.getMascotas().stream()
                                       .map(Mascota::getId) 
                                       .collect(Collectors.toList());
        } else {
            this.mascotaIds = List.of(); 
        }
    }

    // --- Getters y Setters para todos los campos, incluyendo mascotaIds ---
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

    public List<Long> getMascotaIds() { return mascotaIds; }
    public void setMascotaIds(List<Long> mascotaIds) { this.mascotaIds = mascotaIds; }


    /*
    @JsonIgnore  // Esto evitará la serialización de 'mascotas' en el DTO si no lo necesitas.
    private List<MascotaDTO> mascotas;  // Aquí agregamos la lista de mascotas

    // Constructor vacío (necesario para JPA)
    public DuenoDTO() {}

    // Constructor que recibe la entidad Dueno
    public DuenoDTO(Dueno dueno) {
        this.id = dueno.getId();
        this.rut = dueno.getRut();
        this.nombre = dueno.getNombre();
        this.apellido = dueno.getApellido();
        this.direccion = dueno.getDireccion();
        this.telefono = dueno.getTelefono();
        this.email = dueno.getEmail();
        this.estado = dueno.getEstado();

        // Mapeamos la lista de mascotas, pero solo sus IDs, evitando la recursión infinita
        if (dueno.getMascotas() != null) {
            this.mascotas = dueno.getMascotas().stream()
                                 .map(mascota -> new MascotaDTO())  // Mapea solo el ID de la mascota
                                 .collect(Collectors.toList());
        }
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public List<MascotaDTO> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<MascotaDTO> mascotas) {
        this.mascotas = mascotas;
    }
    */
}
