package microvetcare.microvetcare.raza.service;

import org.springframework.stereotype.Service;

import microvetcare.microvetcare.especie.repository.EspecieRepository;
import microvetcare.microvetcare.raza.DTO.RazaDTO;
import microvetcare.microvetcare.raza.entity.Raza;
import microvetcare.microvetcare.raza.repository.RazaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RazaServiceImpl implements RazaService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository; // Inyectar el repositorio de Especie

    public RazaServiceImpl(RazaRepository razaRepository, EspecieRepository especieRepository) {
        this.razaRepository = razaRepository;
        this.especieRepository = especieRepository;
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<RazaDTO> findAllRazas() {
        // Paso 1: Obtener todas las razas de la base de datos
        List<Raza> razas = razaRepository.findAll();

        // Paso 2: Convertir la lista de Raza a RazaDTO usando un stream
        return razas.stream()
                    .map(RazaDTO::new)  // Usamos el constructor de RazaDTO que convierte Raza a RazaDTO
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RazaDTO> findRazaById(Long id) {
        // Paso 1: Buscar la raza en la base de datos
        Optional<Raza> raza = razaRepository.findById(id);

        // Paso 2: Convertir la entidad Raza a RazaDTO
        return raza.map(RazaDTO::new);
    }

    @Override
    @Transactional
    public RazaDTO saveRaza(RazaDTO razaDTO) {
        // Paso 1: Convertir RazaDTO a entidad Raza
        Raza raza = new Raza();
        raza.setNombre(razaDTO.getNombre());
        raza.setEstado(razaDTO.getEstado());

        // Paso 2: Validar que la especie exista en la base de datos
        if (razaDTO.getEspecieId() != null) {
            raza.setEspecie(especieRepository.findById(razaDTO.getEspecieId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada")));
        }

        // Paso 3: Guardar la entidad Raza en la base de datos
        Raza savedRaza = razaRepository.save(raza);

        // Paso 4: Convertir la entidad Raza guardada a RazaDTO y devolverla
        return new RazaDTO(savedRaza);
    }

    @Override
    @Transactional
    public RazaDTO updateRaza(Long id, RazaDTO razaDTO) {
        // Paso 1: Buscar la raza existente
        Raza existingRaza = razaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Raza no encontrada"));

        // Paso 2: Actualizar los campos de la raza
        existingRaza.setNombre(razaDTO.getNombre());
        existingRaza.setEstado(razaDTO.getEstado());

        // Paso 3: Actualizar la especie si se proporciona un nuevo especieId
        if (razaDTO.getEspecieId() != null) {
            existingRaza.setEspecie(especieRepository.findById(razaDTO.getEspecieId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada")));
        }

        // Paso 4: Guardar los cambios en la base de datos
        Raza updatedRaza = razaRepository.save(existingRaza);

        // Paso 5: Convertir la entidad Raza actualizada a RazaDTO y devolverla
        return new RazaDTO(updatedRaza);
    }

    @Override
    @Transactional
    public void deleteRaza(Long id) {
        // Verificar si la raza existe antes de eliminarla
        if (!razaRepository.existsById(id)) {
            throw new IllegalArgumentException("Raza no encontrada");
        }
        // Eliminar la raza de la base de datos
        razaRepository.deleteById(id);
    }
    

    @Override
    @Transactional(readOnly = true)
    public Optional<RazaDTO> findRazaByNombre(String nombre) {
        // Buscar la raza por nombre
        Optional<Raza> raza = razaRepository.findByNombre(nombre);

        // Convertir la entidad Raza a RazaDTO y devolverla
        return raza.map(RazaDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaDTO> findRazasByEspecieId(Long especieId) {
        // Buscar las razas por el ID de especie
        List<Raza> razas = razaRepository.findByEspecieId(especieId);

        // Convertir las entidades Raza a RazaDTO y devolverlas
        return razas.stream()
                    .map(RazaDTO::new)
                    .collect(Collectors.toList());
    }

}
