package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RandomAutoInvestFrequencyDayDate {
    
    private WebDriver driver;
    private WebDriverWait wait;

    public String getRandomFrequency() {
        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1; 
        String selectedFrequency = (randomNumber % 2 != 0) ? "weekly" : "monthly";
        System.out.println("Frekuensi Terpilih: " + selectedFrequency.toUpperCase());
        return selectedFrequency;
    }

    // Random hari bebas (Minggu - Sabtu) tanpa parameter
    public static String getRandomDay() {
        List<String> days = Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
        
        Random random = new Random();
        int index = random.nextInt(days.size());
        String selectedDay = days.get(index);
        
        System.out.println("Hari Terpilih: " + selectedDay.toUpperCase());
        return selectedDay;
    }

    // Random khusus hari kerja selain Sabtu dan Minggu (Senin - Jumat) untuk retry saat error
    public static String getRandomWeekday() {
        List<String> weekdays = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday");
        
        Random random = new Random();
        int index = random.nextInt(weekdays.size());
        String selectedDay = weekdays.get(index);
        
        System.out.println("⚠️ Mengulang dengan Hari Kerja Terpilih (Selain Sabtu/Minggu): " + selectedDay.toUpperCase());
        return selectedDay;
    }

    public static String getRandomMonthlyDate() {
        Random random = new Random();
        int randomDate = random.nextInt(31) + 1; // Menghasilkan angka random dari 1 sampai 31
        String selectedDate = String.valueOf(randomDate);
        
        System.out.println("Tanggal Bulanan Terpilih: " + selectedDate);
        return selectedDate;
    }

    public static String getRandomNominal() {
        List<String> nominals = Arrays.asList("50.000", "100.000", "500.000", "1.000.000");
        
        Random random = new Random();
        int index = random.nextInt(nominals.size());
        String selectedNominal = nominals.get(index);
        
        System.out.println("Nominal Investasi Terpilih: " + selectedNominal);
        return selectedNominal;
    }
}
