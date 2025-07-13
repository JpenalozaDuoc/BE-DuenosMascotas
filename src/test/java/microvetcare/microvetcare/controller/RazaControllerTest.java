package microvetcare.microvetcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import microvetcare.microvetcare.raza.DTO.RazaDTO;
import microvetcare.microvetcare.raza.controller.RazaController;
import microvetcare.microvetcare.raza.service.RazaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Anotación para pruebas de la capa web (controladores)
@WebMvcTest(RazaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RazaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON y viceversa

    @MockBean
    private RazaService razaService; // Mock del servicio, para aislar el controlador

    // Objetos de prueba que usaremos
    private RazaDTO testRazaDTO;
    private EspecieDTO testEspecieDTO; // Si tu RazaDTO incluye EspecieDTO, si no, puedes omitirlo

    @BeforeEach
    void setUp() {
        // Inicialización de objetos de prueba antes de cada test
        testEspecieDTO = new EspecieDTO(1L, "Canis familiaris", "Perro", 1);
        
        // Asumiendo que RazaDTO tiene un constructor adecuado o setters
        testRazaDTO = new RazaDTO();
        testRazaDTO.setId(10L);
        testRazaDTO.setNombre("Labrador");
        testRazaDTO.setEstado("A");
        testRazaDTO.setEspecieId(testEspecieDTO.getId());
        // Si RazaDTO tiene un campo EspecieDTO:
        // testRazaDTO.setEspecieDTO(testEspecieDTO);
    }

    // --- Pruebas para GET /api/razas (getAllRazas) ---
    @Test
    @DisplayName("Debería retornar una lista de todas las razas con status 200 OK")
    void getAllRazas_shouldReturnListOfRazaDTOs() throws Exception {
        // Given
        RazaDTO raza2 = new RazaDTO(11L, "Pastor Alemán", "A", testEspecieDTO.getId());
        List<RazaDTO> razas = Arrays.asList(testRazaDTO, raza2);

        when(razaService.findAllRazas()).thenReturn(razas);

        // When & Then
        mockMvc.perform(get("/api/razas")
                .header("Authorization", "Bearer token")) // Simulamos un token para pasar el @PreAuthorize
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Labrador")))
                .andExpect(jsonPath("$[1].nombre", is("Pastor Alemán")));

        verify(razaService, times(1)).findAllRazas();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía de razas con status 200 OK si no hay datos")
    void getAllRazas_shouldReturnEmptyList_whenNoData() throws Exception {
        // Given
        when(razaService.findAllRazas()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/razas")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(razaService, times(1)).findAllRazas();
    }

    // --- Pruebas para GET /api/razas/{id} (getRazaById) ---
    @Test
    @DisplayName("Debería retornar una raza por ID con status 200 OK")
    void getRazaById_shouldReturnRazaDTO() throws Exception {
        // Given
        when(razaService.findRazaById(testRazaDTO.getId())).thenReturn(Optional.of(testRazaDTO));

        // When & Then
        mockMvc.perform(get("/api/razas/{id}", testRazaDTO.getId())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testRazaDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(testRazaDTO.getNombre())));

        verify(razaService, times(1)).findRazaById(testRazaDTO.getId());
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la raza no se encuentra por ID")
    void getRazaById_shouldReturn404NotFound_whenRazaNotFound() throws Exception {
        Long nonExistentId = 99L;
        // Mockear que el servicio lanza ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Raza no encontrada con ID: " + nonExistentId))
            .when(razaService).findRazaById(nonExistentId); // <--- Asegúrate que el servicio lanza ResourceNotFoundException aquí

        mockMvc.perform(get("/api/razas/{id}", nonExistentId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound()) // Status 404
                .andExpect(jsonPath("$.message", is("Raza no encontrada con ID: " + nonExistentId)));
    }


    // --- Pruebas para GET /api/razas/nombre/{nombre} (getRazaByNombre) ---
    @Test
    @DisplayName("Debería retornar una raza por nombre con status 200 OK")
    void getRazaByNombre_shouldReturnRazaDTO() throws Exception {
        // Given
        when(razaService.findRazaByNombre(testRazaDTO.getNombre())).thenReturn(Optional.of(testRazaDTO));

        // When & Then
        mockMvc.perform(get("/api/razas/nombre/{nombre}", testRazaDTO.getNombre())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testRazaDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(testRazaDTO.getNombre())));

        verify(razaService, times(1)).findRazaByNombre(testRazaDTO.getNombre());
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la raza no se encuentra por nombre")
    void getRazaByNombre_shouldReturn404NotFound_whenRazaNotFound() throws Exception {
        String nonExistentName = "RazaInexistente";
        // Mockear que el servicio lanza ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Raza no encontrada con nombre: " + nonExistentName))
            .when(razaService).findRazaByNombre(nonExistentName); // <--- Asegúrate que el servicio lanza ResourceNotFoundException aquí

        mockMvc.perform(get("/api/razas/nombre/{nombre}", nonExistentName)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound()) // Status 404
                .andExpect(jsonPath("$.message", is("Raza no encontrada con nombre: " + nonExistentName)));
    }

    // --- Pruebas para POST /api/razas/create (createRaza) ---
    @Test
    @DisplayName("Debería crear una nueva raza con status 201 CREATED")
    void createRaza_shouldCreateNewRaza() throws Exception {
        // Given
        RazaDTO newRazaDTO = new RazaDTO();
        newRazaDTO.setNombre("Bulldog");
        newRazaDTO.setEstado("A");
        newRazaDTO.setEspecieId(testEspecieDTO.getId()); // Asociado a una especie existente

        RazaDTO createdRazaDTO = new RazaDTO(20L, "Bulldog", "A", testEspecieDTO.getId()); // DTO con ID después de guardar

        when(razaService.saveRaza(any(RazaDTO.class))).thenReturn(createdRazaDTO);

        // When & Then
        mockMvc.perform(post("/api/razas/create")
                .header("Authorization", "Bearer token")
                .param("especieId", testEspecieDTO.getId().toString()) // Parámetro de query para especieId
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRazaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdRazaDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Bulldog")));

        verify(razaService, times(1)).saveRaza(any(RazaDTO.class));
    }

    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si no se proporciona especieId al crear raza")
    void createRaza_shouldReturn400BadRequest_ifNoEspecieIdProvided() throws Exception {
        RazaDTO newRazaDTO = new RazaDTO();
        newRazaDTO.setNombre("Bulldog");
        newRazaDTO.setEstado("A");

        // No necesitamos un doThrow para el servicio aquí, ya que la validación del controlador lanza la excepción directamente.
        // El controlador lanza la IllegalArgumentException ANTES de llamar al servicio.

        mockMvc.perform(post("/api/razas/create")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRazaDTO)))
                .andExpect(status().isBadRequest()) // Status 400
                .andExpect(jsonPath("$.message", is("Debe proporcionar especieId en la URL"))); // Tu mensaje exacto

        verify(razaService, never()).saveRaza(any(RazaDTO.class));
    }

    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si la especie asociada no existe al crear")
    void createRaza_shouldReturn400BadRequest_whenEspecieNotFoundOnCreate() throws Exception {
        Long nonExistentEspecieId = 99L;
        RazaDTO newRazaDTO = new RazaDTO();
        newRazaDTO.setNombre("Bulldog");
        newRazaDTO.setEstado("A");
        newRazaDTO.setEspecieId(nonExistentEspecieId);

        // Aquí sí, el servicio lanza la IllegalArgumentException
        doThrow(new IllegalArgumentException("Especie no encontrada")).when(razaService).saveRaza(any(RazaDTO.class));

        mockMvc.perform(post("/api/razas/create")
                .header("Authorization", "Bearer token")
                .param("especieId", nonExistentEspecieId.toString()) // ¡Importante! El controlador lee el especieId de aquí.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRazaDTO)))
                .andExpect(status().isBadRequest()) // Status 400
                .andExpect(jsonPath("$.message", is("Especie no encontrada")));

        verify(razaService, times(1)).saveRaza(any(RazaDTO.class));
    }


    // --- Pruebas para PUT /api/razas/{id} (updateRaza) ---
    @Test
    @DisplayName("Debería actualizar una raza existente con status 200 OK")
    void updateRaza_shouldUpdateExistingRaza() throws Exception {
        // Given
        RazaDTO updatedRazaDTO = new RazaDTO(testRazaDTO.getId(), "Labrador (Updated)", "I", testEspecieDTO.getId());

        when(razaService.updateRaza(eq(testRazaDTO.getId()), any(RazaDTO.class))).thenReturn(updatedRazaDTO);

        // When & Then
        mockMvc.perform(put("/api/razas/{id}", testRazaDTO.getId())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRazaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testRazaDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Labrador (Updated)")));

        verify(razaService, times(1)).updateRaza(eq(testRazaDTO.getId()), any(RazaDTO.class));
    }

    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si la raza no se encuentra al actualizar") // <--- CAMBIO DE DISPLAY NAME
    void updateRaza_shouldReturn400BadRequest_whenRazaNotFoundOnUpdate() throws Exception { // <--- CAMBIO DE NOMBRE DE MÉTODO (si aplica)
        Long nonExistentId = 99L;
        RazaDTO updatedRazaDTO = new RazaDTO(nonExistentId, "No existe", "A", testEspecieDTO.getId());

        doThrow(new IllegalArgumentException("Raza no encontrada")).when(razaService).updateRaza(eq(nonExistentId), any(RazaDTO.class));

        mockMvc.perform(put("/api/razas/{id}", nonExistentId)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRazaDTO)))
                .andExpect(status().isBadRequest()) // <--- Status 400
                .andExpect(jsonPath("$.message", is("Raza no encontrada")));

        verify(razaService, times(1)).updateRaza(eq(nonExistentId), any(RazaDTO.class));
    }


    // --- Pruebas para DELETE /api/razas/{id} (deleteRaza) ---
    @Test
    @DisplayName("Debería eliminar una raza existente con status 204 NO CONTENT")
    void deleteRaza_shouldDeleteExistingRaza() throws Exception {
        // Given
        Long idToDelete = testRazaDTO.getId();
        // No necesitamos stubbing para void methods a menos que lancen una excepción
        doNothing().when(razaService).deleteRaza(idToDelete);

        // When & Then
        mockMvc.perform(delete("/api/razas/{id}", idToDelete)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());

        verify(razaService, times(1)).deleteRaza(idToDelete);
    }

    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si la raza no se encuentra al eliminar")
    void deleteRaza_shouldReturn400BadRequest_whenRazaNotFoundOnDelete() throws Exception {
        Long nonExistentId = 99L;
        doThrow(new IllegalArgumentException("Raza no encontrada")).when(razaService).deleteRaza(nonExistentId);

        mockMvc.perform(delete("/api/razas/{id}", nonExistentId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest()) // Status 400
                .andExpect(jsonPath("$.message", is("Raza no encontrada")));
    }

    // --- Pruebas para GET /api/razas/especie/{especieId} (getRazasByEspecieId) ---
    @Test
    @DisplayName("Debería retornar una lista de razas por ID de especie con status 200 OK")
    void getRazasByEspecieId_shouldReturnListOfRazaDTOs() throws Exception {
        // Given
        Long especieId = testEspecieDTO.getId();
        RazaDTO raza1 = new RazaDTO(10L, "Labrador", "A", especieId);
        RazaDTO raza2 = new RazaDTO(11L, "Pastor Alemán", "A", especieId);
        List<RazaDTO> razasPorEspecie = Arrays.asList(raza1, raza2);

        when(razaService.findRazasByEspecieId(especieId)).thenReturn(razasPorEspecie);

        // When & Then
        mockMvc.perform(get("/api/razas/especie/{especieId}", especieId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Labrador")))
                .andExpect(jsonPath("$[1].nombre", is("Pastor Alemán")));

        verify(razaService, times(1)).findRazasByEspecieId(especieId);
    }

    @Test
    @DisplayName("Debería retornar una lista vacía si no hay razas para el ID de especie")
    void getRazasByEspecieId_shouldReturnEmptyList_whenNoRazasForEspecieId() throws Exception {
        // Given
        Long nonExistentEspecieId = 99L;
        when(razaService.findRazasByEspecieId(nonExistentEspecieId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/razas/especie/{especieId}", nonExistentEspecieId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(razaService, times(1)).findRazasByEspecieId(nonExistentEspecieId);
    }
}
