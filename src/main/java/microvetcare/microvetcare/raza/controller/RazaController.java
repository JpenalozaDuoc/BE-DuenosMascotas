package microvetcare.microvetcare.raza.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import microvetcare.microvetcare.exception.ResourceNotFoundException;
import microvetcare.microvetcare.raza.DTO.RazaDTO;
import microvetcare.microvetcare.raza.service.RazaService;

@RestController
@RequestMapping("/api/razas")
public class RazaController {

    private final RazaService razaService;

    public RazaController(RazaService razaService) {
        this.razaService = razaService;
    }

    @GetMapping
    public ResponseEntity<List<RazaDTO>> getAllRazas() {
        List<RazaDTO> razaDTOs = razaService.findAllRazas(); // Cambiado de List<Raza> a List<RazaDTO>

        return ResponseEntity.ok(razaDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RazaDTO> getRazaById(@PathVariable Long id) {
        RazaDTO razaDTO = razaService.findRazaById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con ID: " + id));
        return ResponseEntity.ok(razaDTO);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RazaDTO> getRazaByNombre(@PathVariable String nombre) {
        RazaDTO razaDTO = razaService.findRazaByNombre(nombre)
                                    .orElseThrow(() -> new ResourceNotFoundException("Raza no encontrada con nombre: " + nombre));
        return ResponseEntity.ok(razaDTO);
    }


    /**
     * Crea una nueva raza, asociándola a una especie existente.
     * POST /api/razas?especieId={id_especie}
     * @param razaDTO El objeto RazaDTO a crear
     * @param especieId El ID de la especie a la que pertenece esta raza
     * @return ResponseEntity con la raza creada y estado 201 Created
     */
    @PostMapping
    public ResponseEntity<RazaDTO> createRaza(@RequestBody RazaDTO razaDTO, @RequestParam(required = false) Long especieId) {
        if (especieId == null) {
            throw new IllegalArgumentException("Debe proporcionar especieId en la URL");
        }

        // El servicio ya maneja la conversión, no es necesario crear el objeto Raza manualmente
        RazaDTO createdRazaDTO = razaService.saveRaza(razaDTO);

        return new ResponseEntity<>(createdRazaDTO, HttpStatus.CREATED);
    }

    /**
     * Actualiza una raza existente por su ID, y opcionalmente cambia su especie asociada.
     * PUT /api/razas/{id}?especieId={id_especie} (especieId es opcional si no se quiere cambiar)
     * @param id ID de la raza a actualizar
     * @param razaDTO El objeto RazaDTO con los datos actualizados
     * @param especieId (Opcional) El ID de la nueva especie a la que se asociará la raza
     * @return ResponseEntity con la raza actualizada y estado 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<RazaDTO> updateRaza(@PathVariable Long id, @RequestBody RazaDTO razaDTO,
                                            @RequestParam(required = false) Long especieId) {
        RazaDTO updatedRazaDTO = razaService.updateRaza(id, razaDTO); // Ahora pasa el RazaDTO directamente al servicio

        return ResponseEntity.ok(updatedRazaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRaza(@PathVariable Long id) {
        razaService.deleteRaza(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca razas por el ID de una especie.
     * GET /api/razas/especie/{especieId}
     * @param especieId El ID de la especie para filtrar las razas
     * @return ResponseEntity con la lista de razas encontradas y estado 200 OK
     */
    @GetMapping("/especie/{especieId}")
    public ResponseEntity<List<RazaDTO>> getRazasByEspecieId(@PathVariable Long especieId) {
        List<RazaDTO> razasDTO = razaService.findRazasByEspecieId(especieId); // Cambiar de List<Raza> a List<RazaDTO>
        return ResponseEntity.ok(razasDTO);
    }
}
