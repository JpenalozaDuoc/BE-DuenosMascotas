package microvetcare.microvetcare.mascota.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.mascota.entity.Mascota;
import microvetcare.microvetcare.raza.entity.Raza;

public interface MascotaRepository extends JpaRepository <Mascota, Long> {
    // JpaRepository ya proporciona los métodos CRUD básicos.

    // Métodos personalizados:
    // Buscar mascotas por nombre
    List<Mascota> findByNombre(String nombre);

    // Buscar mascotas por dueño
    List<Mascota> findByDueno(Dueno dueno);
    List<Mascota> findByDuenoId(Long duenoId); // Conveniente para buscar por ID de dueño

    // Buscar mascotas por raza
    List<Mascota> findByRaza(Raza raza);
    List<Mascota> findByRazaId(Long razaId); // Conveniente para buscar por ID de raza

    // Buscar mascotas por género
    List<Mascota> findByGenero(String genero);

    // Buscar mascotas nacidas después de una fecha
    List<Mascota> findByFechaNacimientoAfter(LocalDate fecha);

    // Buscar mascotas nacidas antes de una fecha
    List<Mascota> findByFechaNacimientoBefore(LocalDate fecha);

    // Puedes combinar criterios, por ejemplo:
    // List<Mascota> findByDuenoIdAndRazaId(Long duenoId, Long razaId);
    // List<Mascota> findByDuenoIdAndSexo(Long duenoId, String sexo);

}
