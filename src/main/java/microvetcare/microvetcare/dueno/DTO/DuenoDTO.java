package microvetcare.microvetcare.dueno.DTO;

import java.util.List;
import java.util.stream.Collectors;
import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.mascota.entity.Mascota;

public class DuenoDTO {

    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean estado;
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

}
