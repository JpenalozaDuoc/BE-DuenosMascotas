package microvetcare.microvetcare.dueno.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import microvetcare.microvetcare.dueno.entity.Dueno;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    boolean existsByRut(String rut);
    Optional<Dueno> findByRut(String rut);
    Optional<Dueno> findByEmail(String email);
    boolean existsByEmail(String email); 

}
