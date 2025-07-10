package microvetcare.microvetcare.especie.repository;
import microvetcare.microvetcare.especie.entity.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface EspecieRepository extends JpaRepository<Especie, Long> {

    Optional<Especie> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    boolean existsByNombreEspecie(String nombreEspecie);

}
