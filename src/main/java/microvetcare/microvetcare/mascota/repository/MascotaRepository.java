package microvetcare.microvetcare.mascota.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importar Optional
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar Query

import microvetcare.microvetcare.dueno.entity.Dueno;
import microvetcare.microvetcare.mascota.entity.Mascota;
import microvetcare.microvetcare.raza.entity.Raza;

public interface MascotaRepository extends JpaRepository <Mascota, Long> {

    // --- NUEVOS MÉTODOS CON JOIN FETCH PARA OPTIMIZAR CONSULTAS ---
    @Query("SELECT m FROM Mascota m JOIN FETCH m.dueno JOIN FETCH m.raza")
    List<Mascota> findAllWithDuenoAndRaza();

    @Query("SELECT m FROM Mascota m JOIN FETCH m.dueno JOIN FETCH m.raza WHERE m.id = :id")
    Optional<Mascota> findByIdWithDuenoAndRaza(Long id);
    // -------------------------------------------------------------

    List<Mascota> findByNombre(String nombre);
    List<Mascota> findByDueno(Dueno dueno); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByDuenoId(Long duenoId); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByRaza(Raza raza); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByRazaId(Long razaId); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByGenero(String genero); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByFechaNacimientoAfter(LocalDate fecha); // Considera si quieres optimizar con JOIN FETCH aquí también
    List<Mascota> findByFechaNacimientoBefore(LocalDate fecha); // Considera si quieres optimizar con JOIN FETCH aquí también

}

/*
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
*/