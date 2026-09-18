package helper;

public class SelectedAssetAndProtocol {
        private String currency;
        private String protocol;
        private boolean isOptionalMemoId;
        private boolean useMemoId;

        public SelectedAssetAndProtocol(String currency, String protocol, boolean isOptionalMemoId, boolean useMemoId) {
            this.currency = currency;
            this.protocol = protocol;
            this.isOptionalMemoId = isOptionalMemoId;
            this.useMemoId = useMemoId;
        }

        public String getCurrency() { return currency; }
        public String getProtocol() { return protocol; }
        public boolean isOptionalMemoId() { return isOptionalMemoId; }
        public boolean isUseMemoId() { return useMemoId; }
}
