package microvetcare.microvetcare.mascota.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.mascota.entity.Mascota;
import microvetcare.microvetcare.raza.entity.Raza;

public interface MascotaRepository extends JpaRepository <Mascota, Long> {

    List<Mascota> findByNombre(String nombre);
    List<Mascota> findByDueno(Dueno dueno);
    List<Mascota> findByDuenoId(Long duenoId); 
    List<Mascota> findByRaza(Raza raza);
    List<Mascota> findByRazaId(Long razaId);
    List<Mascota> findByGenero(String genero);
    List<Mascota> findByFechaNacimientoAfter(LocalDate fecha);
    List<Mascota> findByFechaNacimientoBefore(LocalDate fecha);

}
