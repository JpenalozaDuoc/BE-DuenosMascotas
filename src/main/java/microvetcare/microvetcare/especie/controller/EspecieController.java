package microvetcare.microvetcare.especie.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import microvetcare.microvetcare.especie.DTO.EspecieDTO;
import microvetcare.microvetcare.especie.service.EspecieService;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/api/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<EspecieDTO>> getAllEspecies(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        List<EspecieDTO> especies = especieService.findAllEspecies();
        return ResponseEntity.ok(especies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<EspecieDTO> getEspecieById(@PathVariable Long id) {
        EspecieDTO especie = especieService.findEspecieById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada con ID: " + id));
        return ResponseEntity.ok(especie);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')")
    public ResponseEntity<EspecieDTO> createEspecie(@RequestBody EspecieDTO especieDTO) {
        EspecieDTO createdEspecie = especieService.saveEspecie(especieDTO);
        return new ResponseEntity<>(createdEspecie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')")
    public ResponseEntity<EspecieDTO> updateEspecie(@PathVariable Long id, @RequestBody EspecieDTO especieDTO) {
        EspecieDTO updatedEspecie = especieService.updateEspecie(id, especieDTO);
        return ResponseEntity.ok(updatedEspecie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')")
    public ResponseEntity<Void> deleteEspecie(@PathVariable Long id) {
        especieService.deleteEspecie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<EspecieDTO> getEspecieByNombre(@PathVariable String nombre) {
        EspecieDTO especie = especieService.findEspecieByNombre(nombre)
                                        .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada con nombre: " + nombre));
        return ResponseEntity.ok(especie);
    }
}
