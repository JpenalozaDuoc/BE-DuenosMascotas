package microvetcare.microvetcare.raza.DTO;

import microvetcare.microvetcare.raza.entity.Raza;

public class RazaDTO {

    private Long id;
    private String nombre;
    private String estado;
    private Long especieId;  // Aquí agregas el campo especieId

    // Constructor completo
    public RazaDTO(Long id, String nombre, String estado, Long especieId) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.especieId = especieId;
    }

    // Constructor que recibe una entidad Raza y mapea sus campos al DTO
    public RazaDTO(Raza raza) {
        this.id = raza.getId();
        this.nombre = raza.getNombre();
        this.estado = raza.getEstado();
        this.especieId = raza.getEspecie().getId();  // Suponiendo que la especie es una entidad con ID
    }

    // Constructor vacío (necesario para JPA)
    public RazaDTO() {}

    // Getters y Setters
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

    public Long getEspecieId() {
        return especieId;
    }

    public void setEspecieId(Long especieId) {
        this.especieId = especieId;
    }

    // Método para convertir el DTO de vuelta a la entidad Raza
    public Raza toEntity() {
        Raza raza = new Raza();
        raza.setId(this.id);
        raza.setNombre(this.nombre);
        raza.setEstado(this.estado);
        // En este punto, debes buscar la especie por su ID antes de asignarla
        // Esto puede ser una llamada a un servicio o un repositorio para obtener la entidad Especie
        // Por ahora se asume que el ID de la especie es suficiente para representarla
        return raza;
    }

}
