package microvetcare.microvetcare.raza.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.raza.entity.Raza;
import java.util.List; 
import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, Long> {
    Optional<Raza> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Raza> findByEspecie(Especie especie);
    List<Raza> findByEspecieId(Long especieId);

}
