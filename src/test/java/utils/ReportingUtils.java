package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.print.PrintOptions;
import java.nio.file.StandardOpenOption;

public class ReportingUtils {

    public static void savePageAsPdf(WebDriver driver, String filePath) {
        // 1. Casting driver ke PrintsPage untuk fitur Print PDF
        PrintsPage printer = (PrintsPage) driver;
        
        // 2. Konfigurasi print (bisa disesuaikan dengan kebutuhan)
        PrintOptions printOptions = new PrintOptions();
        
        // 3. Generate PDF dari halaman web
        Pdf pdf = printer.print(printOptions);
        
        try {
            Path path = Paths.get(filePath);
            
            // 5. Pastikan folder parent tersedia
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            // 6. Decode Base64 String menjadi byte[] sebelum ditulis
            byte[] pdfBytes = Base64.getDecoder().decode(pdf.getContent());
            
            // 7. Tulis file dengan byte array yang sudah didecode
            Files.write(path, pdfBytes, StandardOpenOption.CREATE);
            
            System.out.println("PDF berhasil disimpan di: " + filePath);
        } 
        catch (IOException e) {
            System.err.println("Gagal menyimpan PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
