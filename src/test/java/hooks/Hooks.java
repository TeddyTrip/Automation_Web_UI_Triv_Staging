package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import src.test.java.driver.DriverManager;
import utils.ConfigReader;
import utils.PdfReportUtils;
import utils.ReportingUtils;

public class Hooks {

    @Before
    public void setup(Scenario scenario) {
        
        // Ambil nama file feature yang sedang berjalan
        String featurePath = scenario.getUri().toString();
        String fileName = featurePath.substring(featurePath.lastIndexOf('/') + 1);
        String cleanFileName = fileName.replace(".feature", "");

        // // Mulai penulisan laporan
        // PdfReportUtils.startPdfReport(cleanFileName);
        
        // Hooks hanya perlu "memanggil" saja. 
        // Urusan "bagaimana cara membuka browser" sudah diurus oleh DriverManager.
        DriverManager.getDriver(); 

    }

    @After
    public void tearDown(Scenario scenario) {
        
        // Baca variabel global dari config.properties
        String orientation = ConfigReader.getProperty("report.orientation");
        
        // Tentukan orientasi
        boolean isLandscape = orientation != null && orientation.equalsIgnoreCase("landscape");
        
        // // Buat dan simpan file PDF
        // PdfReportUtils.finishPdfReport(isLandscape);

        // if (scenario.isFailed()) {
        //     // Buat nama file unik berdasarkan nama scenario
        //     String timestamp = String.valueOf(System.currentTimeMillis());
        //     String path = "target/reports/failed-" + scenario.getName() + "-" + timestamp + ".pdf";
            
        //     // Simpan halaman sebagai PDF saat gagal
        //     ReportingUtils.savePageAsPdf(DriverManager.getDriver(), path);
        // }
        DriverManager.quitDriver();
    }
}
