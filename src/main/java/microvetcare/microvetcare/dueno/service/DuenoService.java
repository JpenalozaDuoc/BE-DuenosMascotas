package microvetcare.microvetcare.dueno.service;

import java.util.Optional;
import java.util.List;
import microvetcare.microvetcare.dueno.DTO.DuenoDTO;

public interface DuenoService {

    List<DuenoDTO> findAllDuenos();
    Optional<DuenoDTO> findDuenoById(Long id);
    DuenoDTO saveDueno(DuenoDTO dueno); 
    DuenoDTO updateDueno(Long id, DuenoDTO dueno); 
    void deleteDueno(Long id);
    Optional<DuenoDTO> findDuenoByRut(String rut);
    Optional<DuenoDTO> findDuenoByEmail(String email);
}
