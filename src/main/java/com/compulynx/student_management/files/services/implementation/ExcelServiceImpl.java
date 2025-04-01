package com.compulynx.student_management.files.services.implementation;

/**
 * @author Stanley Mungai
 */

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

public class ExcelServiceImpl {
    @Value("${spring.file.path}")
    private static final String basePath = "";

    public static void generateStudentData(int recordCount) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        String[] columns = {"studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        Random random = new Random();
        String[] classes = {"Class1", "Class2", "Class3", "Class4", "Class5"};

        for (int i = 1; i <= recordCount; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue(generateRandomString(random, 3, 8));
            row.createCell(2).setCellValue(generateRandomString(random, 3, 8));
            row.createCell(3).setCellValue("200" + random.nextInt(10) + "-0" + (1 + random.nextInt(9)) + "-0" + (1 + random.nextInt(9)));
            row.createCell(4).setCellValue(classes[random.nextInt(classes.length)]);
            row.createCell(5).setCellValue(55 + random.nextInt(31));
            row.createCell(6).setCellValue(1);
            row.createCell(7).setCellValue("");
        }

        String filePath = basePath.concat("dataprocessing/students.xlsx");
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private static String generateRandomString(Random random, int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        return random.ints(97, 123)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

