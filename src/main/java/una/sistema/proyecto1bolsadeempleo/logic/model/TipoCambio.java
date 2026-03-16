package una.sistema.proyecto1bolsadeempleo.logic.model;

public class TipoCambio {
    public double venta;
    public double compra;
    public String tipoModena;

    public TipoCambio(double compra, double venta, String tipoModena) {
        this.compra = compra;
        this.tipoModena = tipoModena;
        this.venta = venta;
    }

    public double getCompra() { return compra; }
    public String getTipoModena() { return tipoModena; }
    public double getVenta() { return venta; }

    public void setCompra(double compra) { this.compra = compra; }
    public void setTipoModena(String tipoModena) { this.tipoModena = tipoModena; }
    public void setVenta(double venta) { this.venta = venta; }

}
