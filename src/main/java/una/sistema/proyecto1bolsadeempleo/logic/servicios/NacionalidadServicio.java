package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import una.sistema.proyecto1bolsadeempleo.logic.model.Nacionalidad;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class NacionalidadServicio {

    private List<Nacionalidad> nacionalidades = new ArrayList<>();

    public NacionalidadServicio() {
        cargarNacionalidades();
    }

    public void cargarNacionalidades() {
        try {
            FileInputStream file = new FileInputStream(new File("nacionalidades.xlsx"));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String iso               = formatter.formatCellValue(row.getCell(0));
                String nombre            = formatter.formatCellValue(row.getCell(2));
                String iso3              = formatter.formatCellValue(row.getCell(3));
                String codigoNumeroStr   = formatter.formatCellValue(row.getCell(4));
                String codigoTelefonoStr = formatter.formatCellValue(row.getCell(5));
                String descripcion       = "";

                if (nombre == null || nombre.isEmpty()) continue;

                int codigoNumero   = 0;
                int codigoTelefono = 0;
                try {
                    if (!codigoNumeroStr.isEmpty())
                        codigoNumero = (int) Double.parseDouble(codigoNumeroStr);
                    if (!codigoTelefonoStr.isEmpty())
                        codigoTelefono = (int) Double.parseDouble(codigoTelefonoStr);
                } catch (NumberFormatException e) {}

                nacionalidades.add(new Nacionalidad(codigoNumero, codigoTelefono, iso3, descripcion, iso, nombre));
            }

            workbook.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Nacionalidad> obtenerNacionalidades() {
        return nacionalidades;
    }

    public Nacionalidad buscarPorIso(String iso) {
        for (Nacionalidad n : nacionalidades) {
            if (n.getIso().equalsIgnoreCase(iso)) {
                return n;
            }
        }
        return null;
    }
}