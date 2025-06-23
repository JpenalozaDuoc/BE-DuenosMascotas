package microvetcare.microvetcare.dueno.service;

import java.util.Optional;
import java.util.List;
import microvetcare.microvetcare.dueno.DTO.DuenoDTO;

public interface DuenoService {

    List<DuenoDTO> findAllDuenos(); // Retorna List<Dueno>
    Optional<DuenoDTO> findDuenoById(Long id); // Retorna Optional<Dueno>, id es Long
    DuenoDTO saveDueno(DuenoDTO dueno); // Recibe Dueno
    DuenoDTO updateDueno(Long id, DuenoDTO dueno); // Recibe Dueno, id es Long
    void deleteDueno(Long id); // id es Long

    // Métodos para búsqueda por campos únicos, retornan Optional<Dueno>
    Optional<DuenoDTO> findDuenoByRut(String rut);
    Optional<DuenoDTO> findDuenoByEmail(String email);
}
