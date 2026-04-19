package una.sistema.proyecto1bolsadeempleo.logic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "puesto")
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "salario", nullable = false)
    private Double salario;

    @NotNull
    @Column(name = "tipoPublicacion", nullable = false, length = 20)
    private String tipoPublicacion = "publico";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @NotNull
    @Column(name = "fechaRegistro", nullable = false, updatable = false)
    private Instant fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = Instant.now();
        }
    }

    @OneToMany(mappedBy = "puesto", fetch = FetchType.LAZY)
    private List<PuestoCaracteristica> caracteristicas;

}