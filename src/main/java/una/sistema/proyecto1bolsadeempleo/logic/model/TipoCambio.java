package una.sistema.proyecto1bolsadeempleo.logic.model;

public class TipoCambio {
    public double venta;
    public double compra;
    public String tipoMoneda;

    public TipoCambio(double compra, double venta, String tipoMoneda) {
        this.compra = compra;
        this.tipoMoneda = tipoMoneda;
        this.venta = venta;
    }

    public double getCompra() { return compra; }
    public String getTipoMoneda() { return tipoMoneda; }
    public double getVenta() { return venta; }

    public void setCompra(double compra) { this.compra = compra; }
    public void setTipoMoneda(String tipoMoneda) { this.tipoMoneda = tipoMoneda; }
    public void setVenta(double venta) { this.venta = venta; }

}
