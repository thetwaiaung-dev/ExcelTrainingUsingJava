package TWAJavaTraining.ExcelTraining.Reports;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import TWAJavaTraining.ExcelTraining.ExcelTrainingApplication;
import TWAJavaTraining.ExcelTraining.Models.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;

public class UserExcelReporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<User> listUsers;

    public UserExcelReporter(List<User> listUsers) {
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
    }

    private Map<Integer, String> writeHeaderLine() throws IOException {
        sheet = workbook.createSheet("UserList");

        // set image
        InputStream inputStream = ExcelTrainingApplication.class.getClassLoader()
                .getResourceAsStream("animeWallpaper.jpg");
        byte[] inputImageBytes = IOUtils.toByteArray(inputStream);
        int inputImagePicture = workbook.addPicture(inputImageBytes, Workbook.PICTURE_TYPE_JPEG);

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor animeImageAnchor = new XSSFClientAnchor();

        animeImageAnchor.setCol1(4);
        animeImageAnchor.setCol2(6);
        animeImageAnchor.setRow1(0);
        animeImageAnchor.setRow2(2);
        drawing.createPicture(animeImageAnchor, inputImagePicture);

        // for autosize columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // Header Line
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = workbook.createCellStyle();
        // header row height
        headerRow.setHeight((short) 1000);
        // Set font
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight(16);
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);    
        // set alignment
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        MergeCell(headerRow, 0, "User List", headerCellStyle,
                new CellRangeAddress(0, 0, 0, 3));

        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        Map<Integer, String> dynamicColumn = new HashMap<>();

        createCell(row, 0, "User_Id", style);
        createCell(row, 1, "Name", style);
        createCell(row, 2, "Phone", style);
        createCell(row, 3, "Address", style);

        for (int i = 4; i < 6; i++) {
            createCell(row, i, "Income", style);
            dynamicColumn.put(i, "Income");
        }

        return dynamicColumn;

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Short) {
            cell.setCellValue((Short) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else
            cell.setCellValue((String) value);

        cell.setCellStyle(style);
    }

    private void writeDataLines() throws IOException {
        int rowCount = 2;

        Map<Integer, String> dynamicColumns = writeHeaderLine();

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        // addressHeader cell style
        CellStyle addressHeaderCellStyle = workbook.createCellStyle();
        addressHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
        addressHeaderCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        String address = null;
        int index = 0;
        Map<Integer, Double> totalIncome = new HashMap<>();

        for (User user : listUsers) {
            if (index == 0) {
                address = user.getAddress();
                index++;
            }
            if (!Objects.equals(address, user.getAddress())) {
                Row addressHeaderRow = sheet.createRow(rowCount);
                addressHeaderRow.setHeight((short) 600);
                MergeCell(addressHeaderRow, 0, "Address(" + address + ")", addressHeaderCellStyle,
                        new CellRangeAddress(rowCount, rowCount, 0, 3));

                Iterator<Map.Entry<Integer, Double>> iterator = totalIncome.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Double> entry = iterator.next();
                    Integer key = entry.getKey();
                    Double value = entry.getValue();
                    String stringValue = String.valueOf(value);

                    createCell(addressHeaderRow, key, stringValue, addressHeaderCellStyle);
                }
                totalIncome.clear();
                rowCount++;
                address = user.getAddress();
            }

            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, user.getId(), style);
            createCell(row, columnCount++, user.getName(), style);
            createCell(row, columnCount++, user.getPhone(), style);
            createCell(row, columnCount++, user.getAddress(), style);

            Iterator<Map.Entry<Integer, String>> iterator = dynamicColumns.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                Integer key = entry.getKey();
                String incomeValue = String.valueOf(user.getIncome());
                createCell(row, key, incomeValue, style);

                Double income = totalIncome.get(key);
                if (income != null) {
                    income += user.getIncome();

                    totalIncome.replace(key, income);
                } else {
                    income = user.getIncome();
                    totalIncome.put(key, income);
                }
            }
        }

        Row addressHeaderRow = sheet.createRow(rowCount);
        addressHeaderRow.setHeight((short) 600);
        MergeCell(addressHeaderRow, 0, "Address(" + address + ")", addressHeaderCellStyle,
                new CellRangeAddress(rowCount, rowCount, 0, 3));

        Iterator<Map.Entry<Integer, Double>> iterator = totalIncome.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Double> entry = iterator.next();
            Integer key = entry.getKey();
            Double value = entry.getValue();
            String stringValue = String.valueOf(value);

            createCell(addressHeaderRow, key, stringValue, addressHeaderCellStyle);
        }
    }

    private void MergeCell(Row row, int cellIndex, String value, CellStyle style, CellRangeAddress rangeAddress) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        sheet.addMergedRegion(rangeAddress);
    }

    public void export(HttpServletResponse response) throws IOException {
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

}
