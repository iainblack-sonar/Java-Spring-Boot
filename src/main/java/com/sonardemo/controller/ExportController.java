package com.sonardemo.controller;

import com.sonardemo.service.UserExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private UserExportService exportService;

    @PostMapping("/users/csv")
    public ResponseEntity<String> exportUsersCsv(
            @RequestParam String filename,
            @RequestParam(defaultValue = "USER") String role) {
        String path = exportService.exportUsersToCsv(filename, role);
        return ResponseEntity.ok("Export created: " + path);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadExport(@PathVariable String filename) {
        byte[] data = exportService.downloadExport(filename);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/users/html")
    @ResponseBody
    public String exportUsersHtml(
            @RequestParam String title,
            @RequestParam(defaultValue = "USER") String role) {
        return exportService.exportToHtml(title, role);
    }

    @PostMapping("/backup")
    public ResponseEntity<String> createBackup(@RequestParam String name) {
        exportService.createBackup(name);
        return ResponseEntity.ok("Backup created: " + name + ".zip");
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteExport(@PathVariable String filename) {
        if (exportService.deleteExport(filename)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

