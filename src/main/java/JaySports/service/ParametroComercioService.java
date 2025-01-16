package JaySports.service;



import JaySports.model.ParametroComercio;
import JaySports.repository.ParametroComercioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParametroComercioService {

    @Autowired
    private ParametroComercioRepository parametroComercioRepository;

    /**
     * Obtiene el valor de un parámetro por su clave.
     *
     * @param clave La clave del parámetro.
     * @return El valor del parámetro si existe, de lo contrario, Optional vacío.
     */
    public Optional<String> getValorParametro(String clave) {
        return parametroComercioRepository.findByClave(clave).map(ParametroComercio::getValor);
    }
}

