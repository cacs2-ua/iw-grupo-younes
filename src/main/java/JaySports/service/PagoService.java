package JaySports.service;


import JaySports.dto.PedidoCompletoRequest;
import JaySports.model.PedidoCompletado;
import JaySports.repository.PedidoCompletadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PagoService {



    @Autowired
    private PedidoCompletadoRepository pedidoCompletadoRepository;



    /**
     * Procesa el PedidoCompletoRequest que llega desde el TPVV (servidor).
     * Ahora fecha e importe son String en el DTO, así que se parsean aquí
     * antes de persistir en la base de datos.
     */
    public void procesarPedido(PedidoCompletoRequest request) {

        Long pagoId = request.getPagoId();
        Long pedidoId = request.getPedidoId();
        String ticketId = request.getTicketExt();

        // MODIFICADO: parsear fecha (String -> Date)
        Date fechaDate = null;
        try {
            String fechaStr = request.getFecha();
            if (fechaStr != null && !fechaStr.isBlank()) {
                // Ajusta el formato a como se envía desde el servidor (dd/MM/yyyy HH:mm)
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                fechaDate = sdf.parse(fechaStr);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha no válido en PedidoCompletoRequest.");
        }

        // MODIFICADO: parsear importe (String -> double)
        double importeDouble;
        try {
            String importeStr = request.getImporte();
            importeDouble = Double.parseDouble(importeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: Importe no válido en PedidoCompletoRequest.");
        }

        String estadoPago = request.getEstadoPago();
        String comercioNombre = request.getComercioNombre();
        String tarjeta = request.getNumeroTarjeta();

        // Construimos el PedidoCompletado con los tipos nativos para la BD
        PedidoCompletado pedidoBD = new PedidoCompletado(
                ticketId,
                fechaDate,
                importeDouble,
                pagoId,
                pedidoId,
                tarjeta,
                estadoPago,
                comercioNombre,
                tarjeta
        );

        // Guardar en BD
        pedidoCompletadoRepository.save(pedidoBD);
    }
}
