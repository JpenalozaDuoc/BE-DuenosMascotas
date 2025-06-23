package microvetcare.microvetcare.dueno.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import microvetcare.microvetcare.dueno.DTO.DuenoDTO;
import microvetcare.microvetcare.dueno.service.DuenoService;
import microvetcare.microvetcare.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/duenos")
//@CrossOrigin(origins = "*") // Puedes especificar Vercel/Netlify más adelante
public class DuenoController {

    @Autowired
    private DuenoService duenoService; // Inyección de la INTERFAZ del servicio

    // Constructor para inyección de dependencias
    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    /**
     * Obtiene una lista de todos los dueños.
     * GET /api/duenos
     * @return ResponseEntity con la lista de dueños y estado 200 OK
     */
    @GetMapping
    public ResponseEntity<List<DuenoDTO>> getAllDuenos() {
        List<DuenoDTO> duenos = duenoService.findAllDuenos();  // Usar DuenoDTO
        return ResponseEntity.ok(duenos); // Retorna 200 OK con la lista de dueños
    }

    /**
     * Obtiene un dueño por su ID.
     * GET /api/duenos/{id}
     * @param id ID del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<DuenoDTO> getDuenoById(@PathVariable Long id) {
        DuenoDTO dueno = duenoService.findDuenoById(id)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con ID: " + id));
        return ResponseEntity.ok(dueno);
    }

    /**
     * Crea un nuevo dueño.
     * POST /api/duenos
     * @param duenoDTO El objeto DuenoDTO a crear
     * @return ResponseEntity con el dueño creado y estado 201 Created
     */
    @PostMapping
    public ResponseEntity<DuenoDTO> createDueno(@RequestBody DuenoDTO duenoDTO) {
        DuenoDTO createdDueno = duenoService.saveDueno(duenoDTO);  // Usar DuenoDTO en la capa de servicio
        return new ResponseEntity<>(createdDueno, HttpStatus.CREATED); // Retorna 201 Created
    }

    /**
     * Actualiza un dueño existente por su ID.
     * PUT /api/duenos/{id}
     * @param id ID del dueño a actualizar
     * @param duenoDTO El objeto DuenoDTO con los datos actualizados
     * @return ResponseEntity con el dueño actualizado y estado 200 OK, o 404 Not Found si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<DuenoDTO> updateDueno(@PathVariable Long id, @RequestBody DuenoDTO duenoDTO) {
        DuenoDTO updatedDueno = duenoService.updateDueno(id, duenoDTO);  // Usar DuenoDTO en la capa de servicio
        return ResponseEntity.ok(updatedDueno); // Retorna 200 OK
    }

    /**
     * Elimina un dueño por su ID.
     * DELETE /api/duenos/{id}
     * @param id ID del dueño a eliminar
     * @return ResponseEntity con estado 204 No Content, o 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDueno(@PathVariable Long id) {
        duenoService.deleteDueno(id);  // Eliminar el dueño por ID
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (eliminación exitosa sin contenido de respuesta)
    }

    /**
     * Busca un dueño por su RUT.
     * GET /api/duenos/rut/{rut}
     * @param rut RUT del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/rut/{rut}")
    public ResponseEntity<DuenoDTO> getDuenoByRut(@PathVariable String rut) {
        DuenoDTO dueno = duenoService.findDuenoByRut(rut)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con RUT: " + rut));
        return ResponseEntity.ok(dueno);
    }

    /**
     * Busca un dueño por su Email.
     * GET /api/duenos/email/{email}
     * @param email Email del dueño a buscar
     * @return ResponseEntity con el dueño encontrado y estado 200 OK, o 404 Not Found si no existe
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<DuenoDTO> getDuenoByEmail(@PathVariable String email) {
        DuenoDTO dueno = duenoService.findDuenoByEmail(email)
                                     .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado con Email: " + email));
        return ResponseEntity.ok(dueno);
    }
}
