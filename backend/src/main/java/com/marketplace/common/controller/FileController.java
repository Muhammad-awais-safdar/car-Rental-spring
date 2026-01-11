package com.marketplace.common.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.common.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.uploadFile(file);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "File uploaded successfully", fileUrl),
                HttpStatus.CREATED);
    }

    @PostMapping("/upload-multiple")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files) {

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = fileStorageService.uploadFile(file);
            fileUrls.add(fileUrl);
        }

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Files uploaded successfully", fileUrls),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String filename) {
        fileStorageService.deleteFile(filename);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, "File deleted successfully"),
                HttpStatus.OK);
    }
}
