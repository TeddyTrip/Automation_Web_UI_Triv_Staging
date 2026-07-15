package utils;


import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PdfReportUtil {

    private static String reportName;
    private static String folderPath = "src/test/java/report/";
    private static List<StepData> stepsData = new ArrayList<>();
    private static int stepCounter = 1;

    // Struktur Data untuk tiap Baris Tabel
    private static class StepData {
        int stepNum;
        String testCase; 
        String imageUri; 
        String note;     
        String status;   

        public StepData(int stepNum, String testCase, String imageUri, String note, String status) {
            this.stepNum = stepNum;
            this.testCase = testCase;
            this.imageUri = imageUri;
            this.note = note;
            this.status = status;
        }
    }

    // 1. Inisialisasi Laporan 
    public static void startPdfReport(String featureName) {
        reportName = featureName;
        stepsData.clear(); 
        stepCounter = 1;
        
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();
    }

    // 2. Tambah Step ke Tabel 
    public static void addStepToReport(WebDriver driver, String testCase, String note, String status) {
        String imageUri = "";
        try {
            // Ambil screenshot & simpan sementara
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String tempImgName = "temp_step_" + stepCounter + "_" + System.currentTimeMillis() + ".png";
            File destFile = new File(folderPath + tempImgName);
            Files.copy(srcFile.toPath(), destFile.toPath());
            
            // Konversi path ke format URI (file:/...) agar terbaca oleh HTML
            imageUri = destFile.toURI().toString(); 
        } catch (Exception e) {
            System.out.println("Gagal mengambil screenshot: " + e.getMessage());
        }

        // Masukkan ke dalam list memori
        stepsData.add(new StepData(stepCounter++, testCase, imageUri, note, status.toUpperCase()));
    }

    // 3. Generate PDF 
    public static void finishPdfReport(boolean isLandscape) {
        if (stepsData.isEmpty()) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pdfFileName = folderPath + "Report " + reportName + " " + timestamp + ".pdf";
        
        // --- PENGATURAN ORIENTASI KERTAS ---
        String orientation = isLandscape ? "landscape" : "portrait";
        String imgMaxWidth = isLandscape ? "250px" : "150px"; 

        // --- DESAIN TABEL HTML ---
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("@page { size: A4 ").append(orientation).append("; margin: 1cm; } ")
            .append("body { font-family: 'Helvetica', Arial, sans-serif; font-size: 12px; } ")
            .append("h2 { text-align: center; color: #333; } ")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; } ")
            .append("th, td { border: 1px solid #333; padding: 10px; text-align: left; vertical-align: top; } ")
            .append("th { background-color: #f2f2f2; font-weight: bold; } ")
            // Pastikan gambar tidak melebihi batas kolom
            .append("img { max-width: ").append(imgMaxWidth).append("; height: auto; border: 1px solid #ccc; display: block; margin: 0 auto; } ")
            // CSS UNTUK STATUS WARNA
            .append(".PASSED { background-color: #28a745; color: white; font-weight: bold; text-align: center; vertical-align: middle; } ")
            .append(".FAILED { background-color: #dc3545; color: white; font-weight: bold; text-align: center; vertical-align: middle; } ")
            .append("</style></head><body>")
            .append("<h2>Dokumentasi Testing: ").append(reportName).append("</h2>")
            .append("<table>")
            .append("<tr><th style='width: 5%; text-align: center;'>Step</th><th style='width: 25%;'>Test Case (Gherkin)</th><th style='width: 35%; text-align: center;'>Gambar</th><th style='width: 25%;'>Note</th><th style='width: 10%; text-align: center;'>Status</th></tr>");

        // Looping Data ke dalam baris tabel
        for (StepData step : stepsData) {
            String statusClass = step.status.equals("PASSED") ? "PASSED" : "FAILED";
            
            html.append("<tr>")
                .append("<td style='text-align: center;'>").append(step.stepNum).append("</td>")
                .append("<td>").append(step.testCase).append("</td>")
                .append("<td><img src='").append(step.imageUri).append("'/></td>")
                .append("<td>").append(step.note).append("</td>")
                .append("<td class='").append(statusClass).append("'>").append(step.status).append("</td>")
                .append("</tr>");
        }

        html.append("</table></body></html>");

        // --- PROSES KONVERSI HTML KE PDF ---
        try (FileOutputStream os = new FileOutputStream(pdfFileName)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html.toString(), new File(folderPath).toURI().toString());
            builder.toStream(os);
            builder.run();
            
            System.out.println("==================================================");
            System.out.println("PDF Table Report dibuat di: " + pdfFileName);
            System.out.println("==================================================");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Bersihkan file screenshot sementara
            for (StepData step : stepsData) {
                if (step.imageUri != null && !step.imageUri.isEmpty()) {
                    try {
                        // Hilangkan prefix 'file:/' sebelum di-delete
                        String filePath = step.imageUri.replaceFirst("^file:/+", "");
                        // Khusus Windows, tambahkan huruf drive jika hilang, namun biasanya File Object bisa menanganinya
                        new File(filePath).delete();
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}