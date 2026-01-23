package com.sonardemo.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private static final String BASE_PATH = "/var/data/uploads";

    /**
     * SECURITY ISSUE: Path traversal vulnerability
     * User input used directly in file path without sanitization
     */
    public String readFile(String filename) {
        try {
            // SECURITY: Path traversal - user can use "../" to access arbitrary files
            Path filePath = Paths.get(BASE_PATH, filename);
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            return "Error reading file";
        }
    }

    /**
     * SECURITY ISSUE: Path traversal in file write
     */
    public void writeFile(String filename, String content) {
        try {
            // SECURITY: Path traversal vulnerability
            File file = new File(BASE_PATH + "/" + filename);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            // RELIABILITY: Resource leak - writer not closed
        } catch (IOException e) {
            // RELIABILITY: Empty catch block
        }
    }

    /**
     * RELIABILITY: Resource leak - stream not properly closed
     */
    public byte[] readBinaryFile(String path) {
        try {
            // RELIABILITY: InputStream not closed in case of exception
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return data;
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * RELIABILITY: Multiple resource leaks
     */
    public void copyFile(String source, String destination) {
        try {
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(destination);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            // RELIABILITY: Streams not closed - resource leak
        } catch (IOException e) {
            System.err.println("Copy failed");
        }
    }

    /**
     * SECURITY ISSUE: Command injection vulnerability
     */
    public String executeCommand(String userInput) {
        try {
            // SECURITY: Command injection - user input passed directly to runtime exec
            Process process = Runtime.getRuntime().exec("ls -la " + userInput);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (IOException e) {
            return "Command failed";
        }
    }

    /**
     * MAINTAINABILITY: High cyclomatic complexity
     */
    public String getFileType(String filename) {
        if (filename == null) {
            return "unknown";
        } else if (filename.endsWith(".txt")) {
            return "text";
        } else if (filename.endsWith(".pdf")) {
            return "pdf";
        } else if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
            return "word";
        } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
            return "excel";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image";
        } else if (filename.endsWith(".png")) {
            return "image";
        } else if (filename.endsWith(".gif")) {
            return "image";
        } else if (filename.endsWith(".mp3")) {
            return "audio";
        } else if (filename.endsWith(".mp4")) {
            return "video";
        } else if (filename.endsWith(".zip") || filename.endsWith(".rar")) {
            return "archive";
        } else {
            return "unknown";
        }
    }
}



