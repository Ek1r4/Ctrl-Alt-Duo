package reframe.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingPassword {
	
    public static String hashPassword(String passwordInChiaro) 
    {
        try 
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            byte[] hashBytes = digest.digest(passwordInChiaro.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // Eccezione di sicurezza se il server non supporta SHA-512
            throw new RuntimeException("Errore di sistema: Algoritmo SHA-512 non trovato", e);
        }
    }
}