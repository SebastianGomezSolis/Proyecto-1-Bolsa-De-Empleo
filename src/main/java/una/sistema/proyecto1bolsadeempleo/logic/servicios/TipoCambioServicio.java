package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.json.JSONObject;
import una.sistema.proyecto1bolsadeempleo.logic.model.TipoCambio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TipoCambioServicio {
    private static final String API_URL = "https://api.hacienda.go.cr/indicadores/tc/dolar";

    public TipoCambio obtenerTipoCambio() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conexion.getInputStream())
            );

            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                respuesta.append(linea);
            }
            reader.close();

            JSONObject json = new JSONObject(respuesta.toString());

            // La API devuelve objetos anidados: {"fecha":"...","valor":463.24}
            double compra = json.getJSONObject("compra").getDouble("valor");
            double venta  = json.getJSONObject("venta").getDouble("valor");

            return new TipoCambio(compra, venta, "USD");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
