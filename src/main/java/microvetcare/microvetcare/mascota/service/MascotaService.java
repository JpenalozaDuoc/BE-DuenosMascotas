package microvetcare.microvetcare.mascota.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import microvetcare.microvetcare.mascota.DTO.MascotaDTO;

public interface MascotaService {

    List<MascotaDTO> findAllMascotas();
    Optional<MascotaDTO> findMascotaById(Long id);
    // Para guardar y actualizar, necesitamos los IDs de Dueno y Raza
    MascotaDTO saveMascota(MascotaDTO mascota, Long duenoId, Long razaId);
    MascotaDTO updateMascota(Long id, MascotaDTO mascota, Long duenoId, Long razaId);
    void deleteMascota(Long id);

    // Métodos de búsqueda
    List<MascotaDTO> findMascotasByNombre(String nombre);
    List<MascotaDTO> findMascotasByDuenoId(Long duenoId);
    List<MascotaDTO> findMascotasByRazaId(Long razaId);
    List<MascotaDTO> findMascotasByGenero(String genero);
    List<MascotaDTO> findMascotasBornAfter(LocalDate date);
    List<MascotaDTO> findMascotasBornBefore(LocalDate date);
}
