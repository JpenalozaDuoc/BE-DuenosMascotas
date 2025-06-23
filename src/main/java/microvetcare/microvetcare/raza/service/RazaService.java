package microvetcare.microvetcare.raza.service;

import microvetcare.microvetcare.raza.DTO.RazaDTO;
import java.util.List;
import java.util.Optional;


public interface RazaService {

    List<RazaDTO> findAllRazas();
    Optional<RazaDTO> findRazaById(Long id);
    RazaDTO saveRaza(RazaDTO razaDTO);
    RazaDTO updateRaza(Long id, RazaDTO razaDTO);
    void deleteRaza(Long id);
    Optional<RazaDTO> findRazaByNombre(String nombre);
    List<RazaDTO> findRazasByEspecieId(Long especieId);

}
