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

    @NotNull
    private String estado;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;


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
        if (this.pedido == pedido) {
            return; // No hacer nada si ya están vinculados
        }

        if (this.pedido != null) {
            Pedido pedidoAnterior = this.pedido;
            this.pedido = null; // Romper la referencia
            pedidoAnterior.setPago(null); // Actualizar la relación inversa
        }

        this.pedido = pedido;

        // Vincular la relación inversa si es necesario
        if (pedido != null && pedido.getPago() != this) {
            pedido.setPago(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
