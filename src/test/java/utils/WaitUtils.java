package utils;

public class WaitUtils {

    /**
     * Menahan eksekusi program dalam detik (bisa desimal, misal 0.5 detik)
     * @param seconds Durasi jeda dalam detik (contoh: 0.5, 1.5, 30)
     */
    public static void waitForSeconds(double seconds) {
        System.out.println("[UI Test] Menunggu jeda " + seconds + " detik agar sistem siap...");
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            System.out.println("[UI Warning] Jeda waktu terinterupsi: " + e.getMessage());
            Thread.currentThread().interrupt(); // Best practice: restore interrupted status
        }
    }
}
