package com.sonardemo.service;

import com.sonardemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class UserExportService {

    private static final String EXPORT_DIR = "/var/exports";
    
    @Autowired
    private UserService userService;

    public String exportUsersToCsv(String filename, String role) {
        List<User> users = userService.findByRole(role, "username");
        
        Path exportPath = Paths.get(EXPORT_DIR, filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(exportPath)) {
            writer.write("ID,Username,Email,Role,Active\n");
            
            for (User user : users) {
                String line = String.format("%d,%s,%s,%s,%b\n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive());
                writer.write(line);
            }
            
            return exportPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Export failed: " + e.getMessage());
        }
    }

    public byte[] downloadExport(String filename) {
        Path filePath = Paths.get(EXPORT_DIR, filename);
        
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Download failed");
        }
    }

    public String exportToHtml(String title, String role) {
        List<User> users = userService.findByRole(role, "username");
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<title>").append(title).append("</title>");
        html.append("</head><body>");
        html.append("<h1>").append(title).append("</h1>");
        html.append("<table border='1'>");
        html.append("<tr><th>Username</th><th>Email</th><th>Role</th></tr>");
        
        for (User user : users) {
            html.append("<tr>");
            html.append("<td>").append(user.getUsername()).append("</td>");
            html.append("<td>").append(user.getEmail()).append("</td>");
            html.append("<td>").append(user.getRole()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table></body></html>");
        return html.toString();
    }

    public void createBackup(String backupName) {
        File exportDir = new File(EXPORT_DIR);
        File[] files = exportDir.listFiles();
        
        String zipPath = EXPORT_DIR + "/" + backupName + ".zip";
        
        try {
            FileOutputStream fos = new FileOutputStream(zipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            for (File file : files) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);
                    
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
            
            zos.close();
            fos.close();
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }

    public boolean deleteExport(String filename) {
        File file = new File(EXPORT_DIR + "/" + filename);
        return file.delete();
    }

    public String getExportPath(String subdir, String filename) {
        return EXPORT_DIR + "/" + subdir + "/" + filename;
    }
}

