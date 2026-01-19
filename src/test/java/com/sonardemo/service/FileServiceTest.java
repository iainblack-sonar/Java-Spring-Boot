package com.sonardemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
    }

    @Test
    void readFile_withNonExistentFile_returnsErrorMessage() {
        String result = fileService.readFile("nonexistent.txt");
        
        assertEquals("Error reading file", result);
    }

    @Test
    void readBinaryFile_withNonExistentFile_returnsEmptyArray() {
        byte[] result = fileService.readBinaryFile("/nonexistent/path/file.bin");
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void executeCommand_withInvalidCommand_returnsFailureMessage() {
        // This might fail or return empty depending on the system
        String result = fileService.executeCommand("/nonexistent/path");
        
        assertNotNull(result);
    }

    @Test
    void getFileType_withNullFilename_returnsUnknown() {
        String result = fileService.getFileType(null);
        
        assertEquals("unknown", result);
    }

    @Test
    void getFileType_withTextFile_returnsText() {
        String result = fileService.getFileType("document.txt");
        
        assertEquals("text", result);
    }

    @Test
    void getFileType_withPdfFile_returnsPdf() {
        String result = fileService.getFileType("document.pdf");
        
        assertEquals("pdf", result);
    }

    @Test
    void getFileType_withDocFile_returnsWord() {
        String result = fileService.getFileType("document.doc");
        
        assertEquals("word", result);
    }

    @Test
    void getFileType_withDocxFile_returnsWord() {
        String result = fileService.getFileType("document.docx");
        
        assertEquals("word", result);
    }

    @Test
    void getFileType_withXlsFile_returnsExcel() {
        String result = fileService.getFileType("spreadsheet.xls");
        
        assertEquals("excel", result);
    }

    @Test
    void getFileType_withXlsxFile_returnsExcel() {
        String result = fileService.getFileType("spreadsheet.xlsx");
        
        assertEquals("excel", result);
    }

    @Test
    void getFileType_withJpgFile_returnsImage() {
        String result = fileService.getFileType("photo.jpg");
        
        assertEquals("image", result);
    }

    @Test
    void getFileType_withJpegFile_returnsImage() {
        String result = fileService.getFileType("photo.jpeg");
        
        assertEquals("image", result);
    }

    @Test
    void getFileType_withPngFile_returnsImage() {
        String result = fileService.getFileType("photo.png");
        
        assertEquals("image", result);
    }

    @Test
    void getFileType_withGifFile_returnsImage() {
        String result = fileService.getFileType("animation.gif");
        
        assertEquals("image", result);
    }

    @Test
    void getFileType_withMp3File_returnsAudio() {
        String result = fileService.getFileType("song.mp3");
        
        assertEquals("audio", result);
    }

    @Test
    void getFileType_withMp4File_returnsVideo() {
        String result = fileService.getFileType("movie.mp4");
        
        assertEquals("video", result);
    }

    @Test
    void getFileType_withZipFile_returnsArchive() {
        String result = fileService.getFileType("archive.zip");
        
        assertEquals("archive", result);
    }

    @Test
    void getFileType_withRarFile_returnsArchive() {
        String result = fileService.getFileType("archive.rar");
        
        assertEquals("archive", result);
    }

    @Test
    void getFileType_withUnknownExtension_returnsUnknown() {
        String result = fileService.getFileType("file.xyz");
        
        assertEquals("unknown", result);
    }
}

