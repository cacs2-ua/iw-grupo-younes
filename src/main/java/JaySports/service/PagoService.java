package JaySports.service;


import JaySports.dto.PedidoCompletoRequest;
import JaySports.model.*;
import JaySports.repository.PagoRepository;
import JaySports.repository.PedidoCompletadoRepository;
import JaySports.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

@Service
public class PagoService {



    @Autowired
    private PedidoCompletadoRepository pedidoCompletadoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private  CarritoService carritoService;
    @Autowired
    private PagoRepository pagoRepository;


    /**
     * Procesa el PedidoCompletoRequest que llega desde el TPVV (servidor).
     * Ahora fecha e importe son String en el DTO, así que se parsean aquí
     * antes de persistir en la base de datos.
     */
    public void procesarPedido(PedidoCompletoRequest request) {

        String ticketId = request.getTicketExt();
        /*
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

         */

        Optional<Pedido> pedidoDB = pedidoRepository.findByNumeroPedido(ticketId);

        if (!pedidoDB.isPresent()) {
            throw new IllegalArgumentException("Error: Pedido no encontrado en la Base de Datos.");
        }

        String paymentState = request.getEstadoPago();

        if (paymentState.startsWith("ACEPT")) {

            pedidoDB.get().setEstado("COMPLETADO");
            pedidoRepository.save(pedidoDB.get());

            Usuario usuario = pedidoDB.get().getUsuario();
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);

            carritoService.vaciarCarrito(carrito);
        }


        // CREAR EL PAGO

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
        String razonEstadoPago = request.getRazonEstadoPago();
        String cvcTarjeta = request.getCvcTarjeta();

        Date fechaCaducidadTarjeta = null;
        try {
            String fechaStr = request.getFechaCaducidadTarjeta();
            if (fechaStr != null && !fechaStr.isBlank()) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
                fechaCaducidadTarjeta = sdf.parse(fechaStr);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha no válido en PedidoCompletoRequest.");
        }


        String nombreTarjeta = request.getNombreTarjeta();
        String numeroTarjeta = request.getNumeroTarjeta();

        Pago pagoDB = new Pago(
                ticketId,
                fechaDate,
                importeDouble,
                estadoPago,
                razonEstadoPago,
                cvcTarjeta,
                fechaCaducidadTarjeta,
                nombreTarjeta,
                numeroTarjeta

        );

        pagoDB.setPedido(pedidoDB.get());

        pagoRepository.save(pagoDB);

    }
}
