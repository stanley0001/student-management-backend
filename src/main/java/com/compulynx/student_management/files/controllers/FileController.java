package com.compulynx.student_management.files.controllers;

import com.compulynx.student_management.files.services.ExcelService;
import com.compulynx.student_management.files.services.FileService;
import com.compulynx.student_management.shared.enums.FileProcesses;
import com.compulynx.student_management.shared.events.appevents.FileProcessesEvent;
import com.compulynx.student_management.shared.models.ResponseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * @author Stanley Mungai
 */

@RestController
@RequestMapping("/api/v2/files")
public class FileController {

    private final FileService fileService;
    private final ExcelService excelService;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${spring.file.max-records}")
    private String maxFileSize;
    public FileController(FileService fileService, ExcelService excelService, ApplicationEventPublisher eventPublisher) {
        this.fileService = fileService;
        this.excelService = excelService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/upload/student-photo/{studentId}")
    public ResponseEntity<String> uploadPhoto(@PathVariable Long studentId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (!file.getContentType().matches("image/jpeg|image/png")) {
                return ResponseEntity.badRequest().body("Only PNG and JPEG files are allowed.");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds the 5MB limit.");
            }

            String filename = studentId + "_" + file.getOriginalFilename();
            String filePath = fileService.saveFile(file, "StudentPhotos", filename);

            return ResponseEntity.ok("File uploaded successfully: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/student-photo/{studentId}/{filename}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String studentId, @PathVariable String filename) throws IOException {
        byte[] fileData = fileService.readFile("StudentPhotos", studentId + "_" + filename);
        return ResponseEntity.ok().body(fileData);
    }

    @DeleteMapping("/delete/student-photo/{studentId}/{filename}")
    public ResponseEntity<String> deletePhoto(@PathVariable String studentId, @PathVariable String filename) {
        boolean deleted = fileService.deleteFile("StudentPhotos", studentId + "_" + filename);
        return deleted ? ResponseEntity.ok("File deleted successfully") : ResponseEntity.badRequest().body("File deletion failed.");
    }

    @PostMapping("/generate/student-file/{count}")
    public ResponseEntity<ResponseModel> generateFile(@PathVariable int count) {
        if (count>Integer.parseInt(maxFileSize)){
            //handle file generation using asynchronous event
            //TODO:communication back to the ui using websockets
            this.eventPublisher.publishEvent(new FileProcessesEvent(this,count, FileProcesses.GENERATE));
            return ResponseEntity.ok(new ResponseModel("File generation scheduled", HttpStatus.ACCEPTED));
        }
        try {
            excelService.generateStudentData(count);
            return ResponseEntity.ok(new ResponseModel("File generated successfully", HttpStatus.OK));
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseModel("Error generating file:"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/process/student-file/{processId}")
    public ResponseEntity<ResponseModel> processFile(@PathVariable int processId) {
        //handle file processing using asynchronous event
        //TODO:communication back to the ui using websockets
        this.eventPublisher.publishEvent(new FileProcessesEvent(this,processId, FileProcesses.PROCESS));
        return ResponseEntity.ok(new ResponseModel("File processing scheduled", HttpStatus.ACCEPTED));
     }
    @PostMapping("/upload/student-file/{processId}")
    public ResponseEntity<ResponseModel> uploadFile(@PathVariable int processId) {
        //handle file upload using asynchronous event
        //TODO:communication back to the ui using websockets
        this.eventPublisher.publishEvent(new FileProcessesEvent(this,processId, FileProcesses.UPLOAD));
        return ResponseEntity.ok(new ResponseModel("File upload scheduled", HttpStatus.ACCEPTED));
    }
}
