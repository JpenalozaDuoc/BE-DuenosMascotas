package microvetcare.microvetcare.mascota.DTO;


import java.time.LocalDate;

public class MascotaDTO {

    private Long id;
    private String nombre;
    private String chip;
    private String genero;
    private Integer estado;
    private LocalDate fechaNacimiento;
    private Long idDueno; 
    private Long idRaza;  

    // --- NUEVOS CAMPOS AÑADIDOS ---
    private String nombreDueno;
    private String nombreRaza;
    // ----------------------------

    public MascotaDTO() {}

    // Constructor existente actualizado para incluir los nuevos campos (opcional)
    public MascotaDTO(Long id, String nombre, String chip, String genero, Integer estado, LocalDate fechaNacimiento, 
                      Long idDueno, Long idRaza, String nombreDueno, String nombreRaza) {
        this.id = id;
        this.nombre = nombre;
        this.chip = chip;
        this.genero = genero;
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
        this.idDueno = idDueno;
        this.idRaza = idRaza;
        this.nombreDueno = nombreDueno;
        this.nombreRaza = nombreRaza;
    }

    // Constructor original mantenido por compatibilidad
    public MascotaDTO(Long id, String nombre, LocalDate fechaNacimiento, Integer estado, String chip, String genero, Long idDueno, Long idRaza) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = estado;
        this.chip = chip;
        this.genero = genero;
        this.idDueno = idDueno;
        this.idRaza = idRaza;
    }


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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(Long idDueno) {
        this.idDueno = idDueno;
    }

    public Long getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(Long idRaza) {
        this.idRaza = idRaza;
    }

    // --- NUEVOS Getters y Setters ---
    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }
    // ---------------------------------
}

/*
import java.time.LocalDate;

public class MascotaDTO {

    private Long id;
    private String nombre;
    private String chip;
    private String genero;
    private Integer estado;
    private LocalDate fechaNacimiento;
    private Long idDueno;  
    private Long idRaza;  


    public MascotaDTO() {}

    public MascotaDTO(Long id, String nombre, String chip, String genero, Integer estado, LocalDate fechaNacimiento, Long idDueno, Long idRaza) {
        this.id = id;
        this.nombre = nombre;
        this.chip = chip;
        this.genero = genero;
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
        this.idDueno = idDueno;
        this.idRaza = idRaza;
    }

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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(Long idDueno) {
        this.idDueno = idDueno;
    }

    public Long getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(Long idRaza) {
        this.idRaza = idRaza;
    }

}
*/