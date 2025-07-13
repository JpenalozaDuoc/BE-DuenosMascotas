package microvetcare.microvetcare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.especie.repository.EspecieRepository;
import microvetcare.microvetcare.especie.service.EspecieServiceImpl;
import microvetcare.microvetcare.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class) // Habilita Mockito para JUnit 5
public class EspecieServiceImplTest {

    @Mock // Crea un mock del repositorio. Cuando EspecieServiceImpl pida un EspecieRepository, se le dará esta simulación.
    private EspecieRepository especieRepository;

    @InjectMocks // Inyecta los mocks creados (como especieRepository) en esta instancia de EspecieServiceImpl.
    private EspecieServiceImpl especieService;

    // Métodos auxiliares para crear objetos Especie y EspecieDTO de prueba
    private Especie createTestEspecie(Long id, String nombreEspecie, String nombre, Integer estado) {
        Especie especie = new Especie(id, nombreEspecie, nombre, estado);
        // Aunque no es estrictamente necesario para la entidad, si tuviera relaciones @ManyToOne y necesitaras mockearlas, lo harías aquí.
        return especie;
    }

    private EspecieDTO createTestEspecieDTO(Long id, String nombreEspecie, String nombre, Integer estado) {
        return new EspecieDTO(id, nombreEspecie, nombre, estado);
    }

    @BeforeEach // Este método se ejecuta antes de cada prueba (@Test)
    void setUp() {
        // Inicialización de mocks y objetos si es necesario para múltiples pruebas.
        // En este caso, @Mock y @InjectMocks ya hacen la mayor parte del trabajo.
    }

    // --- Pruebas para findAllEspecies() ---
    @Test
    @DisplayName("Debería retornar una lista de todas las especies")
    void findAllEspecies_shouldReturnListOfEspecies() {
        // Given (Dado)
        Especie especie1 = createTestEspecie(1L, "Canis familiaris", "Perro", 1);
        Especie especie2 = createTestEspecie(2L, "Felis catus", "Gato", 1);
        List<Especie> especies = Arrays.asList(especie1, especie2);

        // Cuando se llama a especieRepository.findAll(), entonces devuelve nuestra lista de prueba.
        when(especieRepository.findAll()).thenReturn(especies);

        // When (Cuando)
        List<EspecieDTO> result = especieService.findAllEspecies();

        // Then (Entonces)
        assertNotNull(result); // Asegura que el resultado no es nulo
        assertEquals(2, result.size()); // Asegura que hay 2 especies en la lista
        assertEquals("Perro", result.get(0).getNombre()); // Verifica el nombre de la primera especie
        assertEquals("Gato", result.get(1).getNombre()); // Verifica el nombre de la segunda especie

        // Verifica que se llamó a especieRepository.findAll() exactamente una vez
        verify(especieRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay especies")
    void findAllEspecies_shouldReturnEmptyListIfNoEspecies() {
        // Given
        when(especieRepository.findAll()).thenReturn(List.of()); // Simula que el repositorio devuelve una lista vacía

        // When
        List<EspecieDTO> result = especieService.findAllEspecies();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Asegura que la lista está vacía
        verify(especieRepository, times(1)).findAll();
    }

    // --- Pruebas para findEspecieById() ---
    @Test
    @DisplayName("Debería encontrar una especie por ID")
    void findEspecieById_shouldReturnEspecieDTO() {
        // Given
        Long id = 1L;
        Especie especie = createTestEspecie(id, "Canis familiaris", "Perro", 1);
        when(especieRepository.findById(id)).thenReturn(Optional.of(especie));

        // When
        Optional<EspecieDTO> result = especieService.findEspecieById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Perro", result.get().getNombre());
        assertEquals(id, result.get().getId());
        verify(especieRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería retornar Optional.empty si la especie no se encuentra por ID")
    void findEspecieById_shouldReturnEmptyOptionalIfNotFound() {
        // Given
        Long id = 99L;
        when(especieRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<EspecieDTO> result = especieService.findEspecieById(id);

        // Then
        assertFalse(result.isPresent());
        verify(especieRepository, times(1)).findById(id);
    }

    // --- Pruebas para saveEspecie() ---
    @Test
    @DisplayName("Debería guardar una nueva especie")
    void saveEspecie_shouldSaveNewEspecie() {
        // Given
        EspecieDTO newEspecieDTO = createTestEspecieDTO(null, "Especie Nueva", "Nombre Nuevo", 1);
        Especie savedEspecie = createTestEspecie(1L, "Especie Nueva", "Nombre Nuevo", 1); // Simula la especie con ID asignado

        when(especieRepository.existsByNombre(newEspecieDTO.getNombre())).thenReturn(false); // No existe el nombre
        when(especieRepository.save(any(Especie.class))).thenReturn(savedEspecie); // Simula el guardado

        // When
        EspecieDTO result = especieService.saveEspecie(newEspecieDTO);

        // Then
        assertNotNull(result.getId()); // Asegura que se asignó un ID
        assertEquals("Nombre Nuevo", result.getNombre());
        verify(especieRepository, times(1)).existsByNombre(newEspecieDTO.getNombre());
        verify(especieRepository, times(1)).save(any(Especie.class)); // Verifica que se llamó a save
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el nombre de la especie ya existe al guardar")
    void saveEspecie_shouldThrowExceptionIfNombreExists() {
        // Given
        EspecieDTO especieDTO = createTestEspecieDTO(null, "Especie Existente", "Nombre Existente", 1);
        when(especieRepository.existsByNombre(especieDTO.getNombre())).thenReturn(true); // Simula que el nombre ya existe

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> especieService.saveEspecie(especieDTO)
        );
        assertEquals("Ya existe una especie con el nombre: " + especieDTO.getNombre(), thrown.getMessage());
        verify(especieRepository, times(1)).existsByNombre(especieDTO.getNombre());
        verify(especieRepository, never()).save(any(Especie.class)); // Asegura que save NO se llamó
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el ID no es nulo al guardar")
    void saveEspecie_shouldThrowExceptionIfIdIsNotNull() {
        // Given
        EspecieDTO especieDTO = createTestEspecieDTO(1L, "Especie con ID", "Nombre con ID", 1); // ID no nulo
        when(especieRepository.existsByNombre(especieDTO.getNombre())).thenReturn(false); // No existe el nombre

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> especieService.saveEspecie(especieDTO)
        );
        assertEquals("El ID debe ser nulo para una nueva especie.", thrown.getMessage());
        verify(especieRepository, times(1)).existsByNombre(especieDTO.getNombre());
        verify(especieRepository, never()).save(any(Especie.class)); // Asegura que save NO se llamó
    }
    
    // --- Pruebas para updateEspecie() ---
    @Test
    @DisplayName("Debería actualizar una especie existente")
    void updateEspecie_shouldUpdateExistingEspecie() {
        // Given
        Long id = 1L;
        Especie existingEspecie = createTestEspecie(id, "Especie Antigua", "Nombre Antiguo", 1);
        EspecieDTO updatedEspecieDTO = createTestEspecieDTO(id, "Especie Nueva", "Nombre Nuevo", 0); // NombreEspecie y Nombre cambiados, estado a inactivo

        when(especieRepository.findById(id)).thenReturn(Optional.of(existingEspecie));
        when(especieRepository.existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie())).thenReturn(false);
        when(especieRepository.existsByNombre(updatedEspecieDTO.getNombre())).thenReturn(false);
        when(especieRepository.save(any(Especie.class))).thenReturn(existingEspecie); // Simula que el guardado devuelve la misma instancia actualizada

        // When
        EspecieDTO result = especieService.updateEspecie(id, updatedEspecieDTO);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Especie Nueva", result.getNombreEspecie());
        assertEquals("Nombre Nuevo", result.getNombre());
        assertEquals(0, result.getEstado());

        verify(especieRepository, times(1)).findById(id);
        verify(especieRepository, times(1)).existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie());
        verify(especieRepository, times(1)).existsByNombre(updatedEspecieDTO.getNombre());
        verify(especieRepository, times(1)).save(any(Especie.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la especie no se encuentra al actualizar")
    void updateEspecie_shouldThrowResourceNotFoundExceptionIfNotFound() {
        // Given
        Long id = 99L;
        EspecieDTO especieDTO = createTestEspecieDTO(id, "Cualquiera", "Cualquiera", 1);
        when(especieRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(
            ResourceNotFoundException.class,
            () -> especieService.updateEspecie(id, especieDTO)
        );
        assertEquals("Especie no encontrada con ID: " + id, thrown.getMessage());
        verify(especieRepository, times(1)).findById(id);
        verify(especieRepository, never()).existsByNombreEspecie(anyString());
        verify(especieRepository, never()).existsByNombre(anyString());
        verify(especieRepository, never()).save(any(Especie.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si nombreEspecie ya existe al actualizar")
    void updateEspecie_shouldThrowExceptionIfNombreEspecieExistsOnUpdate() { // <--- Nombre del método corregido para claridad
        // Given
        Long id = 1L;
        Especie existingEspecie = createTestEspecie(id, "Especie Original", "Nombre Original", 1);
        EspecieDTO updatedEspecieDTO = createTestEspecieDTO(id, "Especie Duplicada", "Nombre Original", 1); // Queremos que 'nombreEspecie' sea el duplicado

        when(especieRepository.findById(id)).thenReturn(Optional.of(existingEspecie));
        // Simula que el nuevo 'nombreEspecie' ya existe y es diferente al original
        when(especieRepository.existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie())).thenReturn(true);

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> especieService.updateEspecie(id, updatedEspecieDTO)
        );
        assertEquals("El nombreEspecie '" + updatedEspecieDTO.getNombreEspecie() + "' ya está registrado para otra especie.", thrown.getMessage());
        
        verify(especieRepository, times(1)).findById(id);
        // Verificamos que se llamó a existsByNombreEspecie porque se intentó cambiarlo y era duplicado
        verify(especieRepository, times(1)).existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie());
        // Aseguramos que NO se llamó a existsByNombre (porque solo cambiamos nombreEspecie)
        verify(especieRepository, never()).existsByNombre(anyString());
        verify(especieRepository, never()).save(any(Especie.class)); // Asegura que save NO se llamó
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si nombre ya existe al actualizar")
    void updateEspecie_shouldThrowExceptionIfNombreExistsOnUpdate() { // <--- Este es para el campo 'nombre'
        // Given
        Long id = 1L;
        Especie existingEspecie = createTestEspecie(id, "Especie Unica", "Nombre Unico", 1);
        EspecieDTO updatedEspecieDTO = createTestEspecieDTO(id, "Especie Unica", "Nombre Existente", 1); // Nombre que ya existe

        when(especieRepository.findById(id)).thenReturn(Optional.of(existingEspecie));
        // Simula que el nombreEspecie no cambia o no causa conflicto (para no entrar en el primer IF del servicio)
        //when(especieRepository.existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie())).thenReturn(false);
        // Simula que el 'nombre' ya existe
        when(especieRepository.existsByNombre(updatedEspecieDTO.getNombre())).thenReturn(true); 

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> especieService.updateEspecie(id, updatedEspecieDTO)
        );
        assertEquals("El nombre '" + updatedEspecieDTO.getNombre() + "' ya está registrado para otra especie.", thrown.getMessage());
        
        verify(especieRepository, times(1)).findById(id);
        // Se debería verificar existsByNombreEspecie una vez para el 'if' del servicio
        //verify(especieRepository, times(1)).existsByNombreEspecie(updatedEspecieDTO.getNombreEspecie());
        verify(especieRepository, never()).existsByNombreEspecie(anyString());
        verify(especieRepository, times(1)).existsByNombre(updatedEspecieDTO.getNombre());
        verify(especieRepository, never()).save(any(Especie.class));
    }

    // --- Pruebas para deleteEspecie() ---
    @Test
    @DisplayName("Debería eliminar una especie existente")
    void deleteEspecie_shouldDeleteExistingEspecie() {
        // Given
        Long id = 1L;
        when(especieRepository.existsById(id)).thenReturn(true); // Simula que la especie existe

        // When
        especieService.deleteEspecie(id);

        // Then
        verify(especieRepository, times(1)).existsById(id);
        verify(especieRepository, times(1)).deleteById(id); // Verifica que deleteById fue llamado
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si la especie no se encuentra al eliminar")
    void deleteEspecie_shouldThrowResourceNotFoundExceptionIfNotFound() {
        // Given
        Long id = 99L;
        when(especieRepository.existsById(id)).thenReturn(false); // Simula que la especie NO existe

        // When & Then
        ResourceNotFoundException thrown = assertThrows(
            ResourceNotFoundException.class,
            () -> especieService.deleteEspecie(id)
        );
        assertEquals("Especie no encontrada con ID: " + id, thrown.getMessage());
        verify(especieRepository, times(1)).existsById(id);
        verify(especieRepository, never()).deleteById(anyLong()); // Asegura que deleteById NO se llamó
    }

    // --- Pruebas para findEspecieByNombre() ---
    @Test
    @DisplayName("Debería encontrar una especie por nombre")
    void findEspecieByNombre_shouldReturnEspecieDTO() {
        // Given
        String nombre = "Perro";
        Especie especie = createTestEspecie(1L, "Canis familiaris", nombre, 1);
        when(especieRepository.findByNombre(nombre)).thenReturn(Optional.of(especie));

        // When
        Optional<EspecieDTO> result = especieService.findEspecieByNombre(nombre);

        // Then
        assertTrue(result.isPresent());
        assertEquals(nombre, result.get().getNombre());
        verify(especieRepository, times(1)).findByNombre(nombre);
    }

    @Test
    @DisplayName("Debería retornar Optional.empty si la especie no se encuentra por nombre")
    void findEspecieByNombre_shouldReturnEmptyOptionalIfNotFound() {
        // Given
        String nombre = "NombreInexistente";
        when(especieRepository.findByNombre(nombre)).thenReturn(Optional.empty());

        // When
        Optional<EspecieDTO> result = especieService.findEspecieByNombre(nombre);

        // Then
        assertFalse(result.isPresent());
        verify(especieRepository, times(1)).findByNombre(nombre);
    }

    // --- Pruebas para existsByNombre() ---
    @Test
    @DisplayName("Debería retornar true si la especie existe por nombre")
    void existsByNombre_shouldReturnTrueIfEspecieExists() {
        // Given
        String nombre = "Perro";
        when(especieRepository.existsByNombre(nombre)).thenReturn(true);

        // When
        boolean result = especieService.existsByNombre(nombre);

        // Then
        assertTrue(result);
        verify(especieRepository, times(1)).existsByNombre(nombre);
    }

    @Test
    @DisplayName("Debería retornar false si la especie no existe por nombre")
    void existsByNombre_shouldReturnFalseIfEspecieDoesNotExist() {
        // Given
        String nombre = "Gato";
        when(especieRepository.existsByNombre(nombre)).thenReturn(false);

        // When
        boolean result = especieService.existsByNombre(nombre);

        // Then
        assertFalse(result);
        verify(especieRepository, times(1)).existsByNombre(nombre);
    }
}
