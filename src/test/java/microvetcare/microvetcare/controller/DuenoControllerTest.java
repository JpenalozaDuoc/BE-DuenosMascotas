package microvetcare.microvetcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microvetcare.microvetcare.dueno.DTO.DuenoDTO;
import microvetcare.microvetcare.dueno.controller.DuenoController;
import microvetcare.microvetcare.dueno.service.DuenoService;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DuenoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DuenoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para realizar peticiones HTTP simuladas

    @MockBean // Simula el servicio, ya que no queremos probar la lógica del servicio aquí
    private DuenoService duenoService;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON y viceversa

    private DuenoDTO duenoDTO1;
    private DuenoDTO duenoDTO2;

    @BeforeEach
    void setUp() {
        duenoDTO1 = new DuenoDTO(1L, "11111111-1", "Juan", "Perez", "Calle Falsa 123", "91111111111", "juan.perez@example.com", true);
        duenoDTO2 = new DuenoDTO(2L, "22222222-2", "Maria", "Gonzalez", "Av. Siempre Viva 456", "92222222222", "maria.g@example.com", true);
    }

    // --- Test para getAllDuenos() ---
    @Test
    @DisplayName("GET /api/duenos debería retornar todos los dueños")
    void getAllDuenos_shouldReturnListOfDuenos() throws Exception {
        // Given
        List<DuenoDTO> duenos = Arrays.asList(duenoDTO1, duenoDTO2);
        when(duenoService.findAllDuenos()).thenReturn(duenos);

        // When & Then
        mockMvc.perform(get("/api/duenos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is(duenoDTO1.getNombre())))
                .andExpect(jsonPath("$[1].email", is(duenoDTO2.getEmail())));

        verify(duenoService, times(1)).findAllDuenos();
    }

    @Test
    @DisplayName("GET /api/duenos debería retornar lista vacía si no hay dueños")
    void getAllDuenos_shouldReturnEmptyList_whenNoDuenos() throws Exception {
        // Given
        when(duenoService.findAllDuenos()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/duenos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(duenoService, times(1)).findAllDuenos();
    }

    // --- Test para getDuenoById() ---
    @Test
    @DisplayName("GET /api/duenos/{id} debería retornar un dueño por ID")
    void getDuenoById_shouldReturnDueno_whenExists() throws Exception {
        // Given
        when(duenoService.findDuenoById(duenoDTO1.getId())).thenReturn(Optional.of(duenoDTO1));

        // When & Then
        mockMvc.perform(get("/api/duenos/{id}", duenoDTO1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(duenoDTO1.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(duenoDTO1.getNombre())));

        verify(duenoService, times(1)).findDuenoById(duenoDTO1.getId());
    }

    @Test
    @DisplayName("GET /api/duenos/{id} debería retornar 404 si el dueño no se encuentra")
    void getDuenoById_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        when(duenoService.findDuenoById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/duenos/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: " + nonExistentId))); // Asumiendo que tu ExceptionHandler retorna un campo 'message'

        verify(duenoService, times(1)).findDuenoById(nonExistentId);
    }

    // --- Test para createDueno() ---
    @Test
    @DisplayName("POST /api/duenos/create debería crear un nuevo dueño")
    void createDueno_shouldCreateDueno() throws Exception {
        // Given
        DuenoDTO newDuenoDTO = new DuenoDTO(null, "33333333-3", "Carlos", "Silva", "Calle Nueva 789", "93333333333", "carlos.s@example.com", true);
        DuenoDTO createdDuenoDTO = new DuenoDTO(3L, "33333333-3", "Carlos", "Silva", "Calle Nueva 789", "93333333333", "carlos.s@example.com", true);

        when(duenoService.saveDueno(any(DuenoDTO.class))).thenReturn(createdDuenoDTO);

        // When & Then
        mockMvc.perform(post("/api/duenos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDuenoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdDuenoDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(createdDuenoDTO.getNombre())))
                .andExpect(jsonPath("$.telefono", is("93333333333"))); // Verifica que el teléfono se limpie

        verify(duenoService, times(1)).saveDueno(any(DuenoDTO.class));
    }

    @Test
    @DisplayName("POST /api/duenos/create debería retornar 400 si el servicio lanza IllegalArgumentException")
    void createDueno_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
        // Given
        DuenoDTO invalidDuenoDTO = new DuenoDTO(null, "11111111-1", "Duplicado", "Rut", "Calle", "123", "email@mail.com", true);

        // Simula que el servicio lanza una excepción de argumento inválido
        when(duenoService.saveDueno(any(DuenoDTO.class))).thenThrow(new IllegalArgumentException("Ya existe un dueño con el RUT: 11111111-1"));

        // When & Then
        mockMvc.perform(post("/api/duenos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDuenoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Ya existe un dueño con el RUT: 11111111-1")));

        verify(duenoService, times(1)).saveDueno(any(DuenoDTO.class));
    }


    // --- Test para updateDueno() ---
    @Test
    @DisplayName("PUT /api/duenos/{id} debería actualizar un dueño existente")
    void updateDueno_shouldUpdateExistingDueno() throws Exception {
        // Given
        Long duenoId = 1L;
        DuenoDTO updatedDuenoDTO = new DuenoDTO(duenoId, "11111111-1", "Juan Actualizado", "Perez", "Nueva Direccion", "91111111111", "juan.perez@example.com", false);

        when(duenoService.updateDueno(eq(duenoId), any(DuenoDTO.class))).thenReturn(updatedDuenoDTO);

        // When & Then
        mockMvc.perform(put("/api/duenos/{id}", duenoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDuenoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is(updatedDuenoDTO.getNombre())))
                .andExpect(jsonPath("$.estado", is(updatedDuenoDTO.getEstado())));

        verify(duenoService, times(1)).updateDueno(eq(duenoId), any(DuenoDTO.class));
    }

    @Test
    @DisplayName("PUT /api/duenos/{id} debería retornar 404 si el dueño no se encuentra al actualizar")
    void updateDueno_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        DuenoDTO updatedDuenoDTO = new DuenoDTO(nonExistentId, "99999999-9", "NoExiste", "NoExiste", "NoExiste", "99999999999", "noexiste@example.com", true);

        when(duenoService.updateDueno(eq(nonExistentId), any(DuenoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Dueño no encontrado con ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(put("/api/duenos/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDuenoDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: " + nonExistentId)));

        verify(duenoService, times(1)).updateDueno(eq(nonExistentId), any(DuenoDTO.class));
    }

    @Test
    @DisplayName("PUT /api/duenos/{id} debería retornar 400 si el servicio lanza IllegalArgumentException")
    void updateDueno_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
        // Given
        Long duenoId = 1L;
        DuenoDTO updatedDuenoDTO = new DuenoDTO(duenoId, "OTRO_RUT", "Juan", "Perez", "Calle Falsa 123", "91111111111", "otro@example.com", true);

        when(duenoService.updateDueno(eq(duenoId), any(DuenoDTO.class)))
                .thenThrow(new IllegalArgumentException("El RUT OTRO_RUT ya está registrado para otro dueño."));

        // When & Then
        mockMvc.perform(put("/api/duenos/{id}", duenoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDuenoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El RUT OTRO_RUT ya está registrado para otro dueño.")));

        verify(duenoService, times(1)).updateDueno(eq(duenoId), any(DuenoDTO.class));
    }

    // --- Test para deleteDueno() ---
    @Test
    @DisplayName("DELETE /api/duenos/{id} debería eliminar un dueño existente")
    void deleteDueno_shouldDeleteExistingDueno() throws Exception {
        // Given
        Long duenoIdToDelete = 1L;
        doNothing().when(duenoService).deleteDueno(duenoIdToDelete);

        // When & Then
        mockMvc.perform(delete("/api/duenos/{id}", duenoIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(duenoService, times(1)).deleteDueno(duenoIdToDelete);
    }

    @Test
    @DisplayName("DELETE /api/duenos/{id} debería retornar 404 si el dueño no se encuentra al eliminar")
    void deleteDueno_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        doThrow(new ResourceNotFoundException("Dueño no encontrado con ID: " + nonExistentId))
                .when(duenoService).deleteDueno(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/duenos/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: " + nonExistentId)));

        verify(duenoService, times(1)).deleteDueno(nonExistentId);
    }

    // --- Test para getDuenoByRut() ---
    @Test
    @DisplayName("GET /api/duenos/rut/{rut} debería retornar un dueño por RUT")
    void getDuenoByRut_shouldReturnDueno_whenExists() throws Exception {
        // Given
        String rut = duenoDTO1.getRut();
        when(duenoService.findDuenoByRut(rut)).thenReturn(Optional.of(duenoDTO1));

        // When & Then
        mockMvc.perform(get("/api/duenos/rut/{rut}", rut)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut", is(duenoDTO1.getRut())))
                .andExpect(jsonPath("$.nombre", is(duenoDTO1.getNombre())));

        verify(duenoService, times(1)).findDuenoByRut(rut);
    }

    @Test
    @DisplayName("GET /api/duenos/rut/{rut} debería retornar 404 si el dueño no se encuentra por RUT")
    void getDuenoByRut_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        String nonExistentRut = "00000000-0";
        when(duenoService.findDuenoByRut(nonExistentRut)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/duenos/rut/{rut}", nonExistentRut)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con RUT: " + nonExistentRut)));

        verify(duenoService, times(1)).findDuenoByRut(nonExistentRut);
    }

    // --- Test para getDuenoByEmail() ---
    @Test
    @DisplayName("GET /api/duenos/email/{email} debería retornar un dueño por Email")
    void getDuenoByEmail_shouldReturnDueno_whenExists() throws Exception {
        // Given
        String email = duenoDTO1.getEmail();
        when(duenoService.findDuenoByEmail(email)).thenReturn(Optional.of(duenoDTO1));

        // When & Then
        mockMvc.perform(get("/api/duenos/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(duenoDTO1.getEmail())))
                .andExpect(jsonPath("$.nombre", is(duenoDTO1.getNombre())));

        verify(duenoService, times(1)).findDuenoByEmail(email);
    }

    @Test
    @DisplayName("GET /api/duenos/email/{email} debería retornar 404 si el dueño no se encuentra por Email")
    void getDuenoByEmail_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        String nonExistentEmail = "noexist@example.com";
        when(duenoService.findDuenoByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/duenos/email/{email}", nonExistentEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con Email: " + nonExistentEmail)));

        verify(duenoService, times(1)).findDuenoByEmail(nonExistentEmail);
    }
}
