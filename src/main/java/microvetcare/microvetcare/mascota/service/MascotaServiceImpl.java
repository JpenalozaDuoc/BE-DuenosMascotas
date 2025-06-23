package microvetcare.microvetcare.mascota.service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.dueno.repository.DuenoRepository;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import microvetcare.microvetcare.mascota.DTO.MascotaDTO;
import microvetcare.microvetcare.mascota.entity.Mascota;
import microvetcare.microvetcare.mascota.repository.MascotaRepository;
import microvetcare.microvetcare.raza.entity.Raza;
import microvetcare.microvetcare.raza.repository.RazaRepository;

@Service
public class MascotaServiceImpl implements MascotaService {


    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;     // Inyección del repositorio de Dueño
    private final RazaRepository razaRepository;       // Inyección del repositorio de Raza

    public MascotaServiceImpl(MascotaRepository mascotaRepository, DuenoRepository duenoRepository, RazaRepository razaRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
        this.razaRepository = razaRepository;
    }

    // -------------------- Métodos para conversión --------------------

    // Convertir MascotaDTO a Mascota (Entidad)
    private Mascota convertirDTOaEntidad(MascotaDTO mascotaDTO) {
        Mascota mascota = new Mascota();
        mascota.setId(mascotaDTO.getId());
        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        mascota.setChip(mascotaDTO.getChip());
        mascota.setGenero(mascotaDTO.getGenero());
        mascota.setEstado(mascotaDTO.getEstado());

        // Asignar Dueno y Raza desde el DTO
        Dueno dueno = duenoRepository.findById(mascotaDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + mascotaDTO.getIdDueno()));
        mascota.setDueno(dueno);

        Raza raza = razaRepository.findById(mascotaDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + mascotaDTO.getIdRaza()));
        mascota.setRaza(raza);

        return mascota;
    }

    // Convertir Mascota (Entidad) a MascotaDTO
    private MascotaDTO convertirEntidadADTO(Mascota mascota) {
    MascotaDTO mascotaDTO = new MascotaDTO();
    mascotaDTO.setId(mascota.getId());
    mascotaDTO.setNombre(mascota.getNombre());
    mascotaDTO.setFechaNacimiento(mascota.getFechaNacimiento());
    mascotaDTO.setChip(mascota.getChip());
    mascotaDTO.setGenero(mascota.getGenero());
    mascotaDTO.setEstado(mascota.getEstado());

    // Asignar solo el id del Dueno en lugar de la entidad completa
    if (mascota.getDueno() != null) {
        mascotaDTO.setIdDueno(mascota.getDueno().getId());  // Asignar solo el ID del Dueno
    }

    // Convertir Raza a DTO
    if (mascota.getRaza() != null) {
        mascotaDTO.setIdRaza(mascota.getRaza().getId());  // Asignar solo el ID de la Raza
    }

    return mascotaDTO;
}


    // -------------------- Métodos del Service --------------------

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findAllMascotas() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MascotaDTO> findMascotaById(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
        return Optional.of(convertirEntidadADTO(mascota));
    }

    @Override
    @Transactional
    public MascotaDTO saveMascota(MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        // Convertir el DTO a entidad Mascota
        Mascota mascota = convertirDTOaEntidad(mascotaDTO);

        // Guardar la entidad Mascota
        mascota = mascotaRepository.save(mascota);

        // Convertir de vuelta a DTO y devolverlo
        return convertirEntidadADTO(mascota);
    }

    @Override
    @Transactional
    public MascotaDTO updateMascota(Long id, MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        // Verificar si la mascota existe
        Mascota existingMascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));

        // Convertir el DTO a entidad y actualizar los valores
        existingMascota.setNombre(mascotaDTO.getNombre());
        existingMascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        existingMascota.setChip(mascotaDTO.getChip());
        existingMascota.setGenero(mascotaDTO.getGenero());
        existingMascota.setEstado(mascotaDTO.getEstado());

        // Actualizar Dueno y Raza si se proporciona un nuevo ID
        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId));
        existingMascota.setDueno(dueno);

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + razaId));
        existingMascota.setRaza(raza);

        // Guardar la mascota actualizada
        existingMascota = mascotaRepository.save(existingMascota);

        // Convertir la entidad actualizada a DTO
        return convertirEntidadADTO(existingMascota);
    }

    @Override
    @Transactional
    public void deleteMascota(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mascota no encontrada con ID: " + id);
        }
        mascotaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByNombre(String nombre) {
        List<Mascota> mascotas = mascotaRepository.findByNombre(nombre);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByDuenoId(Long duenoId) {
        if (!duenoRepository.existsById(duenoId)) {
            throw new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId);
        }
        List<Mascota> mascotas = mascotaRepository.findByDuenoId(duenoId);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByRazaId(Long razaId) {
        if (!razaRepository.existsById(razaId)) {
            throw new ResourceNotFoundException("Raza no encontrada con ID: " + razaId);
        }
        List<Mascota> mascotas = mascotaRepository.findByRazaId(razaId);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByGenero(String genero) {
        List<Mascota> mascotas = mascotaRepository.findByGenero(genero);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasBornAfter(LocalDate date) {
        List<Mascota> mascotas = mascotaRepository.findByFechaNacimientoAfter(date);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasBornBefore(LocalDate date) {
        List<Mascota> mascotas = mascotaRepository.findByFechaNacimientoBefore(date);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

}
