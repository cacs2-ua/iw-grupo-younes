package JaySports.controller;



import JaySports.dto.PagoCompletoForm;
import JaySports.service.PagoService;
import JaySports.service.ParametroComercioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import JaySports.dto.PagoCompletoRequest;
import JaySports.dto.PedidoCompletoRequest;
import JaySports.dto.PagoData;
import JaySports.dto.TarjetaPagoData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/tienda")
public class StoreController {

    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private ParametroComercioService parametroComercioService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PagoService pagoService;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/tienda/checkout";
    }


    @GetMapping("/pagoFormProxy")
    public String pagoFormProxy(@RequestParam("ticket") String ticket,
                                @RequestParam("precio") double precio,
                                @RequestParam("nombreComercio") String nombreComercio,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                @RequestParam(name="errors", required=false) String errors,
                                Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // Si hay errores en la query param, los mostramos
        if (errors != null && !errors.isBlank()) {
            model.addAttribute("errorMessages", errors);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        String url = "http://localhost:8123/tpvv/boardalo/pago/form?importe=" + precio
                + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String originalHtml = response.getBody();

                // Ajustamos el <form> para que apunte a nuestro /tienda/realizarPagoProxy
                String modificadoHtml = originalHtml.replace(
                        "<form class=\"card-form\">",
                        "<form class=\"card-form\" method=\"post\" action=\"/tienda/realizarPagoProxy\">"
                );

                // Añadimos los atributos name="..." a los inputs
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"Nombre Completo\">",
                        "<input type=\"text\" class=\"card-input\" name=\"nombre\" placeholder=\"Nombre Completo\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"0000 0000 0000 0000\">",
                        "<input type=\"text\" class=\"card-input\" name=\"numeroTarjeta\" placeholder=\"0000 0000 0000 0000\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input expiry-date\" placeholder=\"mm/aa\">",
                        "<input type=\"text\" class=\"card-input expiry-date\" name=\"caducidad\" placeholder=\"mm/aa\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"password\" class=\"card-input security-code\" placeholder=\"***\">",
                        "<input type=\"password\" class=\"card-input security-code\" name=\"cvc\" placeholder=\"***\">"
                );

                // Campos ocultos para Importe, Ticket, Fecha+Hora
                String hiddenFields = ""
                        + "<input type=\"hidden\" name=\"importe\" value=\"" + precio + "\"/>"
                        + "<input type=\"hidden\" name=\"ticketExt\" value=\"" + ticket + "\"/>"
                        + "<input type=\"hidden\" name=\"fecha\" value=\"" + fecha + " " + hora + "\"/>";

                modificadoHtml = modificadoHtml.replace("</form>", hiddenFields + "</form>");

                // Ajustar texto en el HTML
                String precioFormateado = String.format("%.2f", precio);
                modificadoHtml = modificadoHtml
                        .replace("218,42 €", precioFormateado + " €")
                        .replace("Paquetería", nombreComercio)
                        .replace("000000001", ticket)
                        .replace("09/12/2024", fecha)
                        .replace("10:46", hora);

                model.addAttribute("paymentFormContent", modificadoHtml);
                model.addAttribute("fullPage", false);
                return "paymentFormProxy";
            } else {
                model.addAttribute("error", "Error al obtener el formulario de pago del TPVV.");
                return "error/404";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Excepción al llamar al TPVV: " + e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/realizarPagoProxy")
    public String realizarPagoProxy(@ModelAttribute PagoCompletoForm form, Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        TarjetaPagoData tarjetaData = new TarjetaPagoData();
        tarjetaData.setNombre(form.getNombre());
        tarjetaData.setNumeroTarjeta(form.getNumeroTarjeta());
        tarjetaData.setCvc(form.getCvc());
        tarjetaData.setFechaCaducidad(form.getCaducidad());

        PagoData pagoData = new PagoData();
        pagoData.setImporte(form.getImporte());
        pagoData.setTicketExt(form.getTicketExt());
        pagoData.setFecha(form.getFecha());

        PagoCompletoRequest requestBody = new PagoCompletoRequest(pagoData, tarjetaData);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PagoCompletoRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        String urlTPVV = "http://localhost:8123/tpvv/boardalo/pago/realizar";

        try {

            ResponseEntity<String> response = restTemplate.postForEntity(urlTPVV, requestEntity, String.class);

            String body = response.getBody() != null ? response.getBody() : "";

            if (body.startsWith("ERROR|")) {
                // Tomamos el substring después de "ERROR|" como mensaje
                String erroresTPVV = body.substring("ERROR|".length()).trim();

                // Redirigir a pagoFormProxy con ?errors=...
                String redirectUrl = "redirect:/tienda/pagoFormProxy?ticket="
                        + urlEncode(form.getTicketExt())
                        + "&precio=" + urlEncode(form.getImporte())
                        + "&nombreComercio=" + urlEncode("Tienda Online v2")
                        + "&fecha=" + urlEncode(extraerSoloFecha(form.getFecha()))
                        + "&hora=" + urlEncode(extraerSoloHora(form.getFecha()))
                        + "&errors=" + urlEncode(erroresTPVV);

                return redirectUrl;

            }

            else if (body.startsWith("RECH|")) {
                // Pago Rechazado
                String msgRech = body.substring("RECH|".length()).trim();
                model.addAttribute("msgRech", msgRech);
                return "pagoRech";
            }

            else if (body.startsWith("PEND|")) {
                // Pago Pendiente
                String msgPend = body.substring("PEND|".length()).trim();
                model.addAttribute("msgPend", msgPend);
                return "pagoPend";
            }

            else if (body.startsWith("OK|")) {
                // Pago Aceptado Correctamente
                String msgOk = body.substring("OK|".length()).trim();
                model.addAttribute("msgOk", msgOk);
                return "pagoOk";

            } else {
                // Body desconocido => error
                model.addAttribute("error", "Respuesta desconocida del TPVV: " + body);
                return "error/404";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Excepción POST a TPVV: " + e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/receivePedido")
    public ResponseEntity<String> receivePedido(@RequestBody PedidoCompletoRequest request) {
        log.debug("Recibido en la tienda un PedidoCompletoRequest: {}", request);

        try {
            pagoService.procesarPedido(request);
            return ResponseEntity.ok("Pedido recibido y guardado con éxito.");

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error 404");
        }
    }

    // ===========================================================
    // Métodos auxiliares para formatear fecha y encode
    // ===========================================================
    private String urlEncode(String raw) {
        if (raw == null) return "";
        return URLEncoder.encode(raw, StandardCharsets.UTF_8);
    }

    private String extraerSoloFecha(String fechaCompleta) {
        if (fechaCompleta == null) return "";
        String[] partes = fechaCompleta.split(" ");
        return (partes.length > 0) ? partes[0] : "";
    }

    private String extraerSoloHora(String fechaCompleta) {
        if (fechaCompleta == null) return "";
        String[] partes = fechaCompleta.split(" ");
        return (partes.length > 1) ? partes[1] : "";
    }
}

