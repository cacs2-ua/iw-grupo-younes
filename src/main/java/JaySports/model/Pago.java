package JaySports.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "pagos")
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private  String ticketExt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @NotNull
    private double total;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull
    private String estado;

    @NotNull
    private String razonEstado;

    @NotNull
    private String cvcTarjeta;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCaducidadTarjeta;

    @NotNull
    private String nombreTarjeta;

    @NotNull
    private String numeroTarjeta;

    public Pago() {}

    public Pago(String ticketExt) {
        this.ticketExt = ticketExt;
        this.fecha = Date.from(LocalDate.of(2000, 12, 12).atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.total = 0.0;

    }

    public Pago(String ticketExt, Date fecha, double total, String estado) {
        this.ticketExt = ticketExt;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public Pago(
                String ticketExt,
                Date fecha,
                double total,
                String estado,
                String razonEstado,
                String cvcTarjeta,
                Date fechaCaducidadTarjeta,
                String nombreTarjeta,
                String numeroTarjeta) {

        this.ticketExt = ticketExt;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.razonEstado = razonEstado;
        this.cvcTarjeta = cvcTarjeta;
        this.fechaCaducidadTarjeta = fechaCaducidadTarjeta;
        this.nombreTarjeta = nombreTarjeta;
        this.numeroTarjeta = numeroTarjeta;

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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getRazonEstado() {
        return razonEstado;
    }

    public void setRazonEstado(String razonEstado) {
        this.razonEstado = razonEstado;
    }

    public String getCvcTarjeta() {
        return cvcTarjeta;
    }

    public void setCvcTarjeta(String cvcTarjeta) {
        this.cvcTarjeta = cvcTarjeta;
    }

    public Date getFechaCaducidadTarjeta() {
        return fechaCaducidadTarjeta;
    }

    public void setFechaCaducidadTarjeta(Date fechaCaducidadTarjeta) {
        this.fechaCaducidadTarjeta = fechaCaducidadTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pago that = (Pago) o;

        // Si ambos objetos tienen un ID no nulo, comparamos por ID
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        // Si no se pueden comparar por ID, consideramos que son diferentes
        return false;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {

        if (this.pedido == pedido || pedido == null) {
            return;
        }

        if (this.pedido != null) {
            this.pedido.getPagos().remove(this);
        }

        this.pedido = pedido;

        if (!pedido.getPagos().contains(this)) {
            pedido.addPago(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
