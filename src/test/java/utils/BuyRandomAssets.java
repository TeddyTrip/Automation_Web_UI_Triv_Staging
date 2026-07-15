package utils;

public class BuyRandomAssets {
    public String id, wallet_id, code, currency, label, buy, sell, category;

    public BuyRandomAssets(String id, String wallet_id, String code, String currency, String label, String buy, String sell, String category) {
        this.id = id; this.wallet_id = wallet_id; this.code = code; 
        this.currency = currency; this.label = label; this.buy = buy; 
        this.sell = sell; this.category = category;
    }
}