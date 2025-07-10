package microvetcare.microvetcare.dueno.service;

import org.springframework.stereotype.Service;

import microvetcare.microvetcare.dueno.DTO.DuenoDTO;
import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.dueno.repository.DuenoRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import microvetcare.microvetcare.exception.ResourceNotFoundException;

@Service
public class DuenoServiceImpl implements DuenoService {

    private final DuenoRepository duenoRepository;

    public DuenoServiceImpl(DuenoRepository duenoRepository) {
        this.duenoRepository = duenoRepository;
    }

    private DuenoDTO convertirADTO(Dueno dueno) {
        return new DuenoDTO(dueno); 
    }

    private Dueno convertirAEntidad(DuenoDTO duenoDTO) {
        Dueno dueno = new Dueno();
        dueno.setId(duenoDTO.getId());
        dueno.setRut(duenoDTO.getRut());
        dueno.setEmail(duenoDTO.getEmail());
        dueno.setNombre(duenoDTO.getNombre());
        dueno.setApellido(duenoDTO.getApellido());
        dueno.setDireccion(duenoDTO.getDireccion());
        dueno.setTelefono(duenoDTO.getTelefono());
        dueno.setEstado(duenoDTO.getEstado());
        return dueno;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DuenoDTO> findAllDuenos() {
        List<Dueno> duenos = duenoRepository.findAll();
        return duenos.stream().map(this::convertirADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DuenoDTO> findDuenoById(Long id) {
        return duenoRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public DuenoDTO saveDueno(DuenoDTO duenoDTO) {
        if (duenoRepository.existsByRut(duenoDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe un dueño con el RUT: " + duenoDTO.getRut());
        }
        if (duenoDTO.getEmail() != null && duenoRepository.existsByEmail(duenoDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un dueño con el Email: " + duenoDTO.getEmail());
        }

        if (duenoDTO.getId() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo para un nuevo dueño.");
        }

        Dueno dueno = convertirAEntidad(duenoDTO);
        Dueno savedDueno = duenoRepository.save(dueno);

        return convertirADTO(savedDueno);
    }

    @Override
    @Transactional
    public DuenoDTO updateDueno(Long id, DuenoDTO duenoDTO) {
        Dueno duenoExistente = duenoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + id));
        if (duenoDTO.getRut() != null && !duenoDTO.getRut().equals(duenoExistente.getRut())) {
            if (duenoRepository.findByRut(duenoDTO.getRut()).isPresent()) {
                throw new IllegalArgumentException("El RUT " + duenoDTO.getRut() + " ya está registrado para otro dueño.");
            }
            duenoExistente.setRut(duenoDTO.getRut());
        }

        if (duenoDTO.getEmail() != null && !duenoDTO.getEmail().equals(duenoExistente.getEmail())) {
            if (duenoRepository.findByEmail(duenoDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El Email " + duenoDTO.getEmail() + " ya está registrado para otro dueño.");
            }
            duenoExistente.setEmail(duenoDTO.getEmail());
        }

        duenoExistente.setNombre(duenoDTO.getNombre() != null ? duenoDTO.getNombre() : duenoExistente.getNombre());
        duenoExistente.setApellido(duenoDTO.getApellido() != null ? duenoDTO.getApellido() : duenoExistente.getApellido());
        duenoExistente.setDireccion(duenoDTO.getDireccion() != null ? duenoDTO.getDireccion() : duenoExistente.getDireccion());
        duenoExistente.setTelefono(duenoDTO.getTelefono() != null ? duenoDTO.getTelefono() : duenoExistente.getTelefono());
        duenoExistente.setEstado(duenoDTO.getEstado() != null ? duenoDTO.getEstado() : duenoExistente.getEstado());

        Dueno updatedDueno = duenoRepository.save(duenoExistente);
        return convertirADTO(updatedDueno);
    }

    @Override
    @Transactional
    public void deleteDueno(Long id) {
        if (!duenoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dueño no encontrado con ID: " + id);
        }
        duenoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DuenoDTO> findDuenoByRut(String rut) {
        return duenoRepository.findByRut(rut).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DuenoDTO> findDuenoByEmail(String email) {
        return duenoRepository.findByEmail(email).map(this::convertirADTO);
    }
}
