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
    private final DuenoRepository duenoRepository;
    private final RazaRepository razaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository, DuenoRepository duenoRepository, RazaRepository razaRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
        this.razaRepository = razaRepository;
    }

    private Mascota convertirDTOaEntidad(MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        Mascota mascota = new Mascota();
        if (mascotaDTO.getId() != null) {
            mascota.setId(mascotaDTO.getId());
        }

        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        mascota.setChip(mascotaDTO.getChip());
        mascota.setGenero(mascotaDTO.getGenero());
        mascota.setEstado(mascotaDTO.getEstado());

        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId));
        mascota.setDueno(dueno);

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + razaId));
        mascota.setRaza(raza);

        return mascota;
    }

    private MascotaDTO convertirEntidadADTO(Mascota mascota) {
        MascotaDTO mascotaDTO = new MascotaDTO();
        mascotaDTO.setId(mascota.getId());
        mascotaDTO.setNombre(mascota.getNombre());
        mascotaDTO.setFechaNacimiento(mascota.getFechaNacimiento());
        mascotaDTO.setChip(mascota.getChip());
        mascotaDTO.setGenero(mascota.getGenero());
        mascotaDTO.setEstado(mascota.getEstado());

        if (mascota.getDueno() != null) {
            mascotaDTO.setIdDueno(mascota.getDueno().getId());
            // --- AÑADIDO: Nombre del dueño ---
            mascotaDTO.setNombreDueno(mascota.getDueno().getNombre() + " " + mascota.getDueno().getApellido());
        } else {
            mascotaDTO.setNombreDueno("N/A"); // Fallback por si acaso, aunque JoinColumn es nullable=false
        }

        if (mascota.getRaza() != null) {
            mascotaDTO.setIdRaza(mascota.getRaza().getId());
            // --- AÑADIDO: Nombre de la raza ---
            mascotaDTO.setNombreRaza(mascota.getRaza().getNombre()); // Asumiendo que Raza tiene getNombre()
        } else {
            mascotaDTO.setNombreRaza("N/A"); // Fallback por si acaso, aunque JoinColumn es nullable=false
        }

        return mascotaDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findAllMascotas() {
        // --- CAMBIADO: Usar el método con JOIN FETCH ---
        List<Mascota> mascotas = mascotaRepository.findAllWithDuenoAndRaza();
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MascotaDTO> findMascotaById(Long id) {
        // --- CAMBIADO: Usar el método con JOIN FETCH ---
        Mascota mascota = mascotaRepository.findByIdWithDuenoAndRaza(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
        return Optional.of(convertirEntidadADTO(mascota));
    }

    @Override
    @Transactional
    public MascotaDTO saveMascota(MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        Mascota mascota = convertirDTOaEntidad(mascotaDTO, duenoId, razaId);
        mascota = mascotaRepository.save(mascota);
        return convertirEntidadADTO(mascota);
    }

    @Override
    @Transactional
    public MascotaDTO updateMascota(Long id, MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        Mascota existingMascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
        existingMascota.setNombre(mascotaDTO.getNombre());
        existingMascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        existingMascota.setChip(mascotaDTO.getChip());
        existingMascota.setGenero(mascotaDTO.getGenero());
        existingMascota.setEstado(mascotaDTO.getEstado());

        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId));
        existingMascota.setDueno(dueno);

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + razaId));
        existingMascota.setRaza(raza);

        existingMascota = mascotaRepository.save(existingMascota);

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
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByNombre(nombre);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByDuenoId(Long duenoId) {
        if (!duenoRepository.existsById(duenoId)) {
            throw new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId);
        }
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByDuenoId(duenoId);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByRazaId(Long razaId) {
        if (!razaRepository.existsById(razaId)) {
            throw new ResourceNotFoundException("Raza no encontrada con ID: " + razaId);
        }
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByRazaId(razaId);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasByGenero(String genero) {
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByGenero(genero);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasBornAfter(LocalDate date) {
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByFechaNacimientoAfter(date);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findMascotasBornBefore(LocalDate date) {
        // Considera si también quieres JOIN FETCH aquí para rendimiento
        List<Mascota> mascotas = mascotaRepository.findByFechaNacimientoBefore(date);
        return mascotas.stream().map(this::convertirEntidadADTO).toList();
    }
}


/*
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
    private final DuenoRepository duenoRepository;
    private final RazaRepository razaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository, DuenoRepository duenoRepository, RazaRepository razaRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
        this.razaRepository = razaRepository;
    }

    private Mascota convertirDTOaEntidad(MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        Mascota mascota = new Mascota();
        if (mascotaDTO.getId() != null) {
            mascota.setId(mascotaDTO.getId());
        }

        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        mascota.setChip(mascotaDTO.getChip());
        mascota.setGenero(mascotaDTO.getGenero());
        mascota.setEstado(mascotaDTO.getEstado());

        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId));
        mascota.setDueno(dueno);

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + razaId));
        mascota.setRaza(raza);

        return mascota;
    }

    private MascotaDTO convertirEntidadADTO(Mascota mascota) {
        MascotaDTO mascotaDTO = new MascotaDTO();
        mascotaDTO.setId(mascota.getId());
        mascotaDTO.setNombre(mascota.getNombre());
        mascotaDTO.setFechaNacimiento(mascota.getFechaNacimiento());
        mascotaDTO.setChip(mascota.getChip());
        mascotaDTO.setGenero(mascota.getGenero());
        mascotaDTO.setEstado(mascota.getEstado());

        if (mascota.getDueno() != null) {
            mascotaDTO.setIdDueno(mascota.getDueno().getId());
        }

        if (mascota.getRaza() != null) {
            mascotaDTO.setIdRaza(mascota.getRaza().getId());
        }

        return mascotaDTO;
    }

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
        Mascota mascota = convertirDTOaEntidad(mascotaDTO, duenoId, razaId);
        mascota = mascotaRepository.save(mascota);
        return convertirEntidadADTO(mascota);
    }

    @Override
    @Transactional
    public MascotaDTO updateMascota(Long id, MascotaDTO mascotaDTO, Long duenoId, Long razaId) {
        Mascota existingMascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
        existingMascota.setNombre(mascotaDTO.getNombre());
        existingMascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        existingMascota.setChip(mascotaDTO.getChip());
        existingMascota.setGenero(mascotaDTO.getGenero());
        existingMascota.setEstado(mascotaDTO.getEstado());

        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + duenoId));
        existingMascota.setDueno(dueno);

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + razaId));
        existingMascota.setRaza(raza);

        existingMascota = mascotaRepository.save(existingMascota);

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
*/