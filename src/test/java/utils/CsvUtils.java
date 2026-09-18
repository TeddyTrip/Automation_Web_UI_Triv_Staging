package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
// import utils.randomTesterEmail;

import com.google.common.collect.Table.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.*;

import helper.EmailAddressMemoIDSend;

import java.nio.file.Files;


public class CsvUtils {
    /**
     * Membaca file CSV dan mengubah setiap baris menjadi Map<Key, Value>
     * Key = Nama Header (baris pertama CSV)
     * Value = Data di baris tersebut
     */

    static RandomTesterEmail randomTesterEmail = new RandomTesterEmail();


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


    public static EmailAddressMemoIDSend getAddressProtocolMemoIDWithExactEmail(String filePath, String targetCode, String targetProtocol) {
        String selectedEmail = randomTesterEmail.getRandomTesterEmail();

        try (InputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String currency = getCellString(row.getCell(0));
                String protocol = getCellString(row.getCell(1));

                boolean isProtocolMatch;
                if (targetProtocol == null || targetProtocol.trim().isEmpty()) {
                    isProtocolMatch = protocol.equalsIgnoreCase("NATIVE") || protocol.isEmpty();
                } else {
                    isProtocolMatch = targetProtocol.equalsIgnoreCase(protocol);
                }

                if (currency.equalsIgnoreCase(targetCode) && isProtocolMatch) {
                    int emailColIdx = findColumnIndex(headerRow, selectedEmail);
                    int memoColIdx = findColumnIndex(headerRow, "Memo ID " + selectedEmail);

                    String address = emailColIdx != -1 ? getCellString(row.getCell(emailColIdx)) : "";
                    String memoId = memoColIdx != -1 ? getCellString(row.getCell(memoColIdx)) : "";

                    if (memoId == null || memoId.equalsIgnoreCase("nan") || memoId.trim().isEmpty()) {
                        memoId = "";
                    }

                    System.out.println("🪙 Asset/Protokol Cocok: " + currency + " (" + (targetProtocol != null ? targetProtocol : "NATIVE") + ")");
                    System.out.println("📍 Address Ditemukan : " + (address.isEmpty() ? "Kosong" : address));
                    System.out.println("🏷️ Memo ID Ditemukan : " + (memoId.isEmpty() ? "Tidak ada (Kosong)" : memoId));
                    
                    return new EmailAddressMemoIDSend(selectedEmail, address, memoId);
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ Gagal membaca file Excel: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static String getCellString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private static int findColumnIndex(Row headerRow, String columnName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (headerRow.getCell(i).getStringCellValue().trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
