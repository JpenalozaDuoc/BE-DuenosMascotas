package microvetcare.microvetcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Necesario para LocalDate
import microvetcare.microvetcare.mascota.DTO.MascotaDTO;
import microvetcare.microvetcare.mascota.controller.MascotaController;
import microvetcare.microvetcare.mascota.service.MascotaService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MascotaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaService mascotaService;

    private ObjectMapper objectMapper; // Lo vamos a inicializar en BeforeEach

    private MascotaDTO mascotaDTO1;
    private MascotaDTO mascotaDTO2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Registra el módulo para manejar LocalDate

        mascotaDTO1 = new MascotaDTO(1L, "Buddy", "CHIP123", "Macho", 1, LocalDate.of(2020, 1, 15),
                101L, 201L, "Juan Perez", "Labrador");
        mascotaDTO2 = new MascotaDTO(2L, "Lucy", "CHIP456", "Hembra", 1, LocalDate.of(2019, 5, 20),
                102L, 202L, "Maria Gonzalez", "Golden Retriever");
    }

    // --- Test para getAllMascotas() ---
    @Test
    @DisplayName("GET /api/mascotas debería retornar todas las mascotas")
    void getAllMascotas_shouldReturnListOfMascotas() throws Exception {
        // Given
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1, mascotaDTO2);
        when(mascotaService.findAllMascotas()).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is(mascotaDTO1.getNombre())))
                .andExpect(jsonPath("$[1].chip", is(mascotaDTO2.getChip())));

        verify(mascotaService, times(1)).findAllMascotas();
    }

    @Test
    @DisplayName("GET /api/mascotas debería retornar lista vacía si no hay mascotas")
    void getAllMascotas_shouldReturnEmptyList_whenNoMascotas() throws Exception {
        // Given
        when(mascotaService.findAllMascotas()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(mascotaService, times(1)).findAllMascotas();
    }

    // --- Test para getMascotaById() ---
    @Test
    @DisplayName("GET /api/mascotas/{id} debería retornar una mascota por ID")
    void getMascotaById_shouldReturnMascota_whenExists() throws Exception {
        // Given
        when(mascotaService.findMascotaById(mascotaDTO1.getId())).thenReturn(Optional.of(mascotaDTO1));

        // When & Then
        mockMvc.perform(get("/api/mascotas/{id}", mascotaDTO1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mascotaDTO1.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(mascotaDTO1.getNombre())));

        verify(mascotaService, times(1)).findMascotaById(mascotaDTO1.getId());
    }

    @Test
    @DisplayName("GET /api/mascotas/{id} debería retornar 404 si la mascota no se encuentra")
    void getMascotaById_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        when(mascotaService.findMascotaById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/mascotas/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mascota no encontrada con ID: " + nonExistentId)));

        verify(mascotaService, times(1)).findMascotaById(nonExistentId);
    }

    // --- Test para createMascota() ---
    @Test
    @DisplayName("POST /api/mascotas/create debería crear una nueva mascota")
    void createMascota_shouldCreateMascota() throws Exception {
        // Given
        MascotaDTO newMascotaDTO = new MascotaDTO(null, "Leo", "CHIP789", "Macho", 1, LocalDate.of(2023, 1, 1),
                101L, 201L, null, null); // Los nombres de dueño y raza no se envían en el request body

        MascotaDTO createdMascotaDTO = new MascotaDTO(3L, "Leo", "CHIP789", "Macho", 1, LocalDate.of(2023, 1, 1),
                101L, 201L, "Juan Perez", "Labrador"); // El servicio los devolverá con nombres

        when(mascotaService.saveMascota(any(MascotaDTO.class), eq(101L), eq(201L))).thenReturn(createdMascotaDTO);

        // When & Then
        mockMvc.perform(post("/api/mascotas/create")
                        .param("duenoId", "101") // Pasamos duenoId como RequestParam
                        .param("razaId", "201") // Pasamos razaId como RequestParam
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMascotaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdMascotaDTO.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(createdMascotaDTO.getNombre())))
                .andExpect(jsonPath("$.nombreDueno", is(createdMascotaDTO.getNombreDueno())))
                .andExpect(jsonPath("$.nombreRaza", is(createdMascotaDTO.getNombreRaza())));

        verify(mascotaService, times(1)).saveMascota(any(MascotaDTO.class), eq(101L), eq(201L));
    }

    @Test
    @DisplayName("POST /api/mascotas/create debería retornar 404 si dueño/raza no se encuentran")
    void createMascota_shouldReturnNotFound_whenDuenoOrRazaNotExists() throws Exception {
        // Given
        MascotaDTO newMascotaDTO = new MascotaDTO(null, "Leo", "CHIP789", "Macho", 1, LocalDate.of(2023, 1, 1),
                999L, 888L, null, null);

        when(mascotaService.saveMascota(any(MascotaDTO.class), eq(999L), eq(888L)))
                .thenThrow(new ResourceNotFoundException("Dueño no encontrado con ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/mascotas/create")
                        .param("duenoId", "999")
                        .param("razaId", "888")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMascotaDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: 999")));

        verify(mascotaService, times(1)).saveMascota(any(MascotaDTO.class), eq(999L), eq(888L));
    }


    // --- Test para updateMascota() ---
    @Test
    @DisplayName("PUT /api/mascotas/{id} debería actualizar una mascota existente")
    void updateMascota_shouldUpdateExistingMascota() throws Exception {
        // Given
        Long mascotaId = 1L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(mascotaId, "Buddy Act.", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                101L, 201L, null, null);

        MascotaDTO returnedMascotaDTO = new MascotaDTO(mascotaId, "Buddy Act.", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                101L, 201L, "Juan Perez", "Labrador");

        when(mascotaService.updateMascota(eq(mascotaId), any(MascotaDTO.class), eq(101L), eq(201L))).thenReturn(returnedMascotaDTO);

        // When & Then
        mockMvc.perform(put("/api/mascotas/{id}", mascotaId)
                        .param("duenoId", "101")
                        .param("razaId", "201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMascotaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is(updatedMascotaDTO.getNombre())))
                .andExpect(jsonPath("$.chip", is(updatedMascotaDTO.getChip())))
                .andExpect(jsonPath("$.nombreDueno", is(returnedMascotaDTO.getNombreDueno())));

        verify(mascotaService, times(1)).updateMascota(eq(mascotaId), any(MascotaDTO.class), eq(101L), eq(201L));
    }

    @Test
    @DisplayName("PUT /api/mascotas/{id} debería retornar 404 si la mascota no se encuentra al actualizar")
    void updateMascota_shouldReturnNotFound_whenMascotaNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(nonExistentId, "NoExiste", "NOCHIP", "Desconocido", 0, LocalDate.now(),
                101L, 201L, null, null);

        when(mascotaService.updateMascota(eq(nonExistentId), any(MascotaDTO.class), anyLong(), anyLong()))
                .thenThrow(new ResourceNotFoundException("Mascota no encontrada con ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(put("/api/mascotas/{id}", nonExistentId)
                        .param("duenoId", "101")
                        .param("razaId", "201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMascotaDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mascota no encontrada con ID: " + nonExistentId)));

        verify(mascotaService, times(1)).updateMascota(eq(nonExistentId), any(MascotaDTO.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("PUT /api/mascotas/{id} debería retornar 404 si el dueño/raza no se encuentran al actualizar")
    void updateMascota_shouldReturnNotFound_whenDuenoOrRazaNotExists() throws Exception {
        // Given
        Long mascotaId = 1L;
        MascotaDTO updatedMascotaDTO = new MascotaDTO(mascotaId, "Buddy Act.", "CHIP_UPDATED", "Hembra", 0, LocalDate.of(2021, 2, 1),
                999L, 201L, null, null); // Dueño no existe

        when(mascotaService.updateMascota(eq(mascotaId), any(MascotaDTO.class), eq(999L), eq(201L)))
                .thenThrow(new ResourceNotFoundException("Dueño no encontrado con ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/mascotas/{id}", mascotaId)
                        .param("duenoId", "999")
                        .param("razaId", "201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMascotaDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: 999")));

        verify(mascotaService, times(1)).updateMascota(eq(mascotaId), any(MascotaDTO.class), eq(999L), eq(201L));
    }


    // --- Test para deleteMascota() ---
    @Test
    @DisplayName("DELETE /api/mascotas/{id} debería eliminar una mascota existente")
    void deleteMascota_shouldDeleteExistingMascota() throws Exception {
        // Given
        Long mascotaIdToDelete = 1L;
        doNothing().when(mascotaService).deleteMascota(mascotaIdToDelete);

        // When & Then
        mockMvc.perform(delete("/api/mascotas/{id}", mascotaIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mascotaService, times(1)).deleteMascota(mascotaIdToDelete);
    }

    @Test
    @DisplayName("DELETE /api/mascotas/{id} debería retornar 404 si la mascota no se encuentra al eliminar")
    void deleteMascota_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long nonExistentId = 99L;
        doThrow(new ResourceNotFoundException("Mascota no encontrada con ID: " + nonExistentId))
                .when(mascotaService).deleteMascota(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/mascotas/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mascota no encontrada con ID: " + nonExistentId)));

        verify(mascotaService, times(1)).deleteMascota(nonExistentId);
    }

    // --- Test para getMascotasByNombre() ---
    @Test
    @DisplayName("GET /api/mascotas/nombre/{nombre} debería retornar mascotas por nombre")
    void getMascotasByNombre_shouldReturnMascotas() throws Exception {
        // Given
        String nombre = "Buddy";
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1);
        when(mascotaService.findMascotasByNombre(nombre)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/nombre/{nombre}", nombre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is(nombre)));

        verify(mascotaService, times(1)).findMascotasByNombre(nombre);
    }

    // --- Test para getMascotasByDuenoId() ---
    @Test
    @DisplayName("GET /api/mascotas/dueno/{duenoId} debería retornar mascotas por ID de dueño")
    void getMascotasByDuenoId_shouldReturnMascotas() throws Exception {
        // Given
        Long duenoId = 101L;
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1);
        when(mascotaService.findMascotasByDuenoId(duenoId)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/dueno/{duenoId}", duenoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idDueno", is(duenoId.intValue())));

        verify(mascotaService, times(1)).findMascotasByDuenoId(duenoId);
    }

    @Test
    @DisplayName("GET /api/mascotas/dueno/{duenoId} debería retornar 404 si el dueño no existe")
    void getMascotasByDuenoId_shouldReturnNotFound_whenDuenoNotExists() throws Exception {
        // Given
        Long nonExistentDuenoId = 999L;
        when(mascotaService.findMascotasByDuenoId(nonExistentDuenoId))
                .thenThrow(new ResourceNotFoundException("Dueño no encontrado con ID: " + nonExistentDuenoId));

        // When & Then
        mockMvc.perform(get("/api/mascotas/dueno/{duenoId}", nonExistentDuenoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Dueño no encontrado con ID: " + nonExistentDuenoId)));

        verify(mascotaService, times(1)).findMascotasByDuenoId(nonExistentDuenoId);
    }

    // --- Test para getMascotasByRazaId() ---
    @Test
    @DisplayName("GET /api/mascotas/raza/{razaId} debería retornar mascotas por ID de raza")
    void getMascotasByRazaId_shouldReturnMascotas() throws Exception {
        // Given
        Long razaId = 201L;
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1);
        when(mascotaService.findMascotasByRazaId(razaId)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/raza/{razaId}", razaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idRaza", is(razaId.intValue())));

        verify(mascotaService, times(1)).findMascotasByRazaId(razaId);
    }

    @Test
    @DisplayName("GET /api/mascotas/raza/{razaId} debería retornar 404 si la raza no existe")
    void getMascotasByRazaId_shouldReturnNotFound_whenRazaNotExists() throws Exception {
        // Given
        Long nonExistentRazaId = 999L;
        when(mascotaService.findMascotasByRazaId(nonExistentRazaId))
                .thenThrow(new ResourceNotFoundException("Raza no encontrada con ID: " + nonExistentRazaId));

        // When & Then
        mockMvc.perform(get("/api/mascotas/raza/{razaId}", nonExistentRazaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Raza no encontrada con ID: " + nonExistentRazaId)));

        verify(mascotaService, times(1)).findMascotasByRazaId(nonExistentRazaId);
    }

    // --- Test para getMascotasByGenero() ---
    @Test
    @DisplayName("GET /api/mascotas/sexo/{genero} debería retornar mascotas por género")
    void getMascotasByGenero_shouldReturnMascotas() throws Exception {
        // Given
        String genero = "Macho";
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1);
        when(mascotaService.findMascotasByGenero(genero)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/sexo/{genero}", genero)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].genero", is(genero)));

        verify(mascotaService, times(1)).findMascotasByGenero(genero);
    }

    // --- Test para getMascotasBornAfter() ---
    @Test
    @DisplayName("GET /api/mascotas/nacidas-despues/{fecha} debería retornar mascotas nacidas después de una fecha")
    void getMascotasBornAfter_shouldReturnMascotas() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2019, 12, 31);
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO1); // Buddy born 2020-01-15
        when(mascotaService.findMascotasBornAfter(date)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/nacidas-despues/{fecha}", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is(mascotaDTO1.getNombre())));

        verify(mascotaService, times(1)).findMascotasBornAfter(date);
    }

    // --- Test para getMascotasBornBefore() ---
    @Test
    @DisplayName("GET /api/mascotas/nacidas-antes/{fecha} debería retornar mascotas nacidas antes de una fecha")
    void getMascotasBornBefore_shouldReturnMascotas() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2020, 1, 1);
        List<MascotaDTO> mascotas = Arrays.asList(mascotaDTO2); // Lucy born 2019-05-20
        when(mascotaService.findMascotasBornBefore(date)).thenReturn(mascotas);

        // When & Then
        mockMvc.perform(get("/api/mascotas/nacidas-antes/{fecha}", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is(mascotaDTO2.getNombre())));

        verify(mascotaService, times(1)).findMascotasBornBefore(date);
    }
}
