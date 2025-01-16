package JaySports.repository;

import JaySports.model.ParametroComercio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametroComercioRepository extends JpaRepository<ParametroComercio, Long> {
    Optional<ParametroComercio> findByClave(String clave);
}

