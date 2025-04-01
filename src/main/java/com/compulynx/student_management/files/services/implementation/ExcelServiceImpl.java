package com.compulynx.student_management.files.services.implementation;

import com.compulynx.student_management.files.services.ExcelService;
import com.compulynx.student_management.shared.enums.StudentClasses;
import com.compulynx.student_management.students.entities.Student;
import com.compulynx.student_management.students.repositories.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.File;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;

/**
 * @author Stanley Mungai
 */

@Service
public class ExcelServiceImpl implements ExcelService {
    private final StudentRepository studentRepository;

    @Value("${spring.file.path}")
    private String basePath;
    @Value("${spring.file.max-records}")
    private String maxFileSize;

    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    public ExcelServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void generateStudentData(int recordCount) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        String[] columns = {"studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        Random random = new Random();
        String[] classes = Arrays.stream(StudentClasses.values())
                .map(Enum::name)
                .toArray(String[]::new);

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

        String filePath = this.basePath.concat("dataprocessing/students.xlsx");

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    public void generateStudentBatchData(int recordCount) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(Integer.parseInt(maxFileSize));
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        String[] columns = {"studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        Random random = new Random();
        String[] classes = Arrays.stream(StudentClasses.values())
                .map(Enum::name)
                .toArray(String[]::new);
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

        String filePath = this.basePath.concat("dataprocessing/students.xlsx");

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } finally {
            workbook.dispose();
            workbook.close();
        }
    }

    private static String generateRandomString(Random random, int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        return random.ints(97, 123)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void processExcelToCSV(String excelFilePath, String csvFilePath) throws IOException {
        log.info("Excel path {}", excelFilePath);
        log.info("CSV path {}", csvFilePath);
        IOUtils.setByteArrayMaxOverride(200_000_000);
        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            csvFile.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            try (FileInputStream excelFile = new FileInputStream(new File(excelFilePath))) {
                Workbook workbook = new XSSFWorkbook(excelFile);
                Sheet sheet = workbook.getSheetAt(0);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                    throw new IOException("The Excel sheet is empty or invalid.");
                }
                Row header = sheet.getRow(0);
                if (header == null) {
                    throw new IOException("Header row is missing in the Excel file.");
                }

                for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                    writer.write(getCellValueAsString(header.getCell(i)));
                    if (i < header.getPhysicalNumberOfCells() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        if (j == 5) {
                            double score = row.getCell(j).getNumericCellValue();
                            score += 10;
                            writer.write(String.valueOf(score));
                        } else {
                            writer.write(getCellValueAsString(row.getCell(j)));
                        }
                        if (j < row.getPhysicalNumberOfCells() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public void uploadFileData(String excelFilePath) throws Exception {
        File file = new File(excelFilePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + excelFilePath);
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            processAndSaveExcelFile(inputStream);
        }
    }

    private void processAndSaveExcelFile(InputStream inputStream) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Student> students = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Student student = new Student();
                student.setFirstName(row.getCell(1).getStringCellValue());
                student.setLastName(row.getCell(2).getStringCellValue());
                student.setDOB(row.getCell(3).getStringCellValue());
                student.setStudentClass(row.getCell(4).getStringCellValue());
                double score = row.getCell(5).getNumericCellValue() + 5;
                //TODO:change the variable data type
                student.setScore(String.valueOf(score));
                //TODO:change the variable data type
                student.setStatus(String.valueOf(row.getCell(6).getNumericCellValue()));
                student.setPhotoPath(row.getCell(7).getStringCellValue());
                students.add(student);
            }
            studentRepository.saveAll(students);
        }
    }
}