package helper;


// Class helper untuk menampung data terstruktur (bisa dibuat file terpisah atau inner class)
public class ProtocolInfo {
    private String protocol;
    private boolean isOptionalMemoId;

    public ProtocolInfo(String protocol, boolean isOptionalMemoId) {
        this.protocol = protocol;
        this.isOptionalMemoId = isOptionalMemoId;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isOptionalMemoId() {
        return isOptionalMemoId;
    }
}
