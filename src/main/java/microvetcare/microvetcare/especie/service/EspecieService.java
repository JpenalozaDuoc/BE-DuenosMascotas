package microvetcare.microvetcare.especie.service;

import java.util.List;
import java.util.Optional;

import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.especie.entity.Especie;

public interface EspecieService {
   
    List<EspecieDTO> findAllEspecies();
    Optional<EspecieDTO> findEspecieById(Long id);
    EspecieDTO saveEspecie(EspecieDTO especieDTO);
    EspecieDTO updateEspecie(Long id, EspecieDTO especieDTO);
    void deleteEspecie(Long id);
    boolean existsByNombre(String nombre);
    EspecieDTO convertToDTO(Especie especie);
    Optional<EspecieDTO> findEspecieByNombre(String nombre);

    
}
