package JaySports.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroPedido;

    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Double total;


    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ProductoPedido> productosPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    Set<Pago> pagos = new HashSet<>();

    // Constructor vacío
    public Pedido() {
    }

    public Pedido(String numeroPedido, LocalDateTime fechaPedido, String estado, Double total) {
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
    }

    // Constructor con parámetros
    public Pedido(Long id, String numeroPedido, LocalDateTime fechaPedido, String estado, Double total, Usuario usuario, List<ProductoPedido> productosPedido) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.usuario = usuario;
        this.productosPedido = productosPedido;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ProductoPedido> getProductosPedido() {
        return productosPedido;
    }

    public void setProductosPedido(List<ProductoPedido> productosPedido) {
        this.productosPedido = productosPedido;
    }

    public Set<Pago> getPagos() {
        return pagos;
    }

    public void addPago(Pago pago) {
        if (pagos.contains(pago)) return;
        pagos.add(pago);
        if (pago.getPedido() != this) {
            pago.setPedido(this);
        }
    }
}
