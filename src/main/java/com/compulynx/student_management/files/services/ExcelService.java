package com.compulynx.student_management.files.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Stanley Mungai
 */
@Service
public interface ExcelService {
    void generateStudentData(int recordCount) throws IOException;

    void generateStudentBatchData(int count) throws IOException;
    void processExcelToCSV(String excelFilePath, String csvFilePath) throws IOException;
    void uploadFileData(String excelFilePath) throws Exception;
}
