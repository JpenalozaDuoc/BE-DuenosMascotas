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
