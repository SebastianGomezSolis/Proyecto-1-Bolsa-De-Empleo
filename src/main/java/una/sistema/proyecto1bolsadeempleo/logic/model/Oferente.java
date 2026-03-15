package una.sistema.proyecto1bolsadeempleo.logic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "oferente")
public class Oferente {
    @Id
    @Size(max = 20)
    @Column(name = "identificacion", nullable = false, length = 20)
    private String identificacion;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 100)
    @NotNull
    @Column(name = "primerApellido", nullable = false, length = 100)
    private String primerApellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nacionalidad")
    private Nacionalidad nacionalidad;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 100)
    @NotNull
    @Column(name = "correo", nullable = false, length = 100)
    private String correo;

    @Size(max = 150)
    @Column(name = "lugarResidencia", length = 150)
    private String lugarResidencia;

    @Size(max = 255)
    @NotNull
    @Column(name = "clave", nullable = false)
    private String clave;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "autorizado", nullable = false)
    private Boolean autorizado;

    @Size(max = 255)
    @Column(name = "curriculum")
    private String curriculum;


}