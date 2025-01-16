package JaySports.dto;


/**
 * Form que agrupa los campos del pago + los campos de la tarjeta.
 */
public class PagoCompletoForm {

    private String importe;   // era Double
    private String ticketExt;
    private String fecha;     // era Date

    private String nombre;
    private String numeroTarjeta;
    private String caducidad; // "mm/aa"
    private String cvc;       // "123"

    public PagoCompletoForm() {}

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getTicketExt() {
        return ticketExt;
    }

    public void setTicketExt(String ticketExt) {
        this.ticketExt = ticketExt;
    }
}

