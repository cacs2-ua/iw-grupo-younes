package JaySports.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "parametros_comercio")
public class ParametroComercio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String clave;

    @NotNull
    private String valor;

    // Constructores
    public ParametroComercio() {}

    public ParametroComercio(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    // Equals y HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParametroComercio parametro = (ParametroComercio) o;

        return Objects.equals(id, parametro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


