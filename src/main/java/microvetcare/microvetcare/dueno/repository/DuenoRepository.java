package microvetcare.microvetcare.dueno.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import microvetcare.microvetcare.dueno.entity.Dueno;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

        // Método para verificar la existencia por RUT (útil para validaciones antes de guardar)
    boolean existsByRut(String rut);

    // Opcional: Métodos para buscar por RUT o Email, que son identificadores únicos según tu EDR
    // Es buena práctica devolver Optional<Dueno> para indicar que el resultado podría no existir.
    Optional<Dueno> findByRut(String rut);
    // Asumiendo que 'email' también es único como se sugirió en el diseño de Dueno.java
    Optional<Dueno> findByEmail(String email);
    // MÉTODO AÑADIDO:
    boolean existsByEmail(String email); // <-- ¡Este era el que faltaba!
    // Puedes agregar más métodos si los necesitas, Spring Data JPA es muy potente
    // List<Dueno> findByEstado(String estado);

}
