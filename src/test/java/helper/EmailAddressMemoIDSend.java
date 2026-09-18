package helper;

public class EmailAddressMemoIDSend {
    private String email;
    private String address;
    private String memoId;

    public EmailAddressMemoIDSend(String email, String address, String memoId) {
        this.email = email;
        this.address = address;
        this.memoId = memoId;
    }

    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getMemoId() { return memoId; }
}
