package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthInfo {
    public String getOtp(String email) {
        String apiUrl = "https://cihuy.triv.id/api/v1/other/auth-info?email=" + email;
        int maksimalPercobaan = 6;
        int jedaWaktu = 10000;

        for (int i = 1; i <= maksimalPercobaan; i++) {
            Response response = (Response) RestAssured.get(apiUrl);
        
            Object otp = response.jsonPath().get("twofa_otp");

            if (otp != null && !String.valueOf(otp).trim().isEmpty()) {
                return String.valueOf(otp);
            }
            try { Thread.sleep(jedaWaktu); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        throw new RuntimeException("Gagal mendapatkan OTP setelah beberapa kali percobaan.");
    }
}
