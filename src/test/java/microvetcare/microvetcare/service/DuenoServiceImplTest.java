package microvetcare.microvetcare.service;

import microvetcare.microvetcare.dueno.DTO.DuenoDTO;
import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.dueno.repository.DuenoRepository;
import microvetcare.microvetcare.dueno.service.DuenoServiceImpl;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DuenoServiceImplTest {

    @Mock
    private DuenoRepository duenoRepository;

    @InjectMocks
    private DuenoServiceImpl duenoService;

    private Dueno dueno1;
    private DuenoDTO duenoDTO1;

    @BeforeEach
    void setUp() {
        dueno1 = new Dueno("11111111-1", "Juan", "Perez", "Calle Falsa 123", "91111111111", "juan.perez@example.com", true);
        dueno1.setId(1L);
        duenoDTO1 = new DuenoDTO(dueno1); // Usando el constructor del DTO
    }

    // --- Tests para findAllDuenos() ---
    @Test
    @DisplayName("Debería retornar una lista de todos los dueños")
    void findAllDuenos_shouldReturnAllDuenos() {
        // Given
        Dueno dueno2 = new Dueno("22222222-2", "Maria", "Gonzalez", "Av. Siempre Viva 456", "92222222222", "maria.g@example.com", true);
        dueno2.setId(2L);
        List<Dueno> duenos = Arrays.asList(dueno1, dueno2);

        when(duenoRepository.findAll()).thenReturn(duenos);

        // When
        List<DuenoDTO> foundDuenos = duenoService.findAllDuenos();

        // Then
        assertThat(foundDuenos).isNotNull();
        assertThat(foundDuenos.size()).isEqualTo(2);
        assertThat(foundDuenos.get(0).getNombre()).isEqualTo("Juan");
        assertThat(foundDuenos.get(1).getRut()).isEqualTo("22222222-2");
        verify(duenoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay dueños")
    void findAllDuenos_shouldReturnEmptyList_whenNoDuenos() {
        // Given
        when(duenoRepository.findAll()).thenReturn(List.of());

        // When
        List<DuenoDTO> foundDuenos = duenoService.findAllDuenos();

        // Then
        assertThat(foundDuenos).isNotNull();
        assertThat(foundDuenos).isEmpty();
        verify(duenoRepository, times(1)).findAll();
    }

    // --- Tests para findDuenoById(Long id) ---
    @Test
    @DisplayName("Debería retornar un dueño por ID cuando existe")
    void findDuenoById_shouldReturnDueno_whenExists() {
        // Given
        when(duenoRepository.findById(dueno1.getId())).thenReturn(Optional.of(dueno1));

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoById(dueno1.getId());

        // Then
        assertThat(foundDueno).isPresent();
        assertThat(foundDueno.get().getId()).isEqualTo(dueno1.getId());
        assertThat(foundDueno.get().getNombre()).isEqualTo(dueno1.getNombre());
        verify(duenoRepository, times(1)).findById(dueno1.getId());
    }

    @Test
    @DisplayName("Debería retornar Optional vacío si el dueño no se encuentra por ID")
    void findDuenoById_shouldReturnEmptyOptional_whenNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(duenoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoById(nonExistentId);

        // Then
        assertThat(foundDueno).isNotPresent();
        verify(duenoRepository, times(1)).findById(nonExistentId);
    }

    // --- Tests para saveDueno(DuenoDTO duenoDTO) ---
    @Test
    @DisplayName("Debería guardar un nuevo dueño exitosamente")
    void saveDueno_shouldSaveNewDueno() {
        // Given
        DuenoDTO newDuenoDTO = new DuenoDTO(null, "33333333-3", "Carlos", "Silva", "Calle de Prueba 789", "93333333333", "carlos.s@example.com", true);
        Dueno savedDuenoEntity = new Dueno("33333333-3", "Carlos", "Silva", "Calle de Prueba 789", "93333333333", "carlos.s@example.com", true);
        savedDuenoEntity.setId(3L); // Simular que el repositorio asigna un ID

        when(duenoRepository.existsByRut(newDuenoDTO.getRut())).thenReturn(false);
        when(duenoRepository.existsByEmail(newDuenoDTO.getEmail())).thenReturn(false);
        when(duenoRepository.save(any(Dueno.class))).thenReturn(savedDuenoEntity);

        // When
        DuenoDTO result = duenoService.saveDueno(newDuenoDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getNombre()).isEqualTo("Carlos");
        verify(duenoRepository, times(1)).existsByRut(newDuenoDTO.getRut());
        verify(duenoRepository, times(1)).existsByEmail(newDuenoDTO.getEmail());
        verify(duenoRepository, times(1)).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el RUT ya existe al guardar")
    void saveDueno_shouldThrowException_whenRutExists() {
        // Given
        DuenoDTO newDuenoDTO = new DuenoDTO(null, "11111111-1", "Pedro", "Gomez", "Otra Calle 111", "94444444444", "pedro.g@example.com", true);

        // Solo se llama existsByRut después de la validación del ID nulo
        when(duenoRepository.existsByRut(newDuenoDTO.getRut())).thenReturn(true);

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            duenoService.saveDueno(newDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("Ya existe un dueño con el RUT: " + newDuenoDTO.getRut());
        verify(duenoRepository, times(1)).existsByRut(newDuenoDTO.getRut());
        verify(duenoRepository, never()).existsByEmail(anyString()); 
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el Email ya existe al guardar")
    void saveDueno_shouldThrowException_whenEmailExists() {
        // Given
        DuenoDTO newDuenoDTO = new DuenoDTO(null, "44444444-4", "Ana", "Lopez", "Calle del Sol 222", "95555555555", "juan.perez@example.com", true); // Email duplicado

        when(duenoRepository.existsByRut(newDuenoDTO.getRut())).thenReturn(false);
        when(duenoRepository.existsByEmail(newDuenoDTO.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            duenoService.saveDueno(newDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("Ya existe un dueño con el Email: " + newDuenoDTO.getEmail());
        verify(duenoRepository, times(1)).existsByRut(newDuenoDTO.getRut());
        verify(duenoRepository, times(1)).existsByEmail(newDuenoDTO.getEmail());
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el ID no es nulo al guardar")
    void saveDueno_shouldThrowException_whenIdIsNotNull() {
        // Given
        DuenoDTO newDuenoDTO = new DuenoDTO(1L, "55555555-5", "Laura", "Diaz", "Calle Luna 333", "96666666666", "laura.d@example.com", true);

        // No stubbing for duenoRepository as it shouldn't be called after initial ID check
        
        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            duenoService.saveDueno(newDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("El ID debe ser nulo para un nuevo dueño.");
        // --- CORRECCIÓN: existsByRut NO DEBERÍA SER LLAMADO si el ID es nulo ---
        verify(duenoRepository, never()).existsByRut(anyString());
        verify(duenoRepository, never()).existsByEmail(anyString());
        verify(duenoRepository, never()).save(any(Dueno.class));
    }


    // --- Tests para updateDueno(Long id, DuenoDTO duenoDTO) ---
    @Test
    @DisplayName("Debería actualizar un dueño existente exitosamente")
    void updateDueno_shouldUpdateExistingDueno() {
        // Given
        // Mantener el mismo RUT y Email para este test para que no se llamen los findByRut/Email
        DuenoDTO updatedDuenoDTO = new DuenoDTO(dueno1.getId(), "11111111-1", "Juan Actualizado", "Perez", "Nueva Direccion 456", "91111111111", "juan.perez@example.com", false);
        Dueno existingDueno = new Dueno("11111111-1", "Juan", "Perez", "Calle Falsa 123", "91111111111", "juan.perez@example.com", true);
        existingDueno.setId(1L);

        when(duenoRepository.findById(dueno1.getId())).thenReturn(Optional.of(existingDueno));
        // No stubbing for findByRut/findByEmail because RUT and Email in DTO are same as existing dueno

        when(duenoRepository.save(any(Dueno.class))).thenReturn(existingDueno); // Simula la actualización

        // When
        DuenoDTO result = duenoService.updateDueno(dueno1.getId(), updatedDuenoDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(result.getDireccion()).isEqualTo("Nueva Direccion 456");
        assertThat(result.getEstado()).isFalse();
        verify(duenoRepository, times(1)).findById(dueno1.getId());
        // --- CORRECCIÓN: No verificar findByRut y findByEmail aquí si los valores no cambian ---
        verify(duenoRepository, never()).findByRut(anyString());
        verify(duenoRepository, never()).findByEmail(anyString());
        verify(duenoRepository, times(1)).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si el dueño no se encuentra al actualizar")
    void updateDueno_shouldThrowException_whenNotFound() {
        // Given
        Long nonExistentId = 99L;
        DuenoDTO updatedDuenoDTO = new DuenoDTO(nonExistentId, "66666666-6", "NotFound", "NotFound", "NotFound", "97777777777", "notfound@example.com", true);

        when(duenoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            duenoService.updateDueno(nonExistentId, updatedDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("Dueño no encontrado con ID: " + nonExistentId);
        verify(duenoRepository, times(1)).findById(nonExistentId);
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el nuevo RUT ya existe para otro dueño al actualizar")
    void updateDueno_shouldThrowException_whenNewRutExistsForOtherDueno() {
        // Given
        // Importante: El RUT del updatedDuenoDTO debe ser diferente del dueno1 para que se llame a findByRut
        DuenoDTO updatedDuenoDTO = new DuenoDTO(dueno1.getId(), "22222222-2", "Juan", "Perez", "Calle Falsa 123", "91111111111", "juan.perez@example.com", true);
        Dueno dueno2 = new Dueno("22222222-2", "Maria", "Gonzalez", "Av. Siempre Viva 456", "92222222222", "maria.g@example.com", true);
        dueno2.setId(2L); // ID diferente al de dueno1

        when(duenoRepository.findById(dueno1.getId())).thenReturn(Optional.of(dueno1));
        when(duenoRepository.findByRut(updatedDuenoDTO.getRut())).thenReturn(Optional.of(dueno2)); // Simula que el RUT ya existe

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            duenoService.updateDueno(dueno1.getId(), updatedDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("El RUT " + updatedDuenoDTO.getRut() + " ya está registrado para otro dueño.");
        verify(duenoRepository, times(1)).findById(dueno1.getId());
        verify(duenoRepository, times(1)).findByRut(updatedDuenoDTO.getRut());
        verify(duenoRepository, never()).findByEmail(anyString()); // Email no cambia, so no findByEmail
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el nuevo Email ya existe para otro dueño al actualizar")
    void updateDueno_shouldThrowException_whenNewEmailExistsForOtherDueno() {
        // Given
        // Importante: El Email del updatedDuenoDTO debe ser diferente del dueno1 para que se llame a findByEmail
        DuenoDTO updatedDuenoDTO = new DuenoDTO(dueno1.getId(), "11111111-1", "Juan", "Perez", "Calle Falsa 123", "91111111111", "maria.g@example.com", true); // Email de dueno2
        Dueno dueno2 = new Dueno("22222222-2", "Maria", "Gonzalez", "Av. Siempre Viva 456", "92222222222", "maria.g@example.com", true);
        dueno2.setId(2L);

        when(duenoRepository.findById(dueno1.getId())).thenReturn(Optional.of(dueno1));
        // Aquí el RUT no cambia, por lo que findByRut no se llama.
        when(duenoRepository.findByEmail(updatedDuenoDTO.getEmail())).thenReturn(Optional.of(dueno2)); // Email ya existe

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            duenoService.updateDueno(dueno1.getId(), updatedDuenoDTO);
        });

        assertThat(thrown.getMessage()).isEqualTo("El Email " + updatedDuenoDTO.getEmail() + " ya está registrado para otro dueño.");
        verify(duenoRepository, times(1)).findById(dueno1.getId());
        verify(duenoRepository, never()).findByRut(anyString()); // Se espera que findByRut no se llame si el RUT no cambia
        verify(duenoRepository, times(1)).findByEmail(updatedDuenoDTO.getEmail());
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    // --- Tests para deleteDueno(Long id) ---
    @Test
    @DisplayName("Debería eliminar un dueño existente exitosamente")
    void deleteDueno_shouldDeleteExistingDueno() {
        // Given
        Long idToDelete = 1L;
        when(duenoRepository.existsById(idToDelete)).thenReturn(true);
        doNothing().when(duenoRepository).deleteById(idToDelete);

        // When
        duenoService.deleteDueno(idToDelete);

        // Then
        verify(duenoRepository, times(1)).existsById(idToDelete);
        verify(duenoRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si el dueño no se encuentra al eliminar")
    void deleteDueno_shouldThrowException_whenNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(duenoRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            duenoService.deleteDueno(nonExistentId);
        });

        assertThat(thrown.getMessage()).isEqualTo("Dueño no encontrado con ID: " + nonExistentId);
        verify(duenoRepository, times(1)).existsById(nonExistentId);
        verify(duenoRepository, never()).deleteById(anyLong());
    }

    // --- Tests para findDuenoByRut(String rut) ---
    @Test
    @DisplayName("Debería retornar un dueño por RUT cuando existe")
    void findDuenoByRut_shouldReturnDueno_whenExists() {
        // Given
        String rut = "11111111-1";
        when(duenoRepository.findByRut(rut)).thenReturn(Optional.of(dueno1));

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoByRut(rut);

        // Then
        assertThat(foundDueno).isPresent();
        assertThat(foundDueno.get().getRut()).isEqualTo(rut);
        verify(duenoRepository, times(1)).findByRut(rut);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío si el dueño no se encuentra por RUT")
    void findDuenoByRut_shouldReturnEmptyOptional_whenNotFound() {
        // Given
        String nonExistentRut = "00000000-0";
        when(duenoRepository.findByRut(nonExistentRut)).thenReturn(Optional.empty());

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoByRut(nonExistentRut);

        // Then
        assertThat(foundDueno).isNotPresent();
        verify(duenoRepository, times(1)).findByRut(nonExistentRut);
    }

    // --- Tests para findDuenoByEmail(String email) ---
    @Test
    @DisplayName("Debería retornar un dueño por Email cuando existe")
    void findDuenoByEmail_shouldReturnDueno_whenExists() {
        // Given
        String email = "juan.perez@example.com";
        when(duenoRepository.findByEmail(email)).thenReturn(Optional.of(dueno1));

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoByEmail(email);

        // Then
        assertThat(foundDueno).isPresent();
        assertThat(foundDueno.get().getEmail()).isEqualTo(email);
        verify(duenoRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío si el dueño no se encuentra por Email")
    void findDuenoByEmail_shouldReturnEmptyOptional_whenNotFound() {
        // Given
        String nonExistentEmail = "noexist@example.com";
        when(duenoRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When
        Optional<DuenoDTO> foundDueno = duenoService.findDuenoByEmail(nonExistentEmail);

        // Then
        assertThat(foundDueno).isNotPresent();
        verify(duenoRepository, times(1)).findByEmail(nonExistentEmail);
    }
    
}
