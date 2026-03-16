package una.sistema.proyecto1bolsadeempleo.logic.servicios;

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

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue; // saltar encabezado

                int codigoNumero = (int) row.getCell(0).getNumericCellValue();
                int codigoTelefono = (int) row.getCell(1).getNumericCellValue();
                String iso3 = row.getCell(2).getStringCellValue();
                String descripcion = row.getCell(3).getStringCellValue();
                String iso = row.getCell(4).getStringCellValue();
                String nombre = row.getCell(5).getStringCellValue();

                Nacionalidad nacionalidad = new Nacionalidad(
                        codigoNumero,
                        codigoTelefono,
                        iso3,
                        descripcion,
                        iso,
                        nombre
                );

                nacionalidades.add(nacionalidad);
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