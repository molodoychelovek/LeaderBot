package com.company.bot.bot.FilesManager;

import com.company.bot.bot.DataBase;
import net.sourceforge.htmlunit.corejs.javascript.NativeArrayIterator;
import net.sourceforge.htmlunit.corejs.javascript.ScriptRuntime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelReader {
    DataBase db = null;

    public ExcelReader(){
        this.db = new DataBase();
    }

    public String uploadFile(File exFile) {
        try {
            FileInputStream inputStr = new FileInputStream(exFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStr) ;
            XSSFSheet sheet = workbook.getSheetAt(0);

            int count_row = sheet.getPhysicalNumberOfRows();
            int count_cell = 16;

            String[][] arr = new String[count_row][count_cell];

            int row_i = 0;
            for(Row row : sheet) {
                int cell_i = 0;
                for(int cell_n = 0; cell_n < row.getLastCellNum(); cell_n++) {
                    Cell cell = row.getCell(cell_n, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    arr[row_i][cell_i] = cell.toString();
                    cell_i++;
                }
                row_i++;
            }

            workbook.close();

            for (int i = 1; i < count_row; i++) {
                // i = 1 because they use the first row as titles
                String[] rowInfo = new String[15];
                for (int n = 0; n < count_cell; n++) {
                    if(arr[i][n] != null)
                        rowInfo[n] = arr[i][n].replace(".0", "");
                }
                String url_up = "https://agency.auto.ru/sales/?client_id=" + rowInfo[11]
                        + "&vin=" + rowInfo[12];

                db.addMain(rowInfo[0], rowInfo[1], rowInfo[2], rowInfo[3], rowInfo[4], rowInfo[5], rowInfo[6],
                        rowInfo[7], rowInfo[8], rowInfo[9], rowInfo[10], rowInfo[11], rowInfo[12],  url_up,
                        rowInfo[14], "", "", "main", "", "");
            }

            return "Файл обновлен, парсинг в процессе";
        } catch (Exception e){
            e.printStackTrace();
            return "Ошибка чтения файла!";
        }
    }

    private static String[] colHead = {
            "Ссылка для проверки", "Лидер", "Дилер", "Марка", "Модель", "Комплектация"
            , "Кузов", "Привод", "id поколения (generation)", "id модификации (tech_param_id)"
            , "id кузова (configuration)", "Наш id дилера", "VIN на поднятие"
            , "Ссылка на поднятие", "Телеграм ид дилера", "Результат", "Время", "Конкурент"};

    public InputStream getFile(){
        ArrayList arr =  db.getParsInfo("main");

        if(arr.size() > 0) {
            List<Users> records = new ArrayList<Users>();
            for(int i = 0; i < arr.size(); i++) {
                HashMap<String, String> info = (HashMap<String, String>) arr.get(i);

                records.add(new Users(
                        info.get("Ссылка для проверки"), info.get("Лидер"), info.get("Дилер"), info.get("Марка"),
                        info.get("Модель"), info.get("Комплектация"), info.get("Кузов"), info.get("Привод"),
                        info.get("id поколения"), info.get("id модификации"), info.get("id кузова"), info.get("Наш id дилера"),
                        info.get("VIN на поднятие"), info.get("Ссылка на поднятие"), info.get("Телеграм ид дилера"),
                        info.get("Результат"), info.get("Время"), info.get("Конкурент")
                ));
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("БД");

            int rowNum = 1;
            int columns = 19;

            Row headerRow = sheet.createRow(0);
            for(int i = 0; i < colHead.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colHead[i]);
            }

            for (Users record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.url);
                row.createCell(1).setCellValue(record.leader);
                row.createCell(2).setCellValue(record.dealer);
                row.createCell(3).setCellValue(record.mark);
                row.createCell(4).setCellValue(record.model);
                row.createCell(5).setCellValue(record.complectation);
                row.createCell(6).setCellValue(record.cuzov);
                row.createCell(7).setCellValue(record.privod);
                row.createCell(8).setCellValue(record.generation);
                row.createCell(9).setCellValue(record.tech_param_id);
                row.createCell(10).setCellValue(record.configuration);
                row.createCell(11).setCellValue(record.our_dealer);
                row.createCell(12).setCellValue(record.VIN);
                row.createCell(13).setCellValue(record.url_up);
                row.createCell(14).setCellValue(record.telegram);
                row.createCell(15).setCellValue(record.result);
                row.createCell(16).setCellValue(record.time);
                row.createCell(17).setCellValue(record.competitor);
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns; i++) {
                sheet.autoSizeColumn(64);
            }

            try {
                // Write the output to a file
                File file = new File("export.xlsx");
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                return new FileInputStream(file);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
