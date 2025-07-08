package microvetcare.microvetcare.mascota.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import microvetcare.microvetcare.exception.ResourceNotFoundException;
import microvetcare.microvetcare.mascota.DTO.MascotaDTO;
import microvetcare.microvetcare.mascota.service.MascotaService;

import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getAllMascotas(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader // <-- ¡Añade este parámetro!
    ) {
        System.out.println("********************************************");
        System.out.println("DEBUG: Solicitud GET /api/mascotas");
        System.out.println("DEBUG: Encabezado Authorization: " + (authorizationHeader != null ? authorizationHeader.substring(0, Math.min(authorizationHeader.length(), 30)) + "..." : "No presente o vacío"));
        System.out.println("********************************************");
        List<MascotaDTO> mascotas = mascotaService.findAllMascotas();
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<MascotaDTO> getMascotaById(@PathVariable Long id) {
        MascotaDTO mascota = mascotaService.findMascotaById(id)
                                         .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
        return ResponseEntity.ok(mascota);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')") // ADMIN y ASISTENTE pueden crear mascotas
    public ResponseEntity<MascotaDTO> createMascota(@RequestBody MascotaDTO mascotaDTO,
                                                 @RequestParam(required = false) Long duenoId,
                                                 @RequestParam(required = false) Long razaId) {
        System.out.println("********************************************");
        System.out.println("Id Dueño :"+duenoId);
        System.out.println("Id Raza :"+razaId);
        System.out.println("Nombre mascota Json :"+mascotaDTO.getNombre());
        System.out.println("********************************************");
        MascotaDTO createdMascota = mascotaService.saveMascota(mascotaDTO, duenoId, razaId);  // Cambiado para trabajar con MascotaDTO
        System.out.println("********************************************");
        System.out.println("Mascota creada :"+createdMascota.getNombre());
        System.out.println("Id Mascota :"+createdMascota.getId());
        System.out.println("Nombre :"+mascotaDTO.getNombre());
        System.out.println("********************************************");
        return new ResponseEntity<>(createdMascota, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')")
    public ResponseEntity<MascotaDTO> updateMascota(@PathVariable Long id, @RequestBody MascotaDTO mascotaDTO,
                                                 @RequestParam(required = false) Long duenoId,
                                                 @RequestParam(required = false) Long razaId) {
        MascotaDTO updatedMascota = mascotaService.updateMascota(id, mascotaDTO, duenoId, razaId);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(updatedMascota);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede actualizar mascotas (ajusta si otros roles necesitan)
    public ResponseEntity<Void> deleteMascota(@PathVariable Long id) {
        mascotaService.deleteMascota(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasByNombre(@PathVariable String nombre) {
        List<MascotaDTO> mascotas = mascotaService.findMascotasByNombre(nombre);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/dueno/{duenoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasByDuenoId(@PathVariable Long duenoId) {
        List<MascotaDTO> mascotas = mascotaService.findMascotasByDuenoId(duenoId);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/raza/{razaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasByRazaId(@PathVariable Long razaId) {
        List<MascotaDTO> mascotas = mascotaService.findMascotasByRazaId(razaId);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/sexo/{sexo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasByGenero(@PathVariable String genero) {
        List<MascotaDTO> mascotas = mascotaService.findMascotasByGenero(genero);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/nacidas-despues/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasBornAfter(@PathVariable String fecha) {
        LocalDate date = LocalDate.parse(fecha); // Asume formato YYYY-MM-DD
        List<MascotaDTO> mascotas = mascotaService.findMascotasBornAfter(date);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/nacidas-antes/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<List<MascotaDTO>> getMascotasBornBefore(@PathVariable String fecha) {
        LocalDate date = LocalDate.parse(fecha); // Asume formato YYYY-MM-DD
        List<MascotaDTO> mascotas = mascotaService.findMascotasBornBefore(date);  // Cambiado para trabajar con MascotaDTO
        return ResponseEntity.ok(mascotas);
    }
}
