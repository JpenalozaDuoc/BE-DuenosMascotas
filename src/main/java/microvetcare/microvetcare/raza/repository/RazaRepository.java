package microvetcare.microvetcare.raza.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import microvetcare.microvetcare.especie.entity.Especie;
import microvetcare.microvetcare.raza.entity.Raza;
import java.util.List; // Para buscar por lista de razas por especie
import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, Long> {

   // JpaRepository ya proporciona los métodos CRUD básicos.

    // Métodos personalizados:
    Optional<Raza> findByNombre(String nombre);
    boolean existsByNombre(String nombre);

    // Buscar razas por una especie específica
    List<Raza> findByEspecie(Especie especie);
    List<Raza> findByEspecieId(Long especieId);

}
