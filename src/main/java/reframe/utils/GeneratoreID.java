package reframe.utils;

import java.security.SecureRandom;

public class GeneratoreID {

    private static final String CARATTERI = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    // Genera un codice Pratica
    public static String generaRMA() {
        StringBuilder sb = new StringBuilder("RMA-");
        for (int i = 0; i < 6; i++) {
            sb.append(CARATTERI.charAt(random.nextInt(CARATTERI.length())));
        }
        return sb.toString();
    }

    // Genera un ID per il singolo messaggio (esattamente 8 caratteri)
    public static String generaIdTicket() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CARATTERI.charAt(random.nextInt(CARATTERI.length())));
        }
        return sb.toString();
    }
}