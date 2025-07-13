package microvetcare.microvetcare.service;

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.dueno.repository.DuenoRepository;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import microvetcare.microvetcare.mascota.DTO.MascotaDTO;
import microvetcare.microvetcare.mascota.entity.Mascota;
import microvetcare.microvetcare.mascota.repository.MascotaRepository;
import microvetcare.microvetcare.mascota.service.MascotaServiceImpl;
import microvetcare.microvetcare.raza.entity.Raza;
import microvetcare.microvetcare.raza.repository.RazaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private DuenoRepository duenoRepository;

    @Mock
    private RazaRepository razaRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Dueno dueno;
    private Raza raza;
    private Mascota mascota1;
    private MascotaDTO mascotaDTO1;

    @BeforeEach
    void setUp() {
        dueno = new Dueno("11111111-1", "Juan", "Perez", "Calle Falsa 123", "91111111111", "juan.perez@example.com", true);
        dueno.setId(1L);

        raza = new Raza(1L, "Labrador");

        mascota1 = new Mascota(1L, "Buddy", LocalDate.of(2020, 1, 15), 1, "CHIP123", "Macho", dueno, raza);
        // Usamos el constructor completo de MascotaDTO para los tests
        mascotaDTO1 = new MascotaDTO(1L, "Buddy", "CHIP123", "Macho", 1, LocalDate.of(2020, 1, 15),
                dueno.getId(), raza.getId(), dueno.getNombre() + " " + dueno.getApellido(), raza.getNombre());
    }

    // --- Tests para findAllMascotas() ---
    @Test
    @DisplayName("Debería retornar una lista de todas las mascotas")
    void findAllMascotas_shouldReturnAllMascotas() {
        // Given
        Mascota mascota2 = new Mascota(2L, "Lucy", LocalDate.of(2019, 5, 20), 1, "CHIP456", "Hembra", dueno, raza);
        List<Mascota> mascotas = Arrays.asList(mascota1, mascota2);

        when(mascotaRepository.findAllWithDuenoAndRaza()).thenReturn(mascotas);

        // When
        List<MascotaDTO> foundMascotas = mascotaService.findAllMascotas();

        // Then
        assertThat(foundMascotas).isNotNull();
        assertThat(foundMascotas).hasSize(2);
        assertThat(foundMascotas.get(0).getNombre()).isEqualTo("Buddy");
        assertThat(foundMascotas.get(1).getChip()).isEqualTo("CHIP456");
        assertThat(foundMascotas.get(0).getNombreDueno()).isEqualTo("Juan Perez");
        assertThat(foundMascotas.get(0).getNombreRaza()).isEqualTo("Labrador");
        verify(mascotaRepository, times(1)).findAllWithDuenoAndRaza();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay mascotas")
    void findAllMascotas_shouldReturnEmptyList_whenNoMascotas() {
        // Given
        when(mascotaRepository.findAllWithDuenoAndRaza()).thenReturn(List.of());

        // When
        List<MascotaDTO> foundMascotas = mascotaService.findAllMascotas();

        // Then
        assertThat(foundMascotas).isNotNull();
        assertThat(foundMascotas).isEmpty();
        verify(mascotaRepository, times(1)).findAllWithDuenoAndRaza();
    }

    // --- Tests para findMascotaById(Long id) ---
    @Test
    @DisplayName("Debería retornar una mascota por ID cuando existe")
    void findMascotaById_shouldReturnMascota_whenExists() {
        // Given
        when(mascotaRepository.findByIdWithDuenoAndRaza(mascota1.getId())).thenReturn(Optional.of(mascota1));

        // When
        Optional<MascotaDTO> foundMascota = mascotaService.findMascotaById(mascota1.getId());

        // Then
        assertThat(foundMascota).isPresent();
        assertThat(foundMascota.get().getId()).isEqualTo(mascota1.getId());
        assertThat(foundMascota.get().getNombre()).isEqualTo(mascota1.getNombre());
        assertThat(foundMascota.get().getNombreDueno()).isEqualTo("Juan Perez");
        assertThat(foundMascota.get().getNombreRaza()).isEqualTo("Labrador");
        verify(mascotaRepository, times(1)).findByIdWithDuenoAndRaza(mascota1.getId());
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la mascota no se encuentra por ID")
    void findMascotaById_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(mascotaRepository.findByIdWithDuenoAndRaza(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.findMascotaById(nonExistentId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Mascota no encontrada con ID: " + nonExistentId);
        verify(mascotaRepository, times(1)).findByIdWithDuenoAndRaza(nonExistentId);
    }

    // --- Tests para saveMascota(MascotaDTO mascotaDTO, Long duenoId, Long razaId) ---
    @Test
    @DisplayName("Debería guardar una nueva mascota exitosamente")
    void saveMascota_shouldSaveNewMascota() {
        // Given
        MascotaDTO newMascotaDTO = new MascotaDTO(null, "Firulais", "CHIP789", "Macho", 1, LocalDate.of(2022, 3, 10),
                dueno.getId(), raza.getId(), null, null); // idDueno y idRaza se pasan por separado

        //Mascota newMascotaEntity = new Mascota(null, "Firulais", LocalDate.of(2022, 3, 10), 1, "CHIP789", "Macho", dueno, raza);
        Mascota savedMascotaEntity = new Mascota(3L, "Firulais", LocalDate.of(2022, 3, 10), 1, "CHIP789", "Macho", dueno, raza);


        when(duenoRepository.findById(dueno.getId())).thenReturn(Optional.of(dueno));
        when(razaRepository.findById(raza.getId())).thenReturn(Optional.of(raza));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(savedMascotaEntity);

        // When
        MascotaDTO result = mascotaService.saveMascota(newMascotaDTO, dueno.getId(), raza.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getNombre()).isEqualTo("Firulais");
        assertThat(result.getIdDueno()).isEqualTo(dueno.getId());
        assertThat(result.getIdRaza()).isEqualTo(raza.getId());
        assertThat(result.getNombreDueno()).isEqualTo("Juan Perez");
        assertThat(result.getNombreRaza()).isEqualTo("Labrador");
        verify(duenoRepository, times(1)).findById(dueno.getId());
        verify(razaRepository, times(1)).findById(raza.getId());
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si el dueño no se encuentra al guardar")
    void saveMascota_shouldThrowException_whenDuenoNotFound() {
        // Given
        Long nonExistentDuenoId = 99L;
        MascotaDTO newMascotaDTO = new MascotaDTO(null, "Firulais", "CHIP789", "Macho", 1, LocalDate.of(2022, 3, 10),
                nonExistentDuenoId, raza.getId(), null, null);

        when(duenoRepository.findById(nonExistentDuenoId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.saveMascota(newMascotaDTO, nonExistentDuenoId, raza.getId());
        });

        assertThat(thrown.getMessage()).isEqualTo("Dueño no encontrado con ID: " + nonExistentDuenoId);
        verify(duenoRepository, times(1)).findById(nonExistentDuenoId);
        verify(razaRepository, never()).findById(anyLong());
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la raza no se encuentra al guardar")
    void saveMascota_shouldThrowException_whenRazaNotFound() {
        // Given
        Long nonExistentRazaId = 99L;
        MascotaDTO newMascotaDTO = new MascotaDTO(null, "Firulais", "CHIP789", "Macho", 1, LocalDate.of(2022, 3, 10),
                dueno.getId(), nonExistentRazaId, null, null);

        when(duenoRepository.findById(dueno.getId())).thenReturn(Optional.of(dueno));
        when(razaRepository.findById(nonExistentRazaId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.saveMascota(newMascotaDTO, dueno.getId(), nonExistentRazaId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Raza no encontrada con ID: " + nonExistentRazaId);
        verify(duenoRepository, times(1)).findById(dueno.getId());
        verify(razaRepository, times(1)).findById(nonExistentRazaId);
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // --- Tests para updateMascota(Long id, MascotaDTO mascotaDTO, Long duenoId, Long razaId) ---
    @Test
    @DisplayName("Debería actualizar una mascota existente exitosamente")
    void updateMascota_shouldUpdateExistingMascota() {
        // Given
        Long mascotaId = 1L;
        Dueno newDueno = new Dueno("44444444-4", "Ana", "Ruiz", "Calle B", "987654321", "ana.ruiz@example.com", true);
        newDueno.setId(4L);
        Raza newRaza = new Raza(2L, "Golden Retriever");

        MascotaDTO updatedMascotaDTO = new MascotaDTO(mascotaId, "Buddy Updated", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                newDueno.getId(), newRaza.getId(), null, null);

        // Simulamos que encuentra la mascota existente
        when(mascotaRepository.findById(mascotaId)).thenReturn(Optional.of(mascota1));
        // Simulamos que encuentra el nuevo dueño y la nueva raza
        when(duenoRepository.findById(newDueno.getId())).thenReturn(Optional.of(newDueno));
        when(razaRepository.findById(newRaza.getId())).thenReturn(Optional.of(newRaza));

        // Simulamos que guarda la mascota actualizada
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(
            new Mascota(mascotaId, "Buddy Updated", LocalDate.of(2021, 2, 1), 0, "CHIP_UPDATED", "Hembra", newDueno, newRaza));

        // When
        MascotaDTO result = mascotaService.updateMascota(mascotaId, updatedMascotaDTO, newDueno.getId(), newRaza.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mascotaId);
        assertThat(result.getNombre()).isEqualTo("Buddy Updated");
        assertThat(result.getChip()).isEqualTo("CHIP_UPDATED");
        assertThat(result.getGenero()).isEqualTo("Hembra");
        assertThat(result.getEstado()).isEqualTo(0);
        assertThat(result.getFechaNacimiento()).isEqualTo(LocalDate.of(2021, 2, 1));
        assertThat(result.getIdDueno()).isEqualTo(newDueno.getId());
        assertThat(result.getIdRaza()).isEqualTo(newRaza.getId());
        assertThat(result.getNombreDueno()).isEqualTo("Ana Ruiz");
        assertThat(result.getNombreRaza()).isEqualTo("Golden Retriever");

        verify(mascotaRepository, times(1)).findById(mascotaId);
        verify(duenoRepository, times(1)).findById(newDueno.getId());
        verify(razaRepository, times(1)).findById(newRaza.getId());
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la mascota no se encuentra al actualizar")
    void updateMascota_shouldThrowException_whenMascotaNotFound() {
        // Given
        Long nonExistentMascotaId = 99L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(nonExistentMascotaId, "Buddy Updated", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                dueno.getId(), raza.getId(), null, null);

        when(mascotaRepository.findById(nonExistentMascotaId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.updateMascota(nonExistentMascotaId, updatedMascotaDTO, dueno.getId(), raza.getId());
        });

        assertThat(thrown.getMessage()).isEqualTo("Mascota no encontrada con ID: " + nonExistentMascotaId);
        verify(mascotaRepository, times(1)).findById(nonExistentMascotaId);
        verify(duenoRepository, never()).findById(anyLong());
        verify(razaRepository, never()).findById(anyLong());
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si el nuevo dueño no se encuentra al actualizar")
    void updateMascota_shouldThrowException_whenNewDuenoNotFound() {
        // Given
        Long nonExistentDuenoId = 99L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(mascota1.getId(), "Buddy Updated", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                nonExistentDuenoId, raza.getId(), null, null);

        when(mascotaRepository.findById(mascota1.getId())).thenReturn(Optional.of(mascota1));
        when(duenoRepository.findById(nonExistentDuenoId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.updateMascota(mascota1.getId(), updatedMascotaDTO, nonExistentDuenoId, raza.getId());
        });

        assertThat(thrown.getMessage()).isEqualTo("Dueño no encontrado con ID: " + nonExistentDuenoId);
        verify(mascotaRepository, times(1)).findById(mascota1.getId());
        verify(duenoRepository, times(1)).findById(nonExistentDuenoId);
        verify(razaRepository, never()).findById(anyLong());
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la nueva raza no se encuentra al actualizar")
    void updateMascota_shouldThrowException_whenNewRazaNotFound() {
        // Given
        Long nonExistentRazaId = 99L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(mascota1.getId(), "Buddy Updated", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                dueno.getId(), nonExistentRazaId, null, null);

        when(mascotaRepository.findById(mascota1.getId())).thenReturn(Optional.of(mascota1));
        when(duenoRepository.findById(dueno.getId())).thenReturn(Optional.of(dueno));
        when(razaRepository.findById(nonExistentRazaId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.updateMascota(mascota1.getId(), updatedMascotaDTO, dueno.getId(), nonExistentRazaId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Raza no encontrada con ID: " + nonExistentRazaId);
        verify(mascotaRepository, times(1)).findById(mascota1.getId());
        verify(duenoRepository, times(1)).findById(dueno.getId());
        verify(razaRepository, times(1)).findById(nonExistentRazaId);
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // --- Tests para deleteMascota(Long id) ---
    @Test
    @DisplayName("Debería eliminar una mascota existente exitosamente")
    void deleteMascota_shouldDeleteExistingMascota() {
        // Given
        Long idToDelete = 1L;
        when(mascotaRepository.existsById(idToDelete)).thenReturn(true);
        doNothing().when(mascotaRepository).deleteById(idToDelete);

        // When
        mascotaService.deleteMascota(idToDelete);

        // Then
        verify(mascotaRepository, times(1)).existsById(idToDelete);
        verify(mascotaRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la mascota no se encuentra al eliminar")
    void deleteMascota_shouldThrowException_whenMascotaNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(mascotaRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.deleteMascota(nonExistentId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Mascota no encontrada con ID: " + nonExistentId);
        verify(mascotaRepository, times(1)).existsById(nonExistentId);
        verify(mascotaRepository, never()).deleteById(anyLong());
    }

    // --- Tests para findMascotasByNombre(String nombre) ---
    @Test
    @DisplayName("Debería retornar mascotas por nombre")
    void findMascotasByNombre_shouldReturnMascotas() {
        // Given
        Mascota mascota2 = new Mascota(2L, "Buddy", LocalDate.of(2021, 6, 1), 1, "CHIP999", "Macho", dueno, raza);
        List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2); // Ambos tienen nombre "Buddy"

        when(mascotaRepository.findByNombre("Buddy")).thenReturn(foundEntities);

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByNombre("Buddy");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getNombre()).isEqualTo("Buddy");
        assertThat(results.get(1).getNombre()).isEqualTo("Buddy");
        verify(mascotaRepository, times(1)).findByNombre("Buddy");
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay mascotas con ese nombre")
    void findMascotasByNombre_shouldReturnEmptyList_whenNotFound() {
        // Given
        when(mascotaRepository.findByNombre("NonExistent")).thenReturn(List.of());

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByNombre("NonExistent");

        // Then
        assertThat(results).isEmpty();
        verify(mascotaRepository, times(1)).findByNombre("NonExistent");
    }

    // --- Tests para findMascotasByDuenoId(Long duenoId) ---
    @Test
    @DisplayName("Debería retornar mascotas por ID de dueño")
    void findMascotasByDuenoId_shouldReturnMascotas() {
        // Given
        Mascota mascota2 = new Mascota(2L, "Lucy", LocalDate.of(2019, 5, 20), 1, "CHIP456", "Hembra", dueno, raza);
        List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2);

        when(duenoRepository.existsById(dueno.getId())).thenReturn(true); // Verificar que el dueño exista
        when(mascotaRepository.findByDuenoId(dueno.getId())).thenReturn(foundEntities);

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByDuenoId(dueno.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getIdDueno()).isEqualTo(dueno.getId());
        assertThat(results.get(1).getIdDueno()).isEqualTo(dueno.getId());
        verify(duenoRepository, times(1)).existsById(dueno.getId());
        verify(mascotaRepository, times(1)).findByDuenoId(dueno.getId());
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si el dueño no existe al buscar por duenoId")
    void findMascotasByDuenoId_shouldThrowException_whenDuenoNotFound() {
        // Given
        Long nonExistentDuenoId = 99L;
        when(duenoRepository.existsById(nonExistentDuenoId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.findMascotasByDuenoId(nonExistentDuenoId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Dueño no encontrado con ID: " + nonExistentDuenoId);
        verify(duenoRepository, times(1)).existsById(nonExistentDuenoId);
        verify(mascotaRepository, never()).findByDuenoId(anyLong());
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay mascotas para ese duenoId")
    void findMascotasByDuenoId_shouldReturnEmptyList_whenNoMascotasForDueno() {
        // Given
        when(duenoRepository.existsById(dueno.getId())).thenReturn(true);
        when(mascotaRepository.findByDuenoId(dueno.getId())).thenReturn(List.of());

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByDuenoId(dueno.getId());

        // Then
        assertThat(results).isEmpty();
        verify(duenoRepository, times(1)).existsById(dueno.getId());
        verify(mascotaRepository, times(1)).findByDuenoId(dueno.getId());
    }


    // --- Tests para findMascotasByRazaId(Long razaId) ---
    @Test
    @DisplayName("Debería retornar mascotas por ID de raza")
    void findMascotasByRazaId_shouldReturnMascotas() {
        // Given
        Mascota mascota2 = new Mascota(2L, "Lucy", LocalDate.of(2019, 5, 20), 1, "CHIP456", "Hembra", dueno, raza); // Misma raza
        List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2);

        when(razaRepository.existsById(raza.getId())).thenReturn(true);
        when(mascotaRepository.findByRazaId(raza.getId())).thenReturn(foundEntities);

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByRazaId(raza.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getIdRaza()).isEqualTo(raza.getId());
        assertThat(results.get(1).getIdRaza()).isEqualTo(raza.getId());
        verify(razaRepository, times(1)).existsById(raza.getId());
        verify(mascotaRepository, times(1)).findByRazaId(raza.getId());
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la raza no existe al buscar por razaId")
    void findMascotasByRazaId_shouldThrowException_whenRazaNotFound() {
        // Given
        Long nonExistentRazaId = 99L;
        when(razaRepository.existsById(nonExistentRazaId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.findMascotasByRazaId(nonExistentRazaId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Raza no encontrada con ID: " + nonExistentRazaId);
        verify(razaRepository, times(1)).existsById(nonExistentRazaId);
        verify(mascotaRepository, never()).findByRazaId(anyLong());
    }

    // --- Tests para findMascotasByGenero(String genero) ---
    @Test
    @DisplayName("Debería retornar mascotas por género")
    void findMascotasByGenero_shouldReturnMascotas() {
        // Given
        Mascota mascota2 = new Mascota(2L, "Panda", LocalDate.of(2021, 6, 1), 1, "CHIP777", "Macho", dueno, raza);
        List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2); // Ambos son "Macho"

        when(mascotaRepository.findByGenero("Macho")).thenReturn(foundEntities);

        // When
        List<MascotaDTO> results = mascotaService.findMascotasByGenero("Macho");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getGenero()).isEqualTo("Macho");
        assertThat(results.get(1).getGenero()).isEqualTo("Macho");
        verify(mascotaRepository, times(1)).findByGenero("Macho");
    }

    // --- Tests para findMascotasBornAfter(LocalDate date) ---
    @Test
    @DisplayName("Debería retornar mascotas nacidas después de una fecha")
    void findMascotasBornAfter_shouldReturnMascotas() {
        // Given
        LocalDate searchDate = LocalDate.of(2019, 12, 31);
        Mascota mascota2 = new Mascota(2L, "Lucy", LocalDate.of(2020, 2, 1), 1, "CHIP456", "Hembra", dueno, raza);
        List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2); // Buddy (Jan 15, 2020), Lucy (Feb 1, 2020)

        when(mascotaRepository.findByFechaNacimientoAfter(searchDate)).thenReturn(foundEntities);

        // When
        List<MascotaDTO> results = mascotaService.findMascotasBornAfter(searchDate);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getNombre()).isEqualTo("Buddy");
        assertThat(results.get(1).getNombre()).isEqualTo("Lucy");
        verify(mascotaRepository, times(1)).findByFechaNacimientoAfter(searchDate);
    }

    // --- Tests para findMascotasBornBefore(LocalDate date) ---
    @Test
    @DisplayName("Debería retornar mascotas nacidas antes de una fecha")
    void findMascotasBornBefore_shouldReturnMascotas() {
        // Given
        LocalDate searchDate = LocalDate.of(2020, 2, 1);
        Mascota mascota2 = new Mascota(2L, "Lucy", LocalDate.of(2019, 5, 20), 1, "CHIP456", "Hembra", dueno, raza);
        //List<Mascota> foundEntities = Arrays.asList(mascota1, mascota2); // Buddy (Jan 15, 2020), Lucy (May 20, 2019)
        // Solo Lucy debería cumplir la condición 'before 2020-02-01'
        when(mascotaRepository.findByFechaNacimientoBefore(searchDate)).thenReturn(List.of(mascota2));

        // When
        List<MascotaDTO> results = mascotaService.findMascotasBornBefore(searchDate);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getNombre()).isEqualTo("Lucy");
        verify(mascotaRepository, times(1)).findByFechaNacimientoBefore(searchDate);
    }
}
