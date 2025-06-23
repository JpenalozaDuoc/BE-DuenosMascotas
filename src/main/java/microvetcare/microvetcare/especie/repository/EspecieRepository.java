package microvetcare.microvetcare.especie.repository;
import microvetcare.microvetcare.especie.entity.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface EspecieRepository extends JpaRepository<Especie, Long> {

    // JpaRepository ya proporciona los métodos CRUD básicos (save, findById, findAll, deleteById, etc.)

    // Puedes añadir métodos personalizados si necesitas buscar por otros atributos.
    // Por ejemplo, para buscar una especie por su nombre (y garantizar unicidad si es necesario)
    Optional<Especie> findByNombre(String nombre);

    // Para verificar si una especie con un nombre dado ya existe
    boolean existsByNombre(String nombre);
    
    boolean existsByNombreEspecie(String nombreEspecie);

}
