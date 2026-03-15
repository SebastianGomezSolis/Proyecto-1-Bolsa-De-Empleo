package una.sistema.proyecto1bolsadeempleo.logic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nacionalidad")
public class Nacionalidad {
    @Id
    @Size(max = 5)
    @Column(name = "iso", nullable = false, length = 5)
    private String iso;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 5)
    @Column(name = "iso3", length = 5)
    private String iso3;

    @Column(name = "codigoNumero")
    private Integer codigoNumero;

    @Column(name = "codigoTelefono")
    private Integer codigoTelefono;


}