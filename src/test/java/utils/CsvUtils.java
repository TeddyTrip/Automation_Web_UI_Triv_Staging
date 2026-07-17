package utils;

import java.io.BufferedReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;


public class CsvUtils {
    /**
     * Membaca file CSV dan mengubah setiap baris menjadi Map<Key, Value>
     * Key = Nama Header (baris pertama CSV)
     * Value = Data di baris tersebut
     */
    public static List<Map<String, String>> readData(String filePath) throws Exception {
        // Membuat list untuk menampung hasil data dari setiap baris
        List<Map<String, String>> list = new ArrayList<>();
        
        // Membuka file dengan BufferedReader agar pembacaan file efisien
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            // Membaca baris pertama (header) untuk mengetahui nama-nama kolom
            String headerLine = br.readLine();
            
            // Jika file kosong, kembalikan list dalam keadaan kosong
            if (headerLine == null) return list;

            // Membersihkan karakter BOM (Byte Order Mark) yang sering muncul di file CSV Excel/Notepad
            // Jika tidak dibersihkan, kolom pertama akan terbaca sebagai "\ufeffCode" bukan "Code"
            headerLine = headerLine.replace("\ufeff", "");
            
            // Memecah baris header berdasarkan koma untuk mendapatkan daftar nama kolom
            String[] headers = headerLine.split(",");
            
            // Melakukan trim pada setiap header untuk membuang spasi yang tidak sengaja terketik
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }

            String line;
            // Melakukan perulangan untuk membaca setiap baris data di bawah header
            while ((line = br.readLine()) != null) {
                // Memecah baris data berdasarkan koma
                String[] values = line.split(",");
                
                // Membuat Map baru untuk menyimpan data baris ini (Key=Header, Value=Data)
                Map<String, String> map = new HashMap<>();
                
                // Memasangkan setiap nilai data dengan header yang sesuai
                for (int i = 0; i < headers.length; i++) {
                    // Mengambil nilai data, jika kolom kosong beri string kosong agar tidak error
                    String value = (i < values.length) ? values[i].trim() : "";
                    
                    // Memasukkan data ke Map dengan Key nama kolom
                    map.put(headers[i], value);
                }
                
                // Menambahkan Map baris ini ke dalam List utama
                list.add(map);
            }
        }
        // Mengembalikan list berisi data yang sudah dipetakan
        return list;
    }
}
