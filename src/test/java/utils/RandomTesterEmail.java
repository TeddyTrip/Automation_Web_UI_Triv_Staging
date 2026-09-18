package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomTesterEmail {
    
    public static final List<String> CUSTOM_EMAIL_LIST = Arrays.asList(
        "andretestertriv@gmail.com",
        "teddytestertriv@gmail.com",
        "okihandy4@gmail.com",
        "testertriv@gmail.com",
        "okihandy4@gmail.com"
        // Tambahkan email lainnya di sini jika diperlukan...
    );

    public static String getRandomTesterEmail() {
        String email = ConfigReader.getProperty("email_global");

        if (CUSTOM_EMAIL_LIST == null || CUSTOM_EMAIL_LIST.isEmpty()) {
            throw new IllegalStateException("⚠️ CUSTOM_EMAIL_LIST masih kosong, mohon isi email terlebih dahulu!");
        }
    
        // Jika list hanya berisi 1 email, langsung kembalikan
        if (CUSTOM_EMAIL_LIST.size() == 1) {
            return CUSTOM_EMAIL_LIST.get(0);
        }

        Random random = new Random();
        String selectedEmail;
        
        // Loop akan mencari terus sampai mendapatkan email yang BEDA dari sebelumnya
        do {
            selectedEmail = CUSTOM_EMAIL_LIST.get(random.nextInt(CUSTOM_EMAIL_LIST.size()));
        } while (selectedEmail.equals(email));

        System.out.println("📧 Email terpilih (menghindari duplikat beruntun): " + selectedEmail);
        return selectedEmail;
    }
}
