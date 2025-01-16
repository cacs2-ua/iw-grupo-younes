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

        LocalDateTime fechaPedido = null;
        try {
            String fechaStr = request.getFechaPedido(); // Obtenemos el string de la fecha
            if (fechaStr != null && !fechaStr.isBlank()) {
                // Ajusta el formato al patrón de la fecha recibida (dd/MM/yyyy HH:mm)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                fechaPedido = LocalDateTime.parse(fechaStr, formatter); // Parseamos la fecha
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha no válido en PedidoCompletoRequest.", e);
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
        Pago pagoDB = new Pago(
                ticketId,
                fechaDate,
                importeDouble,
                estadoPago

        );

        pagoRepository.save(pagoDB);



        Optional<Pedido> pedidoDB = pedidoRepository.findByNumeroPedido(ticketId);

        if (pedidoDB.isPresent()) {
            pedidoDB.get().setEstado("COMPLETADO");
            pedidoRepository.save(pedidoDB.get());
        }

        else {
            throw new IllegalArgumentException("Error: Pedido no encontrado en la Base de Datos.");
        }

        Usuario usuario = pedidoDB.get().getUsuario();

        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);

        carritoService.vaciarCarrito(carrito);

    }
}
