package JaySports.repository;

import JaySports.model.PedidoCompletado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoCompletadoRepository extends JpaRepository<PedidoCompletado, Long> {
}
