package JaySports.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "pedido_completados")
public class PedidoCompletado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String ticketExt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private double importe;

    private Long pagoId;

    private Long pedidoId;

    private String tarjeta;

    private String estadoPago;

    private String comercioNombre;

    private String tarjetaPagoNumero;

    public PedidoCompletado() {}

    public PedidoCompletado(String ticketExt) {
        this.ticketExt = ticketExt;
        this.fecha = Date.from(LocalDate.of(2000, 12, 12).atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.importe = 0.0;
    }


    public PedidoCompletado(String ticketExt, Date fecha, double importe, Long pagoId, Long pedidoId, String tarjeta, String estadoPago, String comercioNombre, String tarjetaPagoNumero) {
        this.ticketExt = ticketExt;
        this.fecha = fecha;
        this.importe = importe;
        this.pagoId = pagoId;
        this.pedidoId = pedidoId;
        this.tarjeta = tarjeta;
        this.estadoPago = estadoPago;
        this.comercioNombre = comercioNombre;
        this.tarjetaPagoNumero = tarjetaPagoNumero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketExt() {
        return ticketExt;
    }

    public void setTicketExt(String ticketExt) {
        this.ticketExt = ticketExt;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getComercioNombre() {
        return comercioNombre;
    }

    public void setComercioNombre(String comercioNombre) {
        this.comercioNombre = comercioNombre;
    }

    public String getTarjetaPagoNumero() {
        return tarjetaPagoNumero;
    }

    public void setTarjetaPagoNumero(String tarjetaPagoNumero) {
        this.tarjetaPagoNumero = tarjetaPagoNumero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PedidoCompletado that = (PedidoCompletado) o;

        // Si ambos objetos tienen un ID no nulo, comparamos por ID
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        // Si no se pueden comparar por ID, consideramos que son diferentes
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

