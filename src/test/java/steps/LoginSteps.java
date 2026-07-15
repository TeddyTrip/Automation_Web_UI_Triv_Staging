package steps;

import io.cucumber.java.en.*;
import utils.ConfigReader;
import org.junit.Assert;
import src.test.java.driver.DriverManager; // Mengambil driver global
import pages.LoginPage;
import api.AuthInfo;



public class LoginSteps {
    // Inisialisasi object
    private LoginPage loginPage = new LoginPage(DriverManager.getDriver());
    AuthInfo authInfo = new AuthInfo();

    @Given("Membuka halaman login web di cihuy")
    public void buka_halaman() {
        DriverManager.getDriver().get("https://cihuy.triv.id/id/login");
    }

    @When("Memasukkan email dari variabel global")
    public void input_email() {
        loginPage.enterEmailLogin(ConfigReader.getProperty("email_global"));
    }

    @And("Memasukkan password dari variabel global")
    public void input_password() {
        loginPage.enterPasswordLogin(ConfigReader.getProperty("password_global"));
    }

    @And("Menekan tombol Masuk")
    public void click_masuk_button_login() {
        loginPage.clickMasukButtonLogin();
    }

    @And("Menyelesaikan proses TwoFA jika diminta")
    public void handle_2fa() {
        if (loginPage.isTwoFaPage()) {
            String email = ConfigReader.getProperty("email_global");
            String otp = authInfo.getOtp(email);
            loginPage.inputOtp(otp);
        }
    }

    @Then("Masuk di Dashboard Triv Staging")
    public void verify_dashboard() {
        Assert.assertTrue("Gagal masuk dashboard", 
            DriverManager.getDriver().getCurrentUrl().contains("dashboard"));
    }
}