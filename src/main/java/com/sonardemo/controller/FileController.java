package com.sonardemo.controller;

import com.sonardemo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * SECURITY: Path traversal vulnerability exposed via API
     */
    @GetMapping("/read")
    public String readFile(@RequestParam String filename) {
        return fileService.readFile(filename);
    }

    /**
     * SECURITY: Path traversal + arbitrary file write
     */
    @PostMapping("/write")
    public String writeFile(@RequestParam String filename, @RequestBody String content) {
        fileService.writeFile(filename, content);
        return "File written";
    }

    /**
     * SECURITY: Command injection exposed via API
     */
    @GetMapping("/list")
    public String listDirectory(@RequestParam String path) {
        return fileService.executeCommand(path);
    }

    /**
     * SECURITY: SSRF potential - internal file access
     */
    @GetMapping("/download")
    public byte[] downloadFile(@RequestParam String path) {
        // SECURITY: No validation of path - could access sensitive system files
        return fileService.readBinaryFile(path);
    }
}

