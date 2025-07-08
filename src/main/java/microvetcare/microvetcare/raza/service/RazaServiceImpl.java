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
    private final EspecieRepository especieRepository;

    public RazaServiceImpl(RazaRepository razaRepository, EspecieRepository especieRepository) {
        this.razaRepository = razaRepository;
        this.especieRepository = especieRepository;
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<RazaDTO> findAllRazas() {
        List<Raza> razas = razaRepository.findAll();
        return razas.stream()
                    .map(RazaDTO::new) 
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RazaDTO> findRazaById(Long id) {
        Optional<Raza> raza = razaRepository.findById(id);
        return raza.map(RazaDTO::new);
    }

    @Override
    @Transactional
    public RazaDTO saveRaza(RazaDTO razaDTO) {
        Raza raza = new Raza();
        raza.setNombre(razaDTO.getNombre());
        raza.setEstado(razaDTO.getEstado());

        if (razaDTO.getEspecieId() != null) {
            raza.setEspecie(especieRepository.findById(razaDTO.getEspecieId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada")));
        }
        Raza savedRaza = razaRepository.save(raza);

        return new RazaDTO(savedRaza);
    }

    @Override
    @Transactional
    public RazaDTO updateRaza(Long id, RazaDTO razaDTO) {
        Raza existingRaza = razaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Raza no encontrada"));
        existingRaza.setNombre(razaDTO.getNombre());
        existingRaza.setEstado(razaDTO.getEstado());

        if (razaDTO.getEspecieId() != null) {
            existingRaza.setEspecie(especieRepository.findById(razaDTO.getEspecieId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada")));
        }

        Raza updatedRaza = razaRepository.save(existingRaza);

        return new RazaDTO(updatedRaza);
    }

    @Override
    @Transactional
    public void deleteRaza(Long id) {

        if (!razaRepository.existsById(id)) {
            throw new IllegalArgumentException("Raza no encontrada");
        }
        razaRepository.deleteById(id);
    }
    

    @Override
    @Transactional(readOnly = true)
    public Optional<RazaDTO> findRazaByNombre(String nombre) {
        Optional<Raza> raza = razaRepository.findByNombre(nombre);
        return raza.map(RazaDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaDTO> findRazasByEspecieId(Long especieId) {
        List<Raza> razas = razaRepository.findByEspecieId(especieId);
        return razas.stream()
                    .map(RazaDTO::new)
                    .collect(Collectors.toList());
    }

}
