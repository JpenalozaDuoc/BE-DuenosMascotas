package microvetcare.microvetcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.especie.controller.EspecieController;
import microvetcare.microvetcare.especie.service.EspecieService;
import microvetcare.microvetcare.exception.ResourceNotFoundException; // Asegúrate de que esta ruta es correcta
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

@WebMvcTest(EspecieController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EspecieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EspecieService especieService; // Mock del servicio de especie

    // Objeto de prueba que usaremos
    private EspecieDTO testEspecieDTO;

    @BeforeEach
    void setUp() {
        // Inicialización de un EspecieDTO de prueba antes de cada test
        testEspecieDTO = new EspecieDTO(1L, "Canis familiaris", "Perro", 1);
    }

    // --- Pruebas para GET /api/especies (getAllEspecies) ---
    @Test
    @DisplayName("Debería retornar una lista de todas las especies con status 200 OK")
    void getAllEspecies_shouldReturnListOfEspecieDTOs() throws Exception {
        // Given
        EspecieDTO especie2 = new EspecieDTO(2L, "Felis catus", "Gato", 1);
        List<EspecieDTO> especies = Arrays.asList(testEspecieDTO, especie2);

        when(especieService.findAllEspecies()).thenReturn(especies);

        // When & Then
        mockMvc.perform(get("/api/especies")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Perro")))
                .andExpect(jsonPath("$[1].nombre", is("Gato")));

        verify(especieService, times(1)).findAllEspecies();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía de especies con status 200 OK si no hay datos")
    void getAllEspecies_shouldReturnEmptyList_whenNoData() throws Exception {
        // Given
        when(especieService.findAllEspecies()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/especies")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(especieService, times(1)).findAllEspecies();
    }

    // --- Pruebas para GET /api/especies/{id} (getEspecieById) ---
    @Test
    @DisplayName("Debería retornar una especie por ID con status 200 OK")
    void getEspecieById_shouldReturnEspecieDTO() throws Exception {
        // Given
        when(especieService.findEspecieById(testEspecieDTO.getId())).thenReturn(Optional.of(testEspecieDTO));

        // When & Then
        mockMvc.perform(get("/api/especies/{id}", testEspecieDTO.getId())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testEspecieDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(testEspecieDTO.getNombre())));

        verify(especieService, times(1)).findEspecieById(testEspecieDTO.getId());
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la especie no se encuentra por ID")
    void getEspecieById_shouldReturn404NotFound_whenEspecieNotFound() throws Exception {
        // Given
        Long nonExistentId = 99L;
        // El servicio retorna Optional.empty(), y el controlador lanza ResourceNotFoundException
        when(especieService.findEspecieById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/especies/{id}", nonExistentId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Especie no encontrada con ID: " + nonExistentId)));
        
        verify(especieService, times(1)).findEspecieById(nonExistentId);
    }

    // --- Pruebas para POST /api/especies/create (createEspecie) ---
    @Test
    @DisplayName("Debería crear una nueva especie con status 201 CREATED")
    void createEspecie_shouldCreateNewEspecie() throws Exception {
        // Given
        EspecieDTO newEspecieDTO = new EspecieDTO(null, "Aves", "Pájaro", 1);
        EspecieDTO createdEspecieDTO = new EspecieDTO(3L, "Aves", "Pájaro", 1); // DTO con ID asignado

        when(especieService.saveEspecie(any(EspecieDTO.class))).thenReturn(createdEspecieDTO);

        // When & Then
        mockMvc.perform(post("/api/especies/create")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEspecieDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdEspecieDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Pájaro")));

        verify(especieService, times(1)).saveEspecie(any(EspecieDTO.class));
    }
    
    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si el nombre de la especie ya existe al crear")
    void createEspecie_shouldReturn400BadRequest_whenNombreExistsOnCreate() throws Exception {
        // Given
        EspecieDTO newEspecieDTO = new EspecieDTO(null, "Canis familiaris", "Perro", 1); // Nombre duplicado

        // Simula que el servicio lanzaría IllegalArgumentException
        doThrow(new IllegalArgumentException("Ya existe una especie con el nombre: " + newEspecieDTO.getNombre()))
            .when(especieService).saveEspecie(any(EspecieDTO.class));

        // When & Then
        mockMvc.perform(post("/api/especies/create")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEspecieDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Ya existe una especie con el nombre: " + newEspecieDTO.getNombre())));

        verify(especieService, times(1)).saveEspecie(any(EspecieDTO.class));
    }


    // --- Pruebas para PUT /api/especies/{id} (updateEspecie) ---
    @Test
    @DisplayName("Debería actualizar una especie existente con status 200 OK")
    void updateEspecie_shouldUpdateExistingEspecie() throws Exception {
        // Given
        EspecieDTO updatedEspecieDTO = new EspecieDTO(testEspecieDTO.getId(), "Canis familiaris (Updated)", "Perro", 0);

        when(especieService.updateEspecie(eq(testEspecieDTO.getId()), any(EspecieDTO.class))).thenReturn(updatedEspecieDTO);

        // When & Then
        mockMvc.perform(put("/api/especies/{id}", testEspecieDTO.getId())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEspecieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testEspecieDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombreEspecie", is("Canis familiaris (Updated)")));

        verify(especieService, times(1)).updateEspecie(eq(testEspecieDTO.getId()), any(EspecieDTO.class));
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la especie no se encuentra al actualizar")
    void updateEspecie_shouldReturn404NotFound_whenEspecieNotFoundOnUpdate() throws Exception {
        // Given
        Long nonExistentId = 99L;
        EspecieDTO updatedEspecieDTO = new EspecieDTO(nonExistentId, "No Existe", "Planta", 1);

        // El servicio lanzaría ResourceNotFoundException si no encuentra la especie
        doThrow(new ResourceNotFoundException("Especie no encontrada")).when(especieService).updateEspecie(eq(nonExistentId), any(EspecieDTO.class));
        
        mockMvc.perform(put("/api/especies/{id}", nonExistentId)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEspecieDTO)))
                .andExpect(status().isNotFound()) // Esperamos 404
                .andExpect(jsonPath("$.message", is("Especie no encontrada")));

        verify(especieService, times(1)).updateEspecie(eq(nonExistentId), any(EspecieDTO.class));
    }

    @Test
    @DisplayName("Debería retornar 400 BAD REQUEST si el nombre de la especie ya existe al actualizar")
    void updateEspecie_shouldReturn400BadRequest_whenNombreExistsOnUpdate() throws Exception {
        // Given
        Long idToUpdate = testEspecieDTO.getId();
        EspecieDTO updatedEspecieDTO = new EspecieDTO(idToUpdate, "Canis familiaris", "Perro", 1); // Nombre duplicado (de otra especie o la misma pero manejando el caso)

        // Simula que el servicio lanza una excepción porque el nombre ya existe
        doThrow(new IllegalArgumentException("Ya existe una especie con el nombre: " + updatedEspecieDTO.getNombre()))
            .when(especieService).updateEspecie(eq(idToUpdate), any(EspecieDTO.class));

        // When & Then
        mockMvc.perform(put("/api/especies/{id}", idToUpdate)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEspecieDTO)))
                .andExpect(status().isBadRequest()) // Esperamos 400
                .andExpect(jsonPath("$.message", is("Ya existe una especie con el nombre: " + updatedEspecieDTO.getNombre())));
        
        verify(especieService, times(1)).updateEspecie(eq(idToUpdate), any(EspecieDTO.class));
    }


    // --- Pruebas para DELETE /api/especies/{id} (deleteEspecie) ---
    @Test
    @DisplayName("Debería eliminar una especie existente con status 204 NO CONTENT")
    void deleteEspecie_shouldDeleteExistingEspecie() throws Exception {
        // Given
        Long idToDelete = testEspecieDTO.getId();
        // No necesitamos stubbing para void methods a menos que lancen una excepción
        doNothing().when(especieService).deleteEspecie(idToDelete);

        // When & Then
        mockMvc.perform(delete("/api/especies/{id}", idToDelete)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());

        verify(especieService, times(1)).deleteEspecie(idToDelete);
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la especie no se encuentra al eliminar")
    void deleteEspecie_shouldReturn404NotFound_whenEspecieNotFoundOnDelete() throws Exception {
        // Given
        Long nonExistentId = 99L;
        // Simula que el servicio lanzaría ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Especie no encontrada")).when(especieService).deleteEspecie(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/especies/{id}", nonExistentId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound()) // Esperamos 404
                .andExpect(jsonPath("$.message", is("Especie no encontrada")));

        verify(especieService, times(1)).deleteEspecie(nonExistentId);
    }

    // --- Pruebas para GET /api/especies/nombre/{nombre} (getEspecieByNombre) ---
    @Test
    @DisplayName("Debería retornar una especie por nombre con status 200 OK")
    void getEspecieByNombre_shouldReturnEspecieDTO() throws Exception {
        // Given
        String nombre = testEspecieDTO.getNombre();
        when(especieService.findEspecieByNombre(nombre)).thenReturn(Optional.of(testEspecieDTO));

        // When & Then
        mockMvc.perform(get("/api/especies/nombre/{nombre}", nombre)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testEspecieDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(testEspecieDTO.getNombre())));

        verify(especieService, times(1)).findEspecieByNombre(nombre);
    }

    @Test
    @DisplayName("Debería retornar 404 NOT FOUND si la especie no se encuentra por nombre")
    void getEspecieByNombre_shouldReturn404NotFound_whenEspecieNotFound() throws Exception {
        // Given
        String nonExistentName = "EspecieInexistente";
        when(especieService.findEspecieByNombre(nonExistentName)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/especies/nombre/{nombre}", nonExistentName)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Especie no encontrada con nombre: " + nonExistentName)));
        
        verify(especieService, times(1)).findEspecieByNombre(nonExistentName);
    }
}
