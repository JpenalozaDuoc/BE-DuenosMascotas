package microvetcare.microvetcare.especie.DTO;

public class EspecieDTO {

    private Long id;
    private String nombreEspecie;
    private String nombre;
    private Integer estado;
    
    
    public EspecieDTO(Long id, String nombreEspecie, String nombre, Integer estado) {
        this.id = id;
        this.nombreEspecie = nombreEspecie;
        this.nombre = nombre;
        this.estado = estado;
    }

    // Constructor vacío (necesario para JPA)
    public EspecieDTO() {}

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

    
    
}
