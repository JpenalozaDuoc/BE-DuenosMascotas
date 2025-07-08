package microvetcare.microvetcare.dueno.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import microvetcare.microvetcare.dueno.DTO.DuenoDTO;
import microvetcare.microvetcare.dueno.service.DuenoService;
import microvetcare.microvetcare.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/duenos")
public class DuenoController {

    private final DuenoService duenoService; 

    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    /**
     * Obtiene una lista de todos los dueños.
     * Acceso para ADMIN y VETERINARIO (para consultas generales).
     * GET /api/duenos
     * @return ResponseEntity con la lista de dueños y estado 200 OK
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<DuenoDTO>> getAllDuenos() {
        List<DuenoDTO> duenos = duenoService.findAllDuenos();
        return ResponseEntity.ok(duenos);
    }

    /**
     * Obtiene un dueño por su ID.
     * Acceso para ADMIN, VETERINARIO y ASISTENTE (para consultas específicas).
     * GET /api/duenos/{id}
     * @param id ID del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") 
    public ResponseEntity<DuenoDTO> getDuenoById(@PathVariable Long id) {
        DuenoDTO dueno = duenoService.findDuenoById(id)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + id));
        return ResponseEntity.ok(dueno);
    }

    /**
     * Crea un nuevo dueño.
     * Solo permitido para ADMINS y ASISTENTES (quienes registran a los dueños).
     * POST /api/duenos
     * @param duenoDTO El objeto DuenoDTO a crear
     * @return ResponseEntity con el dueño creado y estado 201 Created
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')") 
    public ResponseEntity<DuenoDTO> createDueno(@RequestBody DuenoDTO duenoDTO) {
        String telefonoLimpio = duenoDTO.getTelefono() != null ? duenoDTO.getTelefono().replaceAll("\\s+", "") : null;
        duenoDTO.setTelefono(telefonoLimpio);
        DuenoDTO createdDueno = duenoService.saveDueno(duenoDTO);
        return new ResponseEntity<>(createdDueno, HttpStatus.CREATED);
    }

    /**
     * Actualiza un dueño existente por su ID.
     * Acceso para ADMIN solamente (la actualización puede ser una operación más sensible).
     * Si ASISTENTE o VETERINARIO también necesitan actualizar, se añade el rol aquí.
     * PUT /api/duenos/{id}
     * @param id ID del dueño a actualizar
     * @param duenoDTO El objeto DuenoDTO con los datos actualizados
     * @return ResponseEntity con el dueño actualizado y estado 200 OK, o 404 Not Found si no existe
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<DuenoDTO> updateDueno(@PathVariable Long id, @RequestBody DuenoDTO duenoDTO) {
        DuenoDTO updatedDueno = duenoService.updateDueno(id, duenoDTO);
        return ResponseEntity.ok(updatedDueno);
    }

    /**
     * Elimina un dueño por su ID.
     * Generalmente, solo los ADMINS deberían tener permiso para eliminar.
     * DELETE /api/duenos/{id}
     * @param id ID del dueño a eliminar
     * @return ResponseEntity con estado 204 No Content, o 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> deleteDueno(@PathVariable Long id) {
        duenoService.deleteDueno(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca un dueño por su RUT.
     * Acceso para ADMIN, VETERINARIO y ASISTENTE.
     * GET /api/duenos/rut/{rut}
     * @param rut RUT del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/rut/{rut}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") 
    public ResponseEntity<DuenoDTO> getDuenoByRut(@PathVariable String rut) {
        DuenoDTO dueno = duenoService.findDuenoByRut(rut)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con RUT: " + rut));
        return ResponseEntity.ok(dueno);
    }

    /**
     * Busca un dueño por su Email.
     * Acceso para ADMIN, VETERINARIO y ASISTENTE.
     * GET /api/duenos/email/{email}
     * @param email Email del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") 
    public ResponseEntity<DuenoDTO> getDuenoByEmail(@PathVariable String email) {
        DuenoDTO dueno = duenoService.findDuenoByEmail(email)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con Email: " + email));
        return ResponseEntity.ok(dueno);
    }
}
