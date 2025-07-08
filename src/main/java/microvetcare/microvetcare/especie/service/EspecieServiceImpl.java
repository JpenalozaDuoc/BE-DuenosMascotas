package microvetcare.microvetcare.especie.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.especie.repository.EspecieRepository;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class EspecieServiceImpl implements EspecieService{

    private final EspecieRepository especieRepository;

    public EspecieServiceImpl(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecieDTO> findAllEspecies() {
        return especieRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EspecieDTO> findEspecieById(Long id) {
        return especieRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public EspecieDTO saveEspecie(EspecieDTO especieDTO) {
        if (especieRepository.existsByNombre(especieDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe una especie con el nombre: " + especieDTO.getNombre());
        }
        
        if (especieDTO.getId() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo para una nueva especie.");
        }

        Especie especie = convertToEntity(especieDTO);
        return convertToDTO(especieRepository.save(especie));
    }

   @Override
    @Transactional
    public EspecieDTO updateEspecie(Long id, EspecieDTO especieDTO) {
        Especie existingEspecie = especieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada con ID: " + id));

        if (especieDTO.getNombreEspecie() != null && !especieDTO.getNombreEspecie().equals(existingEspecie.getNombreEspecie())) {
            if (especieRepository.existsByNombreEspecie(especieDTO.getNombreEspecie())) {
                throw new IllegalArgumentException("El nombreEspecie '" + especieDTO.getNombreEspecie() + "' ya está registrado para otra especie.");
            }
            existingEspecie.setNombreEspecie(especieDTO.getNombreEspecie());
        }

        if (especieDTO.getNombre() != null && !especieDTO.getNombre().equals(existingEspecie.getNombre())) {
            if (especieRepository.existsByNombre(especieDTO.getNombre())) {
                throw new IllegalArgumentException("El nombre '" + especieDTO.getNombre() + "' ya está registrado para otra especie.");
            }
            existingEspecie.setNombre(especieDTO.getNombre());
        }

        if (especieDTO.getEstado() != null) {
            existingEspecie.setEstado(especieDTO.getEstado());
        }

        return convertToDTO(especieRepository.save(existingEspecie));
    }

    @Override
    @Transactional
    public void deleteEspecie(Long id) {
        if (!especieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especie no encontrada con ID: " + id);
        }
        especieRepository.deleteById(id);
    }
     
    @Override
    @Transactional(readOnly = true)
    public Optional<EspecieDTO> findEspecieByNombre(String nombre) {
        return especieRepository.findByNombre(nombre).map(this::convertToDTO);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return especieRepository.existsByNombre(nombre);
    }

    @Override
    public EspecieDTO convertToDTO(Especie especie) {
        return new EspecieDTO(
            especie.getId(),
            especie.getNombreEspecie(),
            especie.getNombre(),
            especie.getEstado()
        );
    }

    public Especie convertToEntity(EspecieDTO especieDTO) {
        return new Especie(
            especieDTO.getId(),
            especieDTO.getNombreEspecie(),
            especieDTO.getNombre(),
            especieDTO.getEstado()
        );
    }
    
}
