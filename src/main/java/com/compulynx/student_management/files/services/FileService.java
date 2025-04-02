package com.compulynx.student_management.files.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Stanley Mungai
 */

@Service
public interface FileService {
    boolean deleteFile(String studentPhotos, String s);

    byte[] readFile(String studentPhotos, String s) throws IOException;
    byte[] readFile(String filename) throws IOException;
    String saveFile(MultipartFile file, String studentPhotos, String filename) throws IOException;
}
