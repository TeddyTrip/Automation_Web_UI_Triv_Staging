package formula;

import api.v1.AddressBookNetworkList;

public class MinimalWithdrawAmountPlusPercentage {
    
    AddressBookNetworkList addressBookNetworkList = new AddressBookNetworkList();

    public double getMinimalWithdrawPriceWithMinerFee(String code, String protocol) {
        // 1. Ambil minimal withdraw price dasar
        double minWithdrawPrice = addressBookNetworkList.getMinWithdrawWithProtocol(code, protocol);
        System.out.println("Minimal Withdraw Price for " + code + " with Protocol: " + protocol + " is " + minWithdrawPrice);

        // 2. Ambil nilai miner fee (coin) secara dinamis dari API
        double minerFeeCoin = addressBookNetworkList.getMinerFeeCoin(code, protocol);
        System.out.println("Miner Fee (Coin) for " + code + " with Protocol: " + protocol + " is " + minerFeeCoin);

        // 3. Kalkulasi: Minimal Withdraw + Miner Fee (Coin)
        double calculatedFinalAmount = minWithdrawPrice + minerFeeCoin;
        System.out.println("Calculated Final Amount (Min Withdraw + Miner Fee) for " + code + ": " + calculatedFinalAmount);

        // 4. Kembalikan hasil kalkulasi dalam bentuk double (atau String.valueOf jika dibutuhkan sebagai String)
        return calculatedFinalAmount;
    }
}
