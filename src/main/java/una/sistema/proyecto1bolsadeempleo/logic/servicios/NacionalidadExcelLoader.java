package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import una.sistema.proyecto1bolsadeempleo.logic.model.Nacionalidad;

import java.io.InputStream;

@Component
public class NacionalidadExcelLoader implements CommandLineRunner {

    @Autowired
    private NacionalidadService nacionalidadService;

    @Override
    public void run(String... args) throws Exception {

        // Si la tabla ya tiene datos, no se vuelve a cargar el Excel
        if (nacionalidadService.count() > 0) {
            return;
        }

        // Se abre el archivo Excel desde resources
        ClassPathResource resource = new ClassPathResource("nacionalidades.xlsx");

        try (InputStream inputStream = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {

                // Se omite la fila de encabezado
                if (row.getRowNum() == 0) {
                    continue;
                }

                String iso = formatter.formatCellValue(row.getCell(0)).trim();
                String nombre = formatter.formatCellValue(row.getCell(1)).trim();
                String descripcion = formatter.formatCellValue(row.getCell(2)).trim();
                String iso3 = formatter.formatCellValue(row.getCell(3)).trim();
                String codigoNumeroTexto = formatter.formatCellValue(row.getCell(4)).trim();
                String codigoTelefonoTexto = formatter.formatCellValue(row.getCell(5)).trim();

                // Si no hay ISO o nombre, la fila no se procesa
                if (iso.isBlank() || nombre.isBlank()) {
                    continue;
                }

                Nacionalidad nacionalidad = new Nacionalidad();
                nacionalidad.setIso(iso);
                nacionalidad.setNombre(nombre);
                nacionalidad.setDescripcion(descripcion.isBlank() ? null : descripcion);
                nacionalidad.setIso3(iso3.isBlank() ? null : iso3);
                nacionalidad.setCodigoNumero(parseEntero(codigoNumeroTexto));
                nacionalidad.setCodigoTelefono(parseEntero(codigoTelefonoTexto));

                // Se guarda en la base de datos
                nacionalidadService.save(nacionalidad);
            }
        }
    }

    private Integer parseEntero(String valor) {

        // Si viene vacío, se guarda como null
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            // DataFormatter ya suele devolverlo limpio, pero igual se asegura
            return Integer.parseInt(valor.replace(".0", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}