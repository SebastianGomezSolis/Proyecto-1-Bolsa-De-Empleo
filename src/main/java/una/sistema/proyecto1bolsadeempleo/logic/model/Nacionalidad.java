package una.sistema.proyecto1bolsadeempleo.logic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nacionalidad")
public class Nacionalidad {

    @Id
    @Column(name = "iso", nullable = false, length = 5)
    private String iso;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "iso3", length = 5)
    private String iso3;

    @Column(name = "codigoNumero")
    private Integer codigoNumero;

    @Column(name = "codigoTelefono")
    private Integer codigoTelefono;
}