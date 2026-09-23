package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserGiftCardRandomizer {
    private static final Random RANDOM = new Random();

    public static class AccountData {
        private String username;
        private String email;

        public AccountData(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    /* TODO
     *   1. Lakukan pengecekan email dengan file yg ada di config.email
     *   2. Supaya dinamis dan tidak selalu mengganti data di fungsi ACCOUNTS
     **/
    // Daftar akun berpasangan sesuai data kamu
    private static final List<AccountData> ACCOUNTS = Arrays.asList(
            new AccountData("teddy", "teddytestertriv@gmail.com"),
            new AccountData("oki", "okihandy4@gmail.com"),
            new AccountData("enrico", "enricoch@gmail.com"),
            new AccountData("andre", "andretestertriv@gmail.com"),
            new AccountData("testertriv", "testertriv@gmail.com")
    );

    public static AccountData getRandomAccountExcludingGlobal() {
        // 1. Ambil email_global dari config.properties
        String globalEmail = ConfigReader.getProperty("email_global");

        // 2. Filter daftar akun: lewati jika email sama dengan globalEmail
        List<AccountData> filteredAccounts = ACCOUNTS.stream()
                .filter(account -> globalEmail == null
                        || !account.getEmail()
                        .equalsIgnoreCase(globalEmail.trim())).toList();

        if (filteredAccounts.isEmpty()) {
            throw new IllegalStateException("Tidak ada akun tersisa setelah memfilter email_global!");
        }

        // 3. Ambil 1 akun secara acak dari hasil filter
        Random random = new Random();
        return filteredAccounts.get(random.nextInt(filteredAccounts.size()));
    }

    public static AccountData getRandomAccount() {
        return ACCOUNTS.get(RANDOM.nextInt(ACCOUNTS.size()));
    }

    public static String generateRandomPhoneNumber() {
        long randomNumber = 10000000L + (long) (RANDOM.nextDouble() * 90000000L);
        return "0812" + randomNumber;
    }

    public static String generateRandomMessage() {
        String[] messages = {
                "Happy Special Day! Enjoy your gift card.",
                "Congratulation! Best wishes for you."
        };
        return messages[RANDOM.nextInt(messages.length)];
    }
}
