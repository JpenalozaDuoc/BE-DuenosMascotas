package microvetcare.microvetcare.service;

import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.especie.repository.EspecieRepository;
import microvetcare.microvetcare.raza.DTO.RazaDTO;
import microvetcare.microvetcare.raza.entity.Raza;
import microvetcare.microvetcare.raza.repository.RazaRepository;
import microvetcare.microvetcare.raza.service.RazaServiceImpl;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RazaServiceImplTest {

    @Mock
    private RazaRepository razaRepository;

    @Mock
    private EspecieRepository especieRepository; // Necesitamos mockear el EspecieRepository también

    @InjectMocks
    private RazaServiceImpl razaService;

    // Objetos de prueba que usaremos en varios tests
    private Especie testEspecie;
    private Raza testRaza;
    private RazaDTO testRazaDTO;

    // Métodos auxiliares para crear objetos de prueba
    private Especie createTestEspecie(Long id, String nombreEspecie, String nombre, Integer estado) {
        return new Especie(id, nombreEspecie, nombre, estado);
    }

    private Raza createTestRaza(Long id, String nombre, String estado, Especie especie) {
        Raza raza = new Raza(nombre, estado, especie);
        raza.setId(id); // Setear el ID manualmente para el test
        return raza;
    }

    private RazaDTO createTestRazaDTO(Long id, String nombre, String estado, Long especieId) {
        RazaDTO dto = new RazaDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setEstado(estado);
        dto.setEspecieId(especieId);
        // En tu RazaDTO, parece que no tienes constructores que tomen estos parámetros directamente.
        // Asegúrate de que RazaDTO tenga getters/setters o un constructor adecuado.
        // Asumiendo que existe un constructor o setters para mapear.
        return dto;
    }
    
    // Asumiendo que RazaDTO tiene un constructor que toma un objeto Raza
    private RazaDTO createTestRazaDTOFromRaza(Raza raza) {
        return new RazaDTO(raza.getId(), raza.getNombre(), raza.getEstado(), raza.getEspecie().getId());
    }

    @BeforeEach
    void setUp() {
        // Inicialización de objetos de prueba antes de cada test
        testEspecie = createTestEspecie(1L, "Canis familiaris", "Perro", 1);
        testRaza = createTestRaza(10L, "Labrador", "A", testEspecie);
        testRazaDTO = createTestRazaDTO(10L, "Labrador", "A", 1L);
    }

    // --- Pruebas para findAllRazas() ---
    @Test
    @DisplayName("Debería retornar una lista de todas las razas")
    void findAllRazas_shouldReturnListOfRazaDTOs() {
        // Given
        Raza raza2 = createTestRaza(11L, "Pastor Alemán", "A", testEspecie);
        List<Raza> razas = Arrays.asList(testRaza, raza2);

        when(razaRepository.findAll()).thenReturn(razas);

        // When
        List<RazaDTO> result = razaService.findAllRazas();

        // Then
        assertNotNull(result, "La lista resultante no debería ser nula");
        assertEquals(2, result.size(), "Deberían haber 2 razas en la lista");
        assertEquals("Labrador", result.get(0).getNombre(), "El nombre de la primera raza debería ser 'Labrador'");
        assertEquals("Pastor Alemán", result.get(1).getNombre(), "El nombre de la segunda raza debería ser 'Pastor Alemán'");
        verify(razaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay razas")
    void findAllRazas_shouldReturnEmptyListIfNoRazas() {
        // Given
        when(razaRepository.findAll()).thenReturn(List.of());

        // When
        List<RazaDTO> result = razaService.findAllRazas();

        // Then
        assertNotNull(result, "La lista resultante no debería ser nula");
        assertTrue(result.isEmpty(), "La lista debería estar vacía");
        verify(razaRepository, times(1)).findAll();
    }

    // --- Pruebas para findRazaById() ---
    @Test
    @DisplayName("Debería encontrar una raza por ID y retornar un DTO")
    void findRazaById_shouldReturnRazaDTO() {
        // Given
        when(razaRepository.findById(testRaza.getId())).thenReturn(Optional.of(testRaza));

        // When
        Optional<RazaDTO> result = razaService.findRazaById(testRaza.getId());

        // Then
        assertTrue(result.isPresent(), "La raza debería ser encontrada");
        assertEquals(testRazaDTO.getNombre(), result.get().getNombre(), "El nombre de la raza debe coincidir");
        assertEquals(testRazaDTO.getId(), result.get().getId(), "El ID de la raza debe coincidir");
        verify(razaRepository, times(1)).findById(testRaza.getId());
    }

    @Test
    @DisplayName("Debería retornar Optional.empty si la raza no se encuentra por ID")
    void findRazaById_shouldReturnEmptyOptionalIfNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(razaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<RazaDTO> result = razaService.findRazaById(nonExistentId);

        // Then
        assertFalse(result.isPresent(), "La raza no debería ser encontrada");
        verify(razaRepository, times(1)).findById(nonExistentId);
    }

    // --- Pruebas para saveRaza() ---
    @Test
    @DisplayName("Debería guardar una nueva raza con una especie existente")
    void saveRaza_shouldSaveNewRazaWithExistingEspecie() {
        // Given
        RazaDTO newRazaDTO = createTestRazaDTO(null, "Poodle", "A", testEspecie.getId()); // ID de raza nulo, ID de especie existente
        Raza savedRaza = createTestRaza(20L, "Poodle", "A", testEspecie); // Raza con ID asignado después de guardar

        // Simular que la especie existe
        when(especieRepository.findById(testEspecie.getId())).thenReturn(Optional.of(testEspecie));
        // Simular que el repositorio guarda la raza y devuelve la instancia con ID
        when(razaRepository.save(any(Raza.class))).thenReturn(savedRaza);

        // When
        RazaDTO result = razaService.saveRaza(newRazaDTO);

        // Then
        assertNotNull(result, "La raza guardada no debería ser nula");
        assertNotNull(result.getId(), "La raza guardada debería tener un ID asignado");
        assertEquals("Poodle", result.getNombre(), "El nombre de la raza debería coincidir");
        assertEquals(testEspecie.getId(), result.getEspecieId(), "El ID de la especie debería coincidir");

        verify(especieRepository, times(1)).findById(testEspecie.getId()); // Verifica que se buscó la especie
        verify(razaRepository, times(1)).save(any(Raza.class)); // Verifica que se guardó la raza
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la especie asociada no existe al guardar")
    void saveRaza_shouldThrowExceptionIfEspecieNotFound() {
        // Given
        Long nonExistentEspecieId = 99L;
        RazaDTO newRazaDTO = createTestRazaDTO(null, "Husky", "A", nonExistentEspecieId);

        // Simular que la especie NO existe
        when(especieRepository.findById(nonExistentEspecieId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> razaService.saveRaza(newRazaDTO),
            "Debería lanzar IllegalArgumentException si la especie no se encuentra"
        );
        assertEquals("Especie no encontrada", thrown.getMessage());

        verify(especieRepository, times(1)).findById(nonExistentEspecieId);
        verify(razaRepository, never()).save(any(Raza.class)); // Asegura que no se intentó guardar la raza
    }

    // --- Pruebas para updateRaza() ---
    @Test
    @DisplayName("Debería actualizar una raza existente con los nuevos datos y la misma especie")
    void updateRaza_shouldUpdateExistingRaza() {
        // Given
        Long razaId = testRaza.getId();
        RazaDTO updatedRazaDTO = createTestRazaDTO(razaId, "Labrador Ret. (Actualizado)", "I", testEspecie.getId()); // Mismo especieId

        // Simula que la raza existente es encontrada
        when(razaRepository.findById(razaId)).thenReturn(Optional.of(testRaza));
        // Simula que la especie existente es encontrada (necesario porque el servicio busca la especie otra vez)
        when(especieRepository.findById(testEspecie.getId())).thenReturn(Optional.of(testEspecie));
        // Simula que el repositorio guarda la raza y devuelve la instancia actualizada
        when(razaRepository.save(any(Raza.class))).thenReturn(testRaza); // Puede devolver la misma instancia o una nueva

        // When
        RazaDTO result = razaService.updateRaza(razaId, updatedRazaDTO);

        // Then
        assertNotNull(result, "La raza actualizada no debería ser nula");
        assertEquals(razaId, result.getId(), "El ID de la raza debería coincidir");
        assertEquals("Labrador Ret. (Actualizado)", result.getNombre(), "El nombre debería haberse actualizado");
        assertEquals("I", result.getEstado(), "El estado debería haberse actualizado");
        assertEquals(testEspecie.getId(), result.getEspecieId(), "El ID de la especie debería ser el mismo");

        verify(razaRepository, times(1)).findById(razaId);
        verify(especieRepository, times(1)).findById(testEspecie.getId());
        verify(razaRepository, times(1)).save(any(Raza.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la raza no se encuentra al actualizar")
    void updateRaza_shouldThrowExceptionIfRazaNotFound() {
        // Given
        Long nonExistentRazaId = 99L;
        RazaDTO razaDTO = createTestRazaDTO(nonExistentRazaId, "No Existente", "A", 1L);

        // Simula que la raza NO existe
        when(razaRepository.findById(nonExistentRazaId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> razaService.updateRaza(nonExistentRazaId, razaDTO),
            "Debería lanzar IllegalArgumentException si la raza no se encuentra"
        );
        assertEquals("Raza no encontrada", thrown.getMessage());

        verify(razaRepository, times(1)).findById(nonExistentRazaId);
        verify(especieRepository, never()).findById(anyLong()); // No debería buscar la especie si la raza no se encuentra
        verify(razaRepository, never()).save(any(Raza.class));
    }
    
    @Test
    @DisplayName("Debería actualizar una raza y cambiar su especie asociada")
    void updateRaza_shouldUpdateRazaAndChangeEspecie() {
        // Given
        Long razaId = testRaza.getId();
        Especie newEspecie = createTestEspecie(2L, "Felis catus", "Gato", 1);
        RazaDTO updatedRazaDTO = createTestRazaDTO(razaId, "Labrador (Cambio Especie)", "A", newEspecie.getId());

        // Simula que la raza existente es encontrada
        when(razaRepository.findById(razaId)).thenReturn(Optional.of(testRaza));
        // Simula que la NUEVA especie es encontrada
        when(especieRepository.findById(newEspecie.getId())).thenReturn(Optional.of(newEspecie));
        // Simula que el repositorio guarda la raza
        when(razaRepository.save(any(Raza.class))).thenReturn(createTestRaza(razaId, "Labrador (Cambio Especie)", "A", newEspecie));

        // When
        RazaDTO result = razaService.updateRaza(razaId, updatedRazaDTO);

        // Then
        assertNotNull(result, "La raza actualizada no debería ser nula");
        assertEquals(razaId, result.getId(), "El ID de la raza debería coincidir");
        assertEquals(newEspecie.getId(), result.getEspecieId(), "El ID de la especie debería haberse actualizado");
        assertEquals("Labrador (Cambio Especie)", result.getNombre());
        
        verify(razaRepository, times(1)).findById(razaId);
        verify(especieRepository, times(1)).findById(newEspecie.getId());
        verify(razaRepository, times(1)).save(any(Raza.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la nueva especie no se encuentra al actualizar")
    void updateRaza_shouldThrowExceptionIfNewEspecieNotFound() {
        // Given
        Long razaId = testRaza.getId();
        Long nonExistentEspecieId = 99L;
        RazaDTO razaDTO = createTestRazaDTO(razaId, "Nombre", "A", nonExistentEspecieId);

        // Simula que la raza existe
        when(razaRepository.findById(razaId)).thenReturn(Optional.of(testRaza));
        // Simula que la NUEVA especie NO existe
        when(especieRepository.findById(nonExistentEspecieId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> razaService.updateRaza(razaId, razaDTO),
            "Debería lanzar IllegalArgumentException si la nueva especie no se encuentra"
        );
        assertEquals("Especie no encontrada", thrown.getMessage());

        verify(razaRepository, times(1)).findById(razaId);
        verify(especieRepository, times(1)).findById(nonExistentEspecieId);
        verify(razaRepository, never()).save(any(Raza.class));
    }


    // --- Pruebas para deleteRaza() ---
    @Test
    @DisplayName("Debería eliminar una raza existente")
    void deleteRaza_shouldDeleteExistingRaza() {
        // Given
        Long id = testRaza.getId();
        when(razaRepository.existsById(id)).thenReturn(true);

        // When
        razaService.deleteRaza(id);

        // Then
        verify(razaRepository, times(1)).existsById(id);
        verify(razaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la raza no se encuentra al eliminar")
    void deleteRaza_shouldThrowExceptionIfRazaNotFoundOnDelete() {
        // Given
        Long nonExistentId = 99L;
        when(razaRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> razaService.deleteRaza(nonExistentId),
            "Debería lanzar IllegalArgumentException si la raza no se encuentra"
        );
        assertEquals("Raza no encontrada", thrown.getMessage());

        verify(razaRepository, times(1)).existsById(nonExistentId);
        verify(razaRepository, never()).deleteById(anyLong());
    }

    // --- Pruebas para findRazaByNombre() ---
    @Test
    @DisplayName("Debería encontrar una raza por nombre y retornar un DTO")
    void findRazaByNombre_shouldReturnRazaDTO() {
        // Given
        String nombre = "Labrador";
        when(razaRepository.findByNombre(nombre)).thenReturn(Optional.of(testRaza));

        // When
        Optional<RazaDTO> result = razaService.findRazaByNombre(nombre);

        // Then
        assertTrue(result.isPresent(), "La raza debería ser encontrada");
        assertEquals(nombre, result.get().getNombre(), "El nombre de la raza debe coincidir");
        verify(razaRepository, times(1)).findByNombre(nombre);
    }

    @Test
    @DisplayName("Debería retornar Optional.empty si la raza no se encuentra por nombre")
    void findRazaByNombre_shouldReturnEmptyOptionalIfNotFound() {
        // Given
        String nonExistentName = "RazaInexistente";
        when(razaRepository.findByNombre(nonExistentName)).thenReturn(Optional.empty());

        // When
        Optional<RazaDTO> result = razaService.findRazaByNombre(nonExistentName);

        // Then
        assertFalse(result.isPresent(), "La raza no debería ser encontrada");
        verify(razaRepository, times(1)).findByNombre(nonExistentName);
    }

    // --- Pruebas para findRazasByEspecieId() ---
    @Test
    @DisplayName("Debería retornar una lista de razas por ID de especie")
    void findRazasByEspecieId_shouldReturnListOfRazaDTOs() {
        // Given
        Long especieId = testEspecie.getId();
        Raza raza1 = createTestRaza(10L, "Labrador", "A", testEspecie);
        Raza raza2 = createTestRaza(11L, "Pastor Aleman", "A", testEspecie);
        List<Raza> razasPorEspecie = Arrays.asList(raza1, raza2);

        when(razaRepository.findByEspecieId(especieId)).thenReturn(razasPorEspecie);

        // When
        List<RazaDTO> result = razaService.findRazasByEspecieId(especieId);

        // Then
        assertNotNull(result, "La lista resultante no debería ser nula");
        assertEquals(2, result.size(), "Deberían haber 2 razas para la especie");
        assertEquals("Labrador", result.get(0).getNombre());
        assertEquals("Pastor Aleman", result.get(1).getNombre());
        verify(razaRepository, times(1)).findByEspecieId(especieId);
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay razas para el ID de especie")
    void findRazasByEspecieId_shouldReturnEmptyListIfNoRazasForEspecieId() {
        // Given
        Long nonExistentEspecieId = 99L;
        when(razaRepository.findByEspecieId(nonExistentEspecieId)).thenReturn(List.of());

        // When
        List<RazaDTO> result = razaService.findRazasByEspecieId(nonExistentEspecieId);

        // Then
        assertNotNull(result, "La lista resultante no debería ser nula");
        assertTrue(result.isEmpty(), "La lista debería estar vacía");
        verify(razaRepository, times(1)).findByEspecieId(nonExistentEspecieId);
    }
}
