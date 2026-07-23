package formula;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import api.InstallCoinLists;
import api.InstallCoinDetails;
import java.net.URL;

public class MinimalBuyAssetSpotCalculation{
    
    InstallCoinLists installCoinLists = new InstallCoinLists();
    InstallCoinDetails installCoinDetails = new InstallCoinDetails();

    
    public String getMinimalBuyPriceWithCertainCalculation(String code) {
        double minSellPrice = installCoinDetails.getMinimalSellFromApi(code);
        System.out.println("Minimal Sell Price for " + code + ": " + minSellPrice);

        double sellPrice = installCoinLists.getSellPriceFromApi(code);
        System.out.println("Sell Price for " + code + ": " + sellPrice);

        double calculatedMinimalBuyPrice = (sellPrice *  minSellPrice) + 1000;
        System.out.println("Calculated Minimal Buy Price for " + code + ": " + calculatedMinimalBuyPrice);

        return String.valueOf(calculatedMinimalBuyPrice);
    }
        
}
