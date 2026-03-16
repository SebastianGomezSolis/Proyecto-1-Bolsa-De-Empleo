package una.sistema.proyecto1bolsadeempleo.logic.model;

public class Nacionalidad {
    private String iso;
    private String nombre;
    private String descripcion;
    private String iso3;
    private int codigoNumero;
    private int codigoTelefono;

    public Nacionalidad(int codigoNumero, int codigoTelefono, String iso3, String descripcion, String iso, String nombre) {
        this.codigoNumero = codigoNumero;
        this.codigoTelefono = codigoTelefono;
        this.iso3 = iso3;
        this.descripcion = descripcion;
        this.iso = iso;
        this.nombre = nombre;
    }

    public int getCodigoNumero() {
        return codigoNumero;
    }

    public void setCodigoNumero(int codigoNumero) {
        this.codigoNumero = codigoNumero;
    }

    public int getCodigoTelefono() {
        return codigoTelefono;
    }

    public void setCodigoTelefono(int codigoTelefono) {
        this.codigoTelefono = codigoTelefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
