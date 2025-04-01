package com.compulynx.student_management.files.controllers;

/**
 * @author Stanley Mungai
 */
import com.compulynx.student_management.files.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v2/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
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
}

