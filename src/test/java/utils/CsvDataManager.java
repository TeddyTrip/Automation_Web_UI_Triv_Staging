package utils;

public class CsvDataManager {
    // Mengambil path dari config.properties secara dinamis
    public static String getPath(String flow, String file) {
        String basePath = ConfigReader.getProperty("csv.base.path");
        return basePath + flow + "/" + file + ".csv";
    }
}
